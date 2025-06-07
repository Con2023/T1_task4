package org.example.Common.entities;

import jakarta.persistence.*;

import java.util.Random;


//Какая то ошибочка произошла с средой, поэтому Lombok не удалось подключить, все сеттеры и геттеры написаны через генератор( Надо разобраться

@Entity
@Table(name = "accounts")
public class Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long accountId = new Random().nextLong();;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private Long balance;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    public enum AccountType {
        ДЕБЕТ, КРЕДИТ
    }

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private Long frozenAmount = 0L;

    public enum AccountStatus {
        ARRESTED, BLOCKED, CLOSED, OPEN
    }
     public Long getAccountId(){
            return accountId;
     }


    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(Long frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
