package com.financeit.web.repositories;

import com.financeit.web.models.Card;
import com.financeit.web.models.CardType;
import com.financeit.web.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> getCardByTypeAndClient(CardType type, Client client);
}