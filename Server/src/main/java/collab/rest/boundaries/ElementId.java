package collab.rest.boundaries;

public class ElementId {
	private String domain;
	private String id;

	public ElementId() {
	}

	public ElementId(String domain, String id) {
		super();
		this.domain = domain;
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ElementId [domain=" + domain + ", id=" + id + "]";
	}

	@Override
	public boolean equals(Object element) {
		// If the object is compared with itself then return true
		if (element == this) {
			return true;
		}
		if (!(element instanceof ElementId)) {
			return false;
		}
		// typecast o to Complex so that we can compare data members
		ElementId elementToCheck = (ElementId) element;
		// Compare the data members and return accordingly
		return elementToCheck.getDomain().equals(this.getDomain()) && elementToCheck.getId().equals(this.getId());
	}

}
