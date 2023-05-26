package com.financeit.web.controllers;

import com.financeit.web.dtos.AccountDTO;
import com.financeit.web.dtos.TransactionDTO;
import com.financeit.web.dtos.TransactionLinkDTO;
import com.financeit.web.models.Account;
import com.financeit.web.models.Client;
import com.financeit.web.models.TransactionLink;
import com.financeit.web.repositories.*;
import com.financeit.web.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.financeit.web.utils.CardUtil.generateRandomNumber;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final PendingTransactionService pendingTransactionService;
    private final EmailNotificationService emailNotificationService;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final TransactionLinkRepository transactionLinkRepository;

    @Autowired
    public TransactionController(PendingTransactionService pendingTransactionService,
                                 EmailNotificationService emailNotificationService,
                                 TransactionService transactionService,
                                 TransactionRepository transactionRepository,
                                 AccountRepository accountRepository,
                                 ClientRepository clientRepository,
                                 TransactionLinkRepository transactionLinkRepository) {
        this.pendingTransactionService = pendingTransactionService;
        this.emailNotificationService = emailNotificationService;
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
        this.transactionLinkRepository = transactionLinkRepository;
    }

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<?> makePendingTransaction(@RequestParam("fromAccountNumber") String accountFromNumber,
                                                    @RequestParam("toAccountNumber") String accountToNumber,
                                                    @RequestParam("amount") Double amount,
                                                    @RequestParam("description") String description,
                                                    Authentication authentication) {

        ResponseEntity<?> response = pendingTransactionService.makePendingTransaction(accountFromNumber, accountToNumber,
                amount, description, authentication);
        if(response.getStatusCode().equals(HttpStatus.CREATED)){
            emailNotificationService.sendNotification(authentication.getName());
            return response;
        }else {
            return response;
        }
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
