package com.hiss.avalor_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Настройка для выполнения асинхронных задач.
     * @return TaskExecutor - пул потоков.
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Основное количество потоков
        executor.setMaxPoolSize(10); // Максимальное количество потоков
        executor.setQueueCapacity(25); // Очередь задач
        executor.setThreadNamePrefix("Async-"); // Префикс для потоков
        executor.initialize();
        return executor;
    }

}
