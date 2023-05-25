package com.financeit.web.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class TransactionLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String destinationAccount;

    private double amount;

    private String description;

    private String linkCode;

    private String link;

    @OneToOne(mappedBy = "transactionLink")
    private Account account;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public TransactionLink() {
    }

    public TransactionLink(String destinationAccount, double amount, String description, String linkCode, String link) {
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.description = description;
        this.linkCode = linkCode;
        this.link = link;
    }

    public Long getId() {
        return id;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
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

    public String getLinkCode() {
        return linkCode;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
