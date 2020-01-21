package collab.logic;

import java.util.List;

import collab.rest.boundaries.UserBoundary;

public interface UsersService {
	public UserBoundary create(UserBoundary user);
	public UserBoundary update(UserBoundary update);
	public UserBoundary login(String domain, String email);
	public List<UserBoundary> getAllUsers();
	public void deleteAll();
}
