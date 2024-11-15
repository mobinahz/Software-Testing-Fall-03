package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    Transaction t1;
    Transaction t2;

    @BeforeEach
    void set_up() {
        t1 = new Transaction();
        t2 = new Transaction();
    }

    @Test
    void test_same_transaction_equals() {


        t1.setTransactionId(11);
        t2.setTransactionId(11);

        boolean is_equal = t1.equals(t2);
        assertTrue(is_equal);

    }

    @Test
    void test_different_transaction_equals() {
        t1.setTransactionId(12);
        t2.setTransactionId(15);
        boolean is_equal = t1.equals(t2);
        assertFalse(is_equal);
    }


    @Test
    void test_non_transaction_object() {
        TransactionEngine t3 = new TransactionEngine();
         boolean is_equal = t1.equals(t3);

         assertFalse(is_equal);
    }

    @Test
    void test_null_object() {
        t1.setTransactionId(1);
        boolean is_equal = t1.equals("Null");
        assertFalse(is_equal);
    }

    @Test
    void test_get_transaction_id() {
        t1.setTransactionId(7);

        int id = t1.getTransactionId();

        assertEquals(7, id);

    }

    @Test
    void test_get_account_id() {
        t1.setAccountId(7);

        int id = t1.getAccountId();

        assertEquals(7, id);

    }


    @Test
    void test_get_amount_id() {
        t1.setAmount(7);

        int id = t1.getAmount();

        assertEquals(7, id);

    }

    @Test
    void test_get_is_debit() {
        t1.setDebit(true);

        assertTrue(t1.isDebit());

    }
}
