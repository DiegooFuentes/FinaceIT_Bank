package com.financeit.web.controllers;

import com.financeit.web.dtos.ClientDTO;
import com.financeit.web.models.Account;
import com.financeit.web.models.Client;
import com.financeit.web.repositories.AccountRepository;
import com.financeit.web.repositories.ClientRepository;
import com.financeit.web.service.ClientService;
import com.financeit.web.service.ClientServiceImpl;
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
    ClientServiceImpl clientService;

    @GetMapping("/clients")
    public List<ClientDTO> getClients(){
        return clientService.getClients();
    }

    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientService.getClient(id);
    }

    @GetMapping(path = "/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication){
        return clientService.getCurrentClient(authentication);
    }

    @PostMapping(path = "/clients/register")
    public ResponseEntity<Object> register(@RequestParam String firstName,
                                           @RequestParam String lastName,
                                           @RequestParam String email,
                                           @RequestParam String password){

        String returned = clientService.register(firstName, lastName, email, password);

        if(returned.equals("Missing data")){
            return new ResponseEntity<>(returned, HttpStatus.FORBIDDEN);
        }
        else if (returned.equals("Email already in use")) {
            return new ResponseEntity<>(returned, HttpStatus.FORBIDDEN);

        }else {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

    }



}
