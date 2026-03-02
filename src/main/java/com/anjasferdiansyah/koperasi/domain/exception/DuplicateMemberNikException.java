package com.anjasferdiansyah.koperasi.domain.exception;

public class DuplicateMemberNikException extends RuntimeException {

    public DuplicateMemberNikException(String nik) {
        super("Member with nik already exists: " + nik);
    }
}
