package com.ffm.lms;

import java.util.Arrays;

import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import com.ffm.lms.audit.AuditAwareImpl;
import com.ffm.lms.customer.commons.data.CommonRepositoryImpl;

@Configuration
@EnableAsync
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaAuditing(auditorAwareRef = "customAuditProvider")
@EnableJpaRepositories(repositoryBaseClass = CommonRepositoryImpl.class)
/*
 @EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
 */
public class AppConfig {

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        String[] baseNames = new String[]{"messages"};
        source.setBasenames(baseNames);
        source.setCacheSeconds(60 * 30);
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }


    @Bean
    public CacheManager cacheManager() {
        // configure and return an implementation of Spring's CacheManager SPI
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("codes"), new ConcurrentMapCache("settings")));
        return cacheManager;
    }

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	  @Bean
	    public AuditorAware<String> customAuditProvider() {
	        return new AuditAwareImpl();
	    }

    /*
    @Bean
    public PasswordEncoder passwordEncoder() {
        return  PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }*/
}
