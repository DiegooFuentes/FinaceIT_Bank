package com.financeit.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private TransactionType type;

    private double amount;

    private String description;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="account_id")
    private Account account;


    public Transaction(TransactionType type, double amount, String description,LocalDateTime date) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public Transaction(TransactionType type, double amount, String description, LocalDateTime date, Account account) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }



    @JsonIgnore
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account){
        this.account = account;
    }
}
