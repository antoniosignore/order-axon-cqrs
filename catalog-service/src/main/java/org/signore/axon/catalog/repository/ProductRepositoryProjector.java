package org.signore.axon.catalog.repository;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.signore.axon.catalog.events.ProductCreatedEvent;
import org.signore.axon.catalog.events.ProductRemovedEvent;
import org.signore.axon.catalog.events.ProductUpdatedEvent;
import org.signore.axon.catalog.models.ProductBean;
import org.signore.axon.catalog.queries.GetProductsQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductRepositoryProjector {

	private final ProductRepository productRepository;

	public ProductRepositoryProjector(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@EventHandler
	public void addProduct(ProductCreatedEvent event) throws Exception {
		ProductEntity product = new ProductEntity();
		product.setSku(event.getSku());
		product.setCatalogId(event.getCatalogId());
		product.setLabel(event.getLabel());
		productRepository.save(product);
	}

	@EventHandler
	public void addProduct(ProductRemovedEvent event) throws Exception {
		ProductEntity product = new ProductEntity();
		product.setSku(event.getSku());
		product.setCatalogId(event.getCatalogId());
		productRepository.delete(product);
	}

	@EventHandler
	public void updateProduct(ProductUpdatedEvent event) throws Exception {
		ProductEntity product = new ProductEntity();
		product.setSku(event.getSku());
		product.setCatalogId(event.getCatalogId());
		product.setLabel(event.getLabel());
		productRepository.save(product);
	}

	@QueryHandler
	public List<ProductBean> getProducts(GetProductsQuery query) {
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
