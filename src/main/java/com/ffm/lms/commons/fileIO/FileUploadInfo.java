package com.ffm.lms.commons.fileIO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.ffm.lms.commons.fileIO.domain.type.FileUploadStatus;
import com.ffm.lms.commons.fileIO.domain.type.FileUploadType;
import com.ffm.lms.customer.commons.data.SearchableEntity;
import com.ffm.lms.customer.commons.domain.BaseEntity;
import com.ffm.lms.customer.domain.Customer;
import com.ffm.lms.customer.domain.type.CustomerStatus;
import com.ffm.lms.loan.domain.type.LoanType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Where(clause = "del_flag='N'")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"data"})
@EqualsAndHashCode(callSuper = true)
//@Builder
@Audited
@Entity
@Table
public class FileUploadInfo extends BaseEntity {
	
	@NotNull
	@NotEmpty
	private String fileName;
	@NotNull
	@NotEmpty
	private String filePath;
	@Enumerated(EnumType.STRING)
	private FileUploadStatus fileStatus;
	private String url;
	private Long userId;
	private LocalDateTime uploadDate;
	@Transient
	private byte[] data;
	@Enumerated(EnumType.STRING)
	private FileUploadType fileType;
	private LocalDate payDate;
	@Enumerated(EnumType.STRING)
	private LoanType loanType;
	
}
