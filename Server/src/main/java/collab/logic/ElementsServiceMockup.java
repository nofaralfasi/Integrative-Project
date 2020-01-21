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

import collab.rest.boundaries.Element;
import collab.rest.boundaries.ElementBoundary;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.User;
import collab.rest.boundaries.UserId;

//@Service
public class ElementsServiceMockup implements ElementsService {
	private Map<String, ElementBoundary> db;
	private String domain;
	private AtomicLong id;
	@Value("${collab.config.id.delimiter}")
	private String delimiter;

	public ElementsServiceMockup() {
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
		System.err.println("element logic is deleted");
	}

	@Override
	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary element) {
		if (element.getName().isEmpty())
			throw new RuntimeException("Element name is null!");
		if (element.getType().isEmpty())
			throw new RuntimeException("Element type is null!");

		element.setCreatedBy(new User(new UserId(managerDomain, managerEmail)));

		if (element.getActive() != null)
			element.setActive(element.getActive());
		else
			element.setActive(true);

		element.setCreatedTimestamp(new Date());
		element.setElementId(new ElementId(this.domain, "" + this.id.getAndIncrement()));
		if (element.getElementAttributes() != null)
			element.setElementAttributes(element.getElementAttributes());

		if (element.getParentElement() != null) {
			ElementId peid = new ElementId(element.getParentElement().getElementId().getDomain(),
					element.getParentElement().getElementId().getId());
			getElementById(this.generateStringElementId(peid));
			element.setParentElement(new Element(peid));
		}

		this.db.put(this.generateStringElementId(element.getElementId()), element);

		return element;
	}

	@Override
	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId,
			ElementBoundary update) {

		String stringElementId = this.generateStringElementId(new ElementId(elementDomain, elementId));
		ElementBoundary existingElement = this.getElementById(stringElementId);

		boolean dirtyFlag = false;
		if (update.getType() != null) {
			existingElement.setType(update.getType());
			dirtyFlag = true;
		}
		if (update.getName() != null) {
			existingElement.setName(update.getName());
			dirtyFlag = true;
		}
		if (update.getActive() != null) {
			existingElement.setActive(update.getActive());
			dirtyFlag = true;
		}
		if (update.getParentElement() != null) {
			if (!this.domain.equals(update.getParentElement().getElementId().getDomain()))
				throw new RuntimeException("Invalid ParentElement domain!");
			if (Integer.parseInt(update.getParentElement().getElementId().getId()) > this.id.intValue())
				throw new RuntimeException("Invalid ParentElement Id!");
			existingElement.setParentElement(update.getParentElement());
			dirtyFlag = true;
		}
		if (update.getElementAttributes() != null) {
			existingElement.setElementAttributes(update.getElementAttributes());
			dirtyFlag = true;
		}

		// skipped elementId, createdTimestamp & userId - they can't be updated
		System.err.println("updated Element: " + existingElement); // print to console

		if (dirtyFlag) {
			this.db.put(stringElementId, existingElement);
		}

		return update;
	}

	@Override
	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain,
			String elementId) {

		String stringElementId = this.generateStringElementId(new ElementId(elementDomain, elementId));

		return this.getElementById(stringElementId);
	}

	@Override
	public List<ElementBoundary> getAllElements(String userDomain, String userEmail) {
		return new LinkedList<>(this.db.values());
	}

	@Override
	public List<ElementBoundary> getAllElements() {
		return new LinkedList<>(this.db.values());
	}

	@Override
	public void deleteAll() {
		this.db.clear();
	}

	public String generateStringElementId(ElementId elementId) {
		return elementId.getDomain() + delimiter + elementId.getId();
	}

	public ElementBoundary getElementById(String stringElementId) {
		ElementBoundary rv = this.db.get(stringElementId);
		if (rv == null) {
			throw new RuntimeException("no element could be found with id: " + stringElementId);
		}
		return rv;
	}

}
