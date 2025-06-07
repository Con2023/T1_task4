package org.example.Common.DTO;

import java.time.Instant;



public class TransactionSendMessage {
    private Long clientId;
    private Long accountId;
    private Long transactionId;
    private Instant timestamp;
    private Long amount;
    private Long balance;


    public TransactionSendMessage() {
    }
    public TransactionSendMessage(Long clientId, Long accountId, Long transactionId, Long amount, Long balance) {
        this.clientId = clientId;
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.timestamp = Instant.now();
        this.amount = amount;
        this.balance = balance;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}
