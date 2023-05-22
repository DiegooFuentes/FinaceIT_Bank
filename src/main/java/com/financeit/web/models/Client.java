package com.financeit.web.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    public Client(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Client(String firstName, String lastName, String email,String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public Client (String email, String password){
        this.email = email;
        this.password = password;
    }

    public Client() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    Set<Account> accounts = new HashSet<>();

    public Set<Account> getAccounts(){
        return accounts;
    }

    public void addAccount(Account account){
        account.setClient(this);
        accounts.add(account);
    }

    @OneToMany(mappedBy="client", fetch=FetchType.EAGER)
    Set<ClientLoan> clientLoans;

    public Set<ClientLoan> getLoans() {
        return clientLoans;
    }

    @OneToMany(mappedBy="client", fetch=FetchType.EAGER)
    Set<Card> cards;

    public Set<Card> getCards(){
        return cards;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

