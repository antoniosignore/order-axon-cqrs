package com.signore.demo.order.commandmodel;


import com.signore.demo.order.aggregate.OrderAggregate;
import com.signore.demo.order.api.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class OrderAggregateTest {
    private AggregateTestFixture<OrderAggregate> testFixture;

    @Before
    public void setUp() {
        testFixture = new AggregateTestFixture<>(OrderAggregate.class);
    }

    @Test
    public void test_create_order() {
        testFixture.givenNoPriorActivity()
                .when(new CreateOrderCmd("123", "antonio.signore@gmail.com", "sometime"))
                .expectEvents(new CreatedOrderEvt("123", "antonio.signore@gmail.com", "sometime"));
    }

    @Test
    public void test_create_order_empty_order_id() {
        testFixture.givenNoPriorActivity()
                .when(new CreateOrderCmd("", "antonio.signore@gmail.com", "sometime"))
                .expectException(IllegalArgumentException.class)
                .expectExceptionMessage("orderId must be defined");
    }

    @Test
    public void test_create_order_empty_email() {
        testFixture.givenNoPriorActivity()
                .when(new CreateOrderCmd("423", "", "sometime"))
                .expectException(IllegalArgumentException.class)
                .expectExceptionMessage("Email must be defined");
    }

    @Test
    public void test_create_order_bad_email() {
        testFixture.givenNoPriorActivity()
                .when(new CreateOrderCmd("123", "antonio.signoregmail.com", "sometime"))
                .expectException(IllegalArgumentException.class)
                .expectExceptionMessage("Email not RFC 5322 valid");
    }

    @Test
    public void test_add_product_to_order() {
        testFixture
                .given(new CreatedOrderEvt("123", "antonio.signore@gmail.com", "sometime"))
                .when(new AddProductCmd("123", "mysku"))
                .expectEvents(new AddedProductEvt("123", "mysku"));
    }


    @Test
    public void test_delete_product_from_order() {
        testFixture
                .given(new CreatedOrderEvt("123", "antonio.signore@gmail.com", "sometime"),
                        new AddedProductEvt("123", "mysku"))
                .when(new RemoveProductCmd("123", "mysku"))
                .expectEvents(new RemovedProductEvt("123", "mysku"));
    }

}
