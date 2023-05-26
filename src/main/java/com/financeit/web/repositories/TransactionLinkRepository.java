package com.financeit.web.repositories;

import com.financeit.web.models.TransactionLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface TransactionLinkRepository extends JpaRepository<TransactionLink,Long> {

    TransactionLink findByLinkCode (String LinkCode);

    boolean existsTransactionLinkByLinkCode (String LinkCode);

}
