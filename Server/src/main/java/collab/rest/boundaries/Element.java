package collab.rest.boundaries;

public class Element {
	private ElementId elementId;

	public Element() {
	}

	public Element(ElementId elementId) {
		super();
		this.elementId = elementId;
	}

	public ElementId getElementId() {
		return elementId;
	}

	public void setElementId(ElementId elementId) {
		this.elementId = elementId;
	}

	@Override
	public String toString() {
		return "Element [elementId=" + elementId + "]";
	}
	
	@Override
    public boolean equals(Object element) { 
        // If the object is compared with itself then return true   
        if (element == this) { 
            return true; 
        } 
        if (!(element instanceof Element)) { 
            return false; 
        }           
        // typecast o to userId so that we can compare data members  
        Element elementToCheck = (Element) element;          
        // Compare the data members and return accordingly  
        return elementToCheck.getElementId().equals(this.getElementId());    
	} 
	
}
