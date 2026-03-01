package com.anjasferdiansyah.koperasi.domain.exception;

public class DuplicateMemberEmailException extends RuntimeException {

    public DuplicateMemberEmailException(String email) {
        super("Member with email already exists: " + email);
    }
}
