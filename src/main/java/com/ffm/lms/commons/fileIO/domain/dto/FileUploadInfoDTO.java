package com.ffm.lms.commons.fileIO.domain.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.ffm.lms.commons.fileIO.domain.type.FileUploadType;
import com.ffm.lms.loan.domain.type.LoanType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadInfoDTO {
	@NotNull
	@NotEmpty
	private String fileName;
	private byte[] data;
	@NotNull
	private FileUploadType fileType;
	private LocalDate payDate;
	private LoanType loanType;
}
