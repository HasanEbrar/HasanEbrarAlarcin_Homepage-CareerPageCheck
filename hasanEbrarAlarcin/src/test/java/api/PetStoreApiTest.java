package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.internal.http.HttpResponseException;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class PetStoreApiTest {



        private static final String BASE_URL = "https://petstore.swagger.io/v2";
        private static long petId;

        @BeforeClass
        public void setup() {
            RestAssured.baseURI = BASE_URL;
        }

        // ✅ 1️⃣ Create (POST) - Yeni bir pet oluştur
        @Test(priority = 1)
        public void testCreatePet() {
            String requestBody = "{ \"id\": 12345, \"name\": \"Luna\", \"status\": \"available\" }";

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/pet")
                    .then()
                    .extract().response();

            Assert.assertEquals(response.getStatusCode(), 200);
            Assert.assertEquals(response.jsonPath().getString("name"), "Luna");

            petId = response.jsonPath().getLong("id");
            System.out.println("Created Pet ID: " + petId);
        }

        // ✅ 2️⃣ Read (GET) - Oluşturulan pet'i getir
        @Test(priority = 2, dependsOnMethods = "testCreatePet")
        public void testGetPet() {
            Response response = given()
                    .when()
                    .get("/pet/" + petId)
                    .then()
                    .extract().response();

            Assert.assertEquals(response.getStatusCode(), 200);
            Assert.assertEquals(response.jsonPath().getLong("id"), petId);
            Assert.assertEquals(response.jsonPath().getString("name"), "Luna");
        }

        // ✅ 3️⃣ Update (PUT) - Pet'in bilgilerini güncelle
        @Test(priority = 3, dependsOnMethods = "testGetPet")
        public void testUpdatePet() {
            String updatedBody = "{ \"id\": " + petId + ", \"name\": \"LunaUpdated\", \"status\": \"sold\" }";

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(updatedBody)
                    .when()
                    .put("/pet")
                    .then()
                    .extract().response();

            Assert.assertEquals(response.getStatusCode(), 200);
            Assert.assertEquals(response.jsonPath().getString("name"), "LunaUpdated");
            Assert.assertEquals(response.jsonPath().getString("status"), "sold");
        }

        // ✅ 4️⃣ Delete (DELETE) - Pet'i sil
        @Test(priority = 4, dependsOnMethods = "testUpdatePet")
        public void testDeletePet() {
            Response response = given()
                    .when()
                    .delete("/pet/" + petId)
                    .then()
                    .extract().response();

            Assert.assertEquals(response.getStatusCode(), 200);
        }

        // ❌ 5️⃣ Negatif Test: Var olmayan bir pet'i getir
        @Test(priority = 5)
        public void testGetNonExistingPet() throws HttpResponseException {
            Response response = given()
                    .when()
                    .get("/pet/999")
                    .then()
                    .extract().response();

            Assert.assertEquals(response.getStatusCode(), 404);
        }

        // ❌ 6️⃣ Negatif Test: Yanlış JSON formatı ile pet oluştur
        @Test(priority = 6)
        public void testCreatePetWithInvalidJson() {
            String invalidJson = "{ \"id\": \"abc\", \"name\": , \"status\": \"available\" }"; // Hatalı JSON

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(invalidJson)
                    .when()
                    .post("/pet")
                    .then()
                    .extract().response();

            Assert.assertNotEquals(response.getStatusCode(), 200);
        }
    }

