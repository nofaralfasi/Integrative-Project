package collab.dal;

import org.springframework.data.repository.CrudRepository;

import collab.data.ActionEntity;

public interface ActionDao extends CrudRepository<ActionEntity, String> {

}
