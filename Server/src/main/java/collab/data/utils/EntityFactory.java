package collab.data.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import collab.data.ActionEntity;
import collab.data.ElementEntity;
import collab.data.UserEntity;
import collab.data.UserRole;

public interface EntityFactory {
	public UserEntity createNewUser(String userId, String username, String avatar, UserRole role);

	public ElementEntity createNewElement(String elementId, String name, String type, boolean active,
			java.util.Date createdTimestamp, String createdBy, ElementEntity parentElement,
			Map<String, Object> elementAttributes);

	public ActionEntity createNewAction(String actionId, String elementId, String invokedBy, String actionType,
			java.util.Date createdTimestamp, Map<String, Object> actionAttributes);

	public ElementEntity createNewElement(String elementId, String name, String type, boolean active,
			Date createdTimestamp, String createdBy, ElementEntity parentElement,
			HashMap<String, Object> elementAttributes);

}