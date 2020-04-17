package org.signore.axon.catalog.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity, String> {
	List<ProductEntity> findByCatalogId(Integer libraryId);
}
