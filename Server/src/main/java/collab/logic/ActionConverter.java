package collab.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import collab.data.ActionEntity;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.Element;
import collab.rest.boundaries.User;

@Component
public class ActionConverter {
	private GeneralConverter converter;	
	
	@Value("${collab.config.id.delimiter}")
	private String delimiter;
	
	@Autowired
	public ActionConverter(GeneralConverter converter) {
		super();
		this.converter = converter;
	}

	public ActionBoundary fromEntity(ActionEntity action) {
		try {
			return new ActionBoundary(new Element(this.converter.fromStringElementId(action.getElement())), new User(this.converter.fromStringUserId(action.getInvokedBy())), action.getType(),
					action.getCreatedTimestamp(), action.getActionAttributes(), this.converter.fromStringActionId(action.getActionId()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ActionEntity toEntity(ActionBoundary action) {
		try {
			return new ActionEntity(this.converter.toStringActionId(action.getActionId()), this.converter.toStringElementId(action.getElement().getElementId()), this.converter.toStringUser(action.getInvokedBy()),
					action.getType(), action.getCreatedTimestamp(), action.getActionAttributes());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
