package ie.turfclub.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 
public class Hashing {
	 
	// JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://www.turfclub.ie/trainers";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "password";
	
	  public static void main(String[] args) {
	 
		
			Hashing hashing = new Hashing();
			hashing.encryptPassword("T0OIAF");
			//hashing.updatePasswords();
			
		
	 
	  }
	  
	  public String encryptPassword(String password){
		  BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(password);
	 
			System.out.println(hashedPassword);
			return hashedPassword;
	  }
	
	  public void updatePasswords(){
		  
		  Connection conn = null;
		   Statement stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement(
                      ResultSet.TYPE_SCROLL_INSENSITIVE,
                      ResultSet.CONCUR_UPDATABLE);

		      String sql = "SELECT trainer_id, trainer_pwd_clear, trainer_pwd FROM te_trainers";
		      ResultSet rs = stmt.executeQuery(sql);
		      
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		         int id  = rs.getInt("trainer_id");
		         String pwd = rs.getString("trainer_pwd_clear");

		         if(pwd != null){
		        	//Display values
			         System.out.print("ID: " + id);
			         System.out.println(", PWD: " + pwd);
			         rs.updateString( "trainer_pwd", encryptPassword(pwd) );
			         rs.updateRow();

			         
		         }
		         
		         

		      }
		      rs.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   System.out.println("Goodbye!");
		  
	  }

	  
}