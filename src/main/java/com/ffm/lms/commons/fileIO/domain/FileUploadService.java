package com.ffm.lms.commons.fileIO.domain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ffm.lms.commons.fileIO.data.FileUploadDTO;
import com.ffm.lms.commons.fileIO.domain.dto.FileUploadInfoDTO;

public interface FileUploadService {

	FileUploadInfoDTO upload(FileUploadInfoDTO fileUploadInfo);

	void verifyCustomerRecords() throws FileNotFoundException, IOException;

	Page<String> getUploadedFiles(Pageable pageable);

	void doLoanRepaymentBatch();
	
	Page<FileUploadDTO> getFilesUploaded(Pageable pageable);

}
