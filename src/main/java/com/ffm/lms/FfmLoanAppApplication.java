package com.ffm.lms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ffm.lms.user.domain.CreateUserDTO;
import com.ffm.lms.user.domain.UserService;
import com.ffm.lms.user.domain.types.Role;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class FfmLoanAppApplication extends SpringBootServletInitializer implements CommandLineRunner, ApplicationRunner{

	@Autowired
	private UserService userService;
	
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(FfmLoanAppApplication.class);
		//application.setBannerMode(Banner.Mode.LOG);
		application.addListeners(new ApplicationListener<ApplicationEvent>() {

			@Override
			public void onApplicationEvent(ApplicationEvent event) {
				// TODO Auto-generated method stub
				log.info("");
			}
		});
		//application.setWebApplicationType(WebApplicationType.SERVLET);
		application.run(args);
	}
	
	@Override
	public void run(String...args) throws Exception {}
	
	@Override
	 public void run(ApplicationArguments args) throws Exception {
	}
	
	
	@Bean
	CommandLineRunner initializeAdminUser() {
		 return args -> {
			 boolean exist = userService.isUserExist("test");
			 CreateUserDTO user = null;
			 if(!exist) {
				 user = new CreateUserDTO(); 
				 user.setUsername("test");
				 user.setEmail("cmkslee@gmail.com");
				 user.setFirstname("Test");
				 user.setLastname("Admin");
				 user.setMobileNo("01234567891");
				 user.setRole(Role.ROLE_ADMIN.name());
				 userService.create(user);
			 }
		 };
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:8080","https://localhost:8443", "http://localhost:4200",
								"https://localhost:4200","https://loanapp-8bcf8.firebaseapp.com", 
								"https://loanapp-8bcf8.firebaseapp.com:8443","http://localhost:7200",
								"https://loanapp-8bcf8.web.app","https://loanapp-8bcf8.web.app:8443","http://165.22.124.147",
								"https://localhost:7200", "http://165.22.124.147:8080", "http://165.22.124.147:4200")
						.allowedMethods("GET", "PUT", "POST", "DELETE").allowedHeaders("*");
			}
		};
	}
	

}