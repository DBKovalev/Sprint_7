package ru.educationservices.qascooter.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.educationservices.qascooter.Order;
import ru.educationservices.qascooter.steps.OrderSteps;

import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {
    private final OrderSteps orderSteps = new OrderSteps();

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru";
        RestAssured.basePath = "/api/v1/orders";
    }

    /*
    Отмененный заказ остается в базе, так что не уверен, что отмена заказа != удаление заказа, но будто бы лучше
    иметь тестовый заказ в базе в статусе отмененного, чем активного, так что пусть пока так.
     */
    @AfterEach
    public void tearDown() {
        orderSteps.cancelOrderByTrack();
    }

    public static Object[][] setTestData() {
        return new Object[][] {
                {"без передачи цвета", new Order ("Круглов", "Анатолий", "Пушкина, 1", "ВДНХ",  "89111111111", 0, "2026-11-30", "Комментарий")},
                {"GREY", new Order ("Квадратов","Виктор", "Пушкина, 2", "Павелецкая", "89222222222", 1, "2026-09-15", "Комментарий", new String[]{"GREY"})},
                {"BLACK", new Order ("Ромбова","Варвара", "Пушкина, 3", "Автозаводская", "89333333333", 2, "2027-01-07", "Комментарий", new String[]{"BLACK"})},
                {"GREY, BLACK", new Order ("Параллелограммова","Вероника", "Пушкина, 4", "Театральная", "89444444444", 3, "2027-01-19", "Комментарий", new String[]{"GREY", "BLACK"})}
        };
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("setTestData")
    @DisplayName("Заказ создается с цветами: ")
    public void createOrderParametrizedTest(String name, Order order){
        orderSteps.setOrder(order);
        orderSteps.createOrder().then().assertThat().statusCode(201).body("track", notNullValue());
    }
}
