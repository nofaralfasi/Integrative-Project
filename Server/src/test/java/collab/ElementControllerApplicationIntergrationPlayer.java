package collab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import collab.data.UserEntity;
import collab.data.UserRole;
import collab.logic.ElementsService;
import collab.logic.UserConverter;
import collab.logic.UsersService;
import collab.rest.boundaries.Element;
import collab.rest.boundaries.ElementBoundary;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.User;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ElementControllerApplicationIntergrationPlayer {
	private int port;
	private String baseUrl;
	private RestTemplate restTemple;
	private ElementsService elementService;
	private UsersService userService;
	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@Autowired
	public void setMessageService(ElementsService elementService, UsersService userService) {
		this.elementService = elementService;
		this.userService = userService;
	}


	@PostConstruct
	public void init() {
		this.baseUrl = "http://localhost:" + port;
		this.restTemple = new RestTemplate();
		
		
	}
	@BeforeEach
	public void setUp() {
		System.err.println("Here");
		UserBoundary playerUser = new UserBoundary(new UserId("2020a.nofar","yuval@dotncs.com")
				, UserRole.PLAYER, "Yuval", ":)");
		UserBoundary managerUser = new UserBoundary(new UserId("2020a.nofar","manger@dotncs.com")
				, UserRole.MANAGER, "Yuval123", ":)");
		this.userService.create(playerUser);
		this.userService.create(managerUser);

	}
	@AfterEach
	public void teardown() {
		this.elementService.deleteAll();
		this.userService.deleteAll();
	}
	
	@Test
	public void testGetElementWithNoParentElementInDbAfterOneCreation() throws Exception {
		// GIVEN the database is with one user  2020a.nofar/yuval@dotncs.com
		ElementBoundary singleElement = new ElementBoundary(null,"Chair",
				"chairs",true, null, new User(new UserId("2020a.nofar","yuval@dotncs.com")),null, new HashMap<String, Object>() );
		singleElement = this.elementService.create("2020a.nofar","manger@dotncs.com",singleElement);
		// WHEN we invoke GET /collab/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}
		ElementBoundary actual = 
			this.restTemple.getForObject(this.baseUrl + "/collab/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}", 
					ElementBoundary.class,
					singleElement.getCreatedBy().getUserId().getDomain(), singleElement.getCreatedBy().getUserId().getEmail(),
					singleElement.getElementId().getDomain(),singleElement.getElementId().getId());
		
		// THEN the single user is returned
		assertThat(actual)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(singleElement);
	}
	
	@Test
	public void testGetElementWithParentElementInDb() throws Exception {
		// GIVEN the database is with one user  2020a.nofar/yuval@dotncs.com
		ElementBoundary firstElement = new ElementBoundary(null,"Chair",
				"chairs",true, null, new User(new UserId("2020a.nofar","yuval@dotncs.com")),null, new HashMap<String, Object>());
		firstElement = this.elementService.create("2020a.nofar","manger@dotncs.com",firstElement);
		ElementBoundary secondElement = new ElementBoundary(null,"Chair",
				"chairs",true, null, new User(new UserId("2020a.nofar","yuval@dotncs.com")),new Element(new ElementId(firstElement.getElementId().getDomain(),firstElement.getElementId().getId())),new HashMap<String, Object>());
		secondElement = this.elementService.create("2020a.nofar","manger@dotncs.com",secondElement);
		// WHEN we invoke GET /collab/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId} with second element
		ElementBoundary actual = 
			this.restTemple.getForObject(this.baseUrl + "/collab/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}", 
					ElementBoundary.class,
					secondElement.getCreatedBy().getUserId().getDomain(), secondElement.getCreatedBy().getUserId().getEmail(),
					secondElement.getElementId().getDomain(),secondElement.getElementId().getId());
		
		// THEN the single user is returned
		assertThat(actual)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(secondElement);
	}
	
	
	
	//Ali:: test get element by ID with empty data
		@Test
		public void testGetElementByIdWithEmptyDbReturnsnull() throws Exception{
				
			// GIVEN the database is empty
			
					// WHEN we GET GET /elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}
					// THEN an exception occurs
					assertThrows(Exception.class, ()->
						this.restTemple.getForObject(
								this.baseUrl + "/collab/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}", 
								ElementBoundary.class, 
								"userDomain",
								"userEmail",
								"2020a.alik",
								"1"));
			
		}
	
		
		@Test
		public void testGetElementByIdWithOneElementsInDbReturnsTheOnlyObject() throws Exception{
		
			// GIVEN the database contains 1 elments
					
			ElementBoundary e1 = this.elementService.create(
					"2020a.nofar",
					"manger@dotncs.com",
					new ElementBoundary(
							null,
							"name",
							"type",
							true,
							null,
							new User(new UserId("2020a.nofar","yuval@dotncs.com")),
							null,
							new HashMap<String, Object>()));
			
			
			// WHEN we invoke GET /elements/{userDomain}/{userEmail}/{elementDomain}/{elementId} 
			ElementBoundary actual = 
				this.restTemple.getForObject(
					this.baseUrl + "/collab/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}", 
					ElementBoundary.class ,
					"2020a.nofar",
					"yuval@dotncs.com",
					e1.getElementId().getDomain(),
					e1.getElementId().getId());
			
			
			// THEN the specific element with this id is returned
			assertThat(actual)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(e1);
		}
	
		
		//Ali:: test get element by ID with three element in the data
		@Test
		public void testGetElementByIdWithThreeElementsInDbReturnsSpecificElementFromDb() throws Exception{
		
			// GIVEN the database contains 3 elments
			this.elementService.create(
					"2020a.nofar",
					"manger@dotncs.com",
					new ElementBoundary(
							null,
							"name",
							"type",
							true,
							null,
							new User(new UserId("2020a.nofar","yuval@dotncs.com")),
							null,
							new HashMap<String, Object>()));
			
			this.elementService.create(
					"2020a.nofar",
					"manger@dotncs.com",
					new ElementBoundary(
							null,
							"name",
							"type",
							true,
							null,
							new User(new UserId("2020a.nofar","yuval@dotncs.com")),
							null,
							new HashMap<String, Object>()));
			
			ElementBoundary e3 = this.elementService.create(
					"2020a.nofar",
					"manger@dotncs.com",
					new ElementBoundary(
							null,
							"name",
							"type",
							true,
							null,
							new User(new UserId("2020a.nofar","yuval@dotncs.com")),
							null,
							new HashMap<String, Object>()));
			
			
			// WHEN we invoke GET /elements/{userDomain}/{userEmail}/{elementDomain}/{elementId} 
			ElementBoundary actual = 
				this.restTemple.getForObject(
					this.baseUrl + "/collab/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}", 
					ElementBoundary.class ,
					"2020a.nofar",
					"yuval@dotncs.com",
					e3.getElementId().getDomain(),
					e3.getElementId().getId());
			
			
			// THEN the specific element with this id is returned
			assertThat(actual)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(e3);
		}
		
	
	
}