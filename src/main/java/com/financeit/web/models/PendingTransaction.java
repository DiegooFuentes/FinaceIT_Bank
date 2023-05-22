package com.financeit.web.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PendingTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private TransactionType type;
    private double amount;
    private String description;
    //private LocalDateTime date;


    public PendingTransaction() {
    }

    public PendingTransaction(TransactionType type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
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
}
