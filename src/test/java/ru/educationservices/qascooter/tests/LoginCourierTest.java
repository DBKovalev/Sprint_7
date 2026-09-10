package ru.educationservices.qascooter.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.educationservices.qascooter.Courier;
import ru.educationservices.qascooter.steps.CourierSteps;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

    private String uniqueLogin;
    private String uniqueWrongLogin;
    private Courier courierWithFullData;
    private static final String MISSING_CREDENTIALS_ERROR = "Недостаточно данных для входа";
    private static final String WRONG_CREDENTIALS_ERROR = "Учетная запись не найдена";
    private final CourierSteps courierSteps = new CourierSteps();

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        RestAssured.basePath = "/api/v1/courier";
        uniqueLogin = "TestLogin_" + System.currentTimeMillis();
        uniqueWrongLogin = uniqueLogin + "_wrong";
        courierWithFullData = new Courier(uniqueLogin,"TestPassword123.", "TestName");
        courierSteps.setCourier(courierWithFullData);
        courierSteps.createCourier().then().statusCode(201);
    }

    @AfterEach
    public void tearDown() {
        courierSteps.setCourier(courierWithFullData);
        courierSteps.deleteCourier();
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    public void loginValidCourier(){
        courierSteps.loginCourier()
                .then().assertThat().statusCode(200).body("id", notNullValue());
    }

    @Test
    @DisplayName("Нельзя залогинить курьера без логина")
    public void loginCourierWithoutLogin() {
        courierSteps.setCourier(new Courier("", "TestPassword123."));
        courierSteps.loginCourier()
                .then()
                .assertThat().statusCode(400).body("message", equalTo(MISSING_CREDENTIALS_ERROR));
    }

    @Test
    @DisplayName("Нельзя залогинить курьера без пароля")
    public void loginCourierWithoutPassword(){
        courierSteps.setCourier(new Courier(uniqueLogin,""));
        courierSteps.loginCourier()
                .then().assertThat().statusCode(400).body("message", equalTo(MISSING_CREDENTIALS_ERROR));
    }

    @Test
    @DisplayName("Нельзя залогинить курьера с неверным логином")
    public void loginCourierWithWrongLogin(){
        courierSteps.setCourier(new Courier(uniqueWrongLogin,"TestPassword123."));
        courierSteps.loginCourier()
                .then().assertThat().statusCode(404).body("message", equalTo(WRONG_CREDENTIALS_ERROR));
    }

    @Test
    @DisplayName("Нельзя залогинить курьера с неверным паролем")
    public void loginCourierWithWrongPassword(){
        courierSteps.setCourier(new Courier(uniqueLogin,"WrongTestPassword123."));
        courierSteps.loginCourier()
                .then().assertThat().statusCode(404).body("message", equalTo(WRONG_CREDENTIALS_ERROR));
    }

    @Test
    @DisplayName("Нельзя залогинить несуществующего курьера")
    public void loginUnknownCourier(){
        courierSteps.setCourier(new Courier(uniqueWrongLogin,"WrongTestPassword123."));
        courierSteps.loginCourier()
                .then().assertThat().statusCode(404).body("message", equalTo(WRONG_CREDENTIALS_ERROR));
    }
}
