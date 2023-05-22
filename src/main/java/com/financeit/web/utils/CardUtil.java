package com.financeit.web.utils;

import com.financeit.web.models.Card;
import com.financeit.web.models.CardColor;
import com.financeit.web.models.CardType;
import com.financeit.web.models.Client;

import java.time.LocalDateTime;
import java.util.Random;

public final class CardUtil {

    public static Card createCardUtil(CardType type, CardColor color, Client client){

        String cardNumber = generateRandomNumber(16);
        String cvv = generateRandomNumber(3);
        String name = client.getFirstName() + " " + client.getLastName();
        Card card = new Card(name,type,color,cardNumber,cvv, LocalDateTime.now(),LocalDateTime.now().plusYears(5));
        card.setClient(client);
        return card;
    }

    public static String generateRandomNumber(int length) {

        Random random = new Random(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            builder.append(digit);
        }

        if (builder.length() == 16) {
            String in = builder.toString();
            String val = "4";   // use 4 here to insert spaces every 4 characters
            return in.replaceAll("(.{" + val + "})", "$1 ").trim();
        }

        if(builder.length() == 2) {
            String in = builder.toString();
            return String.format("%04d", Integer.parseInt(in));
        }

        return builder.toString();
    }



}
