package com.ffm.lms.commons.fileIO.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ffm.lms.commons.fileIO.FileUploadInfo;
import com.ffm.lms.commons.fileIO.domain.type.FileUploadStatus;
import com.ffm.lms.commons.fileIO.domain.type.FileUploadType;
import com.ffm.lms.customer.commons.data.CommonRepository;

@Repository
public interface FileUploadRepository extends CommonRepository<FileUploadInfo> {

	Optional<FileUploadInfo> findByFileName(String fileName);

	//@Query("select f from FileUploadInfo f where f.fileSatatus = :status")
	List<FileUploadInfo> findByFileStatus(@Param("status") FileUploadStatus status);
	List<FileUploadInfo> findByFileStatusAndFileType(@Param("status") FileUploadStatus status, @Param("type") FileUploadType type);
}
