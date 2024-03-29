package com.financeit.web.controllers;

import com.financeit.web.models.TransactionLink;
import com.financeit.web.repositories.*;
import com.financeit.web.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.net.URISyntaxException;


@RestController
@RequestMapping("/api")
public class PaymentLinkController {

    @Autowired
    private PendingTransactionService pendingTransactionService;
    @Autowired
    private EmailNotificationService emailNotificationService;
    @Autowired
    private TransactionLinkRepository transactionLinkRepository;
    @Autowired
    private PaymentLinkServiceImpl paymentLinkServiceImpl;


    @GetMapping("/transactions/payment_link")
    public ResponseEntity<?> generatePaymentLink(@RequestParam("fromAccountNumber") String fromAccountNumber,
                                                 @RequestParam("amount") double amount,
                                                 @RequestParam("description") String description,
                                                 Authentication authentication) throws URISyntaxException {

        return paymentLinkServiceImpl.generatePaymentLink(fromAccountNumber, amount, description, authentication);

    }

    @Transactional
    @PostMapping("/transactions/pay_with_link")
    public ResponseEntity<?> payWithLink (@RequestParam("linkCode") String linkCode,
                                          @RequestParam("fromAccountNumber") String fromAccountNumber,
                                          Authentication authentication) {

        if (transactionLinkRepository.existsTransactionLinkByLinkCode(linkCode)) {
            TransactionLink link = transactionLinkRepository.findByLinkCode(linkCode);

            ResponseEntity<?> response = pendingTransactionService.makePendingTransaction(fromAccountNumber, link.getDestinationAccount(),
                    link.getAmount(), link.getDescription(), authentication);
            if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                emailNotificationService.sendNotification(authentication.getName());
            }
            return response;
        } else {
            return new ResponseEntity<>("Link de pago invalido", HttpStatus.FORBIDDEN);
        }
    }
}
