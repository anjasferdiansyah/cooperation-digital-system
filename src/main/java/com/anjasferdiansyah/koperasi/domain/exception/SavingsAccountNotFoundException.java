package com.anjasferdiansyah.koperasi.domain.exception;

import java.util.UUID;

public class SavingsAccountNotFoundException extends RuntimeException {

    public SavingsAccountNotFoundException(UUID accountId) {
        super("Savings account with id " + accountId + " not found");
    }
}
