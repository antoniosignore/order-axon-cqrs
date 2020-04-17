package com.signore.demo.order.aggregate;

import com.google.common.base.Strings;
import com.signore.demo.order.api.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Slf4j
@Aggregate
@Profile("command")
@Data
public class OrderAggregate {

    @AggregateIdentifier
    private String id;
    private String email;
    private String created;
    private List<String> products;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCmd cmd) {

        if (Strings.isNullOrEmpty(cmd.getOrderId())) throw new IllegalArgumentException("orderId must be defined");
        if (Strings.isNullOrEmpty(cmd.getCustomerEmail())) throw new IllegalArgumentException("Email must be defined");

        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cmd.getCustomerEmail());
        if (!matcher.matches()) throw new IllegalArgumentException("Email not RFC 5322 valid");

        apply(new CreatedOrderEvt(cmd.getOrderId(), cmd.getCustomerEmail(), cmd.getCreatedOn()));
    }

    @CommandHandler
    public void handle(AddProductCmd cmd) {
        log.debug("handling {}", cmd);

        if (Strings.isNullOrEmpty(cmd.getOrderId())) throw new IllegalArgumentException("orderId must be defined");
        if (Strings.isNullOrEmpty(cmd.getSku())) throw new IllegalArgumentException("sku must be defined");

        apply(new AddedProductEvt(id, cmd.getSku()));
    }

    @CommandHandler
    public void handle(RemoveProductCmd cmd) {
        apply(new RemovedProductEvt(cmd.getOrderId(), cmd.getSku()));
    }

    @EventSourcingHandler
    public void on(CreatedOrderEvt evt) {
        id = evt.getOrderId();
        products = new ArrayList<>();
        created = evt.getCreatedOn();
    }

    @EventSourcingHandler
    public void on(AddedProductEvt evt) {
        products.add(evt.getId());
    }

    @EventSourcingHandler
    public void on(RemovedProductEvt evt) {
        products.remove(evt.getSku());
    }
}
