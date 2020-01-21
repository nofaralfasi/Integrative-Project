package collab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import collab.dal.ActionDao;
import collab.dal.ElementDao;
import collab.dal.UserDao;
import collab.data.ActionEntity;
import collab.data.UserRole;
import collab.data.utils.EntityFactory;
import collab.logic.ActionConverter;
import collab.logic.ActionsService;
import collab.logic.UsersService;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.ActionId;
import collab.rest.boundaries.Element;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.User;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActionControllerApplicationIntegrationTests {
	private int port;
	private String baseUrl;
	private RestTemplate restTemplate;
	private ActionDao actionDao;
	private ActionsService actionService;
	private EntityFactory factory;
	private UsersService userService;
	private ActionConverter actionConvertor;
	private ElementDao elementDao;
	@Autowired
	public void setActionDao(ActionDao actionDao, ElementDao elementDao) {
		this.actionDao = actionDao;
		this.elementDao = elementDao;
	}

	@Autowired
	public void setActioService(ActionsService actionService, UsersService userService, ActionConverter actionConvertor) {
		this.actionService = actionService;
		this.userService = userService;
		this.actionConvertor = actionConvertor;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}


	@PostConstruct
	public void init() {
		this.baseUrl = "http://localhost:" + port + "/collab/actions";
		this.restTemplate = new RestTemplate();
	}
	
	@BeforeEach
	public void setUp() {
		UserBoundary playerUser = new UserBoundary(new UserId("2020a.nofar","player@dotbcs.com")
				, UserRole.PLAYER, "Yuval", ":)");
		UserBoundary managerUser = new UserBoundary(new UserId("2020a.nofar","manger@dotbcs.com")
				, UserRole.MANAGER, "Yuval123", ":)");
		UserBoundary managerUserInSystem = new UserBoundary(new UserId("2020a.nofar","manager@gmail.com")
				, UserRole.MANAGER, "Yuval123", ":)");
		
		this.userService.create(playerUser);
		this.userService.create(managerUser);
		this.userService.create(managerUserInSystem);
		
	}

	@AfterEach
	public void tearDown() {
		this.actionService.deleteAll();
		this.userService.deleteAll();
		
	}
	public Map<String, Object> createAttributesMap(int numOfAttributes) {
		
		Map<String, Object> moreAttributes = new HashMap<String, Object>();
		//keys list
		List<String> keysList=IntStream.range(0, numOfAttributes)
				.mapToObj(num-> "key_"+num)
				.collect(Collectors.toList());
		//values list
		List<Object> valuesList=IntStream.range(0, numOfAttributes)
				.mapToObj(num-> "value_"+num)
				.collect(Collectors.toList());
		//map
		for(int i=0;i<numOfAttributes-1;i++) {
			moreAttributes.put(keysList.get(i),valuesList.get(i));
		}
		moreAttributes.put("inCart", false);
		return moreAttributes;
		
	}
	

	@Test
	public void testInvokeActionsWithusersAndElements() throws Exception {
		
		// GIVEN the database has an player and 20 elements all active
		IntStream.range(0, 20)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i , "name "+i, "Item", true, new Date(), "2020a.nofar@@player@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		String playerEmail = "player@dotbcs.com";
		String playerDomain = "2020a.nofar";
		
		// WHEN I post 1 action as an player
		IntStream.range(0, 1)
		.mapToObj(i -> this.actionConvertor.fromEntity(this.factory.createNewAction("2020a.nofar@@"+i, "2020a.nofar@@1", "2020a.nofar@@player@dotbcs.com", 
				"addToCart", new Date(), createAttributesMap(2))))
		.forEach(this.actionService::invoke);
		
		List<ActionBoundary> actions =  this.actionService.getAllActions();
		// THEN the database invoked 1 action
		
		assertThat(actions).hasSize(1);

		
	}
	
	@Test
	public void testInvokeActionsWithusersAndElementsNotActive() throws Exception {

		// GIVEN the database has an player and 20 elements not active
		IntStream.range(0, 20)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i , "name "+i, "Item", false, new Date(), "2020a.nofar@@player@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		String playerEmail = "player@dotbcs.com";
		String playerDomain = "2020a.nofar";
		
		// WHEN I post 10 actions as an player
		// THEN I get exception
		
		assertThrows(Exception.class, ()->
		IntStream.range(0, 10)
		.mapToObj(i -> this.actionConvertor.fromEntity(this.factory.createNewAction("2020a.nofar@@"+i, "2020a.nofar@@1", "2020a.nofar@@player@dotbcs.com", 
				"type", new Date(), createAttributesMap(2))))
		.forEach(this.actionService::invoke));

		
	}
	
	@Test
	public void testManagerInvokeActionsWithElementsActive() throws Exception {
		// GIVEN the database has an player and 20 elements

		IntStream.range(0, 20)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i , "name "+i, "Item", true, new Date(), "2020a.nofar@@player@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		
		// WHEN I try post 10 actions as an manger
		// THEN  I got exception
		
		assertThrows(Exception.class, ()->
		IntStream.range(0, 10)
		.mapToObj(i -> this.actionConvertor.fromEntity(this.factory.createNewAction("2020a.nofar@@"+i, "2020a.nofar@@1", "2020a.nofar@@manger@dotbcs.com", 
				"type", new Date(), createAttributesMap(2))))
		.forEach(this.actionService::invoke));

		
	}
	
	@Test
	public void testPlayerInvokeActionsWithElementsThatNotInDB() throws Exception {
		// GIVEN the database has an player and 20 elements
		IntStream.range(0, 20)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i , "name "+i, "Item", true, new Date(), "2020a.nofar@@player@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		
		
		String playerEmail = "player@dotbcs.com";
		String playerDomain = "2020a.nofar";
		
	
		// WHEN I post 10 actions as an player with element not in DB
		
		//Then I get exception
		
		assertThrows(Exception.class, ()->
		IntStream.range(0, 10)
		.mapToObj(i -> this.actionConvertor.fromEntity(this.factory.createNewAction("2020a.nofar@@"+i, "2020a.nofar@@300", "2020a.nofar@@player@dotbcs.com", 
				"type", new Date(), createAttributesMap(2))))
		.forEach(this.actionService::invoke));


		
	}
	
	@Test
	public void testPlayerInvokeActionsWithUserThatNotInDB() throws Exception {
		// GIVEN the database has an player and 20 elements
		IntStream.range(0, 20)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i , "name "+i, "Item", true, new Date(), "2020a.nofar@@player@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		
	
		// WHEN I post 10 actions as an player with user not in DB
		
		//Then I get exception
		
		assertThrows(Exception.class, ()->
		IntStream.range(0, 10)
		.mapToObj(i -> this.actionConvertor.fromEntity(this.factory.createNewAction("2020a.nofar@@"+i, "2020a.nofar@@2", "2020a.nofar@@player1@dotbcs.com", 
				"type", new Date(), createAttributesMap(2))))
		.forEach(this.actionService::invoke));


		
	}


}