package org.signore.axon.catalog.repository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.signore.axon.catalog.events.ProductCreatedEvent;
import org.signore.axon.catalog.models.ProductBean;
import org.signore.axon.catalog.queries.GetProductsQuery;
import org.springframework.stereotype.Service;

@Service
public class ProductRepositoryProjector {

	private final ProductRepository productRepository;

	public ProductRepositoryProjector(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@EventHandler
	public void addBook(ProductCreatedEvent event) throws Exception {
		ProductEntity product = new ProductEntity();
		product.setSku(event.getSku());
		product.setCatalogId(event.getCatalogId());
		product.setLabel(event.getLabel());
		productRepository.save(product);
	}

	@QueryHandler
	public List<ProductBean> getBooks(GetProductsQuery query) {
		return productRepository.findByCatalogId(query.getCatalogId()).stream().map(toProduct()).collect(Collectors.toList());
	}

	private Function<ProductEntity, ProductBean> toProduct() {
		return e -> {
			ProductBean productBean = new ProductBean();
			productBean.setSku(e.getSku());
			productBean.setLabel(e.getLabel());
			return productBean;
		};
	}
}
