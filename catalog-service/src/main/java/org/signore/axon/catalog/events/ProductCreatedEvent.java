package org.signore.axon.catalog.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Data;

@Data
public class ProductCreatedEvent {
	@TargetAggregateIdentifier
	private final Integer catalogId;
	private final String sku;
	private final String label;
}
