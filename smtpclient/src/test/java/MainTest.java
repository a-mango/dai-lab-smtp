import ch.heig.dai.lab.smtp.SmtpClient;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpGet;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the Main class. Uses the TestContainers library to run a MailDev container and check that the emails
 * were sent correctly.
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainTest {
    /**
     * The docker host to test against.
     */
    public static String HOST = "maildev";

    /**
     * The host SMTP port.
     */
    public static int SMTP_PORT = 1025;

    /**
     * The host HTTP port.
     */
    public static int HTTP_PORT = 1080;

    /**
     * The container used for the tests. The  `Raw use of parameterized class 'DockerComposeContainer'` warning is
     * supposed to get fixed with TestContainers v2.
     */
    @Container
    public static DockerComposeContainer<?> container = new DockerComposeContainer(
            new File("src/test/resources/test-compose.yml"))
            .withExposedService(HOST, HTTP_PORT)
            .withExposedService(HOST, SMTP_PORT);

    /**
     * Integration test for the client program. The test uses the `maildev` container to check that the correct quantity
     * of emails were sent and received when the program is run with the provided arguments.
     */
    @Test
    @Order(1)
    public void mailSentIsReceivedTest() {
        InputStream standardIn = System.in; // backup System.in to restore it later
        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);

        final int mailCount = 3;

        // Send a mail with the client.
        String[] args = {"victims.txt", "messages.utf8", String.valueOf(3)};
        SmtpClient.main(args);

        // Check that the mail was received.
        String address = "http://localhost:" + container.getServicePort(HOST, HTTP_PORT) + "/email";
        HttpGet request = new HttpGet(address);

        // Get the response from the MailDev API and check that the correct amount of messages has been received.
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            // Check that we got an OK status.
            assertEquals(200, response.getCode());

            // Parse the response.
            String body = EntityUtils.toString(response.getEntity());
            System.out.println(body);
            JsonPath jsonPath = new JsonPath(body);

            // Check that the correct amount of messages has been received.
            int arraySize = jsonPath.getList("$").size();
            System.out.println("Array Size: " + arraySize);
            assertEquals(mailCount, arraySize);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        System.setIn(standardIn); // restore System.in
    }

    /**
     * Check that the received mail uses characters that are out of the ASCII range.
     */
    @Test
    @Order(2)
    public void mailReceivedHasUtf8Test() {
        String address = "http://localhost:" + container.getServicePort(HOST, HTTP_PORT) + "/email";
        HttpGet request = new HttpGet(address);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            // Check that we got an OK status.
            assertEquals(200, response.getCode());

            // Parse the response.
            String body = EntityUtils.toString(response.getEntity());
            System.out.println(body.chars());
            // Check that the response contains non-ascii chars. Because of the unpredictability of the shuffle, we
            // cannot check for a specific message.
            assertTrue(body.chars().anyMatch(c -> c > 127)); // Check that any char is out of the ASCII range (128)
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}