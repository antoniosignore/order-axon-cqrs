package org.signore.axon.catalog.repository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.axonframework.modelling.command.Repository;
import org.axonframework.queryhandling.QueryHandler;
import org.signore.axon.catalog.aggregate.Catalog;
import org.signore.axon.catalog.queries.GetCatalogQuery;
import org.springframework.stereotype.Service;

@Service
public class LibraryProjector {
	private final Repository<Catalog> catalogRepository;

	public LibraryProjector(Repository<Catalog> catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	@QueryHandler
	public Catalog getLibrary(GetCatalogQuery query) throws InterruptedException, ExecutionException {
		CompletableFuture<Catalog> future = new CompletableFuture<Catalog>();
		catalogRepository.load("" + query.getCatalogId()).execute(future::complete);
		return future.get();
	}

}
