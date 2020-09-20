package com.ffm.lms.report.template;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Component
public class ThymeleafReportTemplate {

	 @Autowired 
	 SpringTemplateEngine templateEngine;
	
	public <T> String parseThymeleafTemplate(String templateName, List<T> data) {
		Context context = new Context();
		context.setVariable("data", data);

		return templateEngine.process(templateName, context);
	}

}
