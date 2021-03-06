package in.ekstep.am.integration;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import in.ekstep.am.builders.GrantConsumerRequestBuilder;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class GrantConsumerTest {

  @LocalServerPort
  private int serverPort;

  @Rule
  public WireMockRule amAdminApi = new WireMockRule(3010);

  @Before
  public void setUp() throws Exception {
    amAdminApi.resetAll();
  }

  @Test
  public void shouldGrantAccessToExistingConsumerToSingleGroup() throws Exception {

    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8be-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(404)));
    amAdminApi.stubFor(post(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(201)
            .withBody("{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}")));
    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody("{\"data\":[{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}],\"total\":1}")));

    given()
        .port(serverPort)
        .body(new GrantConsumerRequestBuilder().build())
        .contentType(ContentType.JSON)
        .post("/v1/consumer/Purandara/grant")
        .then()
        .body("id", is("ekstep.api.am.adminutil.consumer.grant"))
        .body("ver", is("1.0"))
        .body("ets", is(greaterThan(0L)))
        .body("params.status", is("successful"))
        .body("params.err", is(nullValue()))
        .body("params.errmsg", is(nullValue()))
        .body("params.resmsgid", is(not(nullValue())))
        .body("result.username", is("Purandara"))
        .body("result.groups", hasItem("group1"))
        .body("result.groups", hasSize(1));

    amAdminApi.verify(postRequestedFor(urlEqualTo("/consumers/Purandara/acls/"))
        .withRequestBody(equalTo("group=group1")));
  }

  @Test
  public void shouldDeleteOldGroupWhenGrantingAccessToExistingConsumerToSingleGroup() throws Exception {

    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8be-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(204)));
    amAdminApi.stubFor(post(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(201)
            .withBody("{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}")));
    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody("{\"data\":[{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}],\"total\":1}")));

    given()
        .port(serverPort)
        .body(new GrantConsumerRequestBuilder().build())
        .contentType(ContentType.JSON)
        .post("/v1/consumer/Purandara/grant")
        .then()
        .body("id", is("ekstep.api.am.adminutil.consumer.grant"))
        .body("ver", is("1.0"))
        .body("ets", is(greaterThan(0L)))
        .body("params.status", is("successful"))
        .body("params.err", is(nullValue()))
        .body("params.errmsg", is(nullValue()))
        .body("params.resmsgid", is(not(nullValue())))
        .body("result.username", is("Purandara"))
        .body("result.groups", hasItem("group1"))
        .body("result.groups", hasSize(1));

    amAdminApi.verify(postRequestedFor(urlEqualTo("/consumers/Purandara/acls/"))
        .withRequestBody(equalTo("group=group1")));
    amAdminApi.verify(deleteRequestedFor(urlEqualTo("/consumers/Purandara/acls/784bc8be-9f59-44b3-8ccb-545d16651f46")));
  }

  @Test
  public void shouldDeleteMultipleOldGroupsWhenGrantingAccessToExistingConsumerToSingleGroup() throws Exception {

    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8be-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(204)));
    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8bf-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(204)));
    amAdminApi.stubFor(post(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(201)
            .withBody("{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}")));
    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody("{\"data\":[{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"},{\"group\":\"group2\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8bf-9f59-44b3-8ccb-545d16651f46\"}],\"total\":1}")));

    given()
        .port(serverPort)
        .body(new GrantConsumerRequestBuilder().build())
        .contentType(ContentType.JSON)
        .post("/v1/consumer/Purandara/grant")
        .then()
        .body("id", is("ekstep.api.am.adminutil.consumer.grant"))
        .body("ver", is("1.0"))
        .body("ets", is(greaterThan(0L)))
        .body("params.status", is("successful"))
        .body("params.err", is(nullValue()))
        .body("params.errmsg", is(nullValue()))
        .body("params.resmsgid", is(not(nullValue())))
        .body("result.username", is("Purandara"))
        .body("result.groups", hasItem("group1"))
        .body("result.groups", hasItem("group2"))
        .body("result.groups", hasSize(2));

    amAdminApi.verify(postRequestedFor(urlEqualTo("/consumers/Purandara/acls/"))
        .withRequestBody(equalTo("group=group1")));
    amAdminApi.verify(deleteRequestedFor(urlEqualTo("/consumers/Purandara/acls/784bc8be-9f59-44b3-8ccb-545d16651f46")));
    amAdminApi.verify(deleteRequestedFor(urlEqualTo("/consumers/Purandara/acls/784bc8bf-9f59-44b3-8ccb-545d16651f46")));
  }

  @Test
  public void shouldGrantAccessToExistingConsumerToMultipleGroups() throws Exception {

    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8be-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(404)));
    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8bf-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(404)));
    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody("{\"data\":[{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"},{\"group\":\"group5\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555627000,\"id\":\"784bc8bf-9f59-44b3-8ccb-545d16651f46\"}],\"total\":2}")));

    amAdminApi.stubFor(post(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(201)
            .withBody("{\"group\":\"group2,group3\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}")));
    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody("{\"data\":[{\"group\":\"group2,group3\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}],\"total\":1}")));

    given()
        .port(serverPort)
        .body(new GrantConsumerRequestBuilder().withGroups(new String[]{"group2", "group3"}).build())
        .contentType(ContentType.JSON)
        .post("/v1/consumer/Purandara/grant")
        .then()
        .body("id", is("ekstep.api.am.adminutil.consumer.grant"))
        .body("ver", is("1.0"))
        .body("ets", is(greaterThan(0L)))
        .body("params.status", is("successful"))
        .body("params.err", is(nullValue()))
        .body("params.errmsg", is(nullValue()))
        .body("params.resmsgid", is(not(nullValue())))
        .body("result.username", is("Purandara"))
        .body("result.groups", hasItem("group2"))
        .body("result.groups", hasItem("group3"))
        .body("result.groups", hasSize(2));

    amAdminApi.verify(postRequestedFor(urlEqualTo("/consumers/Purandara/acls/"))
        .withRequestBody(equalTo("group=group2")));

    amAdminApi.verify(postRequestedFor(urlEqualTo("/consumers/Purandara/acls/"))
        .withRequestBody(equalTo("group=group3")));

  }

  @Test
  public void shouldNotGrantAccessToGroupsForNonExistingConsumer() throws Exception {

    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(404)));

    amAdminApi.stubFor(post(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(404)
            .withBody("{\"message\":\"Not found\"}")));

    given()
        .port(serverPort)
        .body(new GrantConsumerRequestBuilder().withGroups(new String[]{"group2", "group3"}).build())
        .contentType(ContentType.JSON)
        .post("/v1/consumer/Purandara/grant")
        .then()
        .body("id", is("ekstep.api.am.adminutil.consumer.grant"))
        .body("ver", is("1.0"))
        .body("ets", is(greaterThan(0L)))
        .body("params.status", is("failed"))
        .body("params.err", is("GROUP_ASSIGN_ERROR"))
        .body("params.errmsg", is("ERROR WHEN GRANTING CONSUMER ACCESS TO API GROUP : group2. CONSUMER DOES NOT EXISTS"))
        .body("params.resmsgid", is(not(nullValue())))
        .body("result.username", is("Purandara"))
        .body("result.groups", hasItem("group2"))
        .body("result.groups", hasItem("group3"))
        .body("result.groups", hasSize(2));

    amAdminApi.verify(postRequestedFor(urlEqualTo("/consumers/Purandara/acls/"))
        .withRequestBody(equalTo("group=group2")));
  }

  @Test
  public void shouldReturnErrorResponseWhenAmReturnsErrorWhenGrantingAccessToConsumer() throws Exception {
    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8be-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(204)));
    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8bf-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(204)));
    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody("{\"data\":[{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"},{\"group\":\"group5\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555627000,\"id\":\"784bc8bf-9f59-44b3-8ccb-545d16651f46\"}],\"total\":2}")));
    amAdminApi.stubFor(post(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(500)
            .withBody("{\"message\":\"AM internal error\"}")));

    given()
        .port(serverPort)
        .body(new GrantConsumerRequestBuilder().withGroups(new String[]{"group2", "group3"}).build())
        .contentType(ContentType.JSON)
        .post("/v1/consumer/Purandara/grant")
        .then()
        .body("params.status", is("failed"))
        .body("params.err", is("GROUP_ASSIGN_ERROR"))
        .body("params.errmsg", is("ERROR WHEN GRANTING CONSUMER ACCESS TO API GROUP:group2"))
        .body("result.username", is("Purandara"))
        .body("result.groups", hasItem("group2"))
        .body("result.groups", hasItem("group3"))
        .body("result.groups", hasSize(2));
  }

  @Test
  public void shouldNotFailIfConsumerAlreadyHasAccessToApiGroups() throws Exception {
    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8be-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(204)));
    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8bf-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(204)));
    amAdminApi.stubFor(post(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(400)
            .withBody("{\"group\":\"ACL group already exist for this consumer\"}\n")));
    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody("{\"data\":[{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}],\"total\":1}")));
    given()
        .port(serverPort)
        .body(new GrantConsumerRequestBuilder().build())
        .contentType(ContentType.JSON)
        .post("/v1/consumer/Purandara/grant")
        .then()
        .body("params.status", is("successful"))
        .body("result.username", is("Purandara"))
        .body("result.groups", hasItem("group1"))
        .body("result.groups", hasSize(1));
  }

  @Test
  public void shouldGetAllGroupsAfterGrantingAccess() throws Exception {

    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8be-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(204)));
    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8bf-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(204)));
    amAdminApi.stubFor(post(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(201)
            .withBody("{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}")));
    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody("{\"data\":[{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"},{\"group\":\"group5\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555627000,\"id\":\"784bc8bf-9f59-44b3-8ccb-545d16651f46\"}],\"total\":2}")));

    given()
        .port(serverPort)
        .body(new GrantConsumerRequestBuilder().build())
        .contentType(ContentType.JSON)
        .post("/v1/consumer/Purandara/grant")
        .then()
        .body("id", is("ekstep.api.am.adminutil.consumer.grant"))
        .body("ver", is("1.0"))
        .body("ets", is(greaterThan(0L)))
        .body("params.status", is("successful"))
        .body("params.err", is(nullValue()))
        .body("params.errmsg", is(nullValue()))
        .body("params.resmsgid", is(not(nullValue())))
        .body("result.username", is("Purandara"))
        .body("result.groups", hasItem("group1"))
        .body("result.groups", hasItem("group5"))
        .body("result.groups", hasSize(2));

    amAdminApi.verify(postRequestedFor(urlEqualTo("/consumers/Purandara/acls/"))
        .withRequestBody(equalTo("group=group1")));
  }

  @Test
  public void shouldGetAllUniqueGroupsAfterGrantingAccess() throws Exception {

    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8be-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(204)));
    amAdminApi.stubFor(delete(urlEqualTo("/consumers/Purandara/acls/784bc8bf-9f59-44b3-8ccb-545d16651f46"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(204)));
    amAdminApi.stubFor(post(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(201)
            .withBody("{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}")));
    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(200)
            .withBody("{\"data\":[{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"},{\"group\":\"group5,group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555627000,\"id\":\"784bc8bf-9f59-44b3-8ccb-545d16651f46\"}],\"total\":2}")));

    given()
        .port(serverPort)
        .body(new GrantConsumerRequestBuilder().build())
        .contentType(ContentType.JSON)
        .post("/v1/consumer/Purandara/grant")
        .then()
        .body("id", is("ekstep.api.am.adminutil.consumer.grant"))
        .body("ver", is("1.0"))
        .body("ets", is(greaterThan(0L)))
        .body("params.status", is("successful"))
        .body("params.err", is(nullValue()))
        .body("params.errmsg", is(nullValue()))
        .body("params.resmsgid", is(not(nullValue())))
        .body("result.username", is("Purandara"))
        .body("result.groups", hasItem("group1"))
        .body("result.groups", hasItem("group5"))
        .body("result.groups", hasSize(2));

    amAdminApi.verify(postRequestedFor(urlEqualTo("/consumers/Purandara/acls/"))
        .withRequestBody(equalTo("group=group1")));
  }

  @Test
  public void shouldReturnErrorResponseWhenAmReturnsErrorWhenGettingGroups() throws Exception {

    amAdminApi.stubFor(post(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(201)
            .withBody("{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}")));
    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(500)
            .withBody("{\"message\":\"AM internal error\"}")));

    given()
        .port(serverPort)
        .body(new GrantConsumerRequestBuilder().build())
        .contentType(ContentType.JSON)
        .post("/v1/consumer/Purandara/grant")
        .then()
        .body("id", is("ekstep.api.am.adminutil.consumer.grant"))
        .body("ver", is("1.0"))
        .body("ets", is(greaterThan(0L)))
        .body("params.status", is("failed"))
        .body("params.err", is("GROUP_FETCH_ERROR"))
        .body("params.errmsg", is("ERROR WHEN FETCHING GROUPS OF CONSUMER, USERNAME: Purandara"))
        .body("params.resmsgid", is(not(nullValue())))
        .body("result.username", is("Purandara"))
        .body("result.groups", hasSize(0));

  }

  @Test
  public void shouldReturnErrorResponseWhenAmReturnsConsumerNotFoundWhenGettingGroups() throws Exception {

    amAdminApi.stubFor(post(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(201)
            .withBody("{\"group\":\"group1\",\"consumer_id\":\"bbdf1c48-19dc-4ab7-cae0-ff4f59d87dc9\",\"created_at\":1428555626000,\"id\":\"784bc8be-9f59-44b3-8ccb-545d16651f46\"}")));
    amAdminApi.stubFor(get(urlEqualTo("/consumers/Purandara/acls/"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(404)
            .withBody("{\"message\":\"Not found\"}")));

    given()
        .port(serverPort)
        .body(new GrantConsumerRequestBuilder().build())
        .contentType(ContentType.JSON)
        .post("/v1/consumer/Purandara/grant")
        .then()
        .body("id", is("ekstep.api.am.adminutil.consumer.grant"))
        .body("ver", is("1.0"))
        .body("ets", is(greaterThan(0L)))
        .body("params.status", is("failed"))
        .body("params.err", is("GROUP_FETCH_ERROR"))
        .body("params.errmsg", is("ERROR WHEN FETCHING GROUPS OF CONSUMER, USERNAME: Purandara. CONSUMER DOES NOT EXISTS"))
        .body("params.resmsgid", is(not(nullValue())))
        .body("result.username", is("Purandara"))
        .body("result.groups", hasSize(0));

  }

}
