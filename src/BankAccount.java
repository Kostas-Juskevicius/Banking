import java.math.BigDecimal;
import java.math.RoundingMode;

// A class representing a bank account.
public class BankAccount {

    private BigDecimal balance = BigDecimal.ZERO;

    BankAccount() {
    }

    BankAccount(BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cannot register a bank account with a negative initial balance.");
        }
        this.balance = initialBalance;
    }

    // Returns the remaining account balance.
    public BigDecimal getBalance() {
        return this.balance;
    }

    // Prints the remaining account balance.
    public void printBalance() {
        System.out.println("The current bank account balance: " + this.balance.toString());
    }

    // Deposits money into the account.
    // NOTE: returns the account balance after the deposit.
    public BigDecimal deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The deposit amount must be positive.");
        }
        this.balance = this.balance.add(amount);
        return this.balance;
    }

    // Withdraws money from the account.
    // NOTE: returns the account balance after the withdrawal.
    public BigDecimal withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The withdrawal amount must be positive.");
        } else if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Tried to withdraw with insufficient funds.");
        }
        this.balance = this.balance.subtract(amount);
        return this.balance;
    }

    // Transfers money from one account into another.
    // The first account's balance decreases, the second one's increases.
    public void transfer(BankAccount other, BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Tried to transfer with insufficient funds.");
        }
        this.withdraw(amount);
        other.deposit(amount);
    }

    @Override
    public String toString() {
        return balance.setScale(2, RoundingMode.HALF_DOWN).toString(); // let's favour the bank
    }
}
