package com.signore.demo.order.rest;

import com.signore.demo.order.api.AddProductCmd;
import com.signore.demo.order.api.CreateOrderCmd;
import com.signore.demo.order.api.RemoveProductCmd;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;
import java.util.concurrent.Future;

@RestController
public class OrderCommandController {

    private final CommandGateway commandGateway;

    public OrderCommandController(@SuppressWarnings("SpringJavaAutowiringInspection") CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/api/order")
    public Future<String> create(@RequestBody @Valid OrderValueBean orderValueBean) {

        Assert.notNull(orderValueBean.getEmail(), "name is mandatory for Order");

        return commandGateway.send(
                new CreateOrderCmd(orderValueBean.getOrderId(), orderValueBean.getEmail(), Instant.now().toString()));
    }

    @PostMapping("/api/order/{orderId}/add/{sku}")
    public Future<Void> add(@PathVariable String orderId, @RequestBody @Valid String sku) {
        return commandGateway.send(new AddProductCmd(orderId, sku));
    }

    @PostMapping("/api/order/{orderId}/remove/{sku}")
    public Future<Void> remove(@PathVariable String orderId, @RequestBody @Valid String sku) {
        return commandGateway.send(new RemoveProductCmd(orderId, sku));
    }

}
