package collab.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import collab.data.UserEntity;
import collab.rest.boundaries.UserBoundary;

@Component
public class UserConverter {
	private GeneralConverter converter;	
	
	@Autowired
	public UserConverter(GeneralConverter converter) {
		super();
		this.converter = converter;
	}
	
	public UserBoundary fromEntity (UserEntity user) {
		try {
			return new UserBoundary(
				this.converter.fromStringUserId(user.getUserId()),
				user.getRole(),
				user.getUsername(),
				user.getAvatar());
		} catch (Exception e) {
			throw new RuntimeException("could not convert UserEntity to UserBoundary! "+user.toString());
		}
	}

	public UserEntity toEntity (UserBoundary user) {
		try {
			return new UserEntity(
				this.converter.toStringUserId(user.getUserId()),
				user.getRole(),
				user.getUsername(),
				user.getAvatar());
		} catch (Exception e) {
			throw new RuntimeException("could not convert UserBoundary to UserEntity! "+user.toString());
		}
	}

}
