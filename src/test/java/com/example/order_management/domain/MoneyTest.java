package com.example.order_management.domain;

import com.example.order_management.domain.exception.CurrencyMismatchException;
import com.example.order_management.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithDefaultCurrencyUsd() {
        Money money = Money.of(new BigDecimal("10.00"));

        assertThat(money.getAmount()).isEqualByComparingTo("10.00");
        assertThat(money.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldFailToCreateMoneyWithNullAmount() {
        assertThatThrownBy(() -> Money.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldAddMoneyWithSameCurrency() {
        Money m1 = Money.of(new BigDecimal("10.00"), "USD");
        Money m2 = Money.of(new BigDecimal("5.50"), "USD");

        Money result = m1.add(m2);

        assertThat(result.getAmount()).isEqualByComparingTo("15.50");
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldThrowWhenAddingMoneyWithDifferentCurrency() {
        Money usd = Money.of(new BigDecimal("10.00"), "USD");
        Money eur = Money.of(new BigDecimal("5.00"), "EUR");

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void shouldSubtractMoneyWithSameCurrency() {
        Money m1 = Money.of(new BigDecimal("10.00"), "USD");
        Money m2 = Money.of(new BigDecimal("4.00"), "USD");

        Money result = m1.subtract(m2);

        assertThat(result.getAmount()).isEqualByComparingTo("6.00");
    }

    @Test
    void shouldThrowWhenSubtractingMoneyWithDifferentCurrency() {
        Money usd = Money.of(new BigDecimal("10.00"), "USD");
        Money eur = Money.of(new BigDecimal("5.00"), "EUR");

        assertThatThrownBy(() -> usd.subtract(eur))
                .isInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void shouldMultiplyMoneyByPositiveFactor() {
        Money m = Money.of(new BigDecimal("10.00"), "USD");

        Money result = m.multiply(3);

        assertThat(result.getAmount()).isEqualByComparingTo("30.00");
        assertThat(result.getCurrency()).isEqualTo("USD");
    }
}

