package bank.account;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private UUID id;
    private BigDecimal balance;
    private final Lock lock = new ReentrantLock();

    public BankAccount(BigDecimal startBalance) {
        this.id = UUID.randomUUID();
        this.balance = startBalance;
    }

    public UUID getId() {
        return id;
    }

    public void deposit(BigDecimal deposit) {
        lock.lock();
        try {
            balance.add(deposit);
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(BigDecimal sum) {
        lock.lock();
        try {
            if(balance.compareTo(sum) == -1) {
                throw new IllegalArgumentException("недостаточно средств");
            } else {
                balance.subtract(sum);
            }
        } finally {
            lock.unlock();
        }
    }

    public BigDecimal getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    public Lock getLock() {
        return lock;
    }
}
