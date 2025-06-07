package org.example.Common.controllers;

import org.example.Common.entities.Transaction;
import org.example.Common.Metric;
import org.example.Common.services.TransactionService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/check")
    public void slowMethod() {
        transactionService.slowMethod();
    }


    @Metric
    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }


    @Metric
    @PutMapping("/update/{id}")
    public  void updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        transactionService.updateTransactionById(id, transaction);
    }

    @Metric
    @DeleteMapping("/delete/{id}")
    public  void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransactionById(id);
    }

    @Metric
    @PostMapping("/save")
    public void createTransaction(@RequestBody Transaction transaction) {
        transactionService.createTransaction(transaction);
    }

}
