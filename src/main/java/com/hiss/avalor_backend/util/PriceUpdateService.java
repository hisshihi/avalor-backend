package com.hiss.avalor_backend.util;

import com.hiss.avalor_backend.entity.DollarEntity;
import com.hiss.avalor_backend.repo.AdditionalServiceRepo;
import com.hiss.avalor_backend.repo.CarrierRepo;
import com.hiss.avalor_backend.repo.DollarRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceUpdateService {

    private final CbrXmlParser cbrXmlParser;
    private final DollarRepo dollarRepo;
    private final CarrierRepo carrierRepository;
    private final AdditionalServiceRepo additionalServiceRepository;

    @Scheduled(fixedRate = 300000) // Каждые 5 минут
    @Transactional
    public void updatePrices() {
        Double currentDollarRate = cbrXmlParser.getDollarRate();
        if (currentDollarRate == null) {
            throw new IllegalStateException("Не удалось получить курс доллара.");
        }

        // Получение текущей записи курса доллара
        DollarEntity dollarEntity = dollarRepo.findTopByOrderByIdDesc().orElseGet(() -> {
            DollarEntity newDollarEntity = DollarEntity.builder()
                    .newDollar(currentDollarRate)
                    .oldDollar(currentDollarRate) // Первое значение: старый и новый курс одинаковы
                    .build();
            dollarRepo.save(newDollarEntity);
            return newDollarEntity;
        });

        // Проверяем деление на ноль
        if (dollarEntity.getNewDollar() == 0 || dollarEntity.getOldDollar() == 0) {
            throw new IllegalStateException("Некорректные значения курса доллара.");
        }

        // Обновление курса
        dollarEntity.setOldDollar(dollarEntity.getNewDollar());
        dollarEntity.setNewDollar(currentDollarRate);
        dollarRepo.save(dollarEntity);

        // Коэффициент пересчёта цен
        double adjustmentFactor = dollarEntity.getNewDollar() / dollarEntity.getOldDollar();

        // Пересчёт цен перевозчиков
        carrierRepository.findAll().forEach(carrier -> {
            if (carrier.getPriceDollars() != null && carrier.getPriceDollars() > 0) {
                carrier.setPriceDollars((int) Math.round(carrier.getPriceDollars() * adjustmentFactor));
            }

            if (carrier.getPrice() != null && carrier.getPrice() > 0) {
                carrier.setPrice((int) Math.round(carrier.getPrice() * adjustmentFactor));
            }

            if (carrier.getContainerRentalPriceDollars() != null && carrier.getContainerRentalPriceDollars() > 0) {
                carrier.setContainerRentalPriceDollars((int) Math.round(carrier.getContainerRentalPriceDollars() * adjustmentFactor));
            }

            if (carrier.getContainerRentalPrice() != null && carrier.getContainerRentalPrice() > 0) {
                carrier.setContainerRentalPrice((int) Math.round(carrier.getContainerRentalPrice() * adjustmentFactor));
            }

        });

        // Пересчёт цен дополнительных услуг
        additionalServiceRepository.findAll().forEach(service -> {
            if (service.getPrice() != 0) {
                service.setPrice((int) Math.round(service.getPrice() * adjustmentFactor));
            }
        });

        // Сохраняем все изменения в базу данных
        carrierRepository.saveAll(carrierRepository.findAll());
        additionalServiceRepository.saveAll(additionalServiceRepository.findAll());

        System.out.println("Цены успешно обновлены!");
    }
}



