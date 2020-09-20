package com.ffm.lms.wallet.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.service.BaseService;
import com.ffm.lms.customer.domain.CustomerRepository;
import com.ffm.lms.wallet.transaction.domain.WalletTransaction;
import com.ffm.lms.wallet.transaction.domain.WalletTransactionService;
import com.ffm.lms.wallet.transaction.domain.types.WalletTransactionTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WalletServiceImpl extends BaseService implements WalletService {

	private final WalletRepository repository;
	private final WalletTransactionService walletTransactionService;
	private final CustomerRepository customerRepository;

	public Wallet initializeCustomerWallet(Long customerId) {
		List<Wallet> wallets = repository.findByCustomerId(customerId);
		Wallet wallet = null;
		if (Objects.isNull(wallets) || wallets.isEmpty()) {
			customerRepository.findById(customerId)
					.orElseThrow(() -> new ApplicationException("Customer does not exist"));
			wallet = new Wallet();
			wallet.setAmount(BigDecimal.ZERO);
			wallet.setCustomerId(customerId);
			wallet = repository.save(wallet);
		}
		return wallet;
	}

	Wallet getWalletByCustomerId(Long customerId) {
		Wallet wallet = null;
		List<Wallet> wallets = repository.findByCustomerId(customerId);
		if (Objects.nonNull(wallets) && !wallets.isEmpty()) {
			wallet = wallets.get(0);
		}
		return wallet;
	}

	public Wallet createOrUpdateWallet(Long customerId, BigDecimal amount) {
		Wallet wallet = getWalletByCustomerId(customerId);
		if (Objects.isNull(wallet)) {
			wallet = initializeCustomerWallet(customerId);
			wallet.setAmount(wallet.getAmount().add(amount));
			wallet = repository.save(wallet);
		} else {
			// wallet.setCustomerId(customerId);
			wallet.setAmount(wallet.getAmount().add(amount));
			wallet = repository.save(wallet);
		}
		return wallet;
	}

	public Wallet credit(Long customerId, BigDecimal amount) {
		log.info("****WALLET CREDIT*******" + amount);

		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new ApplicationException("Invalid amount");
		}
		// create credit transaction
		Wallet wallet = createOrUpdateWallet(customerId, amount);
		WalletTransaction transaction = new WalletTransaction();
		transaction.setAmount(amount);
		transaction.setCustomerId(customerId);
		transaction.setType(WalletTransactionTypes.CREDIT);
		walletTransactionService.create(transaction);

		return wallet;
	}

	public Wallet debit(Long customerId, BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new ApplicationException("Invalid amount");
		}
		List<Wallet> wallets = repository.findByCustomerId(customerId);
		if (Objects.isNull(wallets) || wallets.isEmpty()) {
			throw new ApplicationException("Customer account does not exist");
		}
		// check account if there is enough money for me debit
		Wallet wallet = wallets.get(0);
		if (wallet.getAmount().compareTo(amount) <= 0) {
			throw new ApplicationException("Customer balance is not sufficient for this transaction");
		}

		amount = amount.multiply(BigDecimal.valueOf(-1));
		createOrUpdateWallet(customerId, amount);
		WalletTransaction transaction = new WalletTransaction();
		transaction.setAmount(amount);
		transaction.setCustomerId(customerId);
		transaction.setType(WalletTransactionTypes.DEBIT);
		walletTransactionService.create(transaction);
		return wallet;
	}

	public Page<WalletTransaction> transactions(Long customerId, Pageable pageable) {
		Page<WalletTransaction> transactions = walletTransactionService.transactions(customerId, pageable);
		if (transactions.isEmpty()) {
			throw new ApplicationException("Empty list");
		}
		return transactions;
	}

	public BigDecimal getBalance(Long customerId) {
		Wallet wallet = getWalletByCustomerId(customerId);
		if (Objects.isNull(wallet))
			return BigDecimal.ZERO;

		return wallet.getAmount();
	}
}
