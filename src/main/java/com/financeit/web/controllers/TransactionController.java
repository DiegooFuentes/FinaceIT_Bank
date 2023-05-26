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

    @PostMapping("/transactions/get_link")
    public ResponseEntity<String> makeLinkOfTransaction (@RequestBody TransactionLinkDTO transactionLinkDTO,
                                                    Authentication authentication){

        String linkCode = generateRandomNumber(21);

        String baseUrl = "http://localhost:8080"; // "http://your-domain.com"; // Replace with your base URL
        String endpoint = "/api/transactions/pay_with_link"; // + linkCode; // Replace with your endpoint

        String generatedLink = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(endpoint)
                .toUriString();

        Account destinationAccount = accountRepository.findByNumber(transactionLinkDTO.getDestinationAccount());
        TransactionLink transactionLink = new TransactionLink(destinationAccount.getNumber(), transactionLinkDTO.getAmount(),
                transactionLinkDTO.getDescription(),linkCode, generatedLink);
        destinationAccount.setTransactionLink(transactionLink);
        transactionLinkRepository.save(transactionLink);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping("/transactions/get_link")
    public ResponseEntity<String> generateLink(@RequestParam("destinationAccount") String destinationAccount,
                                               Authentication authentication) {

        Client client = clientRepository.findByEmail(authentication.getName());
        Account account = accountRepository.findByNumber(destinationAccount);
        TransactionLink transactionLink = account.getTransactionLink();
        String code = transactionLink.getLinkCode();
        String link = transactionLink.getLink();

        return ResponseEntity.ok(link + code);
    }

    @GetMapping("/transactions/pay_with_link/{linkCode}")
    public ResponseEntity<?> payWithLink (@PathVariable String linkCode,
                                          @RequestParam("fromAccountNumber") String accountFromNumber,
                                          Authentication authentication){


        TransactionLink link = transactionLinkRepository.findByLinkCode(linkCode);

        ResponseEntity<?> response = pendingTransactionService.makePendingTransaction(accountFromNumber, link.getDestinationAccount(),
                link.getAmount(), link.getDescription(), authentication);
        emailNotificationService.sendNotification(authentication.getName());
        return response;

    }
}
