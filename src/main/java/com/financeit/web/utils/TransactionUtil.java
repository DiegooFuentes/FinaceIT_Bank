package com.financeit.web.utils;

import com.financeit.web.models.Account;
import com.financeit.web.models.Transaction;
import com.financeit.web.models.TransactionType;

import java.time.LocalDateTime;

public class TransactionUtil {

    public static Transaction transactionUtil(TransactionType type, double amount, String description, LocalDateTime creationDate, Account account ){

        if(type == TransactionType.DEBIT){
            double newAmount = -amount;
            Transaction transactionDebit = new Transaction(type,newAmount,description,creationDate);
            transactionDebit.setAccount(account);
            return transactionDebit;
        }else{
            Transaction transactionCredit = new Transaction(type,amount,description,creationDate);
            transactionCredit.setAccount(account);
            return transactionCredit;
        }

    }


}
