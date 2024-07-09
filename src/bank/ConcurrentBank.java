package bank;

import bank.account.BankAccount;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class ConcurrentBank {
    private Map<UUID, BankAccount> accounts = new HashMap<>();
    private final Lock bankLock = new ReentrantLock();

    public BankAccount createAccount(BigDecimal startBalance) {
        bankLock.lock();
        try {
            BankAccount bankAccount = new BankAccount(startBalance);
            accounts.put(bankAccount.getId(), bankAccount);
            return bankAccount;
        } finally {
            bankLock.unlock();
        }
    }

    public void transfer(BankAccount accountFrom, BankAccount accountTo, BigDecimal sum) {
        BankAccount firstLock = accountFrom;
        BankAccount secondLock = accountTo;

        if (accountFrom.getId().compareTo(accountTo.getId()) == 1) {
            firstLock = accountTo;
            secondLock = accountFrom;
        }
        firstLock.getLock().lock();
        try {
            secondLock.getLock().lock();
            try {
                accountFrom.withdraw(sum);
                accountTo.deposit(sum);
            } finally {
                secondLock.getLock().unlock();
            }
        } finally {
            firstLock.getLock().unlock();
        }
    }

    public BigDecimal getTotalBalance() {
        bankLock.lock();
        try {
            return accounts.values().stream().map(BankAccount::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        } finally {
            bankLock.unlock();
        }
    }

}
