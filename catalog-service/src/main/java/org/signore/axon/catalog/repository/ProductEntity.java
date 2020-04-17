package org.signore.axon.catalog.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class ProductEntity {
	@Id
	private String sku;
	@Column
	private int catalogId;
	@Column
	private String label;
}
