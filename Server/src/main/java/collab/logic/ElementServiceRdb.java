package collab.logic;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import collab.dal.ElementDao;
import collab.dal.IdGenerator;
import collab.dal.IdGeneratorDao;
import collab.data.ElementEntity;
import collab.rest.NotFoundException;
import collab.rest.boundaries.ElementBoundary;
import collab.rest.boundaries.ElementId;
import collab.rest.boundaries.User;
import collab.rest.boundaries.UserBoundary;
import collab.rest.boundaries.UserId;

@Service
public class ElementServiceRdb implements AdvancedElementsService {
	private ElementDao elementDao;
	private ElementConverter elementConverter;
	private GeneralConverter converter;
	private Validator validator;
	private String domain;
	private UserServiceRdb userService;
	private IdGeneratorDao idGenerator;
	
	@Autowired
	public ElementServiceRdb(ElementDao elementDao, UserServiceRdb userService, ElementConverter elementConverter,
			GeneralConverter converter, Validator validator, IdGeneratorDao idGenerator) {
		super();
		this.elementDao = elementDao;
		this.converter = converter;
		this.elementConverter = elementConverter;
		this.validator = validator;
		this.idGenerator = idGenerator;
		this.userService = userService;
	}

	@Value("${collab.config.domain:defaultDomain}")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	@Transactional
	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary element) {
		if (this.validator.isManager(this.userService.getUserById(new UserId(managerDomain, managerEmail))))
			element.setCreatedBy(new User(new UserId(managerDomain, managerEmail)));
		this.generateElementIdAndDate(element);
		if(!element.getType().equals("cartType"))
			element.getElementAttributes().put("inCart", false);
		return this.elementConverter.fromEntity(this.elementDao.save(this.elementConverter.toEntity(element)));
	}

	@Override
	@Transactional
	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId,
			ElementBoundary update) {
		if (this.validator.isManager(this.userService.getUserById(new UserId(managerDomain, managerEmail)))) {
			ElementBoundary existingElement = this.getElementById(new ElementId(elementDomain, elementId));
			if (update.getType() != null)
				existingElement.setType(update.getType());
			if (update.getName() != null && !update.getName().isEmpty())
				existingElement.setName(update.getName());
			if (update.getActive() != null && !update.getActive().toString().isEmpty())
				existingElement.setActive(update.getActive());
			if (update.getElementAttributes() != null)
				existingElement.setElementAttributes(update.getElementAttributes());
			if (update.getParentElement() != null) {
				this.getElementById(update.getParentElement().getElementId());
				existingElement.setParentElement(update.getParentElement());
			}
			return this.elementConverter
					.fromEntity(this.elementDao.save(this.elementConverter.toEntity(existingElement)));
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain,
			String elementId) {
		ElementBoundary element = this.getElementById(new ElementId(elementDomain, elementId));
		UserBoundary user = this.userService.getUserById(new UserId(userDomain, userEmail));
		if (!this.validator.isPlayer(user))
			return this.getElementById(element.getElementId());
		else if (this.validator.isPlayer(user) && element.getActive())
			return this.getElementById(element.getElementId());
		else
			throw new NotFoundException("Element doesn't exsist");
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElements(String userDomain, String userEmail) {
		Iterable<ElementEntity> iter = this.elementDao.findAll();
		return StreamSupport.stream( // create a stream from the iterable returned
				iter.spliterator(), false).map(this.elementConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElements(String userDomain, String userEmail, int size, int page) {
		UserBoundary user = this.userService.getUserById(new UserId(userDomain, userEmail));
		if (this.validator.isPlayer(user)) {
			List<ElementEntity> iter = this.elementDao.findAllByActive(true,
					PageRequest.of(page, size, Direction.ASC, "elementId"));
			return iter.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
		}
		List<ElementEntity> iter = this.elementDao.findAll(PageRequest.of(page, size, Direction.ASC, "elementId"))
				.getContent();
		return iter.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElementsByName(String userDomain, String userEmail, String name, int size,
			int page) {
		UserBoundary user = this.userService.getUserById(new UserId(userDomain, userEmail));
		if (this.validator.isPlayer(user)) {
			List<ElementEntity> iter = this.elementDao.findAllByActiveAndNameLike(true, name,
					PageRequest.of(page, size, Direction.ASC, "elementId"));
			return iter.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
		}
		return this.elementDao.findAllByNameLike(name, PageRequest.of(page, size, Direction.ASC, "elementId")).stream()
				.map(this.elementConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElementsByType(String userDomain, String userEmail, String type, int size,
			int page) {
		UserBoundary user = this.userService.getUserById(new UserId(userDomain, userEmail));
		if (this.validator.isPlayer(user)) {
			List<ElementEntity> iter = this.elementDao.findAllByActiveAndType(true, type,
					PageRequest.of(page, size, Direction.ASC, "elementId"));
			return iter.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
		}
		List<ElementEntity> iter = this.elementDao.findAllByType(type,
				PageRequest.of(page, size, Direction.ASC, "elementId"));
		return iter.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElementsByParentElement(String userDomain, String userEmail, String parentDomain,
			String parentId, int size, int page) {
		ElementId elementId = new ElementId(parentDomain, parentId);
		UserBoundary user = this.userService.getUserById(new UserId(userDomain, userEmail));
		if (this.validator.isPlayer(user)) {
			List<ElementEntity> iter = this.elementDao.findAllByActiveAndParentElement(true,
					this.elementConverter.toEntity(this.getElementById(elementId)),
					PageRequest.of(page, size, Direction.ASC, "elementId"));
			return iter.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
		}
		List<ElementEntity> iter = this.elementDao.findAllByParentElement(
				this.elementConverter.toEntity(this.getElementById(elementId)),
				PageRequest.of(page, size, Direction.ASC, "elementId"));
		return iter.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAllElements() {
		Iterable<ElementEntity> iter = this.elementDao.findAll();
		return StreamSupport.stream( // create a stream from the iterable returned
				iter.spliterator(), false).map(this.elementConverter::fromEntity).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteAll() {
		this.elementDao.deleteAll();
	}

	private void generateElementIdAndDate(ElementBoundary element) {
		element.setCreatedTimestamp(new Date());
		IdGenerator newGeneratedValue = this.idGenerator.save(new IdGenerator());
		element.setElementId(new ElementId(this.domain, "" + newGeneratedValue.getNextId()));
		this.idGenerator.delete(newGeneratedValue);
	}

	public ElementBoundary getElementById(ElementId elementId) {
		return this.getElementByStringId(this.converter.toStringElementId(elementId));
	}

	public ElementBoundary getElementByStringId(String stringElementId) {
		return this.elementConverter.fromEntity(this.elementDao.findById(stringElementId)
				.orElseThrow(() -> new NotFoundException("No element could be found with id: " + stringElementId)));
	}

	@Override
	public List<ElementBoundary> getAllElementsByTypeNot(String userDomain, String userEmail, String type, int size,
			int page) {
		UserBoundary user = this.userService.getUserById(new UserId(userDomain, userEmail));
		if (this.validator.isPlayer(user)) {
			List<ElementEntity> iter = this.elementDao.findAllByActiveAndTypeNotLike(true, type,
					PageRequest.of(page, size, Direction.ASC, "elementId"));
			return iter.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
		}
		List<ElementEntity> iter = this.elementDao.findAllByTypeNotLike(type,
				PageRequest.of(page, size, Direction.ASC, "elementId"));
		return iter.stream().map(this.elementConverter::fromEntity).collect(Collectors.toList());
	}
	
}