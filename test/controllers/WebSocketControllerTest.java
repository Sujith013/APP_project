/*
import Actors.UserActor;
import Actors.TagsDataActor;
import Actors.ChannelDataActor;
import controllers.WebSocketController;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.testkit.javadsl.TestKit;
import org.apache.pekko.stream.javadsl.Source;
import org.apache.pekko.stream.testkit.javadsl.TestSink;
import org.apache.pekko.stream.testkit.javadsl.TestSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.mvc.WebSocket;
import play.mvc.Http;

import java.time.Duration;

import static org.mockito.Mockito.*;

public class WebSocketControllerTest {

    private static ActorSystem system;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final String API_KEY = "dummy-api-key";

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("WebSocketControllerTest");
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
    }

    @Test
    public void testSearchSocket() {
        new TestKit(system) {{
        ActorRef mockUserActor = system.actorOf(TagsDataActor.props());
        WebSocketController controller = new WebSocketController(system, null);

        // Mock the getSearchSource method
        Source<String, ?> mockSource = Source.single("mock-response");
        controller.setMockSearchSource(mockSource); // Create a setter in the controller for testing

        // Simulate WebSocket connection
        Http.RequestHeader mockRequest = mock(Http.RequestHeader.class);
        WebSocket socket = controller.searchSocket();

        // Test the WebSocket flow
        TestSource.probe(String.class, system)
                .via(socket.flow()) // Connect TestSource to the WebSocket flow
                .toMat(TestSink.probe(system), Keep.both()) // Connect to TestSink for validation
                .run(system)
                .request(1)
                .expectNext("mock-response")
                .expectComplete();
    }};
    }
}*/
