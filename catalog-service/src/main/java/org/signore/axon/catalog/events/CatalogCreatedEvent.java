package org.signore.axon.catalog.events;

import lombok.Data;

@Data
public class CatalogCreatedEvent {
	private final Integer catalogId;
	private final String name;
}
