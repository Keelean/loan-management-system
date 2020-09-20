package com.ffm.lms.wallet.transaction.domain;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.customer.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WalletTransactionServiceImpl implements WalletTransactionService {

	private final WalletTransactionRepository repository;
	private final CustomerRepository customerRepository;

	public WalletTransaction create(WalletTransaction transaction) {

		customerRepository.findById(transaction.getCustomerId())
				.orElseThrow(() -> new ApplicationException("Customer does not exist"));

		return repository.save(transaction);

	}

	@Override
	public Page<WalletTransaction> transactions(Long customerId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repository.findByCustomerId(customerId, pageable);
	}
}
