package com.anjasferdiansyah.koperasi.domain.model.savings;

import com.anjasferdiansyah.koperasi.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final BigDecimal value;

    private Money(BigDecimal amount) {
        if (amount == null) {
            throw new DomainValidationException("Amount is required");
        }
        this.value = amount.setScale(SCALE, ROUNDING_MODE);
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public BigDecimal toBigDecimal() {
        return value;
    }

    public boolean isPositive() {
        return value.signum() > 0;
    }

    public boolean isNegative() {
        return value.signum() < 0;
    }

    public Money add(Money other) {
        return new Money(this.value.add(other.value));
    }

    public Money subtract(Money other) {
        return new Money(this.value.subtract(other.value));
    }

    public boolean isLessThan(Money other) {
        return this.value.compareTo(other.value) < 0;
    }
}
