package com.ffm.lms.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.ffm.lms.commons.web.LoanAppMetricInterceptor;
import com.ffm.lms.commons.web.MeasurementInterceptor;
import com.ffm.lms.commons.web.AuthenticationResponseInterceptor;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class LoanAppWebConfig implements WebMvcConfigurer {

	@Value("${file.upload.path}")
	private String filePath;

	@Value("${file.upload.path.url}")
	private String filePathUrl;

	@Value("${file.upload.report.path}")
	private String fileReportPath;

	@Value("{file.upload.customer.doc}")
	private String documentPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// super.addResourceHandlers(registry);
		registry.addResourceHandler("/" + filePathUrl + "**").addResourceLocations("file:" + filePath + "/")
				.addResourceLocations("file:" + documentPath + "/").addResourceLocations("file:" + fileReportPath + "/")
				.setCachePeriod(3600).resourceChain(true).addResolver(new PathResourceResolver());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(authenticationResponseInterceptor());
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(measurementInterceptor()).addPathPatterns("/uploaded-files", "");
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
	}

	@Bean
	public MeasurementInterceptor measurementInterceptor() {
		return new MeasurementInterceptor();
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("language");
		return localeChangeInterceptor;
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("en"));
		return localeResolver;
	}

	@Bean
	public AuthenticationResponseInterceptor authenticationResponseInterceptor() {
		return new AuthenticationResponseInterceptor();
	}

	/*
	 * @Bean public MappedInterceptor metricInterceptor(MeterRegistry registry) {
	 * return new MappedInterceptor(new String[]{"/**"}, new
	 * LoanAppMetricInterceptor(registry)); }
	 */

}
