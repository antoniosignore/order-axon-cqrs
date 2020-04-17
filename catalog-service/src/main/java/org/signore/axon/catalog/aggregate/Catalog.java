package org.signore.axon.catalog.aggregate;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.signore.axon.catalog.commands.RegisterProductCommand;
import org.signore.axon.catalog.commands.RegisterCatalogCommand;
import org.signore.axon.catalog.events.ProductCreatedEvent;
import org.signore.axon.catalog.events.CatalogCreatedEvent;
import org.springframework.util.Assert;

@Aggregate
public class Catalog {

	@AggregateIdentifier
	private Integer catalogId;

	private String name;

	private List<String> skuProducts;

	protected Catalog() {
		// For Axon instantiation
	}

	@CommandHandler
	public Catalog(RegisterCatalogCommand cmd) {
		Assert.notNull(cmd.getCatalogId(), "ID should not be null");
		Assert.notNull(cmd.getName(), "Name should not be null");

		AggregateLifecycle.apply(new CatalogCreatedEvent(cmd.getCatalogId(), cmd.getName()));
	}

	public Integer getCatalogId() {
		return catalogId;
	}

	public String getName() {
		return name;
	}

	public List<String> getSkuProducts() {
		return skuProducts;
	}

	@CommandHandler
	public void addBook(RegisterProductCommand cmd) {
		Assert.notNull(cmd.getCatalogId(), "ID should not be null");
		Assert.notNull(cmd.getSku(), "Book ISBN should not be null");

		AggregateLifecycle.apply(new ProductCreatedEvent(cmd.getCatalogId(), cmd.getSku(), cmd.getLabel()));
	}

	@EventSourcingHandler
	private void handleCreatedEvent(CatalogCreatedEvent event) {
		catalogId = event.getCatalogId();
		name = event.getName();
		skuProducts = new ArrayList<>();
	}

	@EventSourcingHandler
	private void addBook(ProductCreatedEvent event) {
		skuProducts.add(event.getSku());
	}

}
