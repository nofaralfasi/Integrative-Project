package collab.logic.plugins;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import collab.dal.ElementDao;
import collab.data.ElementEntity;
import collab.data.UserRole;
import collab.logic.ElementConverter;
import collab.logic.ElementServiceRdb;
import collab.logic.UsersService;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.ElementBoundary;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@Component
public class AddToCartPlugin implements ActionPlugin {
	private ElementDao elementDao;
	private ElementServiceRdb elementService;
	private UsersService userService;
	private ElementConverter elementConverter;

	@Value("${collab.config.domain}")
	private String mangerDomain;
	@Value("${collab.config.manager.email}")
	private String managerEmail;
	@Value("${collab.config.id.delimiter}")
	private String delimiter;

	@Autowired
	public AddToCartPlugin(ElementDao elementDao, ElementServiceRdb elementService, UsersService userService,
			ElementConverter elementConverter) {
		this.elementDao = elementDao;
		this.elementService = elementService;
		this.userService = userService;
		this.elementConverter = elementConverter;
	}

	@PostConstruct
	public void createStoreManager() {
		try {
			userService.login(this.mangerDomain, this.managerEmail);
		} catch (Exception e) {
			UserBoundary managerUser = new UserBoundary(new UserId(this.mangerDomain, this.managerEmail),
					UserRole.MANAGER, "Shop Owner", ":)");
			this.userService.create(managerUser);
		}
	}

	@Override
	public Object manageActionType(ActionBoundary action) {
		try {
			String userId = action.getInvokedBy().getUserId().getDomain() + delimiter
					+ action.getInvokedBy().getUserId().getEmail();
			ElementEntity itemElement = getItemElement(action.getElement().getElementId());
			if ((boolean) itemElement.getElementAttributes().get("inCart") == true) {
				return null;
			}
			ElementEntity cartElement = getCartElement(userId);
			if (cartElement == null) {
				ElementBoundary newCart = new ElementBoundary();
				newCart.setName(userId);
				newCart.setType("cartType");
				newCart.setActive(true);
				HashMap<String, Object> cartAttributes = new HashMap<String, Object>();
				cartAttributes.put("totalPrice", 0);
				cartAttributes.put("numberOfItems", 0);
				newCart.setElementAttributes(cartAttributes);
				cartElement = elementConverter
						.toEntity(this.elementService.create(this.mangerDomain, this.managerEmail, newCart));
			}
			int totalPrice = 0, itemPrice = 0;

			// Update Cart total price and Item
			if (cartElement.getElementAttributes().get("totalPrice") != null)
				totalPrice = (int) cartElement.getElementAttributes().get("totalPrice");
			if (itemElement.getElementAttributes().get("price") != null)
				itemPrice = (int) itemElement.getElementAttributes().get("price");
			cartElement.getElementAttributes().put("totalPrice", totalPrice + itemPrice);
			int numberOfItems = (int) cartElement.getElementAttributes().get("numberOfItems");
			cartElement.getElementAttributes().put("numberOfItems", ++numberOfItems);
			this.elementDao.save(cartElement);

			// Update item's parent to be cart
			itemElement.setParentElement(cartElement);
			itemElement.getElementAttributes().put("inCart", true);
			this.elementDao.save(itemElement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return action;
	}

	private ElementEntity getItemElement(ElementId elementId) {
		return elementConverter.toEntity(elementService.getElementById(elementId));
	}

	private ElementEntity getCartElement(String userId) {
		List<ElementEntity> cartElement = elementDao.findAllByTypeAndName("cartType", userId,
				PageRequest.of(0, 1, Direction.ASC, "elementId"));
		if (cartElement.size() == 1)
			return cartElement.get(0);
		return null;
	}

}