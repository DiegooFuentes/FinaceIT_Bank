package com.financeit.web.utils;

import java.util.Random;

public class AccountUtil {

    private static String generateRandomNumber(int length) {

        Random random = new Random(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            builder.append(digit);
        }

        if(builder.length() == 2) {
            String in = builder.toString();
            return String.format("%08d", Integer.parseInt(in));
        }

        return builder.toString();
    }
}