package ie.turfclub.config;

import ie.turfclub.service.stableStaff.StableStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.multipart.MultipartResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Order(1)
@Configuration
public class ThymeleafConfig {
 
	@Autowired
	StableStaffService stableStaffService;
	
    @Bean
    public TemplateResolver templateResolver(){
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        stableStaffService.initialize();
        return templateResolver;
    }
 
    @Bean
    //view resolver for accounts reports
    public TemplateResolver stableStaffViewResolver(){
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/views/stableStaff/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        return templateResolver;
    }
    
    @Bean
    public MultipartResolver multipartResolver() {
        org.springframework.web.multipart.commons.CommonsMultipartResolver multipartResolver = new org.springframework.web.multipart.commons.CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(10000000);
        return multipartResolver;
    }
  
    
    
    @Bean
    public SpringTemplateEngine templateEngine(){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
      

        templateEngine.addTemplateResolver(stableStaffViewResolver());
        templateEngine.addTemplateResolver(templateResolver());
        return templateEngine;
    }
 
 @Bean
 public ThymeleafViewResolver thymeleafViewResolver() {
  ThymeleafViewResolver resolver = new ThymeleafViewResolver();
  resolver.setTemplateEngine(templateEngine());
  return resolver;
 }
 
 
 
}
