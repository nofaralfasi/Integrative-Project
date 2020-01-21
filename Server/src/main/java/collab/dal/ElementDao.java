package collab.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import collab.data.ElementEntity;

public interface ElementDao extends PagingAndSortingRepository<ElementEntity, String> {		
	public List<ElementEntity> findAllByNameLike(@Param("name") String name, Pageable pageable);
	public List<ElementEntity> findAllByType(@Param("type") String type, Pageable pageable);
	public List<ElementEntity> findAllByTypeNotLike(@Param("type") String type, Pageable pageable);
	public List<ElementEntity> findAllByActiveAndTypeNotLike(@Param("active") boolean active, @Param("type") String type, Pageable pageable);	
	public List<ElementEntity> findAllByTypeAndName(@Param("type") String type,@Param("name") String name,  Pageable pageable);
	public List<ElementEntity> findAllByParentElement(@Param("parentElement") ElementEntity parentElement, Pageable pageable);
	public List<ElementEntity> findAllByActive(@Param("active") boolean active, Pageable pageable);
	public List<ElementEntity> findAllByActiveAndNameLike(@Param("active") boolean active, @Param("name") String name, Pageable pageable);
	public List<ElementEntity> findAllByActiveAndType(@Param("active") boolean active, @Param("type") String type, Pageable pageable);
	public List<ElementEntity> findAllByActiveAndParentElement(@Param("active") boolean active, @Param("parentElement") ElementEntity parentElement, Pageable pageable);
}