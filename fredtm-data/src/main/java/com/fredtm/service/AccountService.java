package com.fredtm.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.fredtm.core.model.Account;

public interface AccountService {

	Account createAccount(String email, String password, String name);

	Optional<Account> loginAccount(String email, String pass);

	Account getAccount(String uuid);

	Account getAccount(Integer id);

	Page<Account> getAllAccounts(int page, int elementsInRequest);

	Account changePassword(Account account, String newPassword);

}