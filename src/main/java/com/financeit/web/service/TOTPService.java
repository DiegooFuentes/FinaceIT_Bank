package com.financeit.web.service;



import com.financeit.web.models.PendingTransaction;
import com.financeit.web.repositories.PendingTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;


@Service
public class TOTPService {
    @Autowired
    PendingTransactionRepository pendingTransactionRepository;
    //Se definen constantes para el Largo y Time-to-Live en minutos de la TOTP
    private static final int TOTP_LENGTH = 6;
    private static final int TOTP_TTL_MINUTES = 5;

    public static String generatePasswordTOTP() {
        Random random = new Random();
        int num = random.nextInt(1000000);
        return String.format("%06d", num);
    }
    public static LocalDateTime generateDateTOTP(){
        return LocalDateTime.now();
    }

    public boolean validateTOTP(String passwordTOTP, String email) {
        PendingTransaction pendingTransaction = pendingTransactionRepository.findByEmail(email);
        LocalDateTime pendingDate = pendingTransaction.getLocalDateTimeTOTP();
        boolean isDifferenceLessThanTTL = pendingDate.until(LocalDateTime.now(), ChronoUnit.MINUTES) <= TOTP_TTL_MINUTES;
        boolean samePassword = pendingTransaction.getPasswordTOTP().equals(passwordTOTP);
        return isDifferenceLessThanTTL && samePassword;
    }


}
