package collab.rest.boundaries;

public class ActionId {
	private String domain;
	private String id;

	public ActionId() {
	}

	public ActionId(String domain, String id) {
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
	public boolean equals(Object action) {
		// If the object is compared with itself then return true
		if (action == this) {
			return true;
		}
		if (!(action instanceof ActionId)) {
			return false;
		}
		// typecast o to Complex so that we can compare data members
		ActionId actionToCheck = (ActionId) action;
		// Compare the data members and return accordingly
		return actionToCheck.getDomain().equals(this.getDomain()) && actionToCheck.getId().equals(this.getId());
	}

}
