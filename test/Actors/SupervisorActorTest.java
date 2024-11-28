package Actors;

import org.apache.pekko.NotUsed;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Status;
import org.apache.pekko.stream.Materializer;
import org.apache.pekko.stream.javadsl.Sink;
import org.apache.pekko.stream.javadsl.Source;
import org.apache.pekko.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import play.libs.typedmap.TypedMap;
import play.mvc.Http;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SupervisorActorTest {
    static ActorSystem system;
    static Materializer materializer;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("TestSystem");
        materializer = Materializer.createMaterializer(system);
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
    }

    @Test
    public void testGetTagSource() {
        // Create a TestKit probe for simulating actor behavior
        TestKit testProbe = new TestKit(system);

        // Mock the TagsDataActor
        ActorRef tagsDataActor = testProbe.getRef();

        // Mock HTTP RequestHeader with a videoId parameter
        Http.RequestHeader mockRequest = Mockito.mock(Http.RequestHeader.class);
        when(mockRequest.getQueryString("videoId")).thenReturn("videoId123");


        // Test API key and timeout
        String apiKey = "mockApiKey";
        Duration timeout = Duration.ofSeconds(5);

        TagsDataActor.TagsData mockResponse = new TagsDataActor.TagsData(
                "mockVideoId",
                "mockVideoTitle",
                "mockChannelTitle",
                "mockChannelId",
                "mockDescription",
                "mockThumbnailURL",
                "mockTags"
        );

        // Call the method under test
        Source<String, NotUsed> source = SupervisorActor.getTagSource(
                system, mockRequest, apiKey, tagsDataActor, timeout);

        // Test the source to verify it emits the expected result
        CompletionStage<String> result = source.runWith(Sink.head(), materializer);

        // Simulate the actor response to FetchVideoData
        TagsDataActor.FetchVideoData fetchMessage = testProbe.expectMsgClass(TagsDataActor.FetchVideoData.class);

        testProbe.reply(mockResponse);

        // Assert the emitted string
        result.toCompletableFuture().thenAccept(tagInfo -> {
            assertEquals("{\"thumbnail\":\"mockThumbnailURL\",\"description\":\"mockDescription\",\"videoId\":\"mockVideoId\",\"videoTitle\":\"mockVideoTitle\",\"channelId\":\"mockChannelId\",\"channelTitle\":\"mockChannelTitle\",\"tags\":\"mockTags\"}", tagInfo); // Expected JSON
        }).toCompletableFuture().join(); // Ensure test waits for completion
    }

    @Test
    public void testTagIndexSuccess() {
        // Create a TestKit probe for the actor system
        TestKit probe = new TestKit(system);

        // Get a reference to the mock actor
        ActorRef mockTagsDataActor = probe.getRef();

        // Define test inputs
        String videoId = "videoId123";
        String apiKey = "mockApiKey";
        Duration timeout = Duration.ofSeconds(5);

        // Simulate a successful response with mock data
        TagsDataActor.TagsData mockResponse = new TagsDataActor.TagsData(
                "mockVideoId",
                "mockVideoTitle",
                "mockChannelTitle",
                "mockChannelId",
                "mockDescription",
                "mockThumbnailURL",
                "mockTags"
        );

        // Call the method under test
        CompletionStage<String> result = SupervisorActor.tagIndex(videoId, apiKey, mockTagsDataActor, timeout);

        // Ensure the FetchVideoData message was sent
        TagsDataActor.FetchVideoData fetchMessage = probe.expectMsgClass(TagsDataActor.FetchVideoData.class);
        assertEquals(videoId, fetchMessage.videoId);
        assertEquals(apiKey, fetchMessage.apiKey);

        // Simulate a reply from the actor
        probe.reply(mockResponse);

        // Verify the response
        result.toCompletableFuture().thenAccept(tagInfo -> {
            assertEquals(mockResponse.getTagsInformation(), tagInfo); // Compare expected and actual output
        }).toCompletableFuture().join(); // Ensure completion for test
    }

    @Test
    public void testTagIndexError() {
        TestKit probe = new TestKit(system);
        ActorRef mockTagsDataActor = probe.getRef();

        String videoId = "videoId123";
        String apiKey = "mockApiKey";
        Duration timeout = Duration.ofSeconds(5);

        // Call the method
        CompletionStage<String> result = SupervisorActor.tagIndex(videoId, apiKey, mockTagsDataActor, timeout);

        // Simulate an error response
        TagsDataActor.FetchVideoData fetchMessage = probe.expectMsgClass(TagsDataActor.FetchVideoData.class);
        probe.reply(new Status.Failure(new Exception("Mock error")));

        // Assert the result
        result.toCompletableFuture().thenAccept(tagInfo -> {
            assertEquals("{}", tagInfo); // Expected on error
        });
    }
}
