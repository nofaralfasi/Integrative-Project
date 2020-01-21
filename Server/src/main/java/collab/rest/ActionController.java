package collab.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import collab.logic.ActionsService;
import collab.rest.boundaries.ActionBoundary;

@RestController
public class ActionController {
	private ActionsService actionsServices;
	
	@Autowired
	public ActionController(ActionsService actionsServices) {
		this.actionsServices = actionsServices;
	}

	@RequestMapping(path = "/collab/actions", method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public Object invokeAction(@RequestBody @Validated ActionBoundary newAction) {	
		return this.actionsServices.invoke(newAction);
	}
}
