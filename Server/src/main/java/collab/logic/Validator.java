package collab.logic;

import org.springframework.stereotype.Component;

import collab.data.UserRole;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.Element;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@Component
public class Validator {

	public boolean validateUserBoundary(UserBoundary user) {
		this.validateUserId(user.getUserId());
		return true;
	}

	public boolean validateUserId(UserId userId) {
		if (userId == null || userId.getDomain().isEmpty() || userId.getEmail().isEmpty())
			throw new RuntimeException("User Id is null!");
		return true;
	}

	public boolean validateElement(Element element) {
		if (element == null || element.getElementId().getId().isEmpty() || element.getElementId().getDomain().isEmpty())
			return false;
		return true;
	}

	public boolean validateActionBoundary(ActionBoundary actionBoundary) {
		this.validateUserId(actionBoundary.getInvokedBy().getUserId());
		return this.validateElement(actionBoundary.getElement());
	}

	public boolean isManager(UserBoundary user) {
		if (user.getRole() != UserRole.MANAGER)
			throw new RuntimeException("The user is not a manager, therefore he cannot create an element");
		return true;
	}

	public boolean isPlayer(UserBoundary user) {
		if (user.getRole() != UserRole.PLAYER)
			return false;
		return true;
	}
}
