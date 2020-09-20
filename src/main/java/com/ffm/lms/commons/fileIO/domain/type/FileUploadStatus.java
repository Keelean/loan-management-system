package com.ffm.lms.commons.fileIO.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileUploadStatus {
	PROCESSED("PRO", "File Processed"), UNPROCESSED("UNP", "File Not Processed"), ERROR("ERR", "File Processing Error");

	private String code;
	private String name;
}
