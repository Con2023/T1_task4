package org.example.Common.DTO;

public class TransactionAsseptedMessage {
    private Long clientId;
    private Long accountId;
    private Long amount;

    public TransactionAsseptedMessage(){

    }
    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
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
}
