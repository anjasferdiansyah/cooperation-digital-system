package com.anjasferdiansyah.koperasi.application.usecase.member.create;

public record CreateMemberCommand(String fullName, String email, String address, String nik, String phoneNumber) {
}
