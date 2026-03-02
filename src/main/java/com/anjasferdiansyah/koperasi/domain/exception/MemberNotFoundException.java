package com.anjasferdiansyah.koperasi.domain.exception;

import java.util.UUID;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(UUID memberId) {
        super("Member not found: " + memberId);
    }
}
