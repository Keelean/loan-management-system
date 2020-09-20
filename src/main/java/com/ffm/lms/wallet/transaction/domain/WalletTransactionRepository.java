package com.ffm.lms.wallet.transaction.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.ffm.lms.customer.commons.data.CommonRepository;

@Repository
public interface WalletTransactionRepository  extends CommonRepository<WalletTransaction>{

	Page<WalletTransaction> findByCustomerId(Long customerId, Pageable pageable);
}
