package com.financeit.web.controllers;

import com.financeit.web.dtos.TransactionDTO;
import com.financeit.web.repositories.AccountRepository;
import com.financeit.web.repositories.ClientRepository;
import com.financeit.web.repositories.TransactionRepository;
import com.financeit.web.service.PendingTransactionService;
import com.financeit.web.service.TOTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public TransactionController(PendingTransactionService pendingTransactionService) {
        this.pendingTransactionService = pendingTransactionService;
    }


    @PostMapping("/transactions")
    public ResponseEntity<?> makePendingTransaction(@RequestParam("fromAccountNumber") String accountFromNumber,
                                                  @RequestParam("toAccountNumber") String accountToNumber,
                                                  @RequestParam("amount") Double amount,
                                                  @RequestParam("description") String description,
                                                  Authentication authentication) {
        return pendingTransactionService.makePendingTransaction(accountFromNumber, accountToNumber,
                amount, description, authentication);
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
