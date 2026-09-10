package ru.educationservices.qascooter.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.educationservices.qascooter.Courier;
import ru.educationservices.qascooter.steps.CourierSteps;

import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {
    private Courier courierWithFullData;
    private String uniqueLogin;
    private static final String DUPLICATE_LOGIN_ERROR = "Этот логин уже используется" +
            //чтоб не смущать себя при прогонах падением теста из-за отличия в постановке и реальности текста ошибки
            ". Попробуйте другой.";
    private static final String MISSING_CREDENTIALS_ERROR = "Недостаточно данных для создания учетной записи";
    private final CourierSteps courierSteps = new CourierSteps();

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        RestAssured.basePath = "/api/v1/courier";
        uniqueLogin = "TestLogin_" + System.currentTimeMillis();
        courierWithFullData = new Courier(uniqueLogin,"TestPassword123.","TestName");
    }

    @AfterEach
    public void tearDown() {
        courierSteps.setCourier(courierWithFullData);
        courierSteps.deleteCourier();
    }

    @Test
    @DisplayName("Курьера можно создать")
    public void createNewCourier(){
        courierSteps.setCourier(courierWithFullData);
        courierSteps.createCourier()
                .then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void createDuplicateCourier(){
        courierSteps.setCourier(courierWithFullData);
        courierSteps.createCourier().then().statusCode(201);
        courierSteps.createCourier()
                .then().assertThat().statusCode(409).body("message", equalTo(DUPLICATE_LOGIN_ERROR));
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина")
    public void createCourierWithoutLogin(){
        courierSteps.setCourier(new Courier("","TestPassword123.","TestName"));
        courierSteps.createCourier()
                .then().assertThat().statusCode(400).body("message", equalTo(MISSING_CREDENTIALS_ERROR));
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля")
    public void createCourierWithoutPassword(){
        courierSteps.setCourier(new Courier(uniqueLogin,"","TestName"));
        courierSteps.createCourier()
                .then().assertThat().statusCode(400).body("message", equalTo(MISSING_CREDENTIALS_ERROR));
    }

    /*
    В документации не отмечено, является ли имя обязательным полем, и ошибки даны только для кейсов с отсутствием
    логина или пароля, так что можно посчитать, что имя необязательное, и на практике так и оказалось,
    а этот тест излишний. Но, если имя все же является обязательным, то этот тест станет нужным и покажет баг.
    */
//    @Test
//    @DisplayName("Нельзя создать курьера без имени")
//    public void createCourierWithoutFirstName(){
//        courierSteps.setCourier(new Courier(uniqueLogin,"TestPassword123.",""));
//        courierSteps.createCourier()
//                .then().assertThat().statusCode(400).body("message", equalTo(MISSING_CREDENTIALS_ERROR));
//    }
}
