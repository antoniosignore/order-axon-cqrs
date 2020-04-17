package org.signore.axon.catalog.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class ProductUpdateCommand {
    @TargetAggregateIdentifier
    private final Integer catalogId;
    private final String sku;
    private final String label;
}
