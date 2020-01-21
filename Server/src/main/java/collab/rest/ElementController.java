package collab.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import collab.logic.AdvancedElementsService;
import collab.rest.boundaries.ElementBoundary;

@RestController
public class ElementController {
	private AdvancedElementsService elementsService;
	
	@Autowired
	public ElementController(AdvancedElementsService elementsService) {
		this.elementsService = elementsService;
	}

	@RequestMapping(path = "/collab/elements/{managerDomain}/{managerEmail}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary createElement(@PathVariable("managerDomain") String managerDomain,
			@PathVariable("managerEmail") String managerEmail, @RequestBody @Validated ElementBoundary elementBoundary) {
		return this.elementsService.create(managerDomain, managerEmail, elementBoundary);
	}

	@RequestMapping(path = "/collab/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateElement(@PathVariable("managerDomain") String managerDomain,
			@PathVariable("managerEmail") String managerEmail, @PathVariable("elementDomain") String elementDomain,
			@PathVariable("elementId") String elementId, @RequestBody ElementBoundary updateElement) {
		this.elementsService.update(managerDomain, managerEmail, elementDomain, elementId, updateElement);
	}
	
	@RequestMapping(path = "/collab/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary getElement(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail, @PathVariable("elementDomain") String elementDomain,
			@PathVariable("elementId") String elementId) {
		return this.elementsService.getSpecificElement(userDomain, userEmail, elementDomain, elementId);
	}

	@RequestMapping(path = "/collab/elements/{userDomain}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElements(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name="size", required = false, defaultValue = "12") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		return this.elementsService.getAllElements(userDomain, userEmail, size, page).toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(path = "/collab/elements/{userDomain}/{userEmail}/byName/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElementsByName(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("name") String name,
			@RequestParam(name="size", required = false, defaultValue = "12") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		return this.elementsService.getAllElementsByName(userDomain, userEmail, name, size, page).toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(path = "/collab/elements/{userDomain}/{userEmail}/byType/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElementsByType(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("type") String type,
			@RequestParam(name="size", required = false, defaultValue = "12") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		return this.elementsService.getAllElementsByType(userDomain, userEmail, type, size, page).toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(path = "/collab/elements/{userDomain}/{userEmail}/byParent/{parentDomain}/{parentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElementsByParent(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("parentDomain") String parentDomain,
			@PathVariable("parentId") String parentId,
			@RequestParam(name="size", required = false, defaultValue = "12") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		return this.elementsService.getAllElementsByParentElement(userDomain, userEmail, parentDomain, parentId, size, page).toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(path = "/collab/elements/{userDomain}/{userEmail}/byNotType/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElementsByParent(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("type") String type,
			@RequestParam(name="size", required = false, defaultValue = "12") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		return this.elementsService.getAllElementsByTypeNot(userDomain, userEmail, type, size, page).toArray(new ElementBoundary[0]);
	}
	
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public Map<String, String> handleElementNotFoundException(NotFoundException e) {
		String message = e.getMessage();
		if (message == null) {
			message = "element could not be found";
		}
		Map<String, String> errorMessage = new HashMap<>();
		errorMessage.put("error", message);
		return errorMessage;
	}
	
}