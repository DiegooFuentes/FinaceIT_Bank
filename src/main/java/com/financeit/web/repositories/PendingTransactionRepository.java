package com.financeit.web.repositories;


import com.financeit.web.models.PendingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;

@RepositoryRestResource
public interface PendingTransactionRepository extends JpaRepository<PendingTransaction,Long> {
   /* PendingTransaction findByPassword(String password);*/
    PendingTransaction findByEmail(String email);

    void deleteByLocalDateTimeTOTPBefore(LocalDateTime cutoffTime);


}
