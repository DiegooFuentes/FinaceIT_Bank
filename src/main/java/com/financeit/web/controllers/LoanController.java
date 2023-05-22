package com.financeit.web.controllers;

import com.financeit.web.dtos.LoanApplicationDTO;
import com.financeit.web.dtos.LoanDTO;
import com.financeit.web.models.*;
import com.financeit.web.repositories.*;
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
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> requestLoan(@RequestBody LoanApplicationDTO loanApplicationDTO,
                                              Authentication authentication){

        Client originClient = clientRepository.findByEmail(authentication.getName());
        Loan loanType = loanRepository.findById(loanApplicationDTO.getLoanId()).orElse(null);
        Account destinationAcct = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());

        if(loanApplicationDTO.getAmount() > 0 && loanApplicationDTO.getPayments() != 0){

            if(loanRepository.existsById(loanApplicationDTO.getLoanId())){

                assert loanType != null;
                if(loanApplicationDTO.getAmount() <= loanType.getMaxAmount()){

                    if(loanType.getPayments().contains(loanApplicationDTO.getPayments())){

                        if(accountRepository.existsAccountByNumber(loanApplicationDTO.getToAccountNumber())){

                            if(originClient.getAccounts().contains(destinationAcct)){
                                ClientLoan loan = new ClientLoan(loanApplicationDTO.getAmount(),loanApplicationDTO.getPayments(),originClient,loanType);
                                clientLoanRepository.save(loan);

                                Transaction transactionDestination = transactionUtil(TransactionType.CREDIT,loanApplicationDTO.getAmount(),
                                        "Loan approved", LocalDateTime.now(),destinationAcct);
                                transactionRepository.save(transactionDestination);
                                destinationAcct.setBalance(destinationAcct.getBalance() + loanApplicationDTO.getAmount());
                                accountRepository.save(destinationAcct);

                                return new ResponseEntity<>("Loan accepted", HttpStatus.CREATED);

                            }else{
                                return new ResponseEntity<>("Destination account does not belong to the current client", HttpStatus.FORBIDDEN);
                            }
                        }else {
                            return new ResponseEntity<>("Destination account does not exist", HttpStatus.FORBIDDEN);
                        }
                    }else{
                        return new ResponseEntity<>("The payments are not correct", HttpStatus.FORBIDDEN);
                    }
                }else{
                    return new ResponseEntity<>("The amount cannot be superior than the determined", HttpStatus.FORBIDDEN);
                }
            }else{
                return new ResponseEntity<>("This loan does not exist", HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>("Verify the amount or the payments", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/loans")
    public List<LoanDTO> getLoans(){
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(toList());
    }


}
