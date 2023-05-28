package com.financeit.web.service;

import com.financeit.web.models.Account;
import com.financeit.web.models.PendingTransaction;
import com.financeit.web.models.Transaction;
import com.financeit.web.models.TransactionType;
import com.financeit.web.repositories.AccountRepository;
import com.financeit.web.repositories.ClientRepository;
import com.financeit.web.repositories.PendingTransactionRepository;
import com.financeit.web.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    TOTPService totpService;
    @Autowired
    PendingTransactionService pendingTransactionService;
    @Autowired
    PendingTransactionRepository pendingTransactionRepository;

    @Transactional
    public ResponseEntity<?> makeTransaction(String passwordTOTP, Authentication authentication) {

        //Se valida si la TOTP es igual a la almacenada o si su tiempo ya expiró
        if (!totpService.validateTOTP(passwordTOTP, authentication.getName())) {
            return new ResponseEntity<>("TOTP doesn't match or expired", HttpStatus.FORBIDDEN);
        }
        PendingTransaction pendingTransaction = pendingTransactionRepository.findByEmail(authentication.getName());

        Account sourceAccount = accountRepository.findByNumber(pendingTransaction.getAccountFromNumber());
        Account destinationAccount = accountRepository.findByNumber(pendingTransaction.getAccountToNumber());


        Transaction transactionSrc = new Transaction(TransactionType.DEBIT
                , -pendingTransaction.getAmount(), pendingTransaction.getDescription() + " - " + destinationAccount.getNumber(), LocalDateTime.now(), sourceAccount);
        Transaction transactionDest = new Transaction(TransactionType.CREDIT,
                pendingTransaction.getAmount(), pendingTransaction.getDescription() + " - " + sourceAccount.getNumber(), LocalDateTime.now(), destinationAccount);

        transactionRepository.save(transactionSrc);
        transactionRepository.save(transactionDest);

        sourceAccount.setBalance(sourceAccount.getBalance() - pendingTransaction.getAmount());
        destinationAccount.setBalance(destinationAccount.getBalance() + pendingTransaction.getAmount());
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
        pendingTransactionRepository.deleteByEmail(authentication.getName());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
