package com.ffm.lms.commons.fileIO.domain;

import static com.ffm.lms.commons.utils.CommonUtils.getStaticFieldValidationErrors;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.validation.Validator;

import org.springframework.stereotype.Component;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.fileIO.domain.dto.FileUploadInfoDTO;
import com.ffm.lms.commons.fileIO.domain.type.FileUploadType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class FileUploadValidator {

	public final Validator validator;
	public FileUploadRepository repository;

	public void validate(FileUploadInfoDTO fileUploadInfo) {
		validateFields(fileUploadInfo);
	}

	private void validateFields(FileUploadInfoDTO fileUploadInfo) {
		List<String> fieldValidationErrors = getStaticFieldValidationErrors(fileUploadInfo, validator);

		if (fieldValidationErrors.isEmpty()) {
			return;
		}
		throw new ApplicationException("Invalid Fields", "", fieldValidationErrors);
	}

	public void isDataEmpty(FileUploadInfoDTO info) {
		if (info.getData().length == 0)
			throw new ApplicationException("Error Processing File", "", Arrays.asList("File data cannot be empty"));

	}

	public void isPaidDateValid(FileUploadInfoDTO info) {
		if (info.getFileType().equals(FileUploadType.LOAN_REPAYMENT) && Objects.isNull(info.getPayDate())) {
			throw new ApplicationException("Error Processing File", "",
					Arrays.asList("Paid Date cannot be empty for File Type: " + FileUploadType.LOAN_REPAYMENT.name()));
		}
		return;

	}

	/*
	 * public void isFileExist(FileUploadInfo info) { info =
	 * repository.findByFileName(info.getFileName()).orElse(null); if(info != null)
	 * throw new ApplicationException("Invalid Fields", "",
	 * Arrays.asList(info.getFileName() + " already exist!")); }
	 */

	public void isFileExtensionValid(FileUploadInfoDTO info) {
		if (info.getFileName().endsWith(".csv"))
			return;
		throw new ApplicationException("Invalid File Format", "", Arrays.asList("Only CSV Files can be uploaded!"));
	}
}
