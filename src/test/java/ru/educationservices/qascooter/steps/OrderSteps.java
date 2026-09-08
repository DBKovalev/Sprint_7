package ru.educationservices.qascooter.steps;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.educationservices.qascooter.Order;

public class OrderSteps {

    private Order order;
    private int track;

    public void setOrder(Order order){
        this.order = order;
    }

    @Step("Создать заказ и сохранить его track")
    public Response createOrder() {
        Response response = RestAssured.given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post();
        if (response.statusCode() == 201) {
            this.track = response.jsonPath().getInt("track");;
        }
        return response;
    }

    @Step("Получить список заказов")
    public Response getOrderList() {
        return RestAssured.given()
                .get();
    }

    @Step("Отменить заказ по track")
    public void cancelOrderByTrack() {
        RestAssured.given()
                .header("Content-type", "application/json")
                .body("\"track\":" + this.track)
                .when()
                .put("/cancel");
    }


}
