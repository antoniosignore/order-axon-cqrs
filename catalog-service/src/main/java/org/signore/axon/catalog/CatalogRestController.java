package org.signore.axon.catalog;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.signore.axon.catalog.aggregate.Catalog;
import org.signore.axon.catalog.commands.ProductUpdateCommand;
import org.signore.axon.catalog.commands.RegisterCatalogCommand;
import org.signore.axon.catalog.commands.RegisterProductCommand;
import org.signore.axon.catalog.commands.RemoveProductCommand;
import org.signore.axon.catalog.models.CatalogBean;
import org.signore.axon.catalog.models.ProductBean;
import org.signore.axon.catalog.queries.GetCatalogQuery;
import org.signore.axon.catalog.queries.GetProductsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class CatalogRestController {

	private final CommandGateway commandGateway;
	private final QueryGateway queryGateway;

	@Autowired
	public CatalogRestController(CommandGateway commandGateway, QueryGateway queryGateway) {
		this.commandGateway = commandGateway;
		this.queryGateway = queryGateway;
	}

	@PostMapping("/api/catalog")
	public String addCatalog(@RequestBody CatalogBean catalogBean) {
		commandGateway.send(new RegisterCatalogCommand(catalogBean.getCatalogId(), catalogBean.getName()));
		return "Saved";
	}

	@GetMapping("/api/catalog/{catalog}")
	public Catalog getCatalog(@PathVariable Integer catalog) throws InterruptedException, ExecutionException {
		CompletableFuture<Catalog> future = queryGateway.query(new GetCatalogQuery(catalog), Catalog.class);
		return future.get();
	}

	@PostMapping("/api/catalog/{catalog}/product")
	public String addProduct(@PathVariable Integer catalog, @RequestBody ProductBean productBean) {
		commandGateway.send(new RegisterProductCommand(catalog, productBean.getSku(), productBean.getLabel()));
		return "Saved";
	}

	@DeleteMapping("/api/catalog/{catalog}/product")
	public String deleteProduct(@PathVariable Integer catalog, @RequestBody ProductBean productBean) {
		commandGateway.send(new RemoveProductCommand(catalog, productBean.getSku()));
		return "Saved";
	}

	@PutMapping("/api/catalog/{catalog}/product")
	public String updateProduct(@PathVariable Integer catalog, @RequestBody ProductBean productBean) {
		commandGateway.send(new ProductUpdateCommand(catalog, productBean.getSku(), productBean.getLabel()));
		return "Updated";
	}

	@GetMapping("/api/catalog/{catalog}/product")
	public List<ProductBean> getProducts(@PathVariable Integer catalog) throws InterruptedException, ExecutionException {
		return queryGateway.query(new GetProductsQuery(catalog), ResponseTypes.multipleInstancesOf(ProductBean.class)).get();
	}

}
