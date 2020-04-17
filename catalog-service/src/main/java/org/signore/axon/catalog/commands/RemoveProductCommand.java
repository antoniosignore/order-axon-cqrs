package org.signore.axon.catalog.commands;

import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
public class RemoveProductCommand {
    @TargetAggregateIdentifier
    private final Integer catalogId;
    private final String sku;
}
