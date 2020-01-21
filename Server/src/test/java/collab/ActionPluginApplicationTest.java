package collab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import collab.dal.ActionDao;
import collab.data.ElementEntity;
import collab.data.UserRole;
import collab.data.utils.EntityFactory;
import collab.logic.ActionConverter;
import collab.logic.ActionsService;
import collab.logic.AdvancedElementsService;
import collab.logic.UserServiceRdb;
import collab.logic.UsersService;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.ElementBoundary;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActionPluginApplicationTest 
{
	private AdvancedElementsService elementService;
	private ActionsService actionService;
	private ActionConverter actionConvertor;
	private EntityFactory factory;
	private UsersService userService;
	
	@Autowired
	public ActionPluginApplicationTest(UsersService userService, AdvancedElementsService elementService,ActionsService actionDao, ActionConverter actionConvertor,  EntityFactory entityFactory) {
		this.factory = entityFactory;
		this.actionConvertor = actionConvertor;
		this.elementService = elementService;
		this.actionService = actionDao;
		this.userService = userService;
		// TODO Auto-generated constructor stub
	}
	
	
	
	@AfterEach
	public void teardown() {
		this.actionService.deleteAll();
		this.elementService.deleteAll();
		this.userService.deleteAll();
	}
	
	
	@BeforeEach
	public void setUp() {
		this.userService.deleteAll();
		UserBoundary playerUser = new UserBoundary(new UserId("2020a.nofar","player@dotbcs.com")
				, UserRole.PLAYER, "Yuval", ":)");
		UserBoundary player1User = new UserBoundary(new UserId("2020a.nofar","player1@dotbcs.com")
				, UserRole.PLAYER, "Yuval", ":)");
		UserBoundary managerUser = new UserBoundary(new UserId("2020a.nofar","manger@dotbcs.com")
				, UserRole.MANAGER, "Yuval123", ":)");
		UserBoundary manager1User = new UserBoundary(new UserId("2020a.nofar","manager@gmail.com")
				, UserRole.MANAGER, "Yuval123", ":)");
		this.userService.create(playerUser);
		this.userService.create(player1User);
		this.userService.create(managerUser);
		this.userService.create(manager1User);
		//this.userService.create(manager1User);
		String domain = "2020a.nofar";
		

		// Element 1 -Cart
		ElementBoundary newCartElement = new ElementBoundary();
		newCartElement.setName("2020a.nofar@@player@dotbcs.com");
		newCartElement.setType("cartType");
		newCartElement.setActive(true);
		Map<String, Object> moreAttributes = new HashMap<String, Object>();
		moreAttributes.put("totalPrice", 0);
		moreAttributes.put("numberOfItems", 0);
		newCartElement.setElementAttributes(moreAttributes);
		this.elementService.create(domain, "manger@dotbcs.com", newCartElement);
		
		

		// Element 2 - item1
		ElementBoundary newItem1Element = new ElementBoundary();
		newItem1Element.setName("item1");
		newItem1Element.setType("itemType");
		newItem1Element.setActive(true);
		Map<String, Object> item1Attributes = new HashMap<String, Object>();
		item1Attributes.put("sku", "sku1");
		item1Attributes.put("price", 51);
		item1Attributes.put("size", "small");
		item1Attributes.put("color", "black");
		item1Attributes.put("picture", "Images/home/product1.jpg");
		item1Attributes.put("tags", "tags1");
		item1Attributes.put("description", "description1");
		item1Attributes.put("category", "category1");
		newItem1Element.setElementAttributes(item1Attributes);
		this.elementService.create(domain, "manger@dotbcs.com", newItem1Element);
		

		// Element 3 - item2
		ElementBoundary newItem2Element = new ElementBoundary();
		newItem2Element.setName("item2");
		newItem2Element.setActive(true);
		newItem2Element.setType("itemType");
		Map<String, Object> item2Attributes = new HashMap<String, Object>();
		item2Attributes.put("sku", "sku2");
		item2Attributes.put("price", 52);
		item2Attributes.put("size", "small");
		item2Attributes.put("color", "pink");
		item2Attributes.put("picture", "Images/home/product2.jpg");
		item2Attributes.put("tags", "tags2");
		item2Attributes.put("description", "description2");
		item2Attributes.put("category", "category2");
		newItem2Element.setElementAttributes(item2Attributes);
		this.elementService.create(domain, "manger@dotbcs.com", newItem2Element);

		// Element 3 - item3
		ElementBoundary newItem3Element = new ElementBoundary();
		newItem3Element.setName("item3");
		newItem3Element.setType("itemType");
		newItem3Element.setActive(true);
		Map<String, Object> item3Attributes = new HashMap<String, Object>();
		item3Attributes.put("sku", "sku3");
		item3Attributes.put("price", 53);
		item3Attributes.put("size", "small");
		item3Attributes.put("color", "yellow");
		item3Attributes.put("picture", "Images/home/product3.jpg");
		item3Attributes.put("tags", "tags3");
		item3Attributes.put("description", "description3");
		item3Attributes.put("category", "category3");
		newItem3Element.setElementAttributes(item3Attributes);
		this.elementService.create(domain, "manger@dotbcs.com", newItem3Element);
		
		
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
		for(int i=0;i<numOfAttributes;i++) {
			moreAttributes.put(keysList.get(i),valuesList.get(i));
		}
	
		return moreAttributes;
		
	}
	
	@Test
	public void testInvokeActionAddToCartExistingCart() throws Exception {
		
		// GIVEN the database has an 2 players and 1 player have cart
		List<ElementBoundary> itemInDB = this.elementService.getAllElementsByType("2020a.nofar", "player@dotbcs.com", "itemType", 10, 0);
		String elementIdNum = itemInDB.get(0).getElementId().getId();
		// WHEN I post action addToCart to cart exist
		
		IntStream.range(0, 1)
		.mapToObj(i -> this.actionConvertor.fromEntity(this.factory.createNewAction("2020a.nofar@@1", "2020a.nofar@@"+elementIdNum, "2020a.nofar@@player@dotbcs.com", 
				"addToCart", new Date(), createAttributesMap(2))))
		.forEach(this.actionService::invoke);
		
		
		// THEN the cart element have 1 item with total price as element AND element have cart as parent
		List<ElementBoundary> cart = this.elementService.getAllElementsByName("2020a.nofar", "player1@dotbcs.com", "2020a.nofar@@player@dotbcs.com", 2, 0);
		ElementBoundary itemInCart = this.elementService.getSpecificElement("2020a.nofar", "player@dotbcs.com", "2020a.nofar", elementIdNum);
		assertThat(cart).hasSize(1);
		assertEquals((int)cart.get(0).getElementAttributes().get("totalPrice"),(int)itemInCart.getElementAttributes().get("price"));
		
		assertEquals((int)cart.get(0).getElementAttributes().get("numberOfItems"), 1);
		assertEquals(itemInCart.getParentElement().getElementId().getId(),cart.get(0).getElementId().getId());
		
	}
	
	@Test
	public void testInvokeActionAddToCartNotExistingCart() throws Exception {
		List<ElementBoundary> itemInDB = this.elementService.getAllElementsByType("2020a.nofar", "player@dotbcs.com", "itemType", 10, 0);
		String elementIdNum = itemInDB.get(0).getElementId().getId();
		String nameOfUserWithoutCart = "2020a.nofar@@player1@dotbcs.com";
		// GIVEN the database has an 2 players and 1 player have cart
		
		// WHEN I post action addToCart to user with no cart
		IntStream.range(0, 1)
		.mapToObj(i -> this.actionConvertor.fromEntity(this.factory.createNewAction("2020a.nofar@@1", "2020a.nofar@@"+elementIdNum, "2020a.nofar@@player1@dotbcs.com", 
				"addToCart", new Date(), createAttributesMap(2))))
		.forEach(this.actionService::invoke);
		
		
		// THEN was created a cart with the name of the user
		List<ElementBoundary> newCart = this.elementService.getAllElementsByName("2020a.nofar", "player1@dotbcs.com", nameOfUserWithoutCart, 2, 0);
		ElementBoundary itemInCart = this.elementService.getSpecificElement("2020a.nofar", "player@dotbcs.com", "2020a.nofar", elementIdNum);
		
		assertThat(newCart).hasSize(1);
		
		assertEquals((int)newCart.get(0).getElementAttributes().get("totalPrice"),(int)itemInCart.getElementAttributes().get("price"));
		
		assertEquals((int)newCart.get(0).getElementAttributes().get("numberOfItems"), 1);
		assertEquals(itemInCart.getParentElement().getElementId().getId(),newCart.get(0).getElementId().getId());
		
	}
	
	@Test
	public void testInvokeActionAddToCartNotExistingCartAndThanByTheCart() throws Exception {
		List<ElementBoundary> itemInDB = this.elementService.getAllElementsByType("2020a.nofar", "player1@dotbcs.com", "itemType", 10, 0);
		String elementIdNum = itemInDB.get(0).getElementId().getId();
		String nameOfUserWithoutCart = "2020a.nofar@@player1@dotbcs.com";
		// GIVEN the database has an 2 players and 1 player have cart
		
		// WHEN I post action addToCart to user with no cart
				IntStream.range(0, 1)
				.mapToObj(i -> this.actionConvertor.fromEntity(this.factory.createNewAction("2020a.nofar@@1", "2020a.nofar@@"+elementIdNum, "2020a.nofar@@player1@dotbcs.com", 
						"addToCart", new Date(), createAttributesMap(2))))
				.forEach(this.actionService::invoke);
		
		
		//THEN when I Post Buy Action
		// THEN was the cart have no items and the item is not active
		List<ElementBoundary> newCart = this.elementService.getAllElementsByName("2020a.nofar", "player1@dotbcs.com", nameOfUserWithoutCart, 2, 0);
		ElementBoundary itemInCart = this.elementService.getSpecificElement("2020a.nofar", "player1@dotbcs.com", "2020a.nofar",elementIdNum);
		
		this.actionService.invoke(this.actionConvertor.fromEntity(this.factory.createNewAction("2020a.nofar@@2", "2020a.nofar@@"+newCart.get(0).getElementId().getId(), "2020a.nofar@@player1@dotbcs.com", 
				"buy", new Date(), createAttributesMap(2))));
		
		newCart = this.elementService.getAllElementsByName("2020a.nofar", "player1@dotbcs.com", nameOfUserWithoutCart, 2, 0);
		assertThat(newCart).hasSize(1);
		
		assertEquals((int)newCart.get(0).getElementAttributes().get("totalPrice"),0);
		
		assertEquals((int)newCart.get(0).getElementAttributes().get("numberOfItems"), 0);
		
		//Throws beacuae get with player will not find anything - element after but is not active
		assertThrows(Exception.class, ()->this.elementService.getSpecificElement("2020a.nofar", "player1@dotbcs.com", "2020a.nofar", elementIdNum));
		
	}
	
	
	
}
