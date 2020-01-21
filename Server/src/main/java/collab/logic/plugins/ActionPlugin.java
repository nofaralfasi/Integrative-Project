package collab.logic.plugins;

import collab.rest.boundaries.ActionBoundary;

public interface ActionPlugin {
	public Object manageActionType(ActionBoundary action);

}
