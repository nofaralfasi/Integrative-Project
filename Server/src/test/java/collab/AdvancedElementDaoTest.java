package collab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import collab.dal.ElementDao;
import collab.data.ElementEntity;
import collab.data.UserRole;
import collab.data.utils.EntityFactory;
import collab.logic.AdvancedElementsService;
import collab.logic.ElementConverter;
import collab.logic.UsersService;
import collab.rest.boundaries.ElementBoundary;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdvancedElementDaoTest {
	private ElementDao elementDao;
	private AdvancedElementsService advancedElementsService;
	private EntityFactory factory;
	private UsersService userService;

	@Autowired
	public void setDao (AdvancedElementsService advancedElementsService, ElementDao elementDao, UsersService usersService) {
		this.advancedElementsService = advancedElementsService;
		this.elementDao = elementDao;
		this.userService = usersService;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}
	
	@BeforeEach
	public void setUp() {
		UserBoundary playerUser = new UserBoundary(new UserId("2020a.nofar","yuval@dotbcs.com")
				, UserRole.PLAYER, "Yuval", ":)");
		UserBoundary managerUser = new UserBoundary(new UserId("2020a.nofar","manger@dotncs.com")
				, UserRole.MANAGER, "Yuval123", ":)");
		this.userService.create(playerUser);
		this.userService.create(managerUser);

	}
	
	@AfterEach
	public void teardown() {
		this.advancedElementsService.deleteAll();
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
		for(int i=0;i<numOfAttributes;i++) {
			moreAttributes.put(keysList.get(i),valuesList.get(i));
		}
	
		return moreAttributes;
		
	}
	
	@Test
	public void testReadAllWithPagination() throws Exception {
		// GIVEN the database contains only 20 elements
		IntStream.range(0, 20)
				.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i , "name "+i, "Item", true, new Date(), "2020a.nofar@@yuval@dotbcs.com",
						null,  createAttributesMap(2)))
				.forEach(this.elementDao::save);
		// WHEN I read 3 elements from page 6
		List<ElementBoundary> actual = this.advancedElementsService.getAllElements("2020a.nofar", "yuval@dotbcs.com", 3, 6);
		
		// THEN I receive 2 elements
		assertThat(actual).hasSize(2);
	}
	
	
	
	
	@Test
	public void testReadAllWithPaginationByNameManager() throws Exception{
		// GIVEN the database contains only 10 elements 5 with the same name
		String name = "Test";
		IntStream.range(0, 10)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i ,(i%2==0) ?name:""+i, "Item", true, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		// WHEN I read 5 elements from page 1 by name
		List<ElementBoundary> actual = this.advancedElementsService.getAllElementsByName("2020a.nofar", "manger@dotncs.com",name, 5, 0);
		// THEN I receive 5 elements
		assertThat(actual)
			.hasSize(5);
	}
	
	@Test
	public void testReadAllWithPaginationByManagerNotAllActive() throws Exception{
		// GIVEN the database contains only 10 elements 5 with the same name
		String name = "Test";
		IntStream.range(0, 10)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i ,name+i, "Item", (i%2==0)?true:false, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		// WHEN I read 10 elements from page 1 
		List<ElementBoundary> actual = this.advancedElementsService.getAllElements("2020a.nofar", "manger@dotncs.com", 10, 0);
		// THEN I receive all elements
		assertThat(actual)
			.hasSize(10);
	}
	
	@Test
	public void testReadAllWithPaginationByManagerAllElmentsNotActive() throws Exception{
		// GIVEN the database contains only 10 elements 5 with the same name
		String name = "Test";
		IntStream.range(0, 10)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i ,name+i, "Item", false, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		// WHEN I read 10 elements from page 1 
		List<ElementBoundary> actual = this.advancedElementsService.getAllElements("2020a.nofar", "manger@dotncs.com", 10, 0);
		// THEN I receive all elements
		assertThat(actual)
			.hasSize(10);
	}
	
	@Test
	public void testReadSpecicficWithPaginationByManagerAllElmentNotActive() throws Exception{
		// GIVEN the database contains only 10 elements 5 with the same name
		String name = "Test";
		IntStream.range(0, 10)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i ,name+i, "Item", false, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		// WHEN I read 10 elements from page 1 
		ElementBoundary actual = this.advancedElementsService.getSpecificElement("2020a.nofar", "manger@dotncs.com", "2020a.nofar", "1");
		
		// THEN I receive all elements
		assertThat(actual)
			.isNotNull();
	}
	
	@Test
	public void testReadAllWithPaginationByPlayerNotAllActive() throws Exception{
		// GIVEN the database contains only 10 elements 5 with the same name
		String name = "Test";
		IntStream.range(0, 10)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i ,name+i, "Item", (i%2==0)?true:false, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		// WHEN I read 10 elements from page 1 
		List<ElementBoundary> actual = this.advancedElementsService.getAllElements("2020a.nofar", "yuval@dotbcs.com", 10, 0);
		// THEN I receive 5 elements
		assertThat(actual)
			.hasSize(5);
	}
	
	@Test
	public void testReadAllWithPaginationByPlayerSpecificElementNotActive() throws Exception{
		// GIVEN the database contains only 10 elements 5 with the same name
		String name = "Test";
		IntStream.range(0, 10)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i ,name+i, "Item", (i%2==0)?true:false, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		// WHEN I read specific element not active
		// THEN I get exception
		assertThrows(Exception.class, ()->
		this.advancedElementsService.getSpecificElement("2020a.nofar", "yuval@dotbcs.com", "2020a.nofar", "3"));
	
	}
	
	@Test
	public void testGetElementsByType() throws Exception{
		String type = "Clothes";
		// GIVEN the database contains 20 elements 
		// AND the first two elements has the type Clothes
		IntStream.range(0,2)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i ,"Test #"+i, type, true, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		IntStream.range(2,2+18)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i ,"Test #"+i, "OtherType", true, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		// WHEN I read the first 3 elements by type
		List<ElementBoundary> result = this.advancedElementsService.getAllElementsByType("2020a.nofar", "manger@dotncs.com",type, 5, 0);
		
		// THEN we receive 2 elements 
		// AND The elements has "flight" as type
		assertThat(result)
			.hasSize(2);
		
		assertThat(result.get(0).getType())
			.contains(type);
		assertThat(result.get(1).getType())
		.contains(type);

	}
	
	@Test
	public void testGetElementsByTypeNotAllActivePlayer() throws Exception{
		String type = "Clothes";
		// GIVEN the database contains 20 elements 
		// AND the first two elements has the type Clothes
		IntStream.range(0,2)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i ,"Test #"+i, type, (i%2==0)?true:false, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		IntStream.range(2,2+18)
		.mapToObj(i -> this.factory.createNewElement("2020a.nofar@@"+i ,"Test #"+i, "OtherType", true, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		// WHEN I read the first 3 elements by type
		List<ElementBoundary> result = this.advancedElementsService.getAllElementsByType("2020a.nofar", "yuval@dotbcs.com",type, 5, 0);
		
		// THEN we receive 1 elements 
		// AND The elements has "Clothes" as type
		assertThat(result)
			.hasSize(1);
		
		assertThat(result.get(0).getType())
			.contains(type);
		assertThat(result.get(0).getName())
		.contains("Test #0");
		

	}
	
	@Test
	public void testReadElementsByNamePlayerNotAllActive() throws Exception{
		String name = "Test";
		// GIVEN the database contains 20 elements 
		// AND the first two elements has the name name test 
		IntStream.range(0,2)
		.mapToObj(i ->  this.factory.createNewElement("2020a.nofar@@"+i ,name, "OtherType", (i%2==0)?true:false, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		IntStream.range(2,2+18)
		.mapToObj(i ->  this.factory.createNewElement("2020a.nofar@@"+i ,"Test #"+i, "OtherType", true, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		// WHEN I read the first 3 elements by name
		List<ElementBoundary> result = this.advancedElementsService.getAllElementsByName("2020a.nofar", "yuval@dotbcs.com",name, 3, 0);
		System.err.println(result.size());
		// THEN we receive 1 elements 
		// AND The elements has the name name test in them
		assertThat(result)
			.hasSize(1);
		
		assertThat(result.get(0).getName())
			.contains(name);
		
	}
	
	@Test
	public void testReadElementsByParent() throws Exception{
		String name = "name Test";
		// GIVEN the database contains 10 elements 
		// AND the first element is the parent of the rest
		ElementEntity parent = this.factory.createNewElement("2020a.nofar@@0" ,name, "OtherType", true, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2));
		this.elementDao.save(parent);
		
		
		IntStream.range(1,10)
		.mapToObj(i ->  this.factory.createNewElement("2020a.nofar@@"+i ,"Test #"+i, "OtherType", true, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				parent,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		// WHEN I read the first 9 elements by parent
		List<ElementBoundary> result = this.advancedElementsService.getAllElementsByParentElement("2020a.nofar", "yuval@dotbcs.com", "2020a.nofar", "0", 9, 0);
		System.err.println(result.size());
		// THEN we receive 1 elements 

		assertThat(result)
			.hasSize(9);
		
		
	}
	
	@Test
	@Disabled
	public void testReadElementsByParentWithPaginationAndNotAllActive() throws Exception{
		String name = "name Test";
		// GIVEN the database contains 10 elements 
		// AND the first element is the parent of the rest
		ElementEntity parent = this.factory.createNewElement("2020a.nofar@@0" ,name, "OtherType", true, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2));
		this.elementDao.save(parent);
		
		
		IntStream.range(1,10)
		.mapToObj(i ->  this.factory.createNewElement("2020a.nofar@@"+i ,"Test #"+i, "OtherType", (i%2==0)?true:false, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				parent,  createAttributesMap(2)))
		.forEach(this.elementDao::save);
		
		// WHEN I read the first 2 elements by parent on page 2
		List<ElementBoundary> result = this.advancedElementsService.getAllElementsByParentElement("2020a.nofar", "yuval@dotbcs.com", "2020a.nofar", "0", 2, 2);
		System.err.println(result.size());
		// THEN we receive 2 elements 

		assertThat(result)
			.hasSize(2);
		
		
		
	}
	
}
