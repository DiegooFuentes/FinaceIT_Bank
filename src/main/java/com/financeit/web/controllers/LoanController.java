package com.financeit.web.controllers;

import com.financeit.web.dtos.LoanApplicationDTO;
import com.financeit.web.dtos.LoanDTO;
import com.financeit.web.models.*;
import com.financeit.web.repositories.*;
import com.financeit.web.service.LoanService;
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

    final
    LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> requestLoan(@RequestBody LoanApplicationDTO loanApplicationDTO,
                                              Authentication authentication){

        return loanService.requestLoan(loanApplicationDTO, authentication);
    }

    @GetMapping("/loans")
    public List<LoanDTO> getLoans(){
       return loanService.getLoans();
    }


}
