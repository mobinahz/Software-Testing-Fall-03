package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class TransactionEngineTest {

    TransactionEngine engine;
    Transaction t1;
    Transaction t2;
    Transaction t3;

    @BeforeEach
    void set_up() {
        engine = new TransactionEngine();
        t1 = new Transaction();
        t2 = new Transaction();
        t3 = new Transaction();
    }

    @Test
    void test_get_avg_transaction_amount_no_transaction() {
        int average = engine.getAverageTransactionAmountByAccount(7);

        assertEquals(average, 0);
    }

    @Test
    void test_get_avg_transaction_single_transaction() {
        t1.setAccountId(1);
        t1.setAmount(500);
        engine.transactionHistory.add(t1);
        int average = engine.getAverageTransactionAmountByAccount(1);

        assertEquals(average, 500);

    }

    @Test
    void test_get_avg_transaction_multiple_different_transaction() {
        t1.setAccountId(1);
        t2.setAccountId(2);

        t1.setAmount(800);
        t2.setAmount(400);

        engine.transactionHistory.add(t1);
        engine.transactionHistory.add(t2);

        int average = engine.getAverageTransactionAmountByAccount(1);
        assertEquals(800, average);
    }

    @Test
    void test_get_avg_transaction_same_transaction_account() {
        t1.setAccountId(1);
        t2.setAccountId(1);

        t1.setAmount(800);
        t2.setAmount(400);

        engine.transactionHistory.add(t1);
        engine.transactionHistory.add(t2);

        int average = engine.getAverageTransactionAmountByAccount(1);
        assertEquals(average, 600);
    }

    @Test
    void test_get_avg_transaction_another_transaction_account() {
        t1.setAccountId(1);
        t2.setAccountId(1);

        t1.setAmount(800);
        t2.setAmount(400);

        engine.transactionHistory.add(t1);
        engine.transactionHistory.add(t2);

        int average = engine.getAverageTransactionAmountByAccount(2);
        assertEquals(0, average);
    }

    @Test
    void test_get_transaction_pattern_above_threshold_empty_transaction() {
        int diff = engine.getTransactionPatternAboveThreshold(1000);

        assertEquals(0, diff);
    }

    @Test
    void test_get_transaction_pattern_above_threshold_same_transaction() {
        t1.setAccountId(1);
        t1.setTransactionId(1);
        t1.setAmount(100);

        engine.transactionHistory.add(t1);


        int diff = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, diff);
    }


    @Test
    void test_get_transaction_pattern_above_threshold_first_diff() {
        t1.setAccountId(1);
        t1.setTransactionId(1);
        t1.setAmount(1500);

        engine.transactionHistory.add(t1);

        t2.setAccountId(2);
        t2.setTransactionId(2);
        t1.setAmount(1800);

        engine.transactionHistory.add(t2);

        t3.setAccountId(2);
        t3.setTransactionId(2);
        t3.setAmount(2100);

        engine.transactionHistory.add(t3);

        int diff = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(300, diff);
    }

    @Test
    void test_get_transaction_pattern_above_threshold_changed_diff_pattern() {
        t1.setAccountId(1);
        t1.setTransactionId(1);
        t1.setAmount(1500);

        engine.transactionHistory.add(t1);

        t2.setAccountId(2);
        t2.setTransactionId(2);
        t2.setAmount(1800);

        engine.transactionHistory.add(t2);

        t3.setAccountId(3);
        t3.setTransactionId(3);
        t3.setAmount(2000);

        engine.transactionHistory.add(t3);

        int diff = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, diff);
    }

    @Test
    void test_get_transaction_pattern_above_threshold_not_greater_than_threshold() {
        t1.setAccountId(1);
        t1.setTransactionId(1);
        t1.setAmount(200);

        engine.transactionHistory.add(t1);

        t2.setAccountId(2);
        t2.setTransactionId(2);
        t2.setAmount(250);

        engine.transactionHistory.add(t2);

        t3.setAccountId(3);
        t3.setTransactionId(3);
        t3.setAmount(300);

        engine.transactionHistory.add(t3);

        int diff = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, diff);
    }

    @Test
    void test_detect_fraudulent_transaction_not_fraudulent() {
        t1.setAccountId(1);
        t1.setAmount(100);
        t1.setDebit(true);

        engine.transactionHistory.add(t1);

        t2.setAccountId(1);
        t2.setAmount(150);

        int fraud_score = engine.detectFraudulentTransaction(t2);

        assertEquals(0, fraud_score);
    }

    @Test
    void test_detect_fraudulent_transaction_is_fraudulent() {
        t1.setAccountId(1);
        t1.setAmount(100);
        t1.setDebit(false);

        engine.transactionHistory.add(t1);

        t2.setAccountId(1);
        t2.setAmount(300);
        t2.setDebit(true);

        int fraud_score = engine.detectFraudulentTransaction(t2);

        assertEquals(100, fraud_score);
    }

    @Test
    void test_detect_fraudulent_transaction_is_not_debit() {
        t1.setAccountId(1);
        t1.setAmount(100);
        t1.setDebit(false);

        engine.transactionHistory.add(t1);

        t2.setAccountId(1);
        t2.setAmount(300);
        t2.setDebit(false);

        int fraud_score = engine.detectFraudulentTransaction(t1);

        assertEquals(0, fraud_score);
    }

    @Test
    void test_detect_fraudulent_transaction_is_debit_but_not_fraud() {
        t1.setAccountId(1);
        t1.setAmount(100);
        t1.setDebit(false);

        engine.transactionHistory.add(t1);

        t2.setAccountId(1);
        t2.setAmount(100);
        t2.setDebit(true);

        int fraud_score = engine.detectFraudulentTransaction(t2);

        assertEquals(0, fraud_score);
    }

    @Test
    void test_detect_fraudulent_transaction_is_not_debit_nor_fraud() {
        t1.setAccountId(1);
        t1.setAmount(100);
        t1.setDebit(false);

        engine.transactionHistory.add(t1);

        t2.setAccountId(1);
        t2.setAmount(150);
        t2.setDebit(false);

        int fraud_score = engine.detectFraudulentTransaction(t2);

        assertEquals(0, fraud_score);
    }

    @Test
    void test_add_transaction_and_detect_fraud_contains_transaction() {
        t1.setTransactionId(1);
        t1.setAccountId(1);
        t1.setAmount(100);
        engine.transactionHistory.add(t1);

        int fraudScore = engine.addTransactionAndDetectFraud(t1);
        assertEquals(0, fraudScore);
        assertTrue(engine.transactionHistory.contains(t1));

    }

    @Test
    void test_add_transaction_and_detect_fraud_not_fraud() {
        t1.setTransactionId(1);
        t1.setAccountId(1);
        t1.setAmount(100);
        t1.setDebit(false);

        int fraudScore = engine.addTransactionAndDetectFraud(t1);

        assertEquals(0, fraudScore);
        assertTrue(engine.transactionHistory.contains(t1));

    }

   @Test
   void test_add_transaction_and_detect_fraud_is_fraud_above_threshold() {
       t1.setTransactionId(1);
       t1.setAccountId(1);
       t1.setAmount(5000);
       t1.setDebit(true);

       int fraudScore = engine.addTransactionAndDetectFraud(t1);

       assertTrue(fraudScore > 0);
   }

   @Test
    void test_add_transaction_and_detect_fraud_is_fraudulent_transaction() {
       t1.setTransactionId(1);
       t1.setAccountId(1);
       t1.setAmount(100);
       t1.setDebit(false);

       engine.transactionHistory.add(t1);

       t2.setTransactionId(2);
       t2.setAccountId(1);
       t2.setAmount(400);
       t2.setDebit(true);

       int fraud_score = engine.addTransactionAndDetectFraud(t2);
       assertEquals(200, fraud_score);
   }

    @Test
    void test_add_transaction_and_detect_fraud_is_fraud_pattern() {
        t1.setTransactionId(1);
        t1.setAccountId(1);
        t1.setAmount(500);
        t1.setDebit(false);
        engine.transactionHistory.add(t1);


        t2.setTransactionId(2);
        t2.setAccountId(1);
        t2.setAmount(2500);
        t2.setDebit(false);
        engine.transactionHistory.add(t2);


        t3.setTransactionId(3);
        t3.setAccountId(1);
        t3.setAmount(4500);
        t3.setDebit(false);

        int fraud_score = engine.addTransactionAndDetectFraud(t3);

        assertEquals(2000, fraud_score);

    }

}
