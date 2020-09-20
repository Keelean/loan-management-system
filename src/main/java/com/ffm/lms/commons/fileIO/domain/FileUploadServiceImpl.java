package com.ffm.lms.commons.fileIO.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ffm.lms.commons.constants.Constants;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.fileIO.FileUploadInfo;
import com.ffm.lms.commons.fileIO.data.FileUploadDTO;
import com.ffm.lms.commons.fileIO.data.LocalLoanRepaymentBean;
import com.ffm.lms.commons.fileIO.data.StateLoanRepaymentBean;
import com.ffm.lms.commons.fileIO.domain.dto.FileUploadInfoDTO;
import com.ffm.lms.commons.fileIO.domain.type.FileUploadStatus;
import com.ffm.lms.commons.fileIO.domain.type.FileUploadType;
import com.ffm.lms.commons.messages.EmailService;
import com.ffm.lms.commons.service.BaseService;
import com.ffm.lms.customer.domain.Customer;
import com.ffm.lms.customer.domain.CustomerRepository;
import com.ffm.lms.customer.domain.bean.CustomerBean;
import com.ffm.lms.customer.domain.type.CustomerStatus;
import com.ffm.lms.loan.domain.Loan;
import com.ffm.lms.loan.domain.LoanRepository;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.domain.type.LoanType;
import com.ffm.lms.loan.transaction.domain.TransactionService;
import com.ffm.lms.loan.transaction.domain.TransactionServiceImpl;
import com.ffm.lms.loan.transaction.domain.dto.CreateTransactionDTO;
import com.ffm.lms.loan.transaction.domain.type.TransactionSource;
import com.ffm.lms.user.domain.User;
import com.ffm.lms.user.domain.UserRepository;
import com.ffm.lms.user.domain.UserService;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl extends BaseService implements FileUploadService {

	private final FileUploadRepository repository;
	private final FileUploadValidator validator;
	private final CustomerRepository customerRepository;
	private final LoanRepository loanRepository;
	private final TransactionService transactionService;
	private final EmailService emailService;
	private final UserRepository userRepository;

	@Value("${file.upload.path}")
	private String filePath;

	@Value("${file.upload.path.url}")
	private String filePathUrl;

	@Value("${file.upload.url.protocol}")
	private String protocol;

	@Value("${file.upload.report.path}")
	private String fileReportPath;

	@Override
	public FileUploadInfoDTO upload(FileUploadInfoDTO fileUploadInfoDTO) {

		FileUploadInfo fileUploadInfo = null;
		validator.validate(fileUploadInfoDTO);
		// validator.isFileExist(fileUploadInfo);
		validator.isFileExtensionValid(fileUploadInfoDTO);
		validator.isDataEmpty(fileUploadInfoDTO);
		validator.isPaidDateValid(fileUploadInfoDTO);

		try {
			fileUploadInfo = generateFileInfo(fileUploadInfoDTO);
		} catch (IOException e) {
			throw new ApplicationException("File Upload Error!", "", Arrays.asList(e.getMessage()));
		}

		fileUploadInfo.setData(null);
		//fileUploadInfo.setFileName(fileName);

		return mapper.map(repository.save(fileUploadInfo), FileUploadInfoDTO.class);

	}

	private FileUploadInfo generateFileInfo(FileUploadInfoDTO fileUploadDTO) throws FileNotFoundException, IOException {

		String path = filePath + File.separator + fileUploadDTO.getFileName().trim().replaceAll("\\s+", "_");

		createDirectory(filePath);

		try (FileOutputStream stream = new FileOutputStream(path)) {
			stream.write(fileUploadDTO.getData());
		}

		FileUploadInfo fileUploadInfo = mapper.map(fileUploadDTO, FileUploadInfo.class);
		fileUploadInfo.setFilePath(path);
		fileUploadInfo.setFileName(fileUploadDTO.getFileName().trim());
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(filePathUrl)
				.path(fileUploadDTO.getFileName())
				.toUriString();
		fileUploadInfo.setUrl(fileDownloadUri);
		fileUploadInfo.setFileStatus(FileUploadStatus.UNPROCESSED);
		fileUploadInfo.setUploadDate(LocalDateTime.now());
		fileUploadInfo.setUserId(getCurrentUser().getId());
		return fileUploadInfo;
	}

	private List<CustomerBean> getListOfBeans(final String filePath) throws FileNotFoundException {
		BeanListProcessor<CustomerBean> rowProcessor = new BeanListProcessor<CustomerBean>(CustomerBean.class);

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.getFormat().setQuoteEscape('\\');
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setLineSeparatorDetectionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(getReader(filePath));

		List<CustomerBean> beans = rowProcessor.getBeans();
		return beans;
	}

	private List<StateLoanRepaymentBean> getListOfStateLoanBeans(final String filePath) throws FileNotFoundException {
		BeanListProcessor<StateLoanRepaymentBean> rowProcessor = new BeanListProcessor<StateLoanRepaymentBean>(
				StateLoanRepaymentBean.class);

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.getFormat().setQuoteEscape('\\');
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setLineSeparatorDetectionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(getReader(filePath));

		List<StateLoanRepaymentBean> beans = rowProcessor.getBeans();
		return beans;
	}
	
	private List<LocalLoanRepaymentBean> getListOfLocalLoanBeans(final String filePath) throws FileNotFoundException {
		BeanListProcessor<LocalLoanRepaymentBean> rowProcessor = new BeanListProcessor<LocalLoanRepaymentBean>(
				LocalLoanRepaymentBean.class);

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.getFormat().setQuoteEscape('\\');
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setLineSeparatorDetectionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(getReader(filePath));

		List<LocalLoanRepaymentBean> beans = rowProcessor.getBeans();
		return beans;
	}

	private List<CreateTransactionDTO> processLoanRepaymentFile() throws IOException {
		log.info("***********00000*****************");
		List<FileUploadInfo> fileInfos = repository.findByFileStatusAndFileType(FileUploadStatus.UNPROCESSED,
				FileUploadType.LOAN_REPAYMENT);
		
		log.info("***********000AASIZE*****************"+ fileInfos.size());

		FileUploadInfo in = null;
		int errorCount = 0;

		PrintWriter writer = null;
		List<String> messages = null;
		List<CreateTransactionDTO> transactionsList = new ArrayList<>();

		for (FileUploadInfo info : fileInfos) {
			in = info;
			List<StateLoanRepaymentBean> stateBeans = null;
			List<LocalLoanRepaymentBean> localBeans = null;

			if(Objects.isNull(info.getLoanType())){
				continue;
			}
			else if(info.getLoanType().equals(LoanType.STATE)) {
				try {
					stateBeans = getListOfStateLoanBeans(info.getFilePath().trim());
				}catch(Exception e) {
					info.setFileStatus(FileUploadStatus.ERROR);
					repository.save(info);
					continue;
				}
			}
			else {
				try {
					localBeans = getListOfLocalLoanBeans(info.getFilePath().trim());
				}catch(Exception e) {
					info.setFileStatus(FileUploadStatus.ERROR);
					repository.save(info);
					continue;
				}
			}

			messages = new ArrayList<>();
			messages.add("*************************************************************");
			String fileName = info.getFileName().replace("csv", "txt");
			writer = getWriter(fileReportPath, fileName);
			messages.add("    ***** LOAN BULK PAYMENT REPORT FOR " + info.getFileName()+ " ********    ");
			messages.add("******************************************************************");
			log.info("***********00000NAME*****************"+ info.getFileName());
			writer.println();
			
			if(Objects.nonNull(stateBeans) && !stateBeans.isEmpty()) {
				transactionsList = retrieveStateLoanTransactions(errorCount, messages, info, stateBeans, writer, fileReportPath + File.separator + fileName);
			}
			else {
				transactionsList = retrieveLocalLoanTransactions(errorCount, messages,info,localBeans, writer, fileReportPath + File.separator + fileName);
			}
			// send mail to user who uplaoded file
		}
		return transactionsList;
	}

	private List<CreateTransactionDTO> retrieveStateLoanTransactions(int errorCount, List<String> messages, FileUploadInfo info,
			List<StateLoanRepaymentBean> beans, PrintWriter writer, String reportPath) {
		List<CreateTransactionDTO> transactionsList = new ArrayList<>();
		for (StateLoanRepaymentBean bean : beans) {
			CreateTransactionDTO transactionDTO = null;
			//String computerNumber = bean.getComputerNumber().replaceAll("^\"|\"$", "").trim();
			Customer customer = customerRepository.findByComputerNumber(bean.getComputerNumber().trim()).orElse(null);
			if (customer == null) {
				errorCount++;
				messages.add("# Invalid customer. Customer with Computer Number[" + bean.getComputerNumber() + "]"
						+ " does not exist.");
				continue;
			}

			/*if (!customer.getStatus().equals(CustomerStatus.VERIFIED)) {
				errorCount++;
				messages.add("# Invalid customer. Customer with Computer Number[" + bean.getComputerNumber() + "]"
						+ " has not been verified.");
				continue;
			}*/
			
			Loan loan = null;
			List<Loan> loans = getLoanTypes(customer.getId());
			if (Objects.nonNull(loans) && !loans.isEmpty()) {
				loan = loans.get(0);
			} else {
				errorCount++;
				messages.add("# Invalid Loan. Customer with Computer Number[" + bean.getComputerNumber() + "]"
						+ " does not have valid loan. Please check if loan has been approved");
				continue;
			}
			
			//if(loan.getLoanType().eq)
			
			log.info("***********3RD*****************"+ customer.getComputerNumber());
			transactionDTO = new CreateTransactionDTO();
			transactionDTO.setAmount(bean.getAmount());
			transactionDTO.setLoanId(loan.getId());
			transactionDTO.setSource(TransactionSource.NORMAL);
			transactionDTO.setPaidDate(info.getPayDate());
			transactionsList.add(transactionDTO);
			log.info("***********4TH*****************"+ customer.getComputerNumber());
			//log.info(String.format("%s"), transactionDTO);

		}
		
		if ((beans.size() - errorCount) != beans.size()) {
			messages.add("# " + errorCount + " customer bulk repayment records could not be processed");
			messages.add("# " + (beans.size() - errorCount) + "of " + beans.size()
					+ " customer repayment records were successfully processed");
			info.setFileStatus(FileUploadStatus.ERROR);
			messages.add("         ***********************************           ");
		}
		log.info("***********4TH*****************END0000");
		if ((beans.size() - errorCount) == beans.size()) {
			messages.add("# " + beans.size() + " customer bulk repayment records were successfully verified");
			info.setFileStatus(FileUploadStatus.PROCESSED);
		}
		log.info("***********4TH*****************END0022");

		repository.save(info);
		Long userId = info.getUserId();
		String name = info.getFileName();
		info = null;
		messages.add(1, "# " + beans.size() + " customer(s) bulk repayment records was processed");
		messages.add("           **************************************     ");
		for (String message : messages)
			writer.println(message);
		messages = null;
		writer.close();
		log.info("***********4TH*****************END0023");
		sendFileUploadReport(userId, reportPath, name);
		log.info("***********4TH*****************END0033");
		return transactionsList;
	}
	
	private List<CreateTransactionDTO> retrieveLocalLoanTransactions(int errorCount, List<String> messages, FileUploadInfo info,
			List<LocalLoanRepaymentBean> beans, PrintWriter writer, String reportPath) {
		List<CreateTransactionDTO> transactionsList = new ArrayList<>();
		for (LocalLoanRepaymentBean bean : beans) {
			CreateTransactionDTO transactionDTO = null;
			//String computerNumber = bean.getComputerNumber().replaceAll("^\"|\"$", "").trim();
			Customer customer = customerRepository.findByComputerNumber(bean.getComputerNumber().trim()).orElse(null);
			if (customer == null) {
				errorCount++;
				messages.add("# Invalid customer. Customer with Computer Number[" + bean.getComputerNumber() + "]"
						+ " does not exist.");
				continue;
			}

			/*if (!customer.getStatus().equals(CustomerStatus.VERIFIED)) {
				errorCount++;
				messages.add("# Invalid customer. Customer with Computer Number[" + bean.getComputerNumber() + "]"
						+ " has not been verified.");
				continue;
			}*/
			
			Loan loan = null;
			List<Loan> loans = getLoanTypes(customer.getId());
			if (Objects.nonNull(loans) && !loans.isEmpty()) {
				loan = loans.get(0);
			} else {
				errorCount++;
				messages.add("# Invalid Loan. Customer with Computer Number[" + bean.getComputerNumber() + "]"
						+ " does not have valid loan. Please check if loan has been approved");
				continue;
			}
			log.info("***********3RD*****************"+customer.getComputerNumber());
			transactionDTO = new CreateTransactionDTO();
			transactionDTO.setAmount(bean.getAmount());
			transactionDTO.setLoanId(loan.getId());
			transactionDTO.setSource(TransactionSource.NORMAL);
			transactionDTO.setPaidDate(info.getPayDate());
			transactionsList.add(transactionDTO);
			log.info("***********4TH*****************"+customer.getComputerNumber());
			//log.info(String.format("%s"), transactionDTO);

		}
		
		if ((beans.size() - errorCount) != beans.size()) {
			messages.add("# " + errorCount + " customer bulk repayment records could not be processed");
			messages.add("# " + (beans.size() - errorCount) + "of " + beans.size()
					+ " customer repayment records were successfully processed");
			info.setFileStatus(FileUploadStatus.ERROR);
			messages.add("         ***********************************           ");
		}
		
		if ((beans.size() - errorCount) == beans.size()) {
			messages.add("# " + beans.size() + " customer bulk repayment records were successfully verified");
			info.setFileStatus(FileUploadStatus.PROCESSED);
		}

		repository.save(info);
		String name = info.getFileName();
		Long userId = info.getUserId();
		info = null;
		messages.add(1, "# " + beans.size() + " customer(s) bulk repayment records was processed");
		messages.add("         ***********************************           ");
		for (String message : messages)
			writer.println(message);
		messages = null;
		writer.close();
		sendFileUploadReport(userId, reportPath, name);
		return transactionsList;	
	}


	@Override
	public void verifyCustomerRecords() throws IOException {

		List<FileUploadInfo> fileInfos = repository.findByFileStatusAndFileType(FileUploadStatus.UNPROCESSED,
				FileUploadType.CUSTOMER_VERIFICATION);

		FileUploadInfo in = null;
		int errorCount = 0;

		PrintWriter writer = null;
		List<String> messages = null;

		for (FileUploadInfo info : fileInfos) {
			in = info;
			List<CustomerBean> beans = getListOfBeans(info.getFilePath().trim());
			messages = new ArrayList<>();
			writer = getWriter(fileReportPath, info.getFileName().replace("csv", "txt"));
			messages.add("*********************** REPORT FOR " + info.getFileName() + " *************************");
			messages.add("*********************************************************************");
			writer.println();
			for (CustomerBean bean : beans) {
				if (bean.getStatus().equalsIgnoreCase(CustomerStatus.VERIFIED.name())) {
					Customer customer = customerRepository.findById(bean.getId()).orElse(null);
					if (customer == null) {
						errorCount++;
						messages.add("# Invalid customer. Customer ID[" + bean.getId() + "]" + " does not exist.");
						continue;
					}
					customer.setStatus(CustomerStatus.VERIFIED);
					customerRepository.save(customer);
				}
			}

			if ((beans.size() - errorCount) != beans.size()) {
				messages.add("# " + errorCount + " customer records could not be processed");
				messages.add("# " + (beans.size() - errorCount) + " customer records were successfully verified");
				in.setFileStatus(FileUploadStatus.ERROR);
			}
			if ((beans.size() - errorCount) == beans.size()) {
				messages.add("# " + beans.size() + " customer records were successfully verified");
				in.setFileStatus(FileUploadStatus.PROCESSED);
			}

			repository.save(in);
			in = null;
			messages.add(1, "# " + beans.size() + " customer(s) records was processed");
			messages.add("*********************************************************************");
			for (String message : messages)
				writer.println(message);
			messages = null;
			writer.close();
		}
	}

	@Override
	public Page<String> getUploadedFiles(Pageable pageable) {
		Page<String> files = repository.findAll(pageable).map(info -> info.getUrl());
		if (files == null || files.isEmpty())
			throw new ApplicationException("Empty Record!");

		return files;
		// stream().map(FileUploadInfo::getUrl).collect(Collectors.toList());
	}
	
	public Page<FileUploadDTO> getFilesUploaded(Pageable pageable) {
		Page<FileUploadInfo> files  = repository.findAll(pageable);
		
		if (files == null || files.isEmpty())
			throw new ApplicationException("Empty Record!");
		
		return files.map(info -> {
			FileUploadDTO fileDTO = new FileUploadDTO();
			fileDTO.setFileName(info.getFileName());
			fileDTO.setUrl(info.getUrl());
			return fileDTO;
		});
	}


	private List<Loan> getLoanTypes(Long customerId) {
		List<Loan> loans = loanRepository.findByCustomerId(customerId);
		List<Loan> loanTypes = new ArrayList<>();
		if (Objects.isNull(loans) || loans.isEmpty())
			return loanTypes;

		loanTypes = loans.stream()
				.filter(loan -> loan.getStatus().equals(LoanStatus.ACTIVE) || loan.getStatus().equals(LoanStatus.TOPUP)
						&& (loan.getLoanType().equals(LoanType.LOCAL) || loan.getLoanType().equals(LoanType.STATE)))
				.collect(Collectors.toList());
		return loanTypes;
	}

	public void doLoanRepaymentBatch() {
		List<CreateTransactionDTO> transactions;
		try {
			transactions = processLoanRepaymentFile();
			for (CreateTransactionDTO transaction : transactions) {
				transactionService.repay(transaction);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void sendFileUploadReport(Long userId, String filePath, String fileName) {
		try {
			boolean exists= userRepository.findById(userId).isPresent();
			if(!exists) {
				return;
			}
			User user = userRepository.findById(userId).get();
			String message = String.format(Constants.FILE_REPORT, user.getFirstname());
			emailService.sendAttachment(user.getEmail(), String.format("File Upload Report: %S", fileName), message, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
