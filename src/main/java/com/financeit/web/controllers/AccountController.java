package com.financeit.web.controllers;

import com.financeit.web.dtos.AccountDTO;
import com.financeit.web.models.Account;
import com.financeit.web.models.Client;
import com.financeit.web.repositories.AccountRepository;
import com.financeit.web.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.financeit.web.utils.CardUtil.generateRandomNumber;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;


    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(toList());
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());

        if(client.getAccounts().size() >=3){
            return new ResponseEntity<>("Account limit is 3", HttpStatus.FORBIDDEN);//FORBIDDEN 403
        }else {
            String accountNumber = generateRandomNumber(2);
            Account account = new Account("VIN" + accountNumber, LocalDateTime.now(),0);
            account.setClient(client);
            accountRepository.save(account);
            return new ResponseEntity<>("Account created",HttpStatus.CREATED);//CREATED 201
        }
    }

    @GetMapping("/clients/current/accounts")
    public List<AccountDTO> getCurrentAccounts(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        return client.getAccounts().stream().map(AccountDTO::new).collect(toList());
    }
}

