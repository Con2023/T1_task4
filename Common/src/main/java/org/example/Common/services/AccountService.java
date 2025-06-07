package org.example.Common.services;

import org.example.Common.DataSourceErrorLogAnnotation;
import org.example.Common.entities.Account;
import org.example.Common.Metric;
import org.example.Common.repositories.AccountRepository;
import org.example.Common.repositories.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;

import java.util.Random;


//В сервисе приписываем точки входа на которые будет реагировать аспект, то есть создаем исключения, которые будут записаны в бд по условию.
//Расписываем основные операции CRUD, задействуем репозитории для получения данных.

@Service
public class AccountService {
    Random random = new Random();

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Metric
    @DataSourceErrorLogAnnotation
    public Account getAccountById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (id <= 0) {
            throw new RuntimeException("ID must be positive", null);
        }
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account with id " + id + " not found"));
    }

    @Metric
    @DataSourceErrorLogAnnotation
    public void saveAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (account.getClient() == null) {
            throw new IllegalStateException("Account must have a client");
        }
        try {
            accountRepository.save(account);
        } catch (ConstraintViolationException ex) {
            throw new RuntimeException("Validation failed: " + ex.getMessage(), ex);
        }
    }

    @Metric
    @DataSourceErrorLogAnnotation
    public void deleteAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found")); // Исключение пробрасывается

        transactionRepository.deleteByAccountId(id);
        accountRepository.delete(account);
    }

    @Metric
    @DataSourceErrorLogAnnotation
    public void updateAccount(Long id, Account account) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account with id " + id + " not found"));
        existingAccount.setAccountType(account.getAccountType());
        existingAccount.setBalance(account.getBalance());
        try {
            accountRepository.save(existingAccount);
        } catch (PersistenceException ex) {
            throw new RuntimeException("Failed to update account with id " + id, ex);
        }

    }
    //метод понадобиться при генерации данных для определения рандомного типа счета
    public Account.AccountType getRandomAccountType() {
        return Account.AccountType.valueOf(random.nextBoolean() ? "ДЕБЕТ" : "КРЕДИТ");
    }
}

//EntityNotFoundException - Запись не найден
//NoSuchElementException - Вызов .get() у пустого Optional
//DataIntegrityViolationException - Нарушение ограничений БД (FK, уникальность)
//ConstraintViolationException - Обрабатывать при сохранении и обновлении
//PersistenceException - Общие ошибки JPA/Hibernate	Обрабатывать для непредвиденных ошибок
//IllegalArgumentException - Некорректные аргументы	Обрабатывать для защиты от ошибок вызова