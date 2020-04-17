package org.signore.axon.catalog.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Data;

@Data
public class RegisterProductCommand {
	@TargetAggregateIdentifier
	private final Integer catalogId;
	private final String sku;
	private final String label;
}
