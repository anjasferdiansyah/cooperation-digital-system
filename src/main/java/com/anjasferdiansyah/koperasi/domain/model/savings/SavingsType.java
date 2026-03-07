package com.anjasferdiansyah.koperasi.domain.model.savings;

import lombok.Getter;

@Getter
public enum SavingsType {
    POKOK(false),
    WAJIB(false),
    SUKARELA(true);

    private final boolean withdrawAllowed;

    SavingsType(boolean withdrawAllowed){
        this.withdrawAllowed = withdrawAllowed;
    }

}
