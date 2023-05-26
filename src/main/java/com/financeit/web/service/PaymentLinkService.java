package com.financeit.web.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface PaymentLinkService {

    public ResponseEntity<?> generatePaymentLink (String destinationAccount,
                                                  double amount,
                                                  String description,
                                                  Authentication authentication);

}
