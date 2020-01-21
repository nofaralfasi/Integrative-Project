package collab;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import collab.dal.ActionDao;
import collab.data.ActionEntity;
import collab.data.utils.EntityFactory;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.Element;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.User;
import collab.rest.boundaries.UserId;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RdbActionDaoIntegrationTests {

	private ActionDao actionDao;
	private EntityFactory factory;


	@Autowired
	public void setActionDao(ActionDao actionDao) {
		this.actionDao = actionDao;
	}


	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}
	
	@AfterEach
	public void teardown() {
		this.actionDao.deleteAll();
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
	public void createManyActionsWithDifferentActionId() throws Exception {

		// GIVEN the database is clean

		// WHEN we create multiple actions and store it in DB
		int numberOfActions = 2;
		int numberOfAdditionalAttributes = 3;
		
		List<ActionEntity> listOfActions = IntStream.range(0, numberOfActions)// int stream
				.mapToObj(num -> this.factory.createNewAction("2020a.nofar@" + num, "2020a.nofar@"+num, "2020a.nofar@@yuval@dot.com", 
						"actionType",new Date(), createAttributesMap(numberOfAdditionalAttributes)))// ActionEntity
				.map(action -> this.actionDao.save(action)) // ActionEntity map
				.collect(Collectors.toList());

		// THEN the actions are stored
		assertThat(this.actionDao.findAll())
		.hasSize(numberOfActions)
		.usingElementComparatorOnFields("actionId")
		.containsAll(listOfActions);

	}
	
	
	@Test
	public void testCreateDeleteAllReadAll() throws Exception {
	
		// GIVEN we have a clear database
		// AND we have a factory

		// WHEN I create an action
		// AND I delete all
		// AND I read all
		ActionEntity actionEntity = this.factory.createNewAction("2020a.nofar@1", "2020a.nofar@1", "2020a.nofar$yuval@dot.com", 
				"actionType",new Date(), createAttributesMap(1));
		actionDao.save(actionEntity);
		Iterable<ActionEntity> dbAfterCreate = actionDao.findAll();
		String actionIdOfCreated = dbAfterCreate.iterator().next().getActionId();
		this.actionDao.deleteAll();
		Iterable<ActionEntity> dbAfterDelete = actionDao.findAll();

		// THEN dbAfterCreate contains the action
		// AND dbAfterDelete is empty
		assertThat(dbAfterCreate).extracting("actionId").containsExactly(actionIdOfCreated);

		assertThat(dbAfterDelete).isEmpty();
	}
	
}

