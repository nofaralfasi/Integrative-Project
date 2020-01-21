package collab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import collab.data.UserRole;
import collab.logic.UsersService;
import collab.rest.boundaries.NewUserForm;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerApplicationIntegrationTests {
	private int port;
	private String baseUrl;
	private RestTemplate restTemple;
	
	private UsersService userService;
	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@Autowired
	public void setMessageService(UsersService userService) {
		this.userService = userService;
	}
	
	@PostConstruct
	public void init() {
		this.baseUrl = "http://localhost:" + port;
		this.restTemple = new RestTemplate();
	}
	
	@AfterEach
	public void teardown() {
		this.userService.deleteAll();
	}
	
	@Test
	public void testGetUserInDbAfterOneCreation() throws Exception {
		// GIVEN the database is with one user  2020a.nofar/new@us.er
		UserBoundary singleUser = new UserBoundary(new UserId("2020a.nofar","new@us.er")
				, UserRole.PLAYER, "Yuval", ":)");
		singleUser = this.userService.create(singleUser);
		
		// WHEN we invoke GET /collab/users/login/{domain}/{email}	
		UserBoundary actual = 
			this.restTemple.getForObject(this.baseUrl + "/collab/users/login/{domain}/{email}", UserBoundary.class,
				singleUser.getUserId().getDomain(), singleUser.getUserId().getEmail());
		
		// THEN the single user is returned
		assertThat(actual)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(singleUser);
	}
	
	@Test
	public void testPostNewUserIsSuccessful() throws Exception{
		// GIVEN the database is clean
		
		// WHEN I POST a new userForm //http://localhost:8080/collab/users
		NewUserForm singleUser = new NewUserForm("new@us.er",UserRole.PLAYER,"Yuval",":)" );
		
		this.restTemple.postForObject(this.baseUrl + "/collab/users", singleUser, UserBoundary.class);
			
		
		// THEN the database contains the new user
		assertThat(this.userService.getAllUsers())
			.isNotEmpty()
			.extracting("avatar")
			.containsExactly(singleUser.getAvatar());
	}
	
	@Test
	public void testPutUserAvatarIsSuccessfull() throws Exception{
		// GIVEN the database contains a message with avatar :)
		UserBoundary singleUser = new UserBoundary(new UserId("2020a.nofar","new@us.er")
				, UserRole.PLAYER, "Yuval", ":)");
		singleUser = this.userService.create(singleUser);
			
		// WHEN we PUT /collab/users/{domain}/{userEmail} and we send an update of the avatar 
		String newAvatar = "=)))";
		this.restTemple
			.put(this.baseUrl + "/collab/users/{domain}/{userEmail}", 
				Collections.singletonMap("avatar", newAvatar), 
				singleUser.getUserId().getDomain(),singleUser.getUserId().getEmail());
		
		
		// THEN the database is updated with the new type
		assertThat(this.userService.login(singleUser.getUserId().getDomain(),singleUser.getUserId().getEmail()))
			.extracting("userId", "avatar")
			.containsExactly(singleUser.getUserId(), newAvatar);
	}
	
	
	@Test
	public void testGetNonExistingFails() throws Exception{
		// GIVEN the database is empty
		
		// WHEN we GET /collab/users/login/{domain}/{email} with any doamin, and email
		// THEN an exception occurs
		assertThrows(Exception.class, ()->
			this.restTemple.getForObject(
					this.baseUrl + "/collab/users/login/{domain}/{email}", 
					UserBoundary.class, 
					"2020a.nofar","new@us.er"));
		
	}
	
	@Test
	public void testGetByIdReturnsUserFromDb() throws Exception{
		// GIVEN the database contains 5 users
		List<UserBoundary> allInDb  = 
		  IntStream.range(1, 6)
			.mapToObj(i->new UserBoundary(new UserId("2020a.nofar",i+"new@us.er")
					, UserRole.PLAYER, "Yuval", ":)"))
			.map(this.userService::create)
			.collect(Collectors.toList());
		
		UserBoundary last = allInDb.get(allInDb.size() - 1);
		
		// WHEN we invoke GET /hello/{lastId} 
		UserBoundary actual = 
				this.restTemple.getForObject(
					this.baseUrl + "/collab/users/login/{domain}/{userEmail}", 
					UserBoundary.class,
					last.getUserId().getDomain(),last.getUserId().getEmail());
		
		// THEN the all messages are returned in any order
		assertThat(actual)
			//.isNotNull()
			.isEqualToComparingFieldByField(last);

	}
}