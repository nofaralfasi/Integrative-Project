package collab.logic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import collab.rest.boundaries.ActionId;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.User;
import collab.rest.boundaries.UserId;

@Component
public class GeneralConverter {

	@Value("${collab.config.id.delimiter}")
	private String delimiter;

	public GeneralConverter() {}

	// USER
	public String toStringUser(User user) {
		return this.toStringUserId(user.getUserId());
	}

	public String toStringUserId(UserId userId) {
		return userId.getDomain() + delimiter + userId.getEmail();
	}

	public UserId fromStringUserId(String userId) {
		return new UserId(userId.split(delimiter)[0], userId.split(delimiter)[1]);
	}
	
	// ELEMENT
	public String toStringElementId(ElementId elementId) {
		return elementId.getDomain() + delimiter + elementId.getId();
	}

	public ElementId fromStringElementId(String elementId) {
		return new ElementId(elementId.split(delimiter)[0], elementId.split(delimiter)[1]);
	}

	// ACTION
	public String toStringActionId(ActionId actionId) {
		return actionId.getDomain() + delimiter + actionId.getId();
	}

	public ActionId fromStringActionId(String actionId) {
		return new ActionId(actionId.split(delimiter)[0], actionId.split(delimiter)[1]);
	}

}
