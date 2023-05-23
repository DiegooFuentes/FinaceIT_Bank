package com.financeit.web.service;

import com.financeit.web.models.Card;
import com.financeit.web.models.CardColor;
import com.financeit.web.models.CardType;
import com.financeit.web.models.Client;
import com.financeit.web.repositories.CardRepository;
import com.financeit.web.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.financeit.web.models.CardType.CREDIT;
import static com.financeit.web.models.CardType.DEBIT;
import static com.financeit.web.utils.CardUtil.createCardUtil;

@Service
public class CardServiceImpl implements CardService{

    private final ClientRepository clientRepository;
    private final CardRepository cardRepository;

    public CardServiceImpl(ClientRepository clientRepository, CardRepository cardRepository) {
        this.clientRepository = clientRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public String createCard(CardType type, CardColor color, Authentication authentication) {
        Client client = clientRepository.findByEmail(authentication.getName());
        List<Card> debitCards = cardRepository.getCardByTypeAndClient(DEBIT,client);
        List<Card> creditCards = cardRepository.getCardByTypeAndClient(CREDIT,client);

        if(type == DEBIT && debitCards.size() <= 2){

            Card card = createCardUtil(DEBIT,color,client);
            cardRepository.save(card);
            return "Debit card created";

        } else if (type == CREDIT && creditCards.size() <= 2) {

            Card card = createCardUtil(CREDIT,color,client);
            cardRepository.save(card);
            return "Credit card created";

        } else {

            return "Credit card limit is 3";

        }
    }
}
