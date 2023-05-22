package com.financeit.web.controllers;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.financeit.web.models.CardType.CREDIT;
import static com.financeit.web.models.CardType.DEBIT;
import static com.financeit.web.utils.CardUtil.createCardUtil;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CardRepository cardRepository;

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam(value="cardType") CardType type,
                                             @RequestParam(value="cardColor") CardColor color,
                                             Authentication authentication){

        Client client = clientRepository.findByEmail(authentication.getName());
        List<Card> debitCards = cardRepository.getCardByTypeAndClient(DEBIT,client);
        List<Card> creditCards = cardRepository.getCardByTypeAndClient(CREDIT,client);

        if(type == DEBIT && debitCards.size() <= 2){

            Card card = createCardUtil(DEBIT,color,client);
            cardRepository.save(card);
            return new ResponseEntity<>("Debit card created", HttpStatus.CREATED);

        } else if (type == CREDIT && creditCards.size() <= 2) {

            Card card = createCardUtil(CREDIT,color,client);
            cardRepository.save(card);
            return new ResponseEntity<>("Credit card created",HttpStatus.CREATED);

        } else {

            return new ResponseEntity<>("Credit card limit is 3",HttpStatus.FORBIDDEN);

        }
    }
}
