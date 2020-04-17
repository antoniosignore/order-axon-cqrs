package com.signore.demo.order.query;

import com.signore.demo.order.api.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.List;

@Component
@XSlf4j
@RequiredArgsConstructor
@Profile("query")
public class OrderSummaryProjection {

    private final EntityManager entityManager;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void on(CreatedOrderEvt event) {
        log.trace("projecting {}", event);
        /*
         * Update our read model by inserting the new card. This is done so that upcoming regular
         * (non-subscription) queries get correct data.
         */
        entityManager.persist(new OrderSummary(event.getOrderId(), 0, event.getEmail(), Instant.now().toString()));
        /*
         * Serve the subscribed queries by emitting an update. This reads as follows:
         * - to all current subscriptions of type CountCardSummariesQuery
         * - for which is true that the id of the gift card having been issued starts with the idStartWith string
         *   in the query's filter
         * - send a message that the count of queries matching this query has been changed.
         */
        queryUpdateEmitter.emit(CountOrderSummariesQuery.class,
                query -> event.getOrderId().startsWith(query.getFilter().getIdStartsWith()),
                new CountChangedUpdate());
    }

    @EventHandler
    public void on(AddedProductEvt event) {
        log.trace("projecting {}", event);
        /*
         * Update our read model by updating the existing order. This is done so that upcoming regular
         * (non-subscription) queries get correct data.
         */
        OrderSummary summary = entityManager.find(OrderSummary.class, event.getId());

        summary.setNumberOfProducts(summary.getNumberOfProducts() + 1);
        /*
         * Serve the subscribed queries by emitting an update. This reads as follows:
         * - to all current subscriptions of type FetchOrderSummariesQuery
         * - send a message containing the new state of this order summary
         */
        queryUpdateEmitter.emit(FetchOrderSummariesQuery.class,
                query -> event.getId().startsWith(query.getFilter().getIdStartsWith()),
                summary);
    }

    @QueryHandler
    public List<OrderSummary> handle(FetchOrderSummariesQuery query) {
        log.trace("handling {}", query);
        TypedQuery<OrderSummary> jpaQuery = entityManager.createNamedQuery("OrderSummary.fetch", OrderSummary.class);
        jpaQuery.setParameter("idStartsWith", query.getFilter().getIdStartsWith());
        jpaQuery.setFirstResult(query.getOffset());
        jpaQuery.setMaxResults(query.getLimit());
        return log.exit(jpaQuery.getResultList());
    }

    @QueryHandler
    public CountOrderSummariesResponse handle(CountOrderSummariesQuery query) {
        log.trace("handling {}", query);
        TypedQuery<Long> jpaQuery = entityManager.createNamedQuery("OrderSummary.count", Long.class);
        jpaQuery.setParameter("idStartsWith", query.getFilter().getIdStartsWith());
        return log.exit(new CountOrderSummariesResponse(jpaQuery.getSingleResult().intValue(), Instant.now().toEpochMilli()));
    }

}
