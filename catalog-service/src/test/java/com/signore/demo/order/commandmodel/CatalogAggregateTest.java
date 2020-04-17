package com.signore.demo.order.commandmodel;


import lombok.extern.slf4j.Slf4j;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class CatalogAggregateTest {
//    private AggregateTestFixture<CatalogAggregate> testFixture;
//
//    @Before
//    public void setUp() {
//        testFixture = new AggregateTestFixture<>(CatalogAggregate.class);
//    }
//
//    @Test
//    public void test_create_catalog() {
//        testFixture.givenNoPriorActivity()
//                .when(new CreateCatalogCmd("123", "me"))
//                .expectEvents(new CreatedCatalogEvt("123", "me"));
//    }
//
//    @Test
//    public void test_create_catalog_missing_catalogId() {
//        testFixture.givenNoPriorActivity()
//                .when(new CreateCatalogCmd("", "me"))
//                .expectException(IllegalArgumentException.class)
//                .expectExceptionMessage("catalogId must be defined")
//                .expectNoEvents();
//    }
//
//    @Test
//    public void test_create_catalog_empty_catalogId() {
//        testFixture.givenNoPriorActivity()
//                .when(new CreateCatalogCmd("", "me"))
//                .expectException(IllegalArgumentException.class)
//                .expectExceptionMessage("catalogId must be defined")
//                .expectNoEvents();
//    }
//
//    @Test
//    public void test_create_catalog_empty_name() {
//        testFixture.givenNoPriorActivity()
//                .when(new CreateCatalogCmd("31231", ""))
//                .expectException(IllegalArgumentException.class)
//                .expectExceptionMessage("name must be defined")
//                .expectNoEvents();
//    }
//
//    @Test
//    public void test_create_product() {
//        testFixture.given(new CreatedCatalogEvt("123", "me"))
//                .when(new CreateProductCmd("123", "me", "pomodori"))
//                .expectSuccessfulHandlerExecution()
//                .expectEvents(new CreatedProductEvt("123", "me", "pomodori"));
//    }
//
//    @Test
//    public void test_delete_product() {
//        testFixture.given(new CreatedCatalogEvt("123", "me"),
//                new CreatedProductEvt("123", "me", "pomodoro"))
//                .when(new DeleteProductCmd("123", "me"))
//                .expectSuccessfulHandlerExecution()
//                .expectEvents(new DeletedProductEvt("123", "me"));
//    }
//
//    @Test
//    public void test_update_product() {
//        testFixture.given(
//                new CreatedCatalogEvt("123", "me"),
//                new CreatedProductEvt("123", "me", "pomodoro"))
//                .when(new UpdateProductCmd("123", "me_2", "big pomodoro"))
//                .expectSuccessfulHandlerExecution()
//                .expectEvents(new UpdatedProductEvt("123", "big pomodoro","me_2"));
//    }

}
