package collab.rest.boundaries;

public class User {
	private UserId userId;

	public User() {
	}

	public User(UserId userId) {
		super();
		this.userId = userId;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + "]";
	}
	
	@Override
    public boolean equals(Object user) { 
  
        // If the object is compared with itself then return true   
        if (user == this) { 
            return true; 
        } 
  
        
        if (!(user instanceof User)) { 
            return false; 
        } 
          
        // typecast o to userId so that we can compare data members  
        User userToCheck = (User) user; 
          
        // Compare the data members and return accordingly  
        return userToCheck.getUserId().equals(this.getUserId());
    
	} 
}
