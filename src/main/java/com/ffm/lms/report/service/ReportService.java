package com.ffm.lms.report.service;

import java.time.LocalDate;

import com.ffm.lms.report.data.ReportParam;
import com.ffm.lms.report.template.types.TemplateName;

public interface ReportService {

	String generateReport(ReportParam reportParam);
}
