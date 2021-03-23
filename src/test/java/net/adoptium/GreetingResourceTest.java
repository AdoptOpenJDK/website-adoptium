package net.adoptium;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHelloEndpointEnglish() {
        given().header("Accept-Language", "en-US") //chrome and edge sends with a "-"
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("Hello World!"));
    }

    @Test
    public void testHelloEndpointGerman() {
        given().header("Accept-Language", "de,en-US;q=0.7,en;q=0.3") //chrome and edge sends with a "-"
                .when().get("/hello")
                .then()
                .statusCode(200)
                .body(is("Hallo Welt!"));
    }


}
