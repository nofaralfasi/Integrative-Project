package collab.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import collab.dal.ElementDao;
import collab.data.ElementEntity;
import collab.rest.boundaries.Element;
import collab.rest.boundaries.ElementBoundary;
import collab.rest.boundaries.User;

@Component
public class ElementConverter {
	private ElementDao elementDao;
	private GeneralConverter converter;

	@Autowired
	public ElementConverter(ElementDao elementDao, GeneralConverter converter) {
		super();
		this.converter = converter;
		this.elementDao = elementDao;
	}

	public ElementBoundary fromEntity(ElementEntity element) {
		try {
			return new ElementBoundary(this.converter.fromStringElementId(element.getElementId()), element.getName(),
					element.getType(), element.getActive(), element.getCreatedTimestamp(),
					new User(this.converter.fromStringUserId(element.getCreatedBy())),
					(element.getParentElement() != null)
							? new Element(this.converter.fromStringElementId(element.getParentElement().getElementId()))
							: null,
					element.getElementAttributes());
		} catch (Exception e) {
			throw new RuntimeException("Convert fromEntity ElementEntity has failed!");
		}
	}

	public ElementEntity toEntity(ElementBoundary element) {
		try {
			ElementEntity parent = null;
			if (element.getParentElement() != null && element.getParentElement().getElementId() != null
					&& !element.getParentElement().getElementId().getDomain().isEmpty()) {
				String parentElement = this.converter.toStringElementId(element.getParentElement().getElementId());
				parent = this.elementDao.findById(parentElement)
						.orElseThrow(() -> new RuntimeException("no parent Element with id: " + parentElement));
			}
			return new ElementEntity(this.converter.toStringElementId(element.getElementId()), element.getName(),
					element.getType(), (element.getActive() != null) ? element.getActive() : true,
					element.getCreatedTimestamp(), this.converter.toStringUser(element.getCreatedBy()), parent,
					element.getElementAttributes());
		} catch (Exception e) {
			throw new RuntimeException("Convert ElementBoundary toEntity has failed!");
		}
	}

}
