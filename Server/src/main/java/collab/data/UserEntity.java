package collab.data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
public class UserEntity {
	private String userId;
	private UserRole role;
	private String username;
	private String avatar;

	public UserEntity() {
	}

	public UserEntity(String userId, UserRole role, String username, String avatar) {
		this();
		this.userId = userId;
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}

	@Id
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Enumerated(EnumType.ORDINAL)
	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

}
