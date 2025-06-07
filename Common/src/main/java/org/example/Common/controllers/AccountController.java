package org.example.Common.controllers;

import org.example.Common.CachedAnnotation;
import org.example.Common.entities.Account;
import org.example.Common.services.AccountService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

@ComponentScan
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @CachedAnnotation
    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @CachedAnnotation
    @PostMapping("/save")
    public void createAccount(@RequestBody Account account) {
        accountService.saveAccount(account);
    }

    @CachedAnnotation
    @PutMapping("/update/{id}")
    public void updateAccount(@PathVariable Long id, @RequestBody Account account) {
        accountService.updateAccount(id, account);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccountById(id);
    }
}
