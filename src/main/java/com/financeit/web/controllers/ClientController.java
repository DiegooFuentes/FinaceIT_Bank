package com.financeit.web.controllers;

import com.financeit.web.dtos.ClientDTO;
import com.financeit.web.models.Account;
import com.financeit.web.models.Client;
import com.financeit.web.repositories.AccountRepository;
import com.financeit.web.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.financeit.web.utils.CardUtil.generateRandomNumber;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/clients")
    public List<ClientDTO> getClients(){
        return clientRepository.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }

    @GetMapping(path = "/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication){
        return new ClientDTO(clientRepository.findByEmail(authentication.getName()));
    }

    @PostMapping(path = "/clients/register")
    public ResponseEntity<Object> register(@RequestParam String firstName,
                                           @RequestParam String lastName,
                                           @RequestParam String email,
                                           @RequestParam String password){
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()){
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if(clientRepository.findByEmail(email) != null){
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }

        Client client = new Client(firstName,lastName,email,passwordEncoder.encode(password));
        clientRepository.save(client);
        String accountNumber = generateRandomNumber(2);
        Account account = new Account("VIN" + accountNumber, LocalDateTime.now(),0);
        account.setClient(client);
        accountRepository.save(account);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }



}
