package com.ffm.lms.report.data;

import java.time.LocalDate;

import com.ffm.lms.report.template.types.TemplateName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReportParam {
	private LocalDate startDate;
	private LocalDate endDate;
	private Long customerId;
	private TemplateName reportType;
}
