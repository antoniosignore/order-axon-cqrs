package com.signore.demo.order.gui;

import com.signore.demo.order.api.*;
import com.vaadin.annotations.Push;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SpringUI
@Push
@Profile("gui")
public class OrderUI extends UI {

    private final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    private OrderSummaryDataProvider orderSummaryDataProvider;
    private ScheduledFuture<?> updaterThread;

    public OrderUI(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        HorizontalLayout commandBar = new HorizontalLayout();
        commandBar.setWidth("100%");
        commandBar.addComponents(issuePanel(), orderPanel());

        Grid summary = summaryGrid();

        HorizontalLayout statusBar = new HorizontalLayout();
        Label statusLabel = new Label("Status");
        statusBar.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        statusBar.addComponent(statusLabel);
        statusBar.setWidth("100%");

        VerticalLayout layout = new VerticalLayout();
        layout.addComponents(commandBar, summary, statusBar);
        layout.setExpandRatio(summary, 1f);
        layout.setSizeFull();

        setContent(layout);

        UI.getCurrent().setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                Throwable cause = event.getThrowable();
                log.error("an error occured", cause);
                while (cause.getCause() != null) cause = cause.getCause();
                Notification.show("Error", cause.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });

        setPollInterval(1000);
        int offset = Page.getCurrent().getWebBrowser().getTimezoneOffset();
        // offset is in milliseconds
        ZoneOffset instantOffset = ZoneOffset.ofTotalSeconds(offset / 1000);
        StatusUpdater statusUpdater = new StatusUpdater(statusLabel, instantOffset);
        updaterThread = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(statusUpdater, 1000,
                5000, TimeUnit.MILLISECONDS);
        setPollInterval(1000);
        getSession().getSession().setMaxInactiveInterval(30);
        addDetachListener((DetachListener) detachEvent -> {
            log.warn("Closing UI");
            updaterThread.cancel(true);
        });
    }

    private Panel issuePanel() {
        TextField id = new TextField("Order Id");
        TextField name = new TextField("Email");
        Button submit = new Button("Submit");

        submit.addClickListener(evt -> {
            commandGateway.sendAndWait(new CreateOrderCmd(id.getValue(), name.getValue(), Instant.now().toString()));
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE)
                    .addCloseListener(e -> orderSummaryDataProvider.refreshAll());
        });

        FormLayout form = new FormLayout();
        form.addComponents(id, name, submit);
        form.setMargin(true);

        Panel panel = new Panel("Create Order");
        panel.setContent(form);
        return panel;
    }

    private Panel orderPanel() {
        TextField id = new TextField("Order");
        TextField sku = new TextField("SKU");
        TextField label = new TextField("Label");
        Button submit = new Button("Submit");

        submit.addClickListener(evt -> {
            commandGateway.sendAndWait(new AddProductCmd(id.getValue(), sku.getValue()));
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE)
                    .addCloseListener(e -> orderSummaryDataProvider.refreshAll());
        });

        FormLayout form = new FormLayout();
        form.addComponents(id, sku, label, submit);
        form.setMargin(true);

        Panel panel = new Panel("Product Panel");
        panel.setContent(form);
        return panel;
    }

    private Grid summaryGrid() {
        orderSummaryDataProvider = new OrderSummaryDataProvider(queryGateway);
        Grid<OrderSummary> grid = new Grid<>();
        grid.addColumn(OrderSummary::getOrderId).setCaption("Order Id");
        grid.addColumn(OrderSummary::getEmail).setCaption("Email");
        grid.addColumn(OrderSummary::getNumberOfProducts).setCaption("Number of Products");
        grid.addColumn(OrderSummary::getCreatedOn).setCaption("Created On");
        grid.setSizeFull();
        grid.setDataProvider(orderSummaryDataProvider);
        return grid;
    }

    public class StatusUpdater implements Runnable {
        private final Label statusLabel;
        private final ZoneOffset instantOffset;

        public StatusUpdater(Label statusLabel, ZoneOffset instantOffset) {
            this.statusLabel = statusLabel;
            this.instantOffset = instantOffset;
        }

        @Override
        public void run() {
            CountOrderSummariesQuery query = new CountOrderSummariesQuery();
            queryGateway.query(
                    query, CountOrderSummariesResponse.class).whenComplete((r, exception) -> {
                if (exception == null)
                    statusLabel.setValue(Instant.ofEpochMilli(r.getLastEvent()).atOffset(instantOffset).toString());
            });

        }

    }
}
