package com.financeit.web.scheduling;

import com.financeit.web.repositories.PendingTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class PendingTransactionCleanupScheduler {
    private final PendingTransactionRepository pendingTransactionRepository;

    @Autowired
    public PendingTransactionCleanupScheduler(PendingTransactionRepository pendingTransactionRepository) {
        this.pendingTransactionRepository = pendingTransactionRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0/3 * * * ?") // Se ejecuta cada 3 minutos
    public void cleanupPendingTransactions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoffTime = now.minusMinutes(5);
        pendingTransactionRepository.deleteByLocalDateTimeTOTPBefore(cutoffTime);
    }
}
