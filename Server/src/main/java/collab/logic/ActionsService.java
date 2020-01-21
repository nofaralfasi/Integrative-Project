package collab.logic;

import java.util.List;

import collab.rest.boundaries.ActionBoundary;

public interface ActionsService {
	public Object invoke(ActionBoundary newAction);
	public List<ActionBoundary> getAllActions();
	public void deleteAll();
}
