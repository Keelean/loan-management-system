package com.ffm.lms.wallet.transaction.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletTransactionService {

	WalletTransaction create(WalletTransaction transaction);
	Page<WalletTransaction> transactions(Long customerId, Pageable pageable);
}
