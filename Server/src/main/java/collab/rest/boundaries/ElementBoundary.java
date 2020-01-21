package collab.rest.boundaries;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

public class ElementBoundary {
	private ElementId elementId;
	@NotEmpty(message = "Name is invalid")
	private String name;
	@NotEmpty(message = "Type is invalid")
	private String type;
	private Boolean active;
	private Date createdTimestamp;
	private User createdBy;
	private Element parentElement;
	private Map<String, Object> elementAttributes;

	public ElementBoundary() {
		this.parentElement = null;
		this.active = false;
		this.elementAttributes = new HashMap<>();
	}

	public ElementBoundary(ElementId elementId, String name, String type, Boolean active, Date createdTimestamp,
			User createdBy, Element parentElement, Map<String, Object> elementAttributes) {
		this();
		this.elementId = elementId;
		this.name = name;
		this.type = type;
		this.active = active;
		this.createdTimestamp = createdTimestamp;
		this.createdBy = createdBy;
		this.parentElement = parentElement;
		this.elementAttributes = elementAttributes;
	}

	public ElementId getElementId() {
		return elementId;
	}

	public void setElementId(ElementId elementId) {
		this.elementId = elementId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Element getParentElement() {
		return parentElement;
	}

	public void setParentElement(Element parentElement) {
		this.parentElement = parentElement;
	}

	public Map<String, Object> getElementAttributes() {
		return this.elementAttributes;
	}

	public void setElementAttributes(Map<String, Object> elementAttributes) {
		this.elementAttributes = elementAttributes;
	}

	@Override
	public String toString() {
		return "ElementBoundary [elementId=" + elementId + ", name=" + name + ", type=" + type + ", active=" + active
				+ ", createdTimestamp=" + createdTimestamp + ", createdBy=" + createdBy + ", parentElement="
				+ parentElement + ", elementAttributes=" + elementAttributes + "]";
	}

}
