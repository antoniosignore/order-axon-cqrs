package org.signore.axon.catalog.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity, String> {
	List<ProductEntity> findByCatalogId(Integer catalogId);
}
