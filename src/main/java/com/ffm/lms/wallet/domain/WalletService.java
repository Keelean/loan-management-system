package com.ffm.lms.wallet.domain;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ffm.lms.wallet.transaction.domain.WalletTransaction;

public interface WalletService {

	Wallet createOrUpdateWallet(Long customerId, BigDecimal amount);
	Wallet debit(Long customerId, BigDecimal amount);
	Wallet credit(Long customerId, BigDecimal amount);
	Page<WalletTransaction> transactions(Long customerId, Pageable pageable);
	Wallet initializeCustomerWallet(Long customerId);
	BigDecimal getBalance(Long customerId);
}
