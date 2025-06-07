package org.example.Common.config;


import org.example.Common.entities.Account;
import org.example.Common.entities.Client;
import org.example.Common.entities.Transaction;
import org.example.Common.repositories.AccountRepository;
import org.example.Common.repositories.ClientRepository;
import org.example.Common.repositories.TransactionRepository;
import org.example.Common.services.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.*;

//Генератор данных. Через зависимости устанавливает взаимосвязь с методами CRUD и выполняет дейстивия по заполнению БД. Случайными! данными.

@Component
public class TestData implements CommandLineRunner {


    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    public TestData(ClientRepository clientRepository,
                    AccountRepository accountRepository, AccountService accountService,
                    TransactionRepository transactionRepository, TransactionRepository transactionRepository1) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.transactionRepository = transactionRepository1;
    }

    private final Random random = new Random();

    private final List<String> firstNames = Arrays.asList(
            "Иван", "Петр", "Сергей", "Алексей", "Дмитрий",
            "Анна", "Елена", "Ольга", "Мария", "Наталья"
    );

    private final List<String> lastNames = Arrays.asList(
            "Иванов", "Петров", "Сидоров", "Смирнов", "Кузнецов",
            "Васильев", "Попов", "Соколов", "Михайлов", "Новиков"
    );

    private final List<String> middleNames = Arrays.asList(
            "Иванович", "Петрович", "Сидорович", "Смирнович", "Кузнецович",
            "Васильевич", "Попович", "Соколович", "Михайлович", "Новикович"
    );

    @Override
    public void run(String... args) {
        if(transactionRepository.count() == 0) {
            generateData();
        }

    }
    private void generateData() {
        for (int i = 0; i < 20; i++) {
            Client client = new Client();
            client.setFirstName(firstNames.get(random.nextInt(firstNames.size())));
            client.setLastName(lastNames.get(random.nextInt(lastNames.size())));
            client.setMiddleName(middleNames.get(random.nextInt(middleNames.size())));
            clientRepository.save(client);

            int countAccounts = random.nextInt(2) + 1;
            for (int j = 0; j < countAccounts; j++) {
                Account account = new Account();
                account.setClient(client);
                account.setAccountType(accountService.getRandomAccountType());
                account.setBalance(random.nextLong(100000));
                account.setStatus(Account.AccountStatus.OPEN);
                accountRepository.save(account);

                int transactionCount = random.nextInt(5) + 2;
                for (int k = 0; k < transactionCount; k++) {
                    Transaction transaction = new Transaction();
                    transaction.setAccount(account);
                    transaction.setAmount(random.nextLong(1000) * (random.nextBoolean() ? 1 : -1));
                    transaction.setTransactionTime(LocalDateTime.now());
                    transaction.setStatus(Transaction.TransactionStatus.REQUESTED);
                    transactionRepository.save(transaction);
                }
            }
        }
    }
}

