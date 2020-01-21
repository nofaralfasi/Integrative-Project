package collab.data.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import collab.dal.UserDao;
import collab.data.ElementEntity;
import collab.data.UserEntity;
import collab.data.UserRole;
import collab.logic.AdvancedElementsService;
import collab.logic.ElementConverter;
import collab.rest.boundaries.ElementBoundary;

@Component
@Profile("utils")
public class UserEntityDemo implements CommandLineRunner {
	private AdvancedElementsService elementService;
	private ElementConverter elementConverter;
	private EntityFactory factory;
	private UserDao userDao;

	@Value("${collab.config.id.delimiter}")
	private String delimiter;
	@Value("${collab.config.domain}")
	private String domain;
	@Value("${collab.config.manager.email}")
	private String managerEmail;

	@Autowired
	public UserEntityDemo(EntityFactory factory, UserDao userDao, AdvancedElementsService elementService,
			ElementConverter elementConverter) {
		super();
		this.factory = factory;
		this.userDao = userDao;
		this.elementService = elementService;
		this.elementConverter = elementConverter;
	}

	@Override
	public void run(String... args) throws Exception {
		int i, j;
		String avatar = ":x";
		String emailSuffix = "@us.er";
		String usernameMangers = "Splinter";
		String usernamePlayers[] = { "Nofar", "Leonardo", "Raphael", "Donatello", "Michelangelo" };
		String idPrefix = domain + delimiter;
		String managersId = idPrefix + managerEmail;
		String playerId;
		UserEntity player;

		UserEntity manager = factory.createNewUser(managersId, usernameMangers, avatar, UserRole.MANAGER);
		manager = this.userDao.save(manager);

		for (i = 0; i < usernamePlayers.length; i++) {
			playerId = idPrefix + usernamePlayers[i].toLowerCase() + emailSuffix;
			player = factory.createNewUser(playerId, usernamePlayers[i], avatar, UserRole.PLAYER);
			player = this.userDao.save(player);
		}

		// Cart Element 1
		ElementBoundary newCartElement = new ElementBoundary();
		newCartElement.setName(idPrefix + "nofaralfasi@gmail.com");
		newCartElement.setType("cartType");
		newCartElement.setActive(true);
		Map<String, Object> moreAttributes = new HashMap<String, Object>();
		moreAttributes.put("totalPrice", 0);
		moreAttributes.put("numberOfItems", 0);
		newCartElement.setElementAttributes(moreAttributes);
		ElementEntity cart = this.elementConverter
				.toEntity(this.elementService.create(domain, managerEmail, newCartElement));

		String names[] = { "Snoopy cartoon", "Multi size stars", "Stylish vertical stripes", "Sailboat cartoon",
				"Modern linear stripes", "Classic bricks", "Glittery stars", "Plain elegant", "Classic rich",
				"Luxurious bricks" };
		String types[] = { "kidsType", "shapesType", "flowersType", "linesType", "animalsType", "starsType",
				"specialsType", "classicsType", "babiesType" };
		String colors[] = { "black", "pink", "white", "yellow", "green", "purple", "blue", "red", "beige" };
		String sizes[] = { "small", "medium", "large" };
		int firstPrice = 51;
		int numOfItems = 15;
		ElementBoundary newItemElement;

		for (i = 0, j = 0; i < numOfItems; i++, j++) {
			if (j == types.length)
				j = 0;
			newItemElement = new ElementBoundary();
			newItemElement.setName(names[j]);
			newItemElement.setType(types[j]);
			newItemElement.setActive(true);
			Map<String, Object> itemAttributes = new HashMap<String, Object>();
			itemAttributes.put("price", firstPrice++);
			itemAttributes.put("size", sizes[i % 3]);
			itemAttributes.put("color", colors[j]);
			itemAttributes.put("picture", "Images/home/product" + i + ".jpg");
			itemAttributes.put("description", "description" + i);
			newItemElement.setElementAttributes(itemAttributes);
			this.elementService.create(domain, managerEmail, newItemElement);
		}
	}

}