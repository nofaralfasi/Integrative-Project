package collab.logic.plugins;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import collab.dal.ElementDao;
import collab.dal.UserDao;
import collab.data.ElementEntity;
import collab.logic.GeneralConverter;
import collab.rest.NotFoundException;
import collab.rest.boundaries.ActionBoundary;

@Component
public class BuyPlugin implements ActionPlugin {
	private ElementDao elementDao;
	private GeneralConverter converter;

	@Autowired
	public BuyPlugin(ElementDao elementDao, UserDao userDao, GeneralConverter converter) {
		this.elementDao = elementDao;
		this.converter = converter;
	}

	@Override
	public Object manageActionType(ActionBoundary action) {
		String elementId = converter.toStringElementId(action.getElement().getElementId());
		ElementEntity elementCart = elementDao.findById(elementId)
				.orElseThrow(() -> new NotFoundException("No element could be found with id: " + elementId));
		if (!elementCart.getType().equals("cartType"))
			throw new RuntimeException("You cannot buy an element that is not a cart");
		List<ElementEntity> allItemsInTheCart = this.getAllItemsFromCart(elementCart);
		for (int i = 0; i < allItemsInTheCart.size(); i++) {
			allItemsInTheCart.get(i).setActive(false);
			//allItemsInTheCart.get(i).setParentElement(null);
			elementDao.save(allItemsInTheCart.get(i));
		}

		// Empty the cart
		elementCart.getElementAttributes().put("totalPrice", 0);
		elementCart.getElementAttributes().put("numberOfItems", 0);
		elementDao.save(elementCart);

		String url = "http:/" + "/localhost:8080/collab/elements/" + action.getInvokedBy().getUserId().getDomain() + "/"
				+ action.getInvokedBy().getUserId().getEmail() + "/byParent/"
				+ action.getElement().getElementId().getDomain() + "/" + action.getElement().getElementId().getId();
		SendEmail.sendFromGMail(action.getInvokedBy().getUserId().getEmail(), url); // First null - option to add url to																			// see all items that was bought
		return action;
	}

	private List<ElementEntity> getAllItemsFromCart(ElementEntity elementCart) {
		List<ElementEntity> results = new ArrayList<>();
		List<ElementEntity> entities;
		int page = 0;
		int size = 50;
		do {
			entities = elementDao.findAllByParentElement(elementCart,
					PageRequest.of(page++, size, Direction.ASC, "elementId"));
			entities.stream().forEach(results::add);
		} while (entities.size() == size);
		return results;
	}

}