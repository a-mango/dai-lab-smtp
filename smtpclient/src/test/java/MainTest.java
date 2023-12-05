import ch.heig.dai.lab.smtp.Client;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpGet;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for the Main class.
 */
@Testcontainers
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
     * The container used for the tests. I couldn't fix the  `Raw use of parameterized class 'DockerComposeContainer'`
     * warningwithout encountering even more obscure warnings. This is supposed to get fixed with testcontainers v2,
     * but it is not yet stable.
     *
     * @see <a href="https://github.com/testcontainers/testcontainers-java/issues/238">
     *        https://github.com/testcontainers/testcontainers-java/issues/238
     *      </a>
     */
    @Container
    public static DockerComposeContainer<?> container =
            new DockerComposeContainer(new File("src/test/resources/test-compose.yml"))
            .withExposedService(HOST, HTTP_PORT)
            .withExposedService(HOST, SMTP_PORT);

    /**
     * Integration test for the Client program. The test uses the `maildev` container to check that the correct quantity
     * of emails were sent when the program is run with the provided arguments.
     */
    @Test
    public void mailSentIsReceivedTest() throws Exception {
        final int mailCount = 3;

        // Send a mail with the client.
        String[] args = {"victims.txt", "messages.txt", String.valueOf(3)};
        Client.main(args);

        // Check that the mail was received.
        String address = "http://localhost:" + container.getServicePort(HOST, HTTP_PORT) + "/email";
        HttpGet request = new HttpGet(address);

        //
        try (CloseableHttpResponse response = HttpClientBuilder.create().build().execute(request)) {
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
    }
}