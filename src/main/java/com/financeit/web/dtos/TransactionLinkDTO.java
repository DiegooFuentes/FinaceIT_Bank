package com.financeit.web.dtos;


public class TransactionLinkDTO {

    private String destinationAccount;

    private double amount;

    private String description;

    private String linkCode;

    public TransactionLinkDTO(String destinationAccount, double amount, String description,String linkCode) {
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.description = description;
        this.linkCode = linkCode;
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
}
