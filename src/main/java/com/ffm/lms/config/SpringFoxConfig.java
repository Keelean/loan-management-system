package com.ffm.lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SpringFoxConfig {
	
	@Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.basePackage("com.ffm.lms"))              
          .paths(PathSelectors.any())                          
          .build()
          .apiInfo(getApiInfo());                                           
    }
	
	private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("Loan App REST API")
                .description("\"Loan App REST API for First Fidelity\"")
                .version("1.0.0")
                .license("Apache License Version 2.0")
                .licenseUrl("cmkslee@gmail.com")
                .contact(new Contact("Kingsley Amaeze", "aboutmeurl", "cmkslee@gmail.com"))
                .build();
    }
}
