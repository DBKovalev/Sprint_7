package ru.educationservices.qascooter.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.educationservices.qascooter.steps.OrderSteps;

import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest {

    private final OrderSteps orderSteps = new OrderSteps();

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru";
        RestAssured.basePath = "/api/v1/orders";
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    public void getOrderListWithoutResponseBody(){
        orderSteps.getOrderList()
                .then().assertThat().statusCode(200).body("orders", notNullValue());
    }

}
