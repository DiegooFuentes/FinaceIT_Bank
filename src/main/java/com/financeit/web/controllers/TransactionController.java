package com.financeit.web.controllers;

import com.financeit.web.dtos.TransactionDTO;
import com.financeit.web.repositories.AccountRepository;
import com.financeit.web.repositories.ClientRepository;
import com.financeit.web.repositories.PendingTransactionRepository;
import com.financeit.web.repositories.TransactionRepository;
import com.financeit.web.service.EmailNotificationService;
import com.financeit.web.service.PendingTransactionService;
import com.financeit.web.service.TOTPService;
import com.financeit.web.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TOTPService totpService;
    @Autowired
    private PendingTransactionService pendingTransactionService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private EmailNotificationService emailNotificationService;
    @Autowired
    private PendingTransactionRepository pendingTransactionRepository;

    @Autowired
    public TransactionController(PendingTransactionService pendingTransactionService, TOTPService totpService) {
        this.pendingTransactionService = pendingTransactionService;
        this.totpService = totpService;
    }

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<?> makePendingTransaction(@RequestParam("fromAccountNumber") String accountFromNumber,
                                                    @RequestParam("toAccountNumber") String accountToNumber,
                                                    @RequestParam("amount") Double amount,
                                                    @RequestParam("description") String description,
                                                    Authentication authentication) {

        ResponseEntity<?> reponse = pendingTransactionService.makePendingTransaction(accountFromNumber, accountToNumber,
                amount, description, authentication);
        emailNotificationService.sendNotification(authentication.getName());
        return reponse;
    }

    @PostMapping("/transactions/validate")
    public ResponseEntity<?> makeTransaction(@RequestParam("dynamicPassword") String passwordTOTP,
                                             Authentication authentication) {

        return transactionService.makeTransaction(passwordTOTP, authentication);
    }


    @GetMapping("/transactions")
    public List<TransactionDTO> getTransactions() {
        return transactionRepository.findAll().stream().map(TransactionDTO::new).collect(toList());
    }

    @GetMapping("/transactions/{id}")
    public TransactionDTO getTransaction(@PathVariable Long id) {
        return transactionRepository.findById(id).map(TransactionDTO::new).orElse(null);
    }
}
