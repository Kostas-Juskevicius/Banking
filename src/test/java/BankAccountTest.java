import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    @Test
    void testPositiveInitialBalance() {
        BankAccount acc = new BankAccount(BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(100), acc.getBalance());
    }

    @Test
    void testNegativeInitialBalanceThrows() {
        assertThrows(IllegalArgumentException.class, () -> new BankAccount(BigDecimal.valueOf(-10)));
    }

    @Test
    void testDeposit() {
        BankAccount acc = new BankAccount();
        acc.deposit(BigDecimal.valueOf(50));
        assertEquals(BigDecimal.valueOf(50), acc.getBalance());

        assertThrows(IllegalArgumentException.class, () -> acc.deposit(BigDecimal.valueOf(-5)));
    }

    @Test
    void testWithdraw() {
        BankAccount acc = new BankAccount(BigDecimal.valueOf(100));
        acc.withdraw(BigDecimal.valueOf(40));
        assertEquals(BigDecimal.valueOf(60), acc.getBalance());

        assertThrows(IllegalArgumentException.class, () -> acc.withdraw(BigDecimal.valueOf(-5)));
        assertThrows(IllegalArgumentException.class, () -> acc.withdraw(BigDecimal.valueOf(1000))); // insufficient funds
    }

    @Test
    void testTransfer() {
        BankAccount acc1 = new BankAccount(BigDecimal.valueOf(200));
        BankAccount acc2 = new BankAccount(BigDecimal.valueOf(50));
        acc1.transfer(acc2, BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(100), acc1.getBalance());
        assertEquals(BigDecimal.valueOf(150), acc2.getBalance());

        assertThrows(IllegalArgumentException.class, () -> acc1.transfer(acc2, BigDecimal.valueOf(200))); // insufficient funds
    }

    @Test
    void testToString() {
        BankAccount acc = new BankAccount(BigDecimal.valueOf(123.435));
        assertEquals("123.43", acc.toString()); // HALF_DOWN rounding, two decimals
    }
}
