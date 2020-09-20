package com.ffm.lms.wallet.domain;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ffm.lms.customer.commons.data.CommonRepository;

@Repository
public interface WalletRepository  extends CommonRepository<Wallet>{

	List<Wallet> findByCustomerId(Long id);
}
