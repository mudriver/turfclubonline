package ie.turfclub.utilities;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;


/**
 * To Encrypt and Decrypt String
 * 
 * @author 
 *
 */
@Service
public class EncryptDecryptUtils {
	
	final static Logger logger = Logger.getLogger(EncryptDecryptUtils.class);
	
	private static final String PASS_PHRASE = "TURFCLUBPROGRAMThisIs#TURFCLUBPROGRAMThis_IsCODEMECHThis#IsTURFCLUBPROGRAM";
	private static final byte[] SALT_BYTE = "WEBS_KWH/OPTI_360".getBytes();
	private static final String UNICODE_FORMAT = "UTF-8";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final int iterations = 15000;
	private static final String SECRET_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";
	
    private static Cipher cipher;
    private static SecretKeySpec serkey; 
    private static final byte[] IV = { 0, 10, 20, 80, 50, 30, 90, 70, 80, 50, 60, 80, 04, 05, 03, 05 };
    private static final IvParameterSpec IVSPEC = new IvParameterSpec(IV);
    
    static { 
    	try {
        	SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_FACTORY_ALGORITHM);
        	SecretKey tmp = factory.generateSecret(new PBEKeySpec(PASS_PHRASE.toCharArray(), SALT_BYTE, iterations, 128));
        	serkey = new SecretKeySpec(tmp.getEncoded(), "AES");
	        cipher = Cipher.getInstance(TRANSFORMATION);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }


    /**
     *  This is a encrypt method which is used to encrypt string
     * 
     * @param unencryptedString
     * @return
     */
    public static String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, serkey, IVSPEC);
            byte[] encryptedText = cipher.doFinal(unencryptedString.getBytes(UNICODE_FORMAT));
            encryptedString = Base64.encodeBase64URLSafeString(encryptedText);
        } catch (Exception e) {
            logger.error("While encrypting String : "+unencryptedString, e);
            e.printStackTrace();
        }
        return encryptedString;
    }


    /**
     * This is a decrypt method which is used to decrypt string
     * 
     * @param encryptedString
     * @return
     */
    public static String decrypt(String encryptedString) {
        String decryptedText=null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, serkey, IVSPEC);
            byte[] encryptedText = Base64.decodeBase64(encryptedString.getBytes());
            decryptedText = new String(cipher.doFinal(encryptedText));
        } catch (Exception e) {
        	logger.error("While decrypting String : "+encryptedString, e);
        	e.printStackTrace();
        }
        return decryptedText;
    }
    	
    public static void main(String[] args) throws Exception {
    	String passphrase = "correct horse battery staple";
    	byte[] salt = "choose a better salt".getBytes();
    	int iterations = 15000;
    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    	SecretKey tmp = factory.generateSecret(new PBEKeySpec(passphrase.toCharArray(), salt, iterations, 128));
    	SecretKeySpec serkey = new SecretKeySpec(tmp.getEncoded(), "AES");
    	
    	Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
    	aes.init(Cipher.ENCRYPT_MODE, serkey);
    	byte[] ciphertext = aes.doFinal("1".getBytes(UNICODE_FORMAT));
    	System.out.println(Base64.encodeBase64URLSafeString(ciphertext));

    	aes.init(Cipher.DECRYPT_MODE, serkey);
    	String cleartext = new String(aes.doFinal(ciphertext));
    	
    	System.out.println(cleartext);
	}
}
