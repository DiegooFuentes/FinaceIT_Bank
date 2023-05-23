package com.financeit.web.service;

import com.financeit.web.models.CardColor;
import com.financeit.web.models.CardType;
import org.springframework.security.core.Authentication;

public interface CardService {

    public String createCard(CardType type, CardColor color, Authentication authentication);
}
