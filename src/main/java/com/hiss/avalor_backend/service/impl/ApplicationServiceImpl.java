package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.dto.SaveApplicationDto;
import com.hiss.avalor_backend.dto.UpdateApplicationDto;
import com.hiss.avalor_backend.entity.AdditionalService;
import com.hiss.avalor_backend.entity.Application;
import com.hiss.avalor_backend.entity.Route;
import com.hiss.avalor_backend.entity.UserEntity;
import com.hiss.avalor_backend.repo.AdditionalServiceRepo;
import com.hiss.avalor_backend.repo.ApplicationRepo;
import com.hiss.avalor_backend.repo.UserRepo;
import com.hiss.avalor_backend.service.ApplicationService;
import com.hiss.avalor_backend.service.CacheService;
import com.hiss.avalor_backend.service.RouteService;
import com.hiss.avalor_backend.util.AmoCRMLeadRequest;
import com.hiss.avalor_backend.util.AmoCrmService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
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
    private final CacheService cacheService;
    private final AmoCrmService amoCrmService;

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
                .line(dto.getLine())
                .comment(dto.getComment())
                .ship(dto.getShip())
                .cityTo(dto.getCityTo())
                .cityFrom(dto.getCityFrom())
                .placeDropOff(dto.getPlaceDropOff())
                .portOfArrival(dto.getPortOfArrival())
                .placeDropOff(dto.getPlaceDropOff())
                .typeOfEquipment(dto.getTypeOfEquipment())
                .freightForwarder(dto.getFreightForwarder())
                .build();

        // Сохранение заявки
        applicationRepository.save(application);

        clearCache();

        amoSubmit(dto.getCityFrom(), dto.getCityTo(), routes, dto.getAdditionalServiceIds(), user.get().getUsername(), dto.getAllTotalCost(), application.getId());
    }

    private void amoSubmit(String cityFrom, String cityTo, List<Route> routes, List<Long> additionalServices, String username, Integer allTotalCost, Long applicationId) {
        List<Integer> priceForRoutes = new ArrayList<>();
        for (Route route : routes) {
//            TODO: обовить вывод цены
            priceForRoutes.add(0);
        }
        List<Long> idsAdditionalServices = new ArrayList<>();
        for (Long additionalService : additionalServices) {
            idsAdditionalServices.add(additionalService);
        }

        String title = "Id: " + applicationId + "\n" + cityFrom + " -> " + cityTo + "\n Цена перевозки: " + priceForRoutes + "\n Ids доп услуг: " + idsAdditionalServices + "\n Пользователь: " + username;
        log.info("Заголовок: {}. Цена: {}", title, allTotalCost);

        AmoCRMLeadRequest amoCRMLeadRequest = new AmoCRMLeadRequest();
        amoCRMLeadRequest.setName(title);
        amoCRMLeadRequest.setPrice(allTotalCost);

        amoCrmService.createLead(amoCRMLeadRequest);
    }

    private void clearCache() {
        cacheService.refreshCacheApplicationUser();
    }

    @Transactional
    @Override
    @Cacheable(value = "userApplications", key = "#principal.name + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Application> findAllByUser(Principal principal, Pageable pageable) {
        Optional<UserEntity> user = userRepository.findByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        Page<Application> applications = applicationRepository.findByCreatedById(user.get().getId(), pageable);
        applications.forEach(application -> {
            Hibernate.initialize(application.getRoutes()); // Инициализация ленивой коллекции
            Hibernate.initialize(application.getAdditionalServices());
        });
        return applications;
    }

    @Override
    @Transactional
    @Cacheable(value = "userApplications", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Application> findAll(Pageable pageable) {
        Page<Application> applications = applicationRepository.findAll(pageable);
        applications.forEach(application -> {
            Hibernate.initialize(application.getRoutes());
            Hibernate.initialize(application.getAdditionalServices());
        });
        return applications;
    }

    @Transactional
    @Override
    public void updateApplication(Long id, UpdateApplicationDto dto) {
        // Найти существующую заявку
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + id));

        // Обновить только переданные поля
        if (dto.getNameOfTheRecipient() != null) {
            application.setNameOfTheRecipient(dto.getNameOfTheRecipient());
        }
        if (dto.getInnOfTheRecipient() != null) {
            application.setInnOfTheRecipient(dto.getInnOfTheRecipient());
        }
        if (dto.getAddressOfTheRecipient() != null) {
            application.setAddressOfTheRecipient(dto.getAddressOfTheRecipient());
        }
        if (dto.getImage1() != null) {
            application.setImage1(saveImage(dto.getImage1()));
        }
        if (dto.getNameOfTheSender() != null) {
            application.setNameOfTheSender(dto.getNameOfTheSender());
        }
        if (dto.getEmailOfTheSender() != null) {
            application.setEmailOfTheSender(dto.getEmailOfTheSender());
        }
        if (dto.getPhoneOfTheSender() != null) {
            application.setPhoneOfTheSender(dto.getPhoneOfTheSender());
        }
        if (dto.getFullNameOfTheSender() != null) {
            application.setFullNameOfTheSender(dto.getFullNameOfTheSender());
        }
        if (dto.getAddressOfTheSender() != null) {
            application.setAddressOfTheSender(dto.getAddressOfTheSender());
        }
        if (dto.getImage2() != null) {
            application.setImage2(saveImage(dto.getImage2()));
        }
        if (dto.getInvoice() != null) {
            application.setInvoice(dto.getInvoice());
        }
        if (dto.getNameOfTheProduct() != null) {
            application.setNameOfTheProduct(dto.getNameOfTheProduct());
        }
        if (dto.getQuantityOfTheProduct() != null) {
            application.setQuantityOfTheProduct(dto.getQuantityOfTheProduct());
        }
        if (dto.getVolumeOfTheProduct() != null) {
            application.setVolumeOfTheProduct(dto.getVolumeOfTheProduct());
        }
        if (dto.getTotalCostRoute() != null) {
            application.setTotalCostRoute(dto.getTotalCostRoute());
        }
        if (dto.getAllTotalCost() != null) {
            application.setAllTotalCost(dto.getAllTotalCost());
        }

        // Обновить маршруты, если переданы
        if (dto.getIds() != null) {
            List<Route> routes = dto.getIds().stream()
                    .map(routeService::getRouteById)
                    .collect(Collectors.toList());
            application.setRoutes(routes);
        }

        // Обновить дополнительные услуги, если переданы
        if (dto.getAdditionalServiceIds() != null) {
            List<AdditionalService> additionalServices = dto.getAdditionalServiceIds().stream()
                    .map(additionalServiceId -> additionalServiceRepo.findById(additionalServiceId)
                            .orElseThrow(() -> new IllegalArgumentException("Additional service not found with ID: " + additionalServiceId)))
                    .collect(Collectors.toList());
            application.setAdditionalServices(additionalServices);
        }

        // Сохранить изменения
        applicationRepository.save(application);

        // Очистить кэш
        clearCache();
    }

    @Transactional
    @Override
    public void updateApplicationWithUser(Long id, UpdateApplicationDto dto, Principal principal) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + principal.getName()));

        Application application = applicationRepository.findByCreatedByIdAndId(user.getId(), id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found with ID: " + id + " for user: " + user.getUsername()));

        // Обновить только переданные поля
        if (dto.getNameOfTheRecipient() != null) {
            application.setNameOfTheRecipient(dto.getNameOfTheRecipient());
        }

        if (dto.getInnOfTheRecipient() != null) {
            application.setInnOfTheRecipient(dto.getInnOfTheRecipient());
        }
        if (dto.getAddressOfTheRecipient() != null) {
            application.setAddressOfTheRecipient(dto.getAddressOfTheRecipient());
        }
        if (dto.getImage1() != null) {
            application.setImage1(saveImage(dto.getImage1()));
        }
        if (dto.getNameOfTheSender() != null) {
            application.setNameOfTheSender(dto.getNameOfTheSender());
        }
        if (dto.getEmailOfTheSender() != null) {
            application.setEmailOfTheSender(dto.getEmailOfTheSender());
        }
        if (dto.getPhoneOfTheSender() != null) {
            application.setPhoneOfTheSender(dto.getPhoneOfTheSender());
        }
        if (dto.getFullNameOfTheSender() != null) {
            application.setFullNameOfTheSender(dto.getFullNameOfTheSender());
        }
        if (dto.getAddressOfTheSender() != null) {
            application.setAddressOfTheSender(dto.getAddressOfTheSender());
        }
        if (dto.getImage2() != null) {
            application.setImage2(saveImage(dto.getImage2()));
        }
        if (dto.getInvoice() != null) {
            application.setInvoice(dto.getInvoice());
        }
        if (dto.getNameOfTheProduct() != null) {
            application.setNameOfTheProduct(dto.getNameOfTheProduct());
        }
        if (dto.getQuantityOfTheProduct() != null) {
            application.setQuantityOfTheProduct(dto.getQuantityOfTheProduct());
        }
        if (dto.getVolumeOfTheProduct() != null) {
            application.setVolumeOfTheProduct(dto.getVolumeOfTheProduct());
        }
        if (dto.getTotalCostRoute() != null) {
            application.setTotalCostRoute(dto.getTotalCostRoute());
        }
        if (dto.getAllTotalCost() != null) {
            application.setAllTotalCost(dto.getAllTotalCost());
        }

        // Обновить маршруты, если переданы
        if (dto.getIds() != null) {
            List<Route> routes = dto.getIds().stream()
                    .map(routeService::getRouteById)
                    .collect(Collectors.toList());
            application.setRoutes(routes);
        }

        // Обновить дополнительные услуги, если переданы
        if (dto.getAdditionalServiceIds() != null) {
            List<AdditionalService> additionalServices = dto.getAdditionalServiceIds().stream()
                    .map(additionalServiceId -> additionalServiceRepo.findById(additionalServiceId)
                            .orElseThrow(() -> new IllegalArgumentException("Additional service not found with ID: " + additionalServiceId)))
                    .collect(Collectors.toList());
            application.setAdditionalServices(additionalServices);
        }

        // Сохранить изменения
        applicationRepository.save(application);

        // Очистить кэш
        clearCache();

    }

    @Override
    @Transactional
    public void deleteApplicationWithUser(Long id, Principal principal) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + principal.getName()));

        Application application = applicationRepository.findByCreatedByIdAndId(user.getId(), id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found with ID: " + id + " for user: " + user.getUsername()));

        // Очищаем связи между заявками и маршрутами
        application.getRoutes().clear();
        applicationRepository.save(application);

        // Удаляем заявку
        applicationRepository.deleteById(application.getId());

        clearCache();

    }

    @Transactional
    @Override
    public void deleteApplication(Long id) {
        Application application = applicationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Application not found"));

        application.getRoutes().clear();
        applicationRepository.save(application);

        applicationRepository.delete(application);
        clearCache();
    }


    private byte[] saveImage(MultipartFile image) {
        try {
            return image != null ? image.getBytes() : null;
        } catch (IOException e) {
            throw new RuntimeException("Error while processing image", e);
        }
    }
}

