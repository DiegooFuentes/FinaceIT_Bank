package com.financeit.web.controllers;

import com.financeit.web.dtos.TransactionDTO;
import com.financeit.web.models.Account;
import com.financeit.web.models.Client;
import com.financeit.web.models.Transaction;
import com.financeit.web.models.TransactionType;
import com.financeit.web.repositories.AccountRepository;
import com.financeit.web.repositories.ClientRepository;
import com.financeit.web.repositories.TransactionRepository;
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


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> makeTransaction(@RequestParam("fromAccountNumber")String accountFromNumber,
                                                  @RequestParam("toAccountNumber")String accountToNumber,
                                                  @RequestParam("amount")Double amount,
                                                  @RequestParam("description")String description,
                                                  Authentication authentication){
        Account destinationAccount = accountRepository.findByNumber(accountToNumber);
        Client originClient = clientRepository.findByEmail(authentication.getName());
        Account originAccount = accountRepository.findByNumber(accountFromNumber);

        if(accountRepository.existsAccountByNumber(accountFromNumber)){ //If the origin account exist in database

            if (originClient.getAccounts().contains(originAccount)){ //If the origin acct belongs to authenticated client

                if(accountRepository.existsAccountByNumber(accountToNumber) && !accountFromNumber.equals(accountToNumber)){ //If the destination acct exist in database and is not the same as the origin acct

                    if(originAccount.getBalance() >= amount){ //If the origin acct have enough balance

                        //Transaction from origin acct // type debit
                        Transaction transactionOrigin = transactionUtil(TransactionType.DEBIT,amount,description, LocalDateTime.now(),originAccount);
                        transactionRepository.save(transactionOrigin);
                        originAccount.setBalance(originAccount.getBalance() - amount);
                        accountRepository.save(originAccount);

                        //Transaction from destination acct // type credit
                        Transaction transactionDestination = transactionUtil(TransactionType.CREDIT,amount,description,LocalDateTime.now(),destinationAccount);
                        transactionRepository.save(transactionDestination);
                        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
                        accountRepository.save(destinationAccount);

                        return new ResponseEntity<>("Transference done", HttpStatus.CREATED);

                    }else{
                        return new ResponseEntity<>("Not enough balance", HttpStatus.FORBIDDEN);
                    }

                }else{
                    return new ResponseEntity<>("Destination account does not exist or is the same as the origin account", HttpStatus.FORBIDDEN);
                }
            } else{
                return new ResponseEntity<>("Origin account does not exist", HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>("Origin account does not exist", HttpStatus.FORBIDDEN);
        }
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
