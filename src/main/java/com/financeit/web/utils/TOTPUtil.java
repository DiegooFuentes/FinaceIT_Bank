package com.financeit.web.utils;

import java.time.LocalDateTime;
import java.util.Random;

public class TOTPUtil {

    private String password;
    private LocalDateTime initialDate;

    public TOTPUtil() {
        this.password = generateOTP();
        this.initialDate = generateDate();
    }

    public static String generateOTP() {
        Random random = new Random();
        int num = random.nextInt(1000000);
        return String.format("%06d", num);
    }
    public static LocalDateTime generateDate(){
        return LocalDateTime.now();
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getInitialDate() {
        return initialDate;
    }

}
