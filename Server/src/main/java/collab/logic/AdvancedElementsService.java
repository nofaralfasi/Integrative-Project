package collab.logic;

import java.util.List;

import collab.rest.boundaries.ElementBoundary;

public interface AdvancedElementsService extends ElementsService {
	public List<ElementBoundary> getAllElements(String userDomain, String userEmail, int size, int page);
	public List<ElementBoundary> getAllElementsByName(String userDomain, String userEmail, String name, int size, int page);
	public List<ElementBoundary> getAllElementsByType(String userDomain, String userEmail, String type, int size, int page);
	public List<ElementBoundary> getAllElementsByParentElement(String userDomain, String userEmail, String parentDomain, String parentId, int size, int page);
	public List<ElementBoundary> getAllElementsByTypeNot(String userDomain, String userEmail, String type, int size, int page);
}