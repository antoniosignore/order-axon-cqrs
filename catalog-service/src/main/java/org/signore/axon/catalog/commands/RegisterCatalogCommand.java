package org.signore.axon.catalog.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Data;

@Data
public class RegisterCatalogCommand {
	@TargetAggregateIdentifier
	private final Integer catalogId;

	private final String name;
}
