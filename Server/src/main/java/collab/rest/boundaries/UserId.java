package collab.rest.boundaries;

public class UserId {
	private String domain;
	private String email;

	public UserId() {
	}

	public UserId(String domain, String email) {
		super();
		this.domain = domain;
		this.email = email;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserId [domain=" + domain + ", email=" + email + "]";
	}
	
	 @Override
	    public boolean equals(Object user) { 
	  
	        // If the object is compared with itself then return true   
	        if (user == this) { 
	            return true; 
	        } 
	  
	        
	        if (!(user instanceof UserId)) { 
	            return false; 
	        } 
	          
	        // typecast o to userId so that we can compare data members  
	        UserId userToCheck = (UserId) user; 
	          
	        // Compare the data members and return accordingly  
	        return userToCheck.getDomain().equals(this.getDomain()) && userToCheck.getEmail().equals(this.getEmail());
	    
	} 
}
