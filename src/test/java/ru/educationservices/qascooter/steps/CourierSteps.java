package ru.educationservices.qascooter.steps;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.educationservices.qascooter.Courier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierSteps {

    private Courier courier;

    public void setCourier(Courier courier){
        this.courier = courier;
    }

    @Step("Создать курьера")
    public Response createCourier() {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post();
    }

    @Step("Логин курьера")
    public Response loginCourier() {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/login");
    }

    @Step("Удалить курьера по id")
    public void deleteCourier() {
        Response loginResponse = loginCourier();
        if (loginResponse.getStatusCode() != 200) {
            return;
        }
        int id = loginResponse.jsonPath().getInt("id");
        given()
                .header("Content-type", "application/json")
                .when()
                .delete("/" + id)
                .then().assertThat().statusCode(200).body("ok", equalTo(true));
    }
}
