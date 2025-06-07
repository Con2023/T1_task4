package org.example.Server1.services;

import org.slf4j.Logger;
import org.example.Common.DTO.TransactionAsseptedMessage;
import org.example.Common.DTO.TransactionResultMessage;
import org.example.Common.DTO.TransactionSendMessage;
import org.example.Common.DataSourceErrorLogAnnotation;
import org.example.Common.entities.Account;
import org.example.Common.entities.Transaction;
import org.example.Common.repositories.AccountRepository;
import org.example.Common.repositories.TransactionRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class TransactionListenerService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, TransactionSendMessage> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(TransactionListenerService.class);

    public TransactionListenerService(TransactionRepository transactionRepository, AccountRepository accountRepository, @Qualifier("kafkaTemplate") KafkaTemplate<String, TransactionSendMessage> kafkaTemplate) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @DataSourceErrorLogAnnotation
    @KafkaListener(topics = "t1_demo_transactions",  groupId = "common-service-group",containerFactory = "kafkaListenerContainerFactory")
    public void listen(TransactionAsseptedMessage transactionAsseptedMessage) {
        try {
            Account account = accountRepository.findByAccountId(transactionAsseptedMessage.getAccountId());

        if (account.getStatus().equals(Account.AccountStatus.OPEN)){
            Transaction transaction = new Transaction();
            transaction.setStatus(Transaction.TransactionStatus.REQUESTED);
            transaction.setAccount(account);
            transaction.setAmount(transactionAsseptedMessage.getAmount());


            account.setFrozenAmount(transactionAsseptedMessage.getAmount()+account.getFrozenAmount());
            try {
                transactionRepository.save(transaction);
                accountRepository.save(account);
            }
            catch (DataAccessException e){
                logger.error(e.getMessage());
            }

            TransactionSendMessage transactionSendMessage = new TransactionSendMessage(
                    account.getClient().getClientId(),
                    account.getAccountId(),
                    transaction.getTransactionId(),
                    transactionAsseptedMessage.getAmount(),
                    account.getBalance()
            );
            kafkaTemplate.send("t1_demo_transaction_accept", transactionSendMessage);
        }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

    }
    @DataSourceErrorLogAnnotation
    @KafkaListener(topics = "t1_demo_transaction_result",  groupId = "common-service-group",containerFactory = "transactionResultKafkaListenerContainerFactory")
    public void listen(TransactionResultMessage transactionResultMessage) {

        try {
            Transaction transaction = transactionRepository.findByTransactionId(transactionResultMessage.getTransactionId());
            Account account = accountRepository.findByAccountId(transactionResultMessage.getAccountId());


            switch (transactionResultMessage.getStatus()) {
                case ACCEPTED -> {
                    transaction.setStatus(Transaction.TransactionStatus.ACCEPTED);
                    Long newBalance = account.getBalance() - transaction.getAmount();
                    Long newFrozenAmount = account.getFrozenAmount() - transaction.getAmount();

                    account.setBalance(newBalance);
                    account.setFrozenAmount(newFrozenAmount);

                    try {
                        accountRepository.save(account);
                        transactionRepository.save(transaction);
                    }
                    catch (DataAccessException e){
                        logger.error(e.getMessage());
                    }

                }
                case BLOCKED -> {
                    account.setStatus(Account.AccountStatus.BLOCKED);
                    transaction.setStatus(Transaction.TransactionStatus.BLOCKED);

                    Long newFrozenAmount = account.getFrozenAmount() - transaction.getAmount();
                    account.setFrozenAmount(newFrozenAmount);

                    try {
                        accountRepository.save(account);
                        transactionRepository.save(transaction);
                    }
                    catch (DataAccessException e){
                        logger.error(e.getMessage());
                    }
                }
                case REJECTED  -> {
                    transaction.setStatus(Transaction.TransactionStatus.REJECTED);
                    Long newFrozenAmount = account.getFrozenAmount() - transaction.getAmount();
                    account.setBalance(account.getBalance() + transaction.getAmount());
                    account.setFrozenAmount(newFrozenAmount);

                    try {
                        accountRepository.save(account);
                        transactionRepository.save(transaction);
                    }
                    catch (DataAccessException e){
                        logger.error(e.getMessage());
                    }
                }
                default -> throw new IllegalArgumentException(
                        "Invalid transaction status: " + transactionResultMessage.getStatus()
                );
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

    }
}
