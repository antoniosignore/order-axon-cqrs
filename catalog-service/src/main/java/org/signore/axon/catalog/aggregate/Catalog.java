package org.signore.axon.catalog.aggregate;

import com.google.common.base.Strings;
import lombok.Getter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.signore.axon.catalog.commands.ProductUpdateCommand;
import org.signore.axon.catalog.commands.RegisterCatalogCommand;
import org.signore.axon.catalog.commands.RegisterProductCommand;
import org.signore.axon.catalog.commands.RemoveProductCommand;
import org.signore.axon.catalog.events.CatalogCreatedEvent;
import org.signore.axon.catalog.events.ProductCreatedEvent;
import org.signore.axon.catalog.events.ProductRemovedEvent;
import org.signore.axon.catalog.events.ProductUpdatedEvent;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Aggregate
@Getter
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

		if (Strings.isNullOrEmpty(cmd.getName()))
			throw new IllegalArgumentException("Name should not be null");

		AggregateLifecycle.apply(new CatalogCreatedEvent(cmd.getCatalogId(), cmd.getName()));
	}

	@CommandHandler
	public void addProduct(RegisterProductCommand cmd) {
		Assert.notNull(cmd.getCatalogId(), "ID should not be null");
		Assert.notNull(cmd.getSku(), "SKU should not be null");

		AggregateLifecycle.apply(new ProductCreatedEvent(cmd.getCatalogId(), cmd.getSku(), cmd.getLabel()));
	}

	@CommandHandler
	public void removeProduct(RemoveProductCommand cmd) {
		Assert.notNull(cmd.getCatalogId(), "ID should not be null");
		Assert.notNull(cmd.getSku(), "SKU should not be null");

		AggregateLifecycle.apply(new ProductRemovedEvent(cmd.getCatalogId(), cmd.getSku()));
	}

	@CommandHandler
	public void updateProduct(ProductUpdateCommand cmd) {
		Assert.notNull(cmd.getCatalogId(), "ID should not be null");
		Assert.notNull(cmd.getSku(), "SKU should not be null");

		AggregateLifecycle.apply(new ProductUpdatedEvent(cmd.getCatalogId(), cmd.getSku(), cmd.getLabel()));
	}

	@EventSourcingHandler
	private void handleCreatedEvent(CatalogCreatedEvent event) {
		catalogId = event.getCatalogId();
		name = event.getName();
		skuProducts = new ArrayList<>();
	}

	@EventSourcingHandler
	private void handleCreatedEvent(ProductRemovedEvent event) {
		catalogId = event.getCatalogId();
		skuProducts.remove(event.getSku());
	}

	@EventSourcingHandler
	private void handleUpdatedEvent(ProductUpdatedEvent event) {
		catalogId = event.getCatalogId();
		skuProducts.remove(event.getSku());
	}

	@EventSourcingHandler
	private void addCreated(ProductCreatedEvent event) {
		skuProducts.add(event.getSku());
	}

}
