package org.signore.axon.catalog.events;

import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
public class ProductRemovedEvent {
    @TargetAggregateIdentifier
    private final Integer catalogId;
    private final String sku;
}
