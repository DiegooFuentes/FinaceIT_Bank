package com.financeit.web.service;

import com.financeit.web.dtos.LoanApplicationDTO;
import com.financeit.web.dtos.LoanDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface LoanService {

    public ResponseEntity<Object> requestLoan(LoanApplicationDTO loanApplicationDTO, Authentication authentication);

    public List<LoanDTO> getLoans();
}
