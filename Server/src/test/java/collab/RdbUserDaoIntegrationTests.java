package collab;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import collab.dal.UserDao;
import collab.data.UserEntity;
import collab.data.UserRole;
import collab.data.utils.EntityFactory;


@SpringBootTest
public class RdbUserDaoIntegrationTests {
	private UserDao userDao;
	private EntityFactory factory;
	
	
	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@AfterEach
	public void teardown() {
		this.userDao.deleteAll();
	}


	@Test
	public void createManyDifferentUsersAndCheckThatItStored() {
		// GIVEN we have clean database
		// AND a factory
		
		// WHEN I create numberOfUsers
		int numberOfUsers = 5;
		List<UserEntity> listOfUsers = IntStream.range(0, numberOfUsers)// int stream
				.mapToObj(num -> "dummy #" + num)// String stream
				.map(name -> this.factory.createNewUser(// user entity stream
						"2020a.nofar@@"+name+"@gmail.com", name, " :)", UserRole.PLAYER))
				.map(this.userDao::save).collect(Collectors.toList());
		Iterable<UserEntity> actualUsers = this.userDao.findAll();
		// THEN the listOfUser is stored

		assertThat(actualUsers)
		.hasSize(numberOfUsers)
		.usingElementComparatorOnFields("userId")
		.containsAll(listOfUsers);

	}
	
	@Test
	public void createManyUsersWithSameUserIdAndCheckOnlyOneIsStored() {
		// GIVEN we have clean database
		// AND a factory
		
		// WHEN I create numberOfUsers
		int numberOfUsers = 5;
		List<UserEntity> listOfUsers = IntStream.range(0, numberOfUsers)// int stream
				.mapToObj(num -> "dummy #" + num)// String stream
				.map(name -> this.factory.createNewUser(// user entity stream
						"2020a.nofar@@@gmail.com", name, " :)", UserRole.PLAYER))
				.map(this.userDao::save).collect(Collectors.toList());
		Iterable<UserEntity> actualUsers = this.userDao.findAll();
		// THEN the listOfUser is stored

		assertThat(actualUsers)
		.hasSize(1);
		

	}

	@Test
	public void testCreateUpdateReadByIdDeleteAllReadAll() throws Exception {
		// GIVEN we have a clear database
		// AND we have a factory

		// WHEN I create a player
		// AND I update a player
		// AND I read player by key
		// AND I delete all
		// AND I read all
		String username = "Player1";
		UserRole role = UserRole.PLAYER;
		String userEmail = "yuval@mail.afeka.ac.il";
		String userDomain = "2020a.nofar";
		String avatar = ":)";

		UserEntity user1 = factory.createNewUser(userDomain+"@@"+userEmail, username, avatar, role);
		user1 = this.userDao.save(user1);

		UserEntity initUser = new UserEntity();
		initUser.setUserId(user1.getUserId());

		String updateAvatar = ":')";
		UserEntity updateUser = this.userDao.findById(user1.getUserId()).get();
		updateUser.setAvatar(updateAvatar);
		this.userDao.save(updateUser);
		
		Optional<UserEntity> userOp = this.userDao.findById(user1.getUserId());

		this.userDao.deleteAll();

		Iterable<UserEntity> listAfterDeletion = this.userDao.findAll();

		// THEN the initially generated users key is not null
		// AND the user read using key is present
		// AND the key of user read is not null
		// AND the list after deletion is empty

		assertThat(initUser.getUserId()).isNotNull();
		
		assertThat(userOp.get().getUserId(), is(equalTo(user1.getUserId())));
		
		assertThat(listAfterDeletion).isEmpty();
	}

}
