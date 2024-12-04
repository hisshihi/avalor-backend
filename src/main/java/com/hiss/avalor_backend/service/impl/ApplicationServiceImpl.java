package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.dto.SaveApplicationDto;
import com.hiss.avalor_backend.entity.AdditionalService;
import com.hiss.avalor_backend.entity.Application;
import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.entity.UserEntity;
import com.hiss.avalor_backend.repo.AdditionalServiceRepo;
import com.hiss.avalor_backend.repo.ApplicationRepo;
import com.hiss.avalor_backend.repo.UserRepo;
import com.hiss.avalor_backend.service.ApplicationService;
import com.hiss.avalor_backend.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepo applicationRepository;
    private final RouteService routeService;
    private final UserRepo userRepository;
    private final AdditionalServiceRepo additionalServiceRepo;

    @CacheEvict(value = "userApplications", key = "#principal.name")
    @Transactional
    public void deleteApplicationById(Long applicationId, Principal principal) {
        Optional<UserEntity> user = userRepository.findByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        applicationRepository.deleteById(applicationId);
    }


    @Override
    public void saveApplication(SaveApplicationDto dto, Principal principal) {
        Optional<UserEntity> user = userRepository.findByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        // Получение маршрутов
        List<Route> routes = dto.getIds().stream()
                .map(routeService::getRouteById)
                .collect(Collectors.toList());

        // Получение дополнительных услуг
        List<AdditionalService> additionalServices = dto.getAdditionalServiceIds().stream()
                .map(id -> additionalServiceRepo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Additional service not found with ID: " + id)))
                .collect(Collectors.toList());

        // Преобразование DTO в сущность Application
        Application application = Application.builder()
                .totalCostRoute(dto.getTotalCostRoute())
                .applicationNumber(dto.getApplicationNumber())
                .nameOfTheRecipient(dto.getNameOfTheRecipient())
                .innOfTheRecipient(dto.getInnOfTheRecipient())
                .addressOfTheRecipient(dto.getAddressOfTheRecipient())
                .image1(saveImage(dto.getImage1()))
                .nameOfTheSender(dto.getNameOfTheSender())
                .emailOfTheSender(dto.getEmailOfTheSender())
                .phoneOfTheSender(dto.getPhoneOfTheSender())
                .fullNameOfTheSender(dto.getFullNameOfTheSender())
                .addressOfTheSender(dto.getAddressOfTheSender())
                .invoice(dto.getInvoice())
                .nameOfTheProduct(dto.getNameOfTheProduct())
                .quantityOfTheProduct(dto.getQuantityOfTheProduct())
                .volumeOfTheProduct(dto.getVolumeOfTheProduct())
                .image2(saveImage(dto.getImage2()))
                .allTotalCost(dto.getAllTotalCost())
                .createdBy(user.get())
                .routes(routes)
                .additionalServices(additionalServices) // Устанавливаем дополнительные услуги
                .build();

        // Сохранение заявки
        applicationRepository.save(application);
    }

    @Transactional
    @Override
    @Cacheable(value = "userApplications", key = "#principal.name")
    public List<Application> findAllByUser(Principal principal) {
        Optional<UserEntity> user = userRepository.findByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        List<Application> applications = applicationRepository.findByCreatedById(user.get().getId());
        applications.forEach(application -> {
            Hibernate.initialize(application.getRoutes()); // Инициализация ленивой коллекции
            Hibernate.initialize(application.getAdditionalServices());
        });
        return applications;
    }

    private byte[] saveImage(MultipartFile image) {
        try {
            return image != null ? image.getBytes() : null;
        } catch (IOException e) {
            throw new RuntimeException("Error while processing image", e);
        }
    }
}

