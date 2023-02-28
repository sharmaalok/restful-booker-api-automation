package stepDefinitions;

import java.util.List;
import java.util.Map;

import io.cucumber.java.en.And;
import org.junit.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Steps {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password123";
    private static final String BASE_URL = "https://restful-booker.herokuapp.com";
    private static final String checkIn = "2017-01-01";
    private static final String checkOut = "2019-12-31";
    private static String token;
    private static Response response;
    private static String jsonString;
    private static Integer bookingId;
    private static String bookingfirstname;
    private static String bookinglastname;
    private static Integer Createdbookingid; //Created during createBooking test


    @Given("API is up and running")
    public void APIhealthCheck (){
        RestAssured.baseURI = BASE_URL;
        RequestSpecification checkrequest = RestAssured.given();

        response = checkrequest.get("/ping");

        int healthcheck = response.getStatusCode();
        Assert.assertEquals(201,healthcheck);

        jsonString = response.asString();
        Assert.assertEquals("Created",jsonString);
    }
    @When("User access the CreateToken endpoint")
    public void request_AccessCreateToken() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        response = request.body("{ \"username\":\"" + USERNAME + "\", \"password\":\"" + PASSWORD + "\"}")
                .post("/auth");

    }

    @Then("A new Authorisation token is created")
    public void response_GeneratedAuthorisationtoken() {
        String jsonString = response.asString();
        token = JsonPath.from(jsonString).get("token");
        Assert.assertNotNull(token);
    }

    @Given("A list of bookings are fetched without filters via GetBookingIds API")
    public void testGetAllBookingIdsWithoutFilter() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.get("/booking");

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200,statusCode);

        jsonString = response.asString();
        List<Map<String, Integer>> bookingids = JsonPath.from(jsonString).get();
        Assert.assertTrue(bookingids.size() > 0);
        bookingId = bookingids.get(0).get("bookingid");
        Assert.assertNotNull(bookingId);
    }

    @When("A specific bookingId from the list is entered via GetBooking API")
    public void request_BookingById() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Accept", "application/json");
        response = request.get("/booking/"+bookingId);
    }

    @Then("Booking details are retrieved for the specified booking id")
    public void response_BookingById() {

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200,statusCode);

        JsonPath jsonPathEvaluator = response.jsonPath();
        bookingfirstname = jsonPathEvaluator.get("firstname");
        Assert.assertNotNull(bookingfirstname);

        bookinglastname = jsonPathEvaluator.get("lastname");
        Assert.assertNotNull(bookinglastname);

        Integer bookingtotalprice = jsonPathEvaluator.get("totalprice");
        Assert.assertNotNull(bookingtotalprice);

        String bookingcheckindate = jsonPathEvaluator.get("bookingdates.checkin");
        Assert.assertNotNull(bookingcheckindate);

        String bookingcheckoutdate = jsonPathEvaluator.get("bookingdates.checkout");
        Assert.assertNotNull(bookingcheckoutdate);
    }


    @When("Booking is fetched with Name filters via GetBooking API")
    public void request_GetAllBookingIdsByName() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.param("firstname",bookingfirstname).param("lastname",bookinglastname).get("/booking");
    }

    @Then("Name filtered booking details are retrieved correctly")
    public void response_GetAllBookingIdsByName() {
        int statusCode = response.getStatusCode();
        Assert.assertEquals(200,statusCode);

        jsonString = response.asString();
        List<Map<String, Integer>> bookingids = JsonPath.from(jsonString).get();
        Assert.assertTrue(bookingids.size() > 0);
        Integer bookingIdByname = bookingids.get(0).get("bookingid");
        Assert.assertNotNull(bookingIdByname);
    }

    @When("Booking is fetched with Date filters via GetBooking API")
    public void request_getAllBookingIdsByDate() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.param("checkin",checkIn).param("checkout",checkOut).get("/booking");

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200,statusCode);

        jsonString = response.asString();
        List<Map<String, Integer>> bookingids = JsonPath.from(jsonString).get();
        Assert.assertTrue(bookingids.size() > 0);
        Integer bookingidsbyDate = bookingids.get(0).get("bookingid");
        Assert.assertNotNull(bookingidsbyDate);
    }

    @Then("Date filtered booking details are retrieved correctly")
    public void response_GetAllBookingIdsByDate() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.param("checkin",checkIn).param("checkout",checkOut).get("/booking");

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200,statusCode);

        jsonString = response.asString();
        List<Map<String, Integer>> bookingids = JsonPath.from(jsonString).get();
        Assert.assertTrue(bookingids.size() > 0);
        Integer bookingidsbyDate = bookingids.get(0).get("bookingid");
        Assert.assertNotNull(bookingidsbyDate);
    }

    @Given("A new booking is created via CreateBooking API")
    public void request_CreateNewBooking() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Accept", "application/json")
                .header("Content-Type", "application/json");
        response = request.body("{ \"firstname\": \"Test\",\"lastname\": \"Jim\",\"totalprice\": 111,\"depositpaid\": true, " +
                        "\"bookingdates\": { \"checkin\": \"2018-01-01\",\"checkout\": \"2019-01-01\" },\"additionalneeds\": \"Breakfast\" }")
                .post("/booking");
    }

    @When("new booking with new bookingid is created")
    public void response_CreateNewBooking() {

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);

        jsonString = response.asString();

        JsonPath jsonPathEvaluator = response.jsonPath();
        Createdbookingid = jsonPathEvaluator.get("bookingid");
        Assert.assertNotNull(Createdbookingid);

        String CreatedFirstName = jsonPathEvaluator.get("booking.firstname");
        Assert.assertNotNull(CreatedFirstName);

        String CreatedLastName = jsonPathEvaluator.get("booking.lastname");
        Assert.assertNotNull(CreatedLastName);
    }

    @Then("Booking created should be available for update via UpdateBooking API")
    public void request_UpdateBooking() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Accept", "application/json")
                .header("Content-Type", "application/json")
                 .header("Cookie", "token=" + token)
                   .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=");

        response = request.body("{ \"firstname\": \"Test-Update\",\"lastname\": \"Jim-Update\",\"totalprice\": 999,\"depositpaid\": false, " +
                        "\"bookingdates\": { \"checkin\": \"2018-02-01\",\"checkout\": \"2019-10-01\" },\"additionalneeds\": \"Babybedje\" }")
                .put("/booking/"+Createdbookingid);
    }

    @And("Booking should be updated successfully")
    public void response_UpdateBooking() {

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);

        jsonString = response.asString();

        JsonPath jsonPathEvaluator = response.jsonPath();

        String UpdatedFirstName = jsonPathEvaluator.get("firstname");
        Assert.assertEquals("Test-Update",UpdatedFirstName);

        String UpdatedLastName = jsonPathEvaluator.get("lastname");
        Assert.assertEquals("Jim-Update",UpdatedLastName);

        Integer Updatedtotalprice = jsonPathEvaluator.get("totalprice");
        Assert.assertNotNull(Updatedtotalprice);

        Boolean Updateddepositpaid = jsonPathEvaluator.get("depositpaid");
        Assert.assertFalse(Updateddepositpaid);

        String Updatedcheckin = jsonPathEvaluator.get("bookingdates.checkin");
        Assert.assertEquals("2018-02-01",Updatedcheckin);

        String Updatedcheckout = jsonPathEvaluator.get("bookingdates.checkout");
        Assert.assertEquals("2019-10-01",Updatedcheckout);

        String Updatedadditionalneeds = jsonPathEvaluator.get("additionalneeds");
        Assert.assertEquals("Babybedje",Updatedadditionalneeds);
    }

    @When("Booking is updated with a partial payload via PartialUpdateBooking")
    public void request_PartialUpdateBooking() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Cookie", "token=" + token)
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=");

        response = request.body("{ \"firstname\": \"Test-PartialUpdate\",\"lastname\": \"Jim-PartialUpdate\",\"additionalneeds\": \"Extra bed\" }")
                .patch("/booking/"+Createdbookingid);
    }

    @Then("partial update should be successfull")
    public void response_PartialUpdateBooking() {

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);

        jsonString = response.asString();

        JsonPath jsonPathEvaluator = response.jsonPath();

        String partialUpdatedFirstName = jsonPathEvaluator.get("firstname");
        Assert.assertEquals("Test-PartialUpdate",partialUpdatedFirstName);

        String partialUpdatedLastName = jsonPathEvaluator.get("lastname");
        Assert.assertEquals("Jim-PartialUpdate",partialUpdatedLastName);

        String partialUpdatedadditionalneeds = jsonPathEvaluator.get("additionalneeds");
        Assert.assertEquals("Extra bed",partialUpdatedadditionalneeds);
    }

    @Given("Previously created booking is deleted via DeleteBooking API")
    public void request_DeleteBooking() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Cookie", "token=" + token)
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=");

        response = request.delete("/booking/"+Createdbookingid);

        int statusCode = response.getStatusCode();
        Assert.assertEquals(201, statusCode);
    }

    @When("Booking details for deleted bookingid are fetched again via GetBooking API")
    public void getBookingToConfirm_DeleteBooking() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification checkrequest = RestAssured.given();
        checkrequest.header("Accept", "application/json");
        response = checkrequest.get("/booking/"+Createdbookingid);
    }

    @Then("No Details should be retrieved")
    public void confirmed_DeleteBooking() {

        int checkstatusCode = response.getStatusCode();
        Assert.assertEquals(404,checkstatusCode);

        jsonString = response.asString();
        Assert.assertEquals("Not Found",jsonString);
    }

}