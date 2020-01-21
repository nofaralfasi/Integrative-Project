package collab.dal;

import org.springframework.data.repository.CrudRepository;

import collab.data.UserEntity;

public interface UserDao extends CrudRepository<UserEntity, String> {

}
