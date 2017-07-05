package ie.turfclub.config;


import ie.turfclub.utilities.EmployeeHistoryUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:resources/config.properties")

public class BeanConfig {

	@Autowired
	private Environment env;
	
	
	 @Bean
	 public static PropertySourcesPlaceholderConfigurer properties() {
	     PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
	     return propertySourcesPlaceholderConfigurer;
	 }
	
	 @Bean
	 public EmployeeHistoryUtils employeeUtils(){
		 return new EmployeeHistoryUtils();
	 }

	
}
