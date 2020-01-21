package collab.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ELEMENTS")
public class ElementEntity {
	private String elementId;
	private String name;
	private String type;
	private Boolean active;
	private Date createdTimestamp;
	private String createdBy;
	private ElementEntity parentElement;
	private Map<String, Object> elementAttributes;

	public ElementEntity() {
		this.parentElement = null;
		this.elementAttributes = new HashMap<>();
	}

	public ElementEntity(String elementId, String name, String type, Boolean active, Date createdTimestamp,
			String createdBy, ElementEntity parentElement, Map<String, Object> elementAttributes) {
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

	@Id
	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
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

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public ElementEntity getParentElement() {
		return parentElement;
	}

	public void setParentElement(ElementEntity parentElement) {
		this.parentElement = parentElement;
	}

	@Convert(converter = MapToJsonStringConveter.class)
	@Lob
	public Map<String, Object> getElementAttributes() {
		return elementAttributes;
	}

	public void setElementAttributes(Map<String, Object> elementAttributes) {
		this.elementAttributes = elementAttributes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementEntity other = (ElementEntity) obj;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		return true;
	}

}
