package ie.turfclub.service.downloads;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
 

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
@Service
public class DownloadService {
 
	
	protected static Logger logger = Logger.getLogger("service");
 

	
	@Autowired
	private ExporterService exporter;
	
	@Autowired
	private TokenService tokenService;
	
	public void download(String jasperTemplateFileName, HashMap<String, Object> params, JRBeanCollectionDataSource dataSource, java.sql.Connection conn, String token, HttpServletResponse response) {
		 
		
			 
			// 5. Create the JasperPrint object
			// Make sure to pass the JasperReport, report parameters, and data source
			JasperPrint print = null;
			if(dataSource == null){
				
				
				try {
					print = JasperFillManager.fillReport(
							this.getClass().getResourceAsStream(jasperTemplateFileName), params, conn);
					
				} catch (JRException e) {
					logger.error("Unable to process download");
					throw new RuntimeException(e);
				}
				
			}
			else{
				
			}
			
			 
			// 6. Create an output byte stream where data will be written
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 
			// 7. Export report
			exporter.export( print, response, baos);
			 
			// 8. Write to reponse stream
			write(token, response, baos);
		
		
	}
	
	/**
	* Writes the report to the output stream
	*/
	private void write(String token, HttpServletResponse response,
			ByteArrayOutputStream baos) {
		 
		try {
			logger.debug(baos.size());
			
			// Retrieve output stream
			ServletOutputStream outputStream = response.getOutputStream();
			// Write to output stream
			baos.writeTo(outputStream);
			// Flush the stream
			outputStream.flush();
			
			// Remove download token
			tokenService.remove(token);
			
		} catch (Exception e) {
			logger.error("Unable to write report to the output stream");
			throw new RuntimeException(e);
		}
	}
}
