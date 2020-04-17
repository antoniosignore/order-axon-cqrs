package com.signore.demo.order.api

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.Instant

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery

// order aggregate
data class CreateOrderCmd(@TargetAggregateIdentifier val orderId: String, val customerEmail: String, val createdOn: String)
data class CreatedOrderEvt(val orderId: String, val email: String, val createdOn: String)

data class AddProductCmd(@TargetAggregateIdentifier val orderId: String, val sku: String)
data class AddedProductEvt(val id: String, val sku: String)

data class RemoveProductCmd(@TargetAggregateIdentifier val orderId: String, val sku: String)
data class RemovedProductEvt(val orderId: String, val sku: String)

@Entity
@NamedQueries(
        NamedQuery(name = "OrderSummary.fetch",
                query = "SELECT c FROM OrderSummary c WHERE c.orderId LIKE CONCAT(:idStartsWith, '%') ORDER BY c.orderId"),
        NamedQuery(name = "OrderSummary.count",
                query = "SELECT COUNT(c) FROM OrderSummary c WHERE c.orderId LIKE CONCAT(:idStartsWith, '%')"))

data class OrderSummary(@Id var orderId: String, var numberOfProducts: Int, val email: String, val createdOn: String) {
    constructor() : this("", 0, "", Instant.now().toString())
}

data class OrderSummaryFilter(val idStartsWith: String = "")

class CountOrderSummariesQuery(val filter: OrderSummaryFilter = OrderSummaryFilter()) {
    override fun toString(): String = "CountOrderSummariesQuery"
}

data class CountOrderSummariesResponse(val count: Int, val lastEvent: Long)

data class FetchOrderSummariesQuery(val offset: Int, val limit: Int, val filter: OrderSummaryFilter)

class CountChangedUpdate()