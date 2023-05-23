package com.financeit.web.controllers;

import com.financeit.web.dtos.TransactionDTO;
import com.financeit.web.models.Account;
import com.financeit.web.models.Client;
import com.financeit.web.models.Transaction;
import com.financeit.web.models.TransactionType;
import com.financeit.web.repositories.AccountRepository;
import com.financeit.web.repositories.ClientRepository;
import com.financeit.web.repositories.TransactionRepository;
import com.financeit.web.service.TOTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.financeit.web.utils.TransactionUtil.transactionUtil;
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


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> makeTransaction(@RequestParam("fromAccountNumber")String accountFromNumber,
                                                  @RequestParam("toAccountNumber")String accountToNumber,
                                                  @RequestParam("amount")Double amount,
                                                  @RequestParam("description")String description,
                                                  Authentication authentication){

        if (amount.toString().isEmpty() || amount.isNaN() || description.isEmpty() || accountFromNumber.isEmpty() || accountToNumber.isEmpty()) {
            return new ResponseEntity<>("Missing data or invalid data", HttpStatus.FORBIDDEN);
        }
        if (accountFromNumber.equals(accountToNumber)) {
            return new ResponseEntity<>("Both accounts are the same number", HttpStatus.FORBIDDEN);
        }
        Account sourceAccount = accountRepository.findByNumber(accountFromNumber);

        if (sourceAccount == null) {
            return new ResponseEntity<>("Source account doesn't exist", HttpStatus.FORBIDDEN);
        }

        if (!sourceAccount.getClient().equals(clientRepository.findByEmail(authentication.getName()))) {
            return new ResponseEntity<>("The source account doesn't belong to the authenticated client.", HttpStatus.FORBIDDEN);
        }

        Account destinationAccount = accountRepository.findByNumber(accountToNumber);
        if (destinationAccount == null) {
            return new ResponseEntity<>("Destination account doesn't exist", HttpStatus.FORBIDDEN);
        }

        if (sourceAccount.getBalance() < amount) {
            return new ResponseEntity<>("Insufficient balance in the source account.", HttpStatus.FORBIDDEN);
        }

        Transaction transactionSrc = new Transaction(TransactionType.DEBIT
                , -amount, description + " - " + destinationAccount.getNumber(), LocalDateTime.now(),sourceAccount);

        Transaction transactionDest = new Transaction(TransactionType.CREDIT,
                amount, description + " - " + sourceAccount.getNumber(), LocalDateTime.now(),destinationAccount);
        transactionRepository.save(transactionSrc);
        transactionRepository.save(transactionDest);

        sourceAccount.setBalance(sourceAccount.getBalance()-amount);
        destinationAccount.setBalance(destinationAccount.getBalance()+amount);
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
        return new ResponseEntity<>(HttpStatus.CREATED);
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
