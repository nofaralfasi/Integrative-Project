package collab.rest.boundaries;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import collab.data.UserRole;

public class UserBoundary {
	private UserId userId;
	@NotNull
	private UserRole role;
	@NotEmpty
	private String username;
	@NotEmpty
	private String avatar;

	public UserBoundary() {
	}

	public UserBoundary(UserId userId, UserRole role, String username, String avatar) {
		this();
		this.userId = userId;
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

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

	@Override
	public String toString() {
		return "UserBoundary [userId=" + userId + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ "]";
	}

}
