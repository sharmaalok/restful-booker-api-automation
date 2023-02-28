package stepDefinitions;

import java.util.List;
import java.util.Map;

import org.junit.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Steps {
    private static final String USER_ID = "admin";
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
    private static String bookingcheckindate;
    private static String bookingcheckoutdate;
    private static Integer Createdbookingid; //Created during createBooking test


    @Given("API is up and running")
    public void APIhealthCheck (){
        RestAssured.baseURI = BASE_URL;
        RequestSpecification checkrequest = RestAssured.given();

        response = checkrequest.get("/ping");

        int healthcheck = response.getStatusCode();
        Assert.assertEquals(200,healthcheck);

        jsonString = response.asString();
        Assert.assertEquals("Created",jsonString);
    }
    @Given("I am an authorized user")
    public void iAmAnAuthorizedUser() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json");
        response = request.body("{ \"username\":\"" + USERNAME + "\", \"password\":\"" + PASSWORD + "\"}")
                .post("/auth");

        String jsonString = response.asString();
        System.out.println("jsonString is "+jsonString);
        token = JsonPath.from(jsonString).get("token");
        System.out.println("Token is "+token);

    }

    @Given("A list of bookings are available without filters")
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
        System.out.println("bookingid is "+bookingId);
    }

    @Given("booking details are available when specific bookingId is entered")
    public void testGetBookingById() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        request.header("Accept", "application/json");

        response = request.get("/booking/"+bookingId);

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200,statusCode);

        JsonPath jsonPathEvaluator = response.jsonPath();
        bookingfirstname = jsonPathEvaluator.get("firstname");
        Assert.assertNotNull(bookingfirstname);
        System.out.println("bookingfirstname is "+bookingfirstname);

        bookinglastname = jsonPathEvaluator.get("lastname");
        Assert.assertNotNull(bookinglastname);
        System.out.println("bookinglastname is "+bookinglastname);

        Integer bookingtotalprice = jsonPathEvaluator.get("totalprice");
        Assert.assertNotNull(bookingtotalprice);
        System.out.println("bookingtotalprice is "+bookingtotalprice);

        bookingcheckindate = jsonPathEvaluator.get("bookingdates.checkin");
        Assert.assertNotNull(bookingcheckindate);
        System.out.println("bookingcheckindate is "+bookingcheckindate);

        bookingcheckoutdate = jsonPathEvaluator.get("bookingdates.checkout");
        Assert.assertNotNull(bookingcheckoutdate);
        System.out.println("bookingcheckoutdate is "+bookingcheckoutdate);

    }
    @Given("A list of bookings are available by Name filters")
    public void testGetAllBookingIdsByName() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.param("firstname",bookingfirstname).param("lastname",bookinglastname).get("/booking");

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200,statusCode);

        jsonString = response.asString();
        List<Map<String, Integer>> bookingids = JsonPath.from(jsonString).get();
        Assert.assertTrue(bookingids.size() > 0);
        Integer bookingIdByname = bookingids.get(0).get("bookingid");
        Assert.assertNotNull(bookingIdByname);
        System.out.println("bookingIdByname is "+bookingIdByname);
    }

    @Given("A list of bookings are available By Date filters")
    public void testGetAllBookingIdsByDate() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        response = request.param("checkin",checkIn).param("checkout",checkOut).get("/booking");

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200,statusCode);

        jsonString = response.asString();
        System.out.println("response is "+jsonString);
        List<Map<String, Integer>> bookingids = JsonPath.from(jsonString).get();
        Assert.assertTrue(bookingids.size() > 0);
        Integer bookingidsbyDate = bookingids.get(0).get("bookingid");
        Assert.assertNotNull(bookingidsbyDate);
        System.out.println("bookingIdByname is "+bookingidsbyDate);
    }

    @Given("A new booking is created")
    public void testCreateNewBooking() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Accept", "application/json")
                .header("Content-Type", "application/json");
        response = request.body("{ \"firstname\": \"Test\",\"lastname\": \"Jim\",\"totalprice\": 111,\"depositpaid\": true, " +
                        "\"bookingdates\": { \"checkin\": \"2018-01-01\",\"checkout\": \"2019-01-01\" },\"additionalneeds\": \"Breakfast\" }")
                .post("/booking");

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);

        jsonString = response.asString();
        System.out.println("response is "+jsonString);

        JsonPath jsonPathEvaluator = response.jsonPath();
        Createdbookingid = jsonPathEvaluator.get("bookingid");
        Assert.assertNotNull(Createdbookingid);
        System.out.println("bookingid is "+Createdbookingid);

        String CreatedFirstName = jsonPathEvaluator.get("booking.firstname");
        Assert.assertNotNull(CreatedFirstName);
        System.out.println("CreatedFirstName is "+CreatedFirstName);

        String CreatedLastName = jsonPathEvaluator.get("booking.lastname");
        Assert.assertNotNull(CreatedLastName);
        System.out.println("CreatedLastName is "+CreatedLastName);
    }

    @Given("A current booking is updated completely")
    public void testUpdateBooking() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Accept", "application/json")
                .header("Content-Type", "application/json")
                 .header("Cookie", "token=" + token)
                   .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=");

        response = request.body("{ \"firstname\": \"Test-Update\",\"lastname\": \"Jim-Update\",\"totalprice\": 999,\"depositpaid\": false, " +
                        "\"bookingdates\": { \"checkin\": \"2018-02-01\",\"checkout\": \"2019-10-01\" },\"additionalneeds\": \"Babybedje\" }")
                .put("/booking/"+Createdbookingid);

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);

        jsonString = response.asString();
        System.out.println("response is "+jsonString);

        JsonPath jsonPathEvaluator = response.jsonPath();

        String UpdatedFirstName = jsonPathEvaluator.get("firstname");
        Assert.assertEquals("Test-Update",UpdatedFirstName);
        System.out.println("UpdatedFirstName is "+UpdatedFirstName);

        String UpdatedLastName = jsonPathEvaluator.get("lastname");
        Assert.assertEquals("Jim-Update",UpdatedLastName);
        System.out.println("UpdatedLastName is "+UpdatedLastName);

        Integer Updatedtotalprice = jsonPathEvaluator.get("totalprice");
        Assert.assertNotNull(Updatedtotalprice);

        Boolean Updateddepositpaid = jsonPathEvaluator.get("depositpaid");
        Assert.assertFalse(Updateddepositpaid);
        System.out.println("Updateddepositpaid is "+Updateddepositpaid);

        String Updatedcheckin = jsonPathEvaluator.get("bookingdates.checkin");
        Assert.assertEquals("2018-02-01",Updatedcheckin);
        System.out.println("Updatedcheckin is "+Updatedcheckin);

        String Updatedcheckout = jsonPathEvaluator.get("bookingdates.checkout");
        Assert.assertEquals("2019-10-01",Updatedcheckout);
        System.out.println("Updatedcheckout is "+Updatedcheckout);

        String Updatedadditionalneeds = jsonPathEvaluator.get("additionalneeds");
        Assert.assertEquals("Babybedje",Updatedadditionalneeds);
        System.out.println("Updatedadditionalneeds is "+Updatedadditionalneeds);
    }

    @Given("A current booking is updated with a partial payload")
    public void testPartialUpdateBooking() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Cookie", "token=" + token)
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=");

        response = request.body("{ \"firstname\": \"Test-PartialUpdate\",\"lastname\": \"Jim-PartialUpdate\",\"additionalneeds\": \"Extra bed\" }")
                .patch("/booking/"+Createdbookingid);
        System.out.println("response is "+response.asString());
        System.out.println("id is "+Createdbookingid);

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);

        jsonString = response.asString();
        System.out.println("response is "+jsonString);

        JsonPath jsonPathEvaluator = response.jsonPath();

        String partialUpdatedFirstName = jsonPathEvaluator.get("firstname");
        Assert.assertEquals("Test-PartialUpdate",partialUpdatedFirstName);
        System.out.println("partialUpdatedFirstName is "+partialUpdatedFirstName);

        String partialUpdatedLastName = jsonPathEvaluator.get("lastname");
        Assert.assertEquals("Jim-PartialUpdate",partialUpdatedLastName);
        System.out.println("partialUpdatedLastName is "+partialUpdatedLastName);

        String partialUpdatedadditionalneeds = jsonPathEvaluator.get("additionalneeds");
        Assert.assertEquals("Extra bed",partialUpdatedadditionalneeds);
        System.out.println("partialUpdatedadditionalneeds is "+partialUpdatedadditionalneeds);
    }

    @Given("A current booking is deleted")
    public void testDeleteBooking() {

        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Cookie", "token=" + token)
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=");

        response = request.delete("/booking/"+Createdbookingid);

        int statusCode = response.getStatusCode();
        Assert.assertEquals(201, statusCode);

        //verify that by fetching booking for the deleted id will result in null response

        RestAssured.baseURI = BASE_URL;
        RequestSpecification checkrequest = RestAssured.given();
        checkrequest.header("Accept", "application/json");
        response = checkrequest.get("/booking/"+Createdbookingid);

        int checkstatusCode = response.getStatusCode();
        Assert.assertEquals(404,checkstatusCode);

        jsonString = response.asString();
        Assert.assertEquals("Not Found",jsonString);
    }

    }