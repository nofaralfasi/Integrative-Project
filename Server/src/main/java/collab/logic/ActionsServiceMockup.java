package collab.logic;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;

import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.ActionId;
import collab.rest.boundaries.Element;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.User;
import collab.rest.boundaries.UserId;

//@Service
public class ActionsServiceMockup implements ActionsService {
	private Map<String, ActionBoundary> db;
	private String domain;
	private AtomicLong id;
	@Value("${collab.config.id.delimiter}")
	private String delimiter;

	public ActionsServiceMockup() {
	}

	@Value("${collab.config.domain:defaultDomain}")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	@PostConstruct
	public void init() {
		this.db = Collections.synchronizedMap(new TreeMap<>());
		this.id = new AtomicLong(1L);
	}

	@PreDestroy
	public void cleanup() {
		System.err.println("action logic is deleted");
	}

	public String getDomain() {
		return domain;
	}

	@Override
	public Object invoke(ActionBoundary newAction) {

		if (newAction.getType().isEmpty())
			throw new RuntimeException("Action type is null!");
		if (newAction.getInvokedBy() == null || newAction.getInvokedBy().getUserId().getDomain().isEmpty())
			throw new RuntimeException("User Id is null!");
		if (newAction.getElement() == null || newAction.getElement().getElementId().getDomain().isEmpty()
				|| newAction.getElement().getElementId().getId().isEmpty())
			throw new RuntimeException("Element Id is nulls!");

		if (newAction.getActionAttributes() != null)
			newAction.setActionAttributes(newAction.getActionAttributes());

		newAction.setActionId(new ActionId(this.domain, "" + this.id.getAndIncrement()));
		newAction.setCreatedTimestamp(new Date());

		User invokedBy = new User(new UserId(newAction.getInvokedBy().getUserId().getDomain(),
				newAction.getInvokedBy().getUserId().getEmail()));
		newAction.setInvokedBy(invokedBy);

		Element element = new Element(new ElementId(newAction.getElement().getElementId().getDomain(),
				newAction.getElement().getElementId().getId()));
		newAction.setElement(element);

		this.db.put(this.generateStringActionId(newAction.getActionId()), newAction);
		return newAction;
	}

	@Override
	public List<ActionBoundary> getAllActions() {
		return new LinkedList<>(this.db.values());
	}

	@Override
	public void deleteAll() {
		this.db.clear();
	}

	public String generateStringActionId(ActionId actionId) {
		return actionId.getDomain() + delimiter + actionId.getId();
	}

}
