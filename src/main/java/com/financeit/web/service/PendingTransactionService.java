package com.financeit.web.service;

import com.financeit.web.models.Account;
import com.financeit.web.models.PendingTransaction;
import com.financeit.web.models.Transaction;
import com.financeit.web.models.TransactionType;
import com.financeit.web.repositories.AccountRepository;
import com.financeit.web.repositories.ClientRepository;
import com.financeit.web.repositories.PendingTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class PendingTransactionService {
    @Autowired
    private PendingTransactionRepository pendingTransactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;


    @Transactional
    public ResponseEntity<?> makePendingTransaction(String accountFromNumber,
                                                    String accountToNumber,
                                                    Double amount,
                                                    String description,
                                                    Authentication authentication) {
        if (amount == null || amount.isNaN() || description.isEmpty() || accountFromNumber.isEmpty() || accountToNumber.isEmpty()) {
            return new ResponseEntity<>("Missing data or invalid data", HttpStatus.FORBIDDEN);
        }
        if (accountFromNumber.equals(accountToNumber)) {
            return new ResponseEntity<>("Both accounts have the same number", HttpStatus.FORBIDDEN);
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
        if (pendingTransactionRepository.findByEmail(authentication.getName()) != null) {
            return new ResponseEntity<>("There is a pending transaction for a current client.", HttpStatus.FORBIDDEN);
        }


        LocalDateTime totpDate = TOTPService.generateDateTOTP();
        String totpPassword = TOTPService.generatePasswordTOTP();

        PendingTransaction pendingTransaction = new PendingTransaction(authentication.getName(),
                TransactionType.DEBIT,
                amount,
                description,
                accountFromNumber,
                accountToNumber,
                totpPassword,
                totpDate);
        pendingTransactionRepository.save(pendingTransaction);


        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
