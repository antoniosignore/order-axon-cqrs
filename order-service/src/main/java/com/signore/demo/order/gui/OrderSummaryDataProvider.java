package com.signore.demo.order.gui;

import com.signore.demo.order.api.*;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.DataChangeEvent;
import com.vaadin.data.provider.Query;
import lombok.*;
import lombok.extern.slf4j.XSlf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@XSlf4j
@RequiredArgsConstructor
public class OrderSummaryDataProvider extends AbstractBackEndDataProvider<OrderSummary, Void> {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private final QueryGateway queryGateway;

    /**
     * We need to keep track of our current subscriptions. To avoid subscriptions being modified while
     * we are processing query updates, the methods on these class are synchronized.
     */

    private SubscriptionQueryResult<List<OrderSummary>, OrderSummary> fetchQueryResult;
    private SubscriptionQueryResult<CountOrderSummariesResponse, CountChangedUpdate> countQueryResult;

    @Getter
    @Setter
    @NonNull
    private OrderSummaryFilter filter = new OrderSummaryFilter("");

    @Override
    @Synchronized
    protected Stream<OrderSummary> fetchFromBackEnd(Query<OrderSummary, Void> query) {
        /*
         * If we are already doing a query (and are subscribed to it), cancel are subscription
         * and forget about the query.
         */
        if (fetchQueryResult != null) {
            fetchQueryResult.cancel();
            fetchQueryResult = null;
        }
        FetchOrderSummariesQuery fetchOrderSummariesQuery =
                new FetchOrderSummariesQuery(query.getOffset(), query.getLimit(), filter);
        log.trace("submitting {}", fetchOrderSummariesQuery);
        /*
         * Submitting our query as a subscriptionquery, specifying both the initially expected
         * response type (multiple CardSummaries) as wel as the expected type of the updates
         * (single CardSummary object). The result is a SubscriptionQueryResult which contains
         * a project reactor Mono for the initial response, and a Flux for the updates.
         */
        fetchQueryResult = queryGateway.subscriptionQuery(fetchOrderSummariesQuery,
                ResponseTypes.multipleInstancesOf(OrderSummary.class),
                ResponseTypes.instanceOf(OrderSummary.class));
        /*
         * Subscribing to the updates before we get the initial results.
         */
        fetchQueryResult.updates().subscribe(
                orderSummary -> {
                    log.trace("processing query update for {}: {}", fetchOrderSummariesQuery, orderSummary);
                    /* This is a Vaadin-specific call to update the UI as a result of data changes. */
                    fireEvent(new DataChangeEvent.DataRefreshEvent<>(this, orderSummary));
                });
        /*
         * Returning the initial result.
         */
        return fetchQueryResult.initialResult().block().stream();
    }

    @Override
    @Synchronized
    protected int sizeInBackEnd(Query<OrderSummary, Void> query) {
        if (countQueryResult != null) {
            countQueryResult.cancel();
            countQueryResult = null;
        }
        CountOrderSummariesQuery countOrderSummariesQuery = new CountOrderSummariesQuery(filter);
        log.trace("submitting {}", countOrderSummariesQuery);
        countQueryResult = queryGateway.subscriptionQuery(countOrderSummariesQuery,
                ResponseTypes.instanceOf(CountOrderSummariesResponse.class),
                ResponseTypes.instanceOf(CountChangedUpdate.class));
        /* When the count changes (new giftcards issued), the UI has to do an entirely new query (this is
         * how the Vaadin grid works). When we're bulk issuing, it doesn't make sense to do that on every single
         * issue event. Therefore, we buffer the updates for 250 milliseconds using reactor, and do the new
         * query at most once per 250ms.
         */
        countQueryResult.updates().buffer(Duration.ofMillis(250)).subscribe(
                countChanged -> {
                    log.trace("processing query update for {}: {}", countOrderSummariesQuery, countChanged);
                    /* This won't do, would lead to immediate new queries, looping a few times. */
//                        fireEvent(new DataChangeEvent(this));
                    executorService.execute(() -> fireEvent(new DataChangeEvent<>(this)));
                });
        return countQueryResult.initialResult().block().getCount();
    }

    @Synchronized
    void shutDown() {
        if (fetchQueryResult != null) {
            fetchQueryResult.cancel();
            fetchQueryResult = null;
        }
        if (countQueryResult != null) {
            countQueryResult.cancel();
            countQueryResult = null;
        }
    }

}
