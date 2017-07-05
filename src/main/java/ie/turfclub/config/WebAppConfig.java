package ie.turfclub.config;

import ie.turfclub.formatters.DateFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
@EnableWebMvc
@ComponentScan( {"ie.turfclub.controller"})
public class WebAppConfig extends WebMvcConfigurerAdapter {


	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
	
	

 
 // Maps resources path to webapp/resources
 public void addResourceHandlers(ResourceHandlerRegistry registry) {
  registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
  registry.addResourceHandler("/favicon.ico").addResourceLocations("/favicon.ico");
 }
 
 // Provides internationalization of messages
 @Bean
 public ResourceBundleMessageSource messageSource() {
  ResourceBundleMessageSource source = new ResourceBundleMessageSource();
  source.setBasename("messages");
  return source;
 }
 
 @Override
 public void addFormatters(FormatterRegistry registry) {
     
     registry.addFormatter(new DateFormatter());
     super.addFormatters(registry);
 }
  
}