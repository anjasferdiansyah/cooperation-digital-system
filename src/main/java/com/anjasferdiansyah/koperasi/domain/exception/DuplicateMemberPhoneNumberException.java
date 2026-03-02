package com.anjasferdiansyah.koperasi.domain.exception;

public class DuplicateMemberPhoneNumberException extends RuntimeException {

    public DuplicateMemberPhoneNumberException(String phoneNumber) {
        super("Member with phone number already exists: " + phoneNumber);
    }
}
