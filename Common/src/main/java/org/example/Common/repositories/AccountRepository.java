package org.example.Common.repositories;

import org.example.Common.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT a FROM Account a WHERE concat(a.status,a.accountId,a.accountType,a.balance) LIKE %?1%")

    public List<Account> findByClientId(Long clientId);
    Account findByAccountId(Long accountId);
}
