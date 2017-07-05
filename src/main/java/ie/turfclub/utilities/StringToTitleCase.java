package ie.turfclub.utilities;

public class StringToTitleCase {

	public static String convertString(String title){
		String[] parts = title.split(" ");
		StringBuilder sb = new StringBuilder(64);
		for (String part : parts) {
		    if(!part.contains("(")){
		    	char[] chars = part.toLowerCase().toCharArray();
			    chars[0] = Character.toUpperCase(chars[0]);
			    sb.append(new String(chars)).append(" ");
		    }
		    else{
		    	char[] chars = part.toCharArray();
		    	sb.append(new String(chars)).append(" ");
		    }

		    
		}

		
		title = sb.toString().trim();
		if(title.contains("(*)")){
			
		}
		return title;
	}
	
	
	
}
