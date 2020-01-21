package collab.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ACTIONS")
public class ActionEntity {
	private String actionId;
	private String elementId;
	private String invokedBy;
	private String type;
	private Date createdTimestamp;
	private Map<String, Object> actionAttributes;

	public ActionEntity() {
		this.actionAttributes = new HashMap<>();
	}

	public ActionEntity(String actionId, String elementId, String invokedBy, String type, Date createdTimestamp,
			Map<String, Object> actionAttributes) {
		this();
		this.actionId = actionId;
		this.elementId = elementId;
		this.invokedBy = invokedBy;
		this.type = type;
		this.createdTimestamp = createdTimestamp;
		this.actionAttributes = actionAttributes;
	}

	@Id
	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getElement() {
		return elementId;
	}

	public void setElement(String element) {
		this.elementId = element;
	}

	public String getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(String invokedBy) {
		this.invokedBy = invokedBy;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	@Convert(converter = MapToJsonStringConveter.class)
	@Lob
	public Map<String, Object> getActionAttributes() {
		return actionAttributes;
	}

	public void setActionAttributes(Map<String, Object> actionAttributes) {
		this.actionAttributes = actionAttributes;
	}

}
