package org.example.Common.DTO;


import org.example.Common.entities.Transaction;


public class TransactionResultMessage {
    private Transaction.TransactionStatus status;
    private Long  transactionId;
    private Long  accountId;

    public TransactionResultMessage(){}
    public TransactionResultMessage(Transaction.TransactionStatus status, Long  transactionId, Long accountId) {
        this.status = status;
        this.transactionId = transactionId;
        this.accountId = accountId;
    }

    public Transaction.TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(Transaction.TransactionStatus status) {
        this.status = status;
    }

    public Long  getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long  getAccountId() {
        return accountId;
    }

    public void setAccountId(Long  accountId) {
        this.accountId = accountId;
    }
}
