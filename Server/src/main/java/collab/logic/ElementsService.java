package collab.logic;

import java.util.List;

import collab.rest.boundaries.ElementBoundary;

public interface ElementsService {
	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary element);
	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId, ElementBoundary update);	
	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain, String elementId);
	public List<ElementBoundary> getAllElements(String userDomain, String userEmail);
	public List<ElementBoundary> getAllElements();
	public void deleteAll();
}
