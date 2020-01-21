package collab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class ElementControllerApplicationIntergrationManager {
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
		UserBoundary singleUser = new UserBoundary(new UserId("2020a.nofar","yuval@dotncs.com")
				, UserRole.MANAGER, "Yuval", ":)");
		this.userService.create(singleUser);
	}
	@AfterEach
	public void teardown() {
		this.elementService.deleteAll();
		this.userService.deleteAll();
	}
	
	
	@Test
	public void testPostNewElementIsSuccessful() throws Exception{
		// GIVEN the database is clean
		
		// WHEN I POST a new userForm /collab/elements/{managerDomain}/{managerEmail}
		ElementBoundary singleElement = new ElementBoundary(null,"Chair",
				"chairs",true, null, new User(new UserId("2020a.nofar","yuval@dotncs.com")),null, new HashMap<String, Object>() );
		
		this.restTemple.postForObject(this.baseUrl + "/collab/elements/{managerDomain}/{managerEmail}",
				singleElement, ElementBoundary.class
				,singleElement.getCreatedBy().getUserId().getDomain(),singleElement.getCreatedBy().getUserId().getEmail());
			
		
		// THEN the database contains the new element
		assertThat(this.elementService.getAllElements())
			.isNotEmpty()
			.usingElementComparatorOnFields("active", "name")
			.containsExactly(singleElement);
	}
	
	@Test
	public void testPutElementNameIsSuccessfull() throws Exception{
		// GIVEN the database contains a message with avatar :)
		System.err.println(this.userService.getAllUsers());
		ElementBoundary singleElement = new ElementBoundary(null,"Chair",
				"chairs",true, null, new User(new UserId("2020a.nofar","yuval@dotncs.com")),null, new HashMap<String, Object>());
		singleElement = this.elementService.create("2020a.nofar","yuval@dotncs.com",singleElement);
			
		// WHEN we PUT /collab/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and we send an update of the element name 
		String newName = "Table";
		this.restTemple
			.put(this.baseUrl + "/collab/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}", 
				Collections.singletonMap("name", newName), 
				singleElement.getCreatedBy().getUserId().getDomain(),
				singleElement.getCreatedBy().getUserId().getEmail(),
				singleElement.getElementId().getDomain(),
				singleElement.getElementId().getId());
		
		
		// THEN the database is updated with the new type
		assertThat(this.elementService.getSpecificElement(singleElement.getCreatedBy().getUserId().getDomain(),
				singleElement.getCreatedBy().getUserId().getEmail(),
				singleElement.getElementId().getDomain(),
				singleElement.getElementId().getId()))
			.extracting("createdBy", "name")
			.containsExactly(singleElement.getCreatedBy(), newName);
	}
	
	
	
	
			
		//Ali:: test post with create method with empty data is success and the db contain the new element
		@Test
		public void testPostNewElementWithEmptyDataIsSuccess() throws Exception{
			// GIVEN the database is clean
			
			// WHEN I POST a new Element		
			Map<String, String> parentElement = new HashMap<>();
			parentElement.put("key1", "stam1");
			parentElement.put("key2", "stam2");
			ElementBoundary e1 = new ElementBoundary(
					null,
					"name",
					"type",
					true,
					null,
					new User(new UserId("2020a.nofar","yuval.a@dotncs.com")),
					null,
					new HashMap<String, Object>());
			
			this.restTemple
				.postForObject(
						this.baseUrl + "/collab/elements/{managerDomain}/{managerEmail}", 
						e1, 
						ElementBoundary.class,
						"2020a.nofar" ,
						"yuval@dotncs.com");
			
			// THEN the database contains the new Element
			assertThat(this.elementService.getAllElements())
				.isNotEmpty()
				.usingElementComparatorOnFields("type", "name")
				.containsExactly(e1);
		}
				
		
		//Ali:: test post with create method with two elements in db is success and the db contain the two elements with the new one 
		@Test
		public void testPostNewElementWithTwoElenemtInDbIsSuccess() throws Exception{
			// GIVEN the database with two elements

			this.elementService.create(
					"2020a.nofar",
					"yuval@dotncs.com",
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
					"yuval@dotncs.com",
					new ElementBoundary(
							null,
							"name",
							"type",
							true,
							null,
							new User(new UserId("2020a.nofar","yuval@dotncs.com")),
							null,
							new HashMap<String, Object>()));

			// WHEN I POST a new Element		
			ElementBoundary e1 = new ElementBoundary(
					null,
					"name",
					"type",
					true,
					null,
					new User(new UserId("2020a.nofar","yuval@dotncs.com")),
					null,
					new HashMap<String, Object>());

			e1 = this.restTemple
					.postForObject(
							this.baseUrl + "/collab/elements/{managerDomain}/{managerEmail}", 
							e1, 
							ElementBoundary.class,
							"2020a.nofar" ,
							"yuval@dotncs.com");

			// THEN the database contains the new Element
			assertThat(this.elementService.getSpecificElement("2020a.nofar", "yuval@dotncs.com", e1.getElementId().getDomain(), e1.getElementId().getId()))
			.isNotNull()
			.isEqualToComparingOnlyGivenFields(e1, "name", "type", "active");
			//.usingRecursiveComparison()
			//.isEqualTo(e1);
		}




		//Ali:: test put for update specific element in the db that contain 2 elemnents
		@Test
		public void testupdateElementIsSuccessfull() throws Exception{
			// GIVEN the database contains 2 elements

			ElementBoundary e1 = this.elementService.create(
					"2020a.nofar",
					"yuval@dotncs.com",
					new ElementBoundary(
							null,
							"name",
							"type",
							true,
							null,
							new User(new UserId("2020a.nofar","yuval@dotncs.com")),
							null,
							new HashMap<String, Object>()));

			ElementBoundary e2 = this.elementService.create(
					"2020a.nofar",
					"yuval@dotncs.com",
					new ElementBoundary(
							null,
							"name",
							"type",
							true,
							null,
							new User(new UserId("2020a.nofar","yuval@dotncs.com")),
							null,
							new HashMap<String, Object>()));
			
			// WHEN we PUT elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and we send an update element
			ElementBoundary newElement = new ElementBoundary(
					null,
					"name",
					"type",
					true,
					null,
					new User(new UserId("2020a.nofar","yuval@dotncs.com")),
					null,
					new HashMap<String, Object>());
			
			this.restTemple
			.put(
					this.baseUrl + "/collab/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}" 
					,newElement
					,"2020a.nofar"
					,"yuval@dotncs.com"
					,e2.getElementId().getDomain()
					,e2.getElementId().getId());

			System.err.println(this.elementService.getSpecificElement("2020a.nofar", "yuval@dotncs.com", e2.getElementId().getDomain(), e2.getElementId().getId()));

			// THEN the database is updated the element
			assertThat(this.elementService.getSpecificElement("2020a.nofar", "yuval@dotncs.com", e2.getElementId().getDomain(), e2.getElementId().getId()))
			.isNotNull()
			.isEqualToComparingOnlyGivenFields(newElement, "name", "type", "active");


		}

	
	
}