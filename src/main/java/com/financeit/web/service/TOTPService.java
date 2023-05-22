package com.financeit.web.service;



import com.financeit.web.utils.TOTPUtil;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;


@Service
public class TOTPService {
    //Se definen constantes para el Largo y Time-to-Live en minutos de la TOTP
    private static final int TOTP_LENGTH = 6;
    private static final int TOTP_TTL_MINUTES = 5;


    TOTPUtil totpUtil = new TOTPUtil();

    public boolean validateTOTP(TOTPUtil sourceTotp, TOTPUtil persistenTotp) {
        boolean isDifferenceLessThanTTL = persistenTotp.getInitialDate().until(sourceTotp.getInitialDate(), ChronoUnit.MINUTES) <= TOTP_TTL_MINUTES;
        boolean samePassword = persistenTotp.getPassword().equals(sourceTotp.getPassword());
        return isDifferenceLessThanTTL && samePassword;
    }


}
