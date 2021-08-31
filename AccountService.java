package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TransferService {
    @Value("${accountService.url}")
    private String url;

    @Value("${transfer.comission}:100")
    private double comission;

    private Long accountTo;

    /**
     * REST-service to get account by BIK (БИК)
     */
    private AccountService accountService;
    /**
     * Database
     */
    private AccountRepository accountRepository;

    @Bean
    public AccountService accountService() {
        return new AccountService(url);
    }

    public void sendMoney(double amount, long accountFrom, long bankBikTo) {
        if (amount <= 0 || accountFrom <= 0 || accountTo <= 0) {
            throw new IllegalArgumentException("Wrong parameter");
        }

        accountTo = accountService.getAccountByBIK(bankBikTo);

        try {
            makeTransfer(accountFrom, amount);
        } catch (Exception exc) {
            log.info("Could not transfer money");
        }
    }

    @Transactional
    private void makeTransfer(long accountFrom, double amount) {
        Account accFrom = accountRepository.findByAccountNumber(accountFrom);
        Account accTo = accountRepository.findByAccountNumber(accountTo);

        accFrom.setBalance(accFrom.getBalance - amount * (1 + comission));
        accTo.setBalance(accTo.getBalance + amount);

        accountRepository.save(accFrom);
        accountRepository.save(accTo);
    }
}
