package org.folio.rest;

import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.RestVerticle.OKAPI_HEADER_TOKEN;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.folio.rest.client.TenantClient;
import org.folio.rest.jaxrs.model.TenantAttributes;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.PomReader;
import org.folio.rest.tools.utils.NetworkUtils;
import org.folio.util.OkapiConnectionParams;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Class for tests that base on testing code using Vertx REST verticle
 */
public abstract class RestVerticleTestBase {
  static {
    System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, "io.vertx.core.logging.Log4j2LogDelegateFactory");
  }

  protected static final String TENANT_ID = "diku";
  protected static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWt1X2FkbWluIiwidXNlcl9pZCI6ImJlOTZmNDg4LTgwY2YtNTVhNC05Njg3LTE1ZjAyMmE4ZDkyYiIsImlhdCI6MTU4NDA5ODc3MywidGVuYW50IjoiZGlrdSJ9.fI3FHPS23tvLVyk3vfAknvhnvrRNBABPchJdfjV0UNI";
  private static final String HOST = "http://localhost:";
  protected static final int PORT = NetworkUtils.nextFreePort();
  private static final String OKAPI_HEADER_URL = "X-Okapi-Url";
  protected static final String BASE_OKAPI_URL = HOST + PORT;
  protected static RequestSpecification jsonRequestSpecification;
  protected static Vertx vertx;
  protected OkapiConnectionParams okapiConnectionParams;
  private static MockServer mockServer;
  protected static final int mockPort = NetworkUtils.nextFreePort();
  protected static final String MOCK_OKAPI_URL = HOST + mockPort;
  protected static final String EXPORT_URL = "/data-export/export";
  protected static final String FILE_DEFINITION_SERVICE_URL = "/data-export/fileDefinitions/";
  protected static final String UPLOAD_URL = "/upload";
  protected static final String STORAGE_DIRECTORY_PATH = "./storage";
  protected static final String FILES_FOR_UPLOAD_DIRECTORY = "endToEndTestFiles/";

  @BeforeClass
  public static void setUpClass(final TestContext context) throws Exception {
    vertx = Vertx.vertx();

    mockServer = new MockServer(mockPort);
    mockServer.start();

    runDatabase();
    deployVerticle(context);
  }

  private static void runDatabase() throws Exception {
    PostgresClient.setIsEmbedded(true);
    PostgresClient.getInstance(vertx).startEmbeddedPostgres();
  }

  private static void deployVerticle(final TestContext context) {
    Async async = context.async();
    TenantClient tenantClient = new TenantClient(BASE_OKAPI_URL, TENANT_ID, TOKEN);
    DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", PORT));
    vertx.deployVerticle(RestVerticle.class.getName(), options, res -> {
      try {
        TenantAttributes tenantAttributes = new TenantAttributes();
        tenantAttributes.setModuleTo(PomReader.INSTANCE.getModuleName());
        tenantClient.postTenant(tenantAttributes, res2 -> {
          async.complete();
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @After
  public void tearDown() throws Exception {
    FileUtils.deleteDirectory(new File(STORAGE_DIRECTORY_PATH));
  }

  @AfterClass
  public static void tearDownClass(final TestContext context) {
    Async async = context.async();
    mockServer.close();
    vertx.close(context.asyncAssertSuccess(res -> {
      PostgresClient.stopEmbeddedPostgres();
      async.complete();
    }));
  }

  @Before
  public void setUp(TestContext context) throws IOException {
    setUpOkapiConnectionParams();
    setUpJsonRequestSpecification();
  }

  private void setUpOkapiConnectionParams() {
    Map<String, String> headers = new HashedMap<>();
    headers.put(OKAPI_HEADER_TENANT, TENANT_ID);
    headers.put(OKAPI_HEADER_URL, BASE_OKAPI_URL);
    this.okapiConnectionParams = new OkapiConnectionParams(headers);
  }

  private void setUpJsonRequestSpecification() {
    jsonRequestSpecification = new RequestSpecBuilder()
      .setContentType(ContentType.JSON)
      .addHeader(OKAPI_HEADER_TOKEN, TOKEN)
      .addHeader(OKAPI_HEADER_TENANT, TENANT_ID)
      .addHeader(OKAPI_HEADER_URL, MOCK_OKAPI_URL)
      .setBaseUri(BASE_OKAPI_URL)
      .addHeader("Accept", "text/plain, application/json")
      .build();
  }

  protected Response postRequest(JsonObject body, String path) {
    return RestAssured.given()
      .spec(jsonRequestSpecification)
      .body(body.encode())
      .when()
      .post(path);
  }

  protected static String getMockData(String path) throws IOException {
    try (InputStream resourceAsStream = RestVerticleTestBase.class.getClassLoader().getResourceAsStream(path)) {
      if (resourceAsStream != null) {
        return IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
      } else {
        StringBuilder sb = new StringBuilder();
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
          lines.forEach(sb::append);
        }
        return sb.toString();
      }
    }
  }

  protected RequestSpecification buildRequestSpecification() {
    return new RequestSpecBuilder()
      .setContentType(ContentType.BINARY)
      .addHeader(OKAPI_HEADER_TENANT, TENANT_ID)
      .setBaseUri(BASE_OKAPI_URL)
      .build();
  }
}
