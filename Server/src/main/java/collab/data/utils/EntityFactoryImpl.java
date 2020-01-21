package collab.data.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import collab.data.ActionEntity;
import collab.data.ElementEntity;
import collab.data.UserEntity;
import collab.data.UserRole;

@Component
public class EntityFactoryImpl implements EntityFactory {

	@Override
	public UserEntity createNewUser(String userId, String username, String avatar, UserRole role) {
		return new UserEntity(userId, role, username, avatar);
	}

	@Override
	public ActionEntity createNewAction(String actionId, String elementId, String invokedBy, String actionType,
			Date creationTimestamp, Map<String, Object> actionAttributes) {
		return new ActionEntity(actionId, elementId, invokedBy, actionType, creationTimestamp, actionAttributes);
	}

	@Override
	public ElementEntity createNewElement(String elementId, String name, String type, boolean active,
			Date createdTimestamp, String createdBy, ElementEntity parentElement,
			HashMap<String, Object> elementAttributes) {
		return new ElementEntity(elementId, name, type, active, createdTimestamp, createdBy, parentElement,
				elementAttributes);
	}

	@Override
	public ElementEntity createNewElement(String elementId, String name, String type, boolean active,
			Date createdTimestamp, String createdBy, ElementEntity parentElement,
			Map<String, Object> elementAttributes) {
		return new ElementEntity(elementId, name, type, active, createdTimestamp, createdBy, parentElement,
				elementAttributes);
	}

}