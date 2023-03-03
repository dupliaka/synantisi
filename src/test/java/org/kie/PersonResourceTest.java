package org.kie;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.kie.domain.Person;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class PersonResourceTest {
    @Test
    public void getAll(){
//        List<Person> personList = given().when().get("person/count")
//                .then()
//                .statusCode(200)
//                .extract()
//                .body().jsonPath();
//        assertFalse(personList.isEmpty());
//        Person firstPerson = personList.get(0);
//        assertNotNull(firstPerson.getFullName());
    }
}
