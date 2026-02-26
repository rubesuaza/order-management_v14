package com.example.order_management.domain;

import com.example.order_management.domain.exception.CurrencyMismatchException;
import com.example.order_management.domain.exception.DomainException;
import com.example.order_management.domain.exception.InvalidItemException;
import com.example.order_management.domain.exception.InvalidOrderStateException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @Test
    void domainExceptionShouldCarryMessage() {
        DomainException ex = new DomainException("error") {
        };

        assertThat(ex.getMessage()).isEqualTo("error");
    }

    @Test
    void specificExceptionsShouldExtendDomainException() {
        DomainException invalidOrder = new InvalidOrderStateException("order");
        DomainException invalidItem = new InvalidItemException("item");
        DomainException currency = new CurrencyMismatchException("currency");

        assertThat(invalidOrder).isInstanceOf(DomainException.class);
        assertThat(invalidItem).isInstanceOf(DomainException.class);
        assertThat(currency).isInstanceOf(DomainException.class);
    }
}

