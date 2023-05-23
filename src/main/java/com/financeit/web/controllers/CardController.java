package com.financeit.web.controllers;

import com.financeit.web.models.Card;
import com.financeit.web.models.CardColor;
import com.financeit.web.models.CardType;
import com.financeit.web.models.Client;
import com.financeit.web.repositories.CardRepository;
import com.financeit.web.repositories.ClientRepository;
import com.financeit.web.service.CardService;
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

    final
    CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam(value="cardType") CardType type,
                                             @RequestParam(value="cardColor") CardColor color,
                                             Authentication authentication){

        String returnedCard = cardService.createCard(type,color,authentication);
        if(returnedCard.contains("Debit")){

            return new ResponseEntity<>(returnedCard, HttpStatus.CREATED);

        } else if (returnedCard.contains("Credit")) {

            return new ResponseEntity<>(returnedCard, HttpStatus.CREATED);

        } else{

            return new ResponseEntity<>(returnedCard,HttpStatus.FORBIDDEN);
        }


    }
}
