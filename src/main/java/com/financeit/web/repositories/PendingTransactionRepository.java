package com.financeit.web.repositories;


import com.financeit.web.models.PendingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PendingTransactionRepository extends JpaRepository<PendingTransaction,Long> {
}
