package com.ffm.lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

@Configuration
public class LoanAppParserConfig {

	/*
	@Bean
	public CsvParser rowParser() {

		CsvParser parser = new CsvParser(parserSettings());

		return parser;
	}

	@Bean
	public CsvParserSettings parserSettings() {
		
		CsvParserSettings parserSettings = new CsvParserSettings();

		return parserSettings;
	}*/
}
