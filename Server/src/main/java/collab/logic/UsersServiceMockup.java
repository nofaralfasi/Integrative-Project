package collab.logic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;

import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

//@Service
public class UsersServiceMockup implements UsersService {
	private Map<String, UserBoundary> db;
	private String domain;
	@Value("${collab.config.id.delimiter}")
	private String delimiter;

	public UsersServiceMockup() {
	}

	@Value("${collab.config.domain:defaultDomain}")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	@PostConstruct
	public void init() {
		this.db = Collections.synchronizedMap(new TreeMap<>());
	}

	@PreDestroy
	public void cleanup() {
		System.err.println("user logic is deleted");
	}

	@Override
	public UserBoundary create(UserBoundary user) {
		String stringUserId;

		if (user.getRole() == null)
			throw new RuntimeException("UserRole is null!");

		user.getUserId().setDomain(this.domain);
		stringUserId = generateStringUserId(user.getUserId());

		if (this.db.containsKey(stringUserId))
			throw new RuntimeException("A user with the id: " + stringUserId + " already exists!");

		this.db.put(stringUserId, user);
		return user;
	}

	@Override
	public UserBoundary update(UserBoundary update) {
		String stringUserId = this.generateStringUserId(update.getUserId());
		UserBoundary existingUser = this.getUserById(stringUserId);

		boolean dirtyFlag = false;
		if (update.getAvatar() != null) {
			existingUser.setAvatar(update.getAvatar());
			dirtyFlag = true;
		}
		if (update.getUsername() != null) {
			existingUser.setUsername(update.getUsername());
			dirtyFlag = true;
		}
		if (update.getRole() != null) {
			existingUser.setRole(update.getRole());
			dirtyFlag = true;
		}

		if (dirtyFlag) {
			this.db.put(stringUserId, existingUser);
		}

		return existingUser;
	}

	@Override
	public UserBoundary login(String domain, String email) {
		String stringUserId = generateStringUserId(new UserId(domain, email));
		return this.getUserById(stringUserId);
	}

	@Override
	public List<UserBoundary> getAllUsers() {
		return new LinkedList<>(this.db.values());
	}

	@Override
	public void deleteAll() {
		this.db.clear();
	}

	public String generateStringUserId(UserId userId) {
		return userId.getDomain() + delimiter + userId.getEmail();
	}

	public UserBoundary getUserById(String stringUserId) {
		UserBoundary rv = this.db.get(stringUserId);
		if (rv == null) {
			throw new RuntimeException("no user could be found with id: " + stringUserId);
		}
		return rv;
	}

}
