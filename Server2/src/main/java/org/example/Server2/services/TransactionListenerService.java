package org.example.Server2.services;


import org.example.Common.DTO.TransactionResultMessage;
import org.example.Common.DTO.TransactionSendMessage;
import org.example.Common.DataSourceErrorLogAnnotation;
import org.example.Common.entities.Transaction;
import org.example.Common.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class TransactionListenerService {

    private static final Logger log = LoggerFactory.getLogger(TransactionListenerService.class);
    @Value("${transaction.limits.max-count}")
    private int maxCount;

    @Value("${transaction.limits.period}")
    private int period;

    private final TransactionRepository transactionRepository;

    private final KafkaTemplate<String, TransactionResultMessage> kafkaTemplate;
    public TransactionListenerService(TransactionRepository transactionRepository, @Qualifier("kafkaResultTemplate") KafkaTemplate<String, TransactionResultMessage> kafkaResultTemplate) {
        this.transactionRepository = transactionRepository;
        this.kafkaTemplate = kafkaResultTemplate;
    }

    @DataSourceErrorLogAnnotation
    @KafkaListener(topics = "t1_demo_transaction_accept",  groupId = "common-service-group",containerFactory = "transactionSendMessageKafkaListenerContainerFactory")
    public void listen(TransactionSendMessage transactionSendMessage) {
       try {
           List<Transaction> transactions = countTransaction(transactionSendMessage.getClientId(), transactionSendMessage.getAccountId(), transactionSendMessage.getTimestamp());

           if (transactions.size() >= maxCount){
               List<Transaction> blocked = transactions.subList(0, maxCount);
               for (Transaction transaction : blocked){
                   sendResult(Transaction.TransactionStatus.BLOCKED, transaction.getTransactionId(), transaction.getAccount().getAccountId());
               }
           }
           else if (transactionSendMessage.getAmount()>transactionSendMessage.getBalance()){
               sendResult(Transaction.TransactionStatus.REJECTED, transactionSendMessage.getTransactionId(), transactionSendMessage.getAccountId());
           }
           else {
               sendResult(Transaction.TransactionStatus.ACCEPTED, transactionSendMessage.getTransactionId(), transactionSendMessage.getAccountId());
           }
       }
       catch (Exception e){
           log.error(e.getMessage());
       }
    }

    @DataSourceErrorLogAnnotation
    public List<Transaction> countTransaction(Long clientId, Long accountId, Instant timestamp){
        Instant countTime = timestamp.minusSeconds(period);  //мы берем время транзакции и вычитаем время в секундах(иаксимум), отправляем запрос в бд и смотрим сколько операций было в этот промежуток.
        Stream<Transaction> transactionStream = transactionRepository.findByAccountId(accountId).stream().filter(transaction -> Objects.equals(transaction.getAccount().getClient().getClientId(), clientId));
        return transactionStream.filter(transaction -> transaction.getTimestamp().isAfter(countTime)).toList();

    }

    @DataSourceErrorLogAnnotation
    public void sendResult(Transaction.TransactionStatus transactionStatus, Long transactionId, Long accountId){
        TransactionResultMessage transactionResultMessage = new TransactionResultMessage();
        transactionResultMessage.setTransactionId(transactionId);
        transactionResultMessage.setAccountId(accountId);
        transactionResultMessage.setStatus(transactionStatus);
        kafkaTemplate.send("t1_demo_transaction_result", transactionResultMessage);
    }
}
