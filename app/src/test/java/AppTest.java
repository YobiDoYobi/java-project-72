import hexlet.code.App;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private static Javalin app;
    private static MockWebServer mockServer;
    private static String testUrl;

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = AppTest.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    @BeforeAll
    public static void createMock() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        MockResponse mockResponse = new MockResponse().setResponseCode(200).setBody(readResourceFile("example.html"));
        mockServer.enqueue(mockResponse);
        testUrl = mockServer.url("/").toString();

    }

    @AfterAll
    public static void shutDown() throws IOException {
        mockServer.shutdown();
        //app.stop();
    }

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.mainPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Бесплатно проверяйте сайты на SEO пригодность");
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example321.com/programs/java";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            //assertThat(response.body().string()).contains("coursename");
            var response2 = client.get(NamedRoutes.urlsPath());
            assertThat(response2.body().string().contains("https://www.example321.com"));
            var url = UrlRepository.find(1L);
            assertThat(url.get().getName()).isEqualTo("https://www.example321.com");
        });
    }

    @Test
    public void testUrlPage() throws SQLException {
        var url = new Url("https://www.example321.com/programs/java");
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(url.getId()));
            assertThat(response.body().string().contains("https://www.example321.com"));
        });
    }

    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(999999L));
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testCheckPath() throws SQLException, IOException {
        var url = new Url(testUrl);
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.runCheckPath(url.getId()));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Example title", "Example h1");
            var actualCheck = UrlCheckRepository.getLastCheck(url.getId());
            assertThat(actualCheck.get().getStatusCode()).isEqualTo(200);
            assertThat(actualCheck.get().getTitle()).isEqualTo("Example title");
            assertThat(actualCheck.get().getH1()).isEqualTo("Example h1");
            // assertThat(actualCheck.get().getDescription()).isEqualTo("fake description");
        });
    }

    @Test
    public void testInvalidUrlCreate() throws SQLException, IOException {
        var urlStr = new Url("testUrl");
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=" + urlStr;
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            var urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(0);
        });
    }
}
