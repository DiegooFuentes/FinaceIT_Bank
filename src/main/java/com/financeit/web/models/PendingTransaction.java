package com.financeit.web.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class PendingTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private TransactionType type;
    private double amount;
    private String description;
    private String accountFromNumber;
    private String accountToNumber;

    //CAMPOS TOTP
    private String passwordTOTP;
    private LocalDateTime localDateTimeTOTP;


    public PendingTransaction() {
    }

    public PendingTransaction(TransactionType type, double amount, String description, String passwordTOTP, LocalDateTime localDateTimeTOTP) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.passwordTOTP = passwordTOTP;
        this.localDateTimeTOTP = localDateTimeTOTP;
    }

    public PendingTransaction(TransactionType type, double amount, String description, String accountFromNumber, String accountToNumber, String passwordTOTP, LocalDateTime localDateTimeTOTP) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.accountFromNumber = accountFromNumber;
        this.accountToNumber = accountToNumber;
        this.passwordTOTP = passwordTOTP;
        this.localDateTimeTOTP = localDateTimeTOTP;

    }

    public Long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPasswordTOTP() {
        return passwordTOTP;
    }

    public void setPasswordTOTP(String passwordTOTP) {
        this.passwordTOTP = passwordTOTP;
    }

    public LocalDateTime getLocalDateTimeTOTP() {
        return localDateTimeTOTP;
    }

    public void setLocalDateTimeTOTP(LocalDateTime localDateTimeTOTP) {
        this.localDateTimeTOTP = localDateTimeTOTP;
    }

    public String getAccountFromNumber() {
        return accountFromNumber;
    }

    public void setAccountFromNumber(String accountFromNumber) {
        this.accountFromNumber = accountFromNumber;
    }

    public String getAccountToNumber() {
        return accountToNumber;
    }

    public void setAccountToNumber(String accountToNumber) {
        this.accountToNumber = accountToNumber;
    }
}
