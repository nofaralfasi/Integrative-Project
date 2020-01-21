package collab.logic.plugins;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import collab.dal.ElementDao;
import collab.data.ElementEntity;
import collab.logic.GeneralConverter;
import collab.rest.NotFoundException;
import collab.rest.boundaries.ActionBoundary;

@Component
public class RemoveFromCartPlugin implements ActionPlugin {
	private ElementDao elementDao;
	private GeneralConverter converter;

	@Autowired
	public RemoveFromCartPlugin(ElementDao elementDao, GeneralConverter converter) {
		this.elementDao = elementDao;
		this.converter = converter;
	}

	@Override
	public Object manageActionType(ActionBoundary action) {
		try {
			String userId = this.converter.toStringUser(action.getInvokedBy());
			ElementEntity cartElement = getCartElement(userId);
			ElementEntity itemElement = elementDao
					.findById(this.converter.toStringElementId(action.getElement().getElementId()))
					.orElseThrow(() -> new RuntimeException("no element was found!"));
			if (cartElement == null) {
				throw new NotFoundException("No Cart was Found");
			}

			// Update Cart total price and Item
			int totalPrice = (int) cartElement.getElementAttributes().get("totalPrice");
			int itemPrice = (int) itemElement.getElementAttributes().get("price");
			cartElement.getElementAttributes().put("totalPrice", totalPrice - itemPrice);
			int numberOfItems = (int) cartElement.getElementAttributes().get("numberOfItems");
			cartElement.getElementAttributes().put("numberOfItems", --numberOfItems);
			this.elementDao.save(cartElement);

			// Update item's parent to be cart
			itemElement.setParentElement(null);
			itemElement.getElementAttributes().put("inCart", false);
			this.elementDao.save(itemElement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return action;
	}

	private ElementEntity getCartElement(String userId) {
		List<ElementEntity> cartElement = elementDao.findAllByTypeAndName("cartType", userId,
				PageRequest.of(0, 1, Direction.ASC, "elementId"));
		if (cartElement.size() == 1)
			return cartElement.get(0);
		return null;
	}

}