package org.example.Common.services;

import org.example.Common.DataSourceErrorLogAnnotation;
import org.example.Common.entities.Transaction;
import org.example.Common.repositories.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import org.example.Common.Metric;


@Service
public class TransactionService {

    private final TransactionRepository repoTransaction;

    public TransactionService(TransactionRepository repoTransaction) {
        this.repoTransaction = repoTransaction;
    }

    @Metric
    public void slowMethod() {
        try {
            Thread.sleep(1500); // Задержка 1.5 секунды (если лимит = 1000 мс)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @DataSourceErrorLogAnnotation
    public void createTransaction(Transaction transaction) {
        repoTransaction.save(transaction);
    }

    @DataSourceErrorLogAnnotation
    public void deleteTransactionById(Long id) {
        try {
            repoTransaction.deleteById(id);
        } catch (EmptyResultDataAccessException | NoSuchElementException ex) {
            throw new EntityNotFoundException("Transaction with id " + id + " not found", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot delete transaction with id " + id + " due to data integrity violation", ex);
        }
    }

    @DataSourceErrorLogAnnotation
    public void updateTransactionById(Long id, Transaction transaction) {
        try {
            Transaction newTransaction = repoTransaction.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Transaction with id " + id + " not found"));
            newTransaction.setTransactionTime(LocalDateTime.now());
            newTransaction.setAmount(transaction.getAmount());
            repoTransaction.save(newTransaction);
        } catch (ConstraintViolationException ex) {
            throw new RuntimeException("Validation failed for transaction update", ex);
        } catch (PersistenceException ex) {
            throw new RuntimeException("Persistence error during transaction update", ex);
        }
    }


    @DataSourceErrorLogAnnotation
    public Transaction getTransactionById(Long id) {
        try {
            return repoTransaction.findById(id).get();
        }
        catch (EntityNotFoundException ex) {
            throw new EntityNotFoundException("Transaction with id " + id + " not found");
        }
    }

}
