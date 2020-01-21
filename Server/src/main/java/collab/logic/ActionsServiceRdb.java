package collab.logic;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import collab.dal.ActionDao;
import collab.dal.IdGenerator;
import collab.dal.IdGeneratorDao;
import collab.data.ActionEntity;
import collab.logic.plugins.ActionPlugin;
import collab.rest.NotFoundException;
import collab.rest.boundaries.ActionBoundary;
import collab.rest.boundaries.ActionId;
import collab.rest.boundaries.ElementBoundary;
import collab.rest.boundaries.UserBoundary;

@Service
public class ActionsServiceRdb implements ActionsService {
	private ActionDao actionDao;
	private ActionConverter actionConverter;
	private Validator validator;
	private String domain;
	private ElementServiceRdb elementService;
	private UserServiceRdb userService;
	private ApplicationContext springContext;

	private IdGeneratorDao idGenerator;

	@Autowired
	public ActionsServiceRdb(ElementServiceRdb elementService, ActionDao actionDao, ActionConverter actionConverter,
			Validator validator, UserServiceRdb userService, IdGeneratorDao idGenerator,
			ApplicationContext springContext) {
		super();
		this.actionDao = actionDao;
		this.actionConverter = actionConverter;
		this.validator = validator;
		this.idGenerator = idGenerator;
		this.elementService = elementService;
		this.userService = userService;
		this.springContext = springContext;
	}

	@Value("${collab.config.domain:defaultDomain}")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	@Transactional
	public Object invoke(ActionBoundary newAction) {
		String typeString = validateInvokeAction(newAction);
		try {
			if (typeString != null && typeString.length() > 1) {
				this.generateIdAndDate(newAction);
				newAction = this.actionConverter
						.fromEntity(this.actionDao.save(this.actionConverter.toEntity(newAction)));
				String plugInClassName = "collab.logic.plugins." + typeString.substring(0, 1).toUpperCase()
						+ typeString.substring(1) + "Plugin";
				Class<?> pluginClass = Class.forName(plugInClassName);
				ActionPlugin plugin = (ActionPlugin) this.springContext.getBean(pluginClass);
				Object rv = plugin.manageActionType(newAction);
//				return .actionConverter.fromEntity(this.actionDao.save(this.actionConverter.toEntity(rv)));
				return rv;
			} else {
				throw new NotFoundException("No valid action for null type");
			}
		} catch (Exception e) {
			throw new ActionInvocationException("Could not invoke action", e);
		}
	}

	public String validateInvokeAction(ActionBoundary newAction) {
		if (this.validator.validateActionBoundary(newAction)) {
			
			UserBoundary user = this.userService.getUserById(newAction.getInvokedBy().getUserId());
			ElementBoundary element = this.elementService.getElementById(newAction.getElement().getElementId());
			System.err.println(this.validator.isPlayer(user));
			System.err.println(element.getActive());
			if (this.validator.isPlayer(user) && element.getActive())
				return newAction.getType();
		}
		throw new ActionInvocationException("Could not invoke action");
	}

	private void generateIdAndDate(ActionBoundary newAction) {
		IdGenerator newGeneratedValue = this.idGenerator.save(new IdGenerator());
		newAction.setActionId(new ActionId(this.domain, "" + newGeneratedValue.getNextId()));
		this.idGenerator.delete(newGeneratedValue);
		newAction.setCreatedTimestamp(new Date());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionBoundary> getAllActions() {
		Iterable<ActionEntity> iter = this.actionDao.findAll();
		return StreamSupport.stream(iter.spliterator(), false).map(this.actionConverter::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteAll() {
		this.actionDao.deleteAll();
	}

}
