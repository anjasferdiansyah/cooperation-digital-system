package com.anjasferdiansyah.koperasi.domain.exception;

import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;

import java.util.UUID;

public class DuplicateSavingsAccountException extends RuntimeException {

    public DuplicateSavingsAccountException(UUID memberId, SavingsType savingsType) {
        super("Savings account already exists for member " + memberId + " and type " + savingsType);
    }
}
