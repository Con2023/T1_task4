package org.example.Common.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;


@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    @Column(unique = true, nullable = false)
    private Long transactionId = new Random().nextLong();

    private Instant timestamp = Instant.now();

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @CreationTimestamp // это аннотация автоматически устанавливает значение поля с текущей датой и временем в момент создания записи в бд.
    private LocalDateTime transactionTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;



    public enum TransactionStatus {
        REQUESTED, ACCEPTED, REJECTED, BLOCKED, CANCELLED
    }


    public Instant getTimestamp() {
        return timestamp;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime now) {
        this.transactionTime = now;
    }
}
