package qut.localization.service;

public abstract class Authenticator {

	static public final String SERVICE_PASSWORD = "localization";
	static public final String SERVICE_USERNAME = "QUT";
	
	/**
	 * Question the request's security clearance.
	 * @param password - The password 
	 * @return - returns true if clearance allow else false.
	 */
	static public boolean authenticate(String password){
		
		if(password == null)
			return false;
		
		else
			if(!password.equals(SERVICE_PASSWORD))
				return false;
		
		return true;
	}
}
