package com.signore.demo.order.commandmodel;


import lombok.extern.slf4j.Slf4j;
import org.axonframework.test.AxonAssertionError;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;
import org.signore.axon.catalog.aggregate.Catalog;
import org.signore.axon.catalog.commands.ProductUpdateCommand;
import org.signore.axon.catalog.commands.RegisterCatalogCommand;
import org.signore.axon.catalog.commands.RegisterProductCommand;
import org.signore.axon.catalog.commands.RemoveProductCommand;
import org.signore.axon.catalog.events.CatalogCreatedEvent;
import org.signore.axon.catalog.events.ProductCreatedEvent;
import org.signore.axon.catalog.events.ProductRemovedEvent;
import org.signore.axon.catalog.events.ProductUpdatedEvent;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class CatalogAggregateTest {
    private AggregateTestFixture<Catalog> testFixture;

    @Before
    public void setUp() {
        testFixture = new AggregateTestFixture<>(Catalog.class);
    }

    @Test
    public void test_create_catalog() {
        testFixture.givenNoPriorActivity()
                .when(new RegisterCatalogCommand(1, "me"))
                .expectEvents(new CatalogCreatedEvent(1, "me"));
    }

    @Test(expected = AxonAssertionError.class)
    public void test_create_catalog_missing_catalogId() {
        testFixture.givenNoPriorActivity()
                .when(new RegisterCatalogCommand(null, "me"))
                .expectException(AxonAssertionError.class)
                .expectExceptionMessage("ID should not be null")
                .expectNoEvents();
    }

    @Test
    public void test_create_catalog_empty_catalogId() {
        testFixture.givenNoPriorActivity()
                .when(new RegisterCatalogCommand(1, ""))
                .expectException(IllegalArgumentException.class)
                .expectExceptionMessage("Name should not be null")
                .expectNoEvents();
    }

    @Test
    public void test_create_product() {
        testFixture.given(new CatalogCreatedEvent(1, "me"))
                .when(new RegisterProductCommand(1, "me", "pomodori"))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new ProductCreatedEvent(1, "me", "pomodori"));
    }

    @Test
    public void test_delete_product() {
        testFixture.given(new CatalogCreatedEvent(1, "me"),
                new ProductCreatedEvent(1, "me", "pomodoro"))
                .when(new RemoveProductCommand(1, "me"))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new ProductRemovedEvent(1, "me"));
    }

    @Test
    public void test_update_product() {
        testFixture.given(
                new CatalogCreatedEvent(1, "me"),
                new ProductCreatedEvent(1, "me", "pomodoro"))
                .when(new ProductUpdateCommand(1, "me_2", "big pomodoro"))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new ProductUpdatedEvent(1, "me_2", "big pomodoro"));
    }

}
