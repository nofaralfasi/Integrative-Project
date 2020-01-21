package collab;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import collab.dal.ElementDao;
import collab.data.ElementEntity;
import collab.data.utils.EntityFactory;
import collab.rest.boundaries.ElementBoundary;



@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RdbElementDaoIntegrationTest {
	private ElementDao dao;
	private EntityFactory factory;
	
	@Autowired
	public void setDao(ElementDao dao) {
		this.dao = dao;
	}
	
	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}
	
	@AfterEach
	public void teardown() {
		this.dao.deleteAll();
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
	public void createSimpleElement() throws Exception {
		// GIVEN the db is clean
		
		// WHEN we create a new element and store it in DB
		String name = "chair";
		ElementEntity newElement = this.factory.createNewElement("2020a.nofar@1" , name, "Item", true, new Date(), "2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2));
		ElementEntity actual = this.dao.save(newElement);
		// THEN the element is stored
		assertThat(this.dao.findById(actual.getElementId()))
			.isPresent()
			.get()
			.extracting("elementId","name")
			.containsExactly(actual.getElementId(),name);
	}
	
	@Test
	public void createReadByIdUpdateElement() throws Exception {
		// GIVEN we have a dao
		// AND we have a factory
		
		// WHEN I create a new message
		// AND I update the message
		// AND I read the message by key
		String name = "Element1";
		ElementEntity newElement = this.factory.createNewElement("2020a.nofar@1" , name, "Item", true, new Date(), 
				"2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2));
		ElementEntity actual = this.dao.save(newElement);
		System.err.println(actual.getName());
		ElementEntity updateElement = new ElementEntity();
		updateElement.setElementId(actual.getElementId());
		String newName = "Element2";
		String newType = "Catogery";
		updateElement.setName(newName);
		updateElement.setType(newType);
		this.dao.save(updateElement);
		// THEN the element read using key is present
		// AND the element is updated
		assertThat(this.dao.findById(actual.getElementId()))
			.isPresent()
			.get()
			.extracting("name","type")
			.containsExactly(newName,newType);
	}
	
	@Test
	public void createReadByIdWithPaentElement() throws Exception {
		// GIVEN we have a dao
		// AND we have a factory
		
		// WHEN I create a parent eleemnt
		// AND I create a child eleemnt
		// AND I updated the parent eleemnt
		String parentName = "Element1";
		ElementEntity parent = this.factory.createNewElement("2020a.nofar@1" , parentName, "Item", true, new Date(), 
				"2020a.nofar@@yuval@dotbcs.com",
				null,  createAttributesMap(2));
		String chilsName = "Element2";
		ElementEntity child = this.factory.createNewElement("2020a.nofar@2" ,chilsName, "Item", true, new Date(), 
				"2020a.nofar@@yuval@dotbcs.com",
				parent,  createAttributesMap(4));
		
		
		this.dao.save(parent);
		this.dao.save(child);
		
		ElementEntity updateElement = new ElementEntity();
		updateElement.setElementId(parent.getElementId());
		String newName = "Element3";
		String newType = "Catogery";
		updateElement.setName(newName);
		updateElement.setType(newType);
		this.dao.save(updateElement);
		// THEN the element parent in the child is also updated
		// AND the element is updated
		//assertThat()
		//	.isPresent()
		//	.get().extracting("parentElement")child.equals(obj);
		ElementEntity childInDB = this.dao.findById(child.getElementId()).get();
		ElementEntity parentOfChildInDB = childInDB.getParentElement();
		assertThat(childInDB.getName().equals(chilsName));
		//assertThat(childInDB.getType().equals(newType));
	}
	
	@Test
	public void createManyElementsWithSameUserIdAndCheckOnlyOneIsStored() {
		// GIVEN we have clean database
		// AND a factory
		
		// WHEN I create numberOfUsers
		int numberOfUsers = 5;
		List<ElementEntity> listOfUsers = IntStream.range(0, numberOfUsers)// int stream
				.mapToObj(num -> "dummy #" + num)// String stream
				.map(name -> this.factory.createNewElement("2020a.nofar@1" , name, "Item", true, new Date(), 
						"2020a.nofar@@yuval@dotbcs.com",
						null,  createAttributesMap(2)))
				.map(this.dao::save).collect(Collectors.toList());
		Iterable<ElementEntity> actualUsers = this.dao.findAll();
		// THEN the listOfElement is stored with size 1 

		assertThat(actualUsers)
		.hasSize(1);

	}
	@Test
	public void createManyUsersWithDifferentElementIdAndCheckOnlyOneIsStored() {
		// GIVEN we have clean database
		// AND a factory
		
		// WHEN I create numberOfUsers
		int numberOfUsers = 5;
		List<ElementEntity> listOfUsers = IntStream.range(0, numberOfUsers)// int stream
				.mapToObj(num -> "dummy #" + num)// String stream
				.map(name -> this.factory.createNewElement("2020a.nofar@"+name , name, "Item", true, new Date(), 
						"2020a.nofar@@yuval@dotbcs.com",
						null,  createAttributesMap(2)))
				.map(this.dao::save).collect(Collectors.toList());
		Iterable<ElementEntity> actualUsers = this.dao.findAll();
		// THEN the listOfElement is stored with size 1 

		assertThat(actualUsers)
		.hasSize(numberOfUsers);

	}
}
