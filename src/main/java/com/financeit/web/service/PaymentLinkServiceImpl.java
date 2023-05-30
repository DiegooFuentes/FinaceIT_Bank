package com.financeit.web.service;

import com.financeit.web.models.Account;
import com.financeit.web.models.Client;
import com.financeit.web.models.TransactionLink;
import com.financeit.web.repositories.AccountRepository;
import com.financeit.web.repositories.ClientRepository;
import com.financeit.web.repositories.TransactionLinkRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Service
public class PaymentLinkServiceImpl implements PaymentLinkService{

    private final AccountRepository accountRepository;
    private  final ClientRepository clientRepository;
    private final TransactionLinkRepository transactionLinkRepository;

    public PaymentLinkServiceImpl(AccountRepository accountRepository, ClientRepository clientRepository, TransactionLinkRepository transactionLinkRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
        this.transactionLinkRepository = transactionLinkRepository;
    }

    @Override
    public ResponseEntity<?> generatePaymentLink(String destinationAccount, double amount, String description, Authentication authentication) throws URISyntaxException {

        UUID linkId = UUID.randomUUID();
        String linkCode = linkId.toString();


        String baseUrl = "https://finaceitbank-production.up.railway.app/web/pay-link.html";
        URI uri = new URI(baseUrl);
        String query = String.format("linkCode=%s", linkCode);
        URI linkUri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, null);
        String url = linkUri.toString();

        Account account = accountRepository.findByNumber(destinationAccount);
        Client client = clientRepository.findByEmail(authentication.getName());

        if (account.getClient().equals(client)){
            TransactionLink transactionLink = new TransactionLink(destinationAccount, amount, description, linkCode, url);
            account.addTransactionLink(transactionLink);
            transactionLinkRepository.save(transactionLink);
            return ResponseEntity.ok(url);
        }
        else{
            return new ResponseEntity<>("Verifica la cuenta seleccionada", HttpStatus.FORBIDDEN);
        }
    }

}
