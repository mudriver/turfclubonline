package ie.turfclub.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUtils {

	FTPClient ftp = null;
    
    public FTPUtils(String host, String user, String pwd) throws Exception{
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        ftp.connect(host);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to FTP Server");
        }
        ftp.login(user, pwd);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalActiveMode();
    }
    public void uploadFile(InputStream inputStream, String fileName, String hostDir)
            throws Exception {
    
        	this.ftp.storeFile(hostDir + fileName, inputStream);
    
    }
 
    public String[] listFiles() {
    
        	String[] fileNames = null;
			try {
				fileNames = this.ftp.listNames();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return fileNames;
    }
    
    public void disconnect(){
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException f) {
                // do nothing as file is already saved to server
            }
        }
    }
    
    
 
 

	
	
	
	boolean checkFileExists(String dirPath) throws IOException {
	    ftp.changeWorkingDirectory(dirPath);
	    int returnCode = ftp.getReplyCode();
	    if (returnCode == 550) {
	        return false;
	    }
	    return true;
	}
	
}
