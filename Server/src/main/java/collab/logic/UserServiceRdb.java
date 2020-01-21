package collab.logic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import collab.dal.UserDao;
import collab.data.UserEntity;
import collab.rest.NotFoundException;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@Service
public class UserServiceRdb implements UsersService {
	private UserDao userDao;
	private GeneralConverter converter;
	private UserConverter userConverter;
	private String domain;

	@Autowired
	public UserServiceRdb(UserDao userDao, GeneralConverter converter, UserConverter userConverter) {
		super();
		this.userDao = userDao;
		this.userConverter = userConverter;
		this.converter = converter;
	}

	@Value("${collab.config.domain:defaultDomain}")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	@Transactional
	public UserBoundary create(UserBoundary user) {
		user.getUserId().setDomain(this.domain);
		boolean userExist = false;
		try {
			user = this.getUserById(user.getUserId());
			userExist = true;
		} catch (Exception e) {
		}
		if (userExist)
			throw new RuntimeException("A user with the id: " + user.getUserId() + " already exists!");
		return this.userConverter.fromEntity(this.userDao.save(this.userConverter.toEntity(user)));
	}

	@Override
	@Transactional
	public UserBoundary update(UserBoundary update) {
		UserBoundary existingUser = this.getUserByStringUserId(this.converter.toStringUserId(update.getUserId()));
		if (update.getAvatar() != null)
			existingUser.setAvatar(update.getAvatar());
		if (update.getUsername() != null)
			existingUser.setUsername(update.getUsername());
		if (update.getRole() != null)
			existingUser.setRole(update.getRole());
		return this.userConverter.fromEntity(this.userDao.save(userConverter.toEntity(existingUser)));
	}

	@Override
	@Transactional(readOnly = true)
	public UserBoundary login(String domain, String email) {
		return this.getUserByStringUserId(this.converter.toStringUserId(new UserId(domain, email)));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers() {
		Iterable<UserEntity> iter = this.userDao.findAll();
		return StreamSupport.stream(iter.spliterator(), false).map(this.userConverter::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteAll() {
		this.userDao.deleteAll();
	}

	@Transactional(readOnly = true)
	public UserBoundary getUserByStringUserId(String stringUserId) {
		UserBoundary rv = this.userConverter.fromEntity(this.userDao.findById(stringUserId)
				.orElseThrow(() -> new NotFoundException("no user could be found with id: " + stringUserId)));
		return rv;
	}

	@Transactional(readOnly = true)
	public UserBoundary getUserById(UserId userId) {
		return this.getUserByStringUserId(this.converter.toStringUserId(userId));
	}

}
