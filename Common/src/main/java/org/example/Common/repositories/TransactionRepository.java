package org.example.Common.repositories;

import org.example.Common.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT a FROM Transaction a WHERE concat(a.amount,a.status,a.account,a.timestamp,a.transactionId,a.transactionTime) LIKE %?1%")
    void deleteByAccountId(Long id);

    public List<Transaction> findByAccountId(Long accountId);
    Transaction findByTransactionId(Long transactionId);
}
