package ie.turfclub.config;

import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;


@Configuration
@EnableTransactionManagement
//@ComponentScan(basePackages = "ie.turfclub.config")
@PropertySource("classpath:database.properties")
public class DatabaseConfig {
 
    private static final String PROPERTY_NAME_DATABASE_DRIVER   = "db.driver";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
    private static final String PROPERTY_NAME_DATABASE_URL      = "db.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
    private static final String PROPERTY_NAME_MINPOOL      = "hibernate.c3p0.min_size";
    private static final String PROPERTY_NAME_MAXPOOL      = "hibernate.c3p0.max_size";
    private static final String PROPERTY_NAME_TIMEOUT      = "hibernate.c3p0.timeout";
    private static final String PROPERTY_NAME_MAXSTATEMENT      = "hibernate.c3p0.max_statements";
    private static final String PROPERTY_NAME_IDLETEST      = "hibernate.c3p0.idle_test_period";
    private static final String PROPERTY_NAME_INCREMENT      = "hibernate.c3p0.acquire_increment";
    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
     
 @Resource
 private Environment env;
  
 @Bean
 public DataSource dataSource() {
  /*
	 DriverManagerDataSource dataSource = new DriverManagerDataSource();
  dataSource.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
  dataSource.setUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
  dataSource.setUsername(env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
  dataSource.setPassword(env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
  return dataSource;
 */
  try {
		ComboPooledDataSource ds = new ComboPooledDataSource();
		ds.setDriverClass(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
		ds.setJdbcUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
		ds.setUser(env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
		ds.setPassword(env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
		ds.setAcquireIncrement(5);
		ds.setIdleConnectionTestPeriod(60);
		ds.setMaxPoolSize(100);
		ds.setMaxStatements(50);
		ds.setMinPoolSize(10);
		return ds;
	} catch (Exception e) {
		throw new RuntimeException(e);
	}

 
 }
  
 private Properties hibProperties() {
  Properties properties = new Properties();
  properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
  properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
  properties.put( PROPERTY_NAME_MINPOOL, env.getRequiredProperty(PROPERTY_NAME_MINPOOL));
  properties.put(PROPERTY_NAME_MAXPOOL, env.getRequiredProperty(PROPERTY_NAME_MAXPOOL));
  properties.put(PROPERTY_NAME_TIMEOUT, env.getRequiredProperty(PROPERTY_NAME_TIMEOUT));
  properties.put(PROPERTY_NAME_MAXSTATEMENT, env.getRequiredProperty(PROPERTY_NAME_MAXSTATEMENT));
  properties.put(PROPERTY_NAME_IDLETEST, env.getRequiredProperty(PROPERTY_NAME_IDLETEST));
  properties.put(PROPERTY_NAME_INCREMENT, env.getRequiredProperty(PROPERTY_NAME_INCREMENT));
  return properties; 
 }
  
 @Bean
 public HibernateTransactionManager transactionManager() {
  HibernateTransactionManager transactionManager = new HibernateTransactionManager();
  transactionManager.setSessionFactory(sessionFactory().getObject());
  return transactionManager;
 }
  
 @Bean
 public LocalSessionFactoryBean sessionFactory() {
	 LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
  sessionFactoryBean.setDataSource(dataSource());
  sessionFactoryBean.setPackagesToScan(env.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
  sessionFactoryBean.setHibernateProperties(hibProperties());
  return sessionFactoryBean;
 }
}