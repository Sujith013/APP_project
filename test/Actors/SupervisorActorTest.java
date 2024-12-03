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
import play.mvc.Http;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SupervisorActorTest {
    static ActorSystem system;
    static Materializer materializer;

    static String[] VIDEO_KIND = {"youtube#channel","youtube#channel","youtube#playlist","youtube#playlist","youtube#playlist","youtube#playlist","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video"};
    static String[] VIDEO_ID = {"https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=2LkDU0iKaro","https://www.youtube.com/watch?v=PBaFURjVrm0","https://www.youtube.com/watch?v=eIho2S0ZahI","https://www.youtube.com/watch?v=HrCbXNRP7eg","https://www.youtube.com/watch?v=8jPQjjsBbIc","https://www.youtube.com/watch?v=O9pD6LTF4Bk","https://www.youtube.com/watch?v=arj7oStGLkU","https://www.youtube.com/watch?v=PY9DcIMGxMs","https://www.youtube.com/watch?v=-moW9jvvMr4","https://www.youtube.com/watch?v=H14bBuluwB8","https://www.youtube.com/watch?v=gUV5DJb6KGs","https://www.youtube.com/watch?v=lg48Bi9DA54","https://www.youtube.com/watch?v=dIYmzf21d1g","https://www.youtube.com/watch?v=Hu4Yvq-g7_Y"};
    static String[] VIDEO_TITLE = {"TED","TEDx Talks","The 20 Most-Watched TEDTalks","10 funniest TED Talks","Most Interesting Ted Talks","TED Talks Daily","Networking Doesn't Have to Feel Gross | Daniel Hallak | TED","How to hack your brain for better focus | Sasha Hamdani | TEDxKC","How to Speak So That People Want to Listen | Julian Treasure | TED","The Problem With Being ΓÇ£Too NiceΓÇ¥ at Work | Tessa West | TED","How to stay calm when you know you&#39;ll be stressed | Daniel Levitin | TED","What Nobody Tells You About Your Twenties | Livi Redden | TEDxBayonne","Inside the Mind of a Master Procrastinator | Tim Urban | TED","Everything you think you know about addiction is wrong | Johann Hari | TED","A simple way to break a bad habit | Judson Brewer | TED","Grit: The Power of Passion and Perseverance | Angela Lee Duckworth | TED","How to talk to the worst parts of yourself | Karen Faith | TEDxKC","The science behind dramatically better conversations | Charles Duhigg | TEDxManchester","How to Claim Your Leadership Power | Michael Timms | TED","How to Get Your Brain to Focus | Chris Bailey | TEDxManchester"};
    static String[] CHANNEL_TITLE = {"https://www.youtube.com/@TED","https://www.youtube.com/@TEDx Talks","https://www.youtube.com/@TED","https://www.youtube.com/@Yoong Cheong Sin","https://www.youtube.com/@Bryan Leonardo","https://www.youtube.com/@TED Audio Collective","https://www.youtube.com/@TED","https://www.youtube.com/@TEDx Talks","https://www.youtube.com/@TED","https://www.youtube.com/@TED","https://www.youtube.com/@TED","https://www.youtube.com/@TEDx Talks","https://www.youtube.com/@TED","https://www.youtube.com/@TED","https://www.youtube.com/@TED","https://www.youtube.com/@TED","https://www.youtube.com/@TEDx Talks","https://www.youtube.com/@TEDx Talks","https://www.youtube.com/@TED","https://www.youtube.com/@TEDx Talks"};
    static String[] CHANNEL_ID = {"https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCo05wfWBCsmAeL50CPA_Vyw","https://www.youtube.com/channel/UCKpc2NQtnrEx9Srs9EDDLUA","https://www.youtube.com/channel/UCy9b8cNJQmxX8Y2bdE6mQNw","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q"};
    static String[] DESCRIPTION = {"The TED Talks channel features the best talks and performances from the TED Conference, where the world's leading thinkers ...","TEDx is an international community that organizes TED-style events anywhere and everywhere -- celebrating locally-driven ideas ...","A list of the 20 most-watched talks on all the platforms we track: TED.com, YouTube, iTunes, embed and download, Hulu and ...","","","Every weekday, TED Talks Daily brings you the latest talks in audio. Join host and journalist Elise Hu for thought-provoking ideas ...","Networking doesn't always have to feel like a self-serving transaction, says executive coach Daniel Hallak. Highlighting the ...","The modern world constantly fragments our attention. In this funny, insightful talk, Dr. Hamdani, a psychiatrist and ADHD expert, ...","Have you ever felt like you're talking, but nobody is listening? Here's Julian Treasure to help you fix that. As the sound expert ...","Are you \"too nice\" at work? Social psychologist Tessa West shares her research on how people attempt to mask anxiety with ...","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","The fact of the matter is many of the biggest decisions that leave a long-lasting impact on our lives generally occur in our teens ...","Tim Urban knows that procrastination doesn't make sense, but he's never been able to shake his habit of waiting until the last ...","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized Talk recommendations and more.","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","NOTE FROM TED: This talk contains a discussion of suicidal ideation. If you are struggling with suicidal thoughts, please consult a ...","In a world of increasing complexity but decreasing free time, the role of the trusted 'explainer' has never been more important.","When faced with challenges, do you often seek someone else to blame? Leadership expert Michael Timms shows why this ...","The latest research is clear: the state of our attention determines the state of our lives. So how do we harness our attention to focus ..."};
    static String[] URL = {"https://yt3.ggpht.com/ytc/AIdro_l_fFETDQgTAl5rWb38pxJww-4kszJH_n0G4fKP1BdK-jc=s800-c-k-c0xffffffff-no-rj-mo","https://yt3.ggpht.com/70r5TkYTLC0cpKLAiQEvcWLeIHB8yxoiog0nQIK9MmnZHqkICy0YA-jAaqfT2ChOBwehskjf5g=s800-c-k-c0xffffffff-no-rj-mo","https://i.ytimg.com/vi/iG9CE55wbtY/hqdefault.jpg","https://i.ytimg.com/vi/buRLc2eWGPQ/hqdefault.jpg","https://i.ytimg.com/vi/MB5IX-np5fE/hqdefault.jpg","https://i.ytimg.com/vi/24wBKuU2rfE/hqdefault.jpg","https://i.ytimg.com/vi/2LkDU0iKaro/hqdefault.jpg","https://i.ytimg.com/vi/PBaFURjVrm0/hqdefault.jpg","https://i.ytimg.com/vi/eIho2S0ZahI/hqdefault.jpg","https://i.ytimg.com/vi/HrCbXNRP7eg/hqdefault.jpg","https://i.ytimg.com/vi/8jPQjjsBbIc/hqdefault.jpg","https://i.ytimg.com/vi/O9pD6LTF4Bk/hqdefault.jpg","https://i.ytimg.com/vi/arj7oStGLkU/hqdefault.jpg","https://i.ytimg.com/vi/PY9DcIMGxMs/hqdefault.jpg","https://i.ytimg.com/vi/-moW9jvvMr4/hqdefault.jpg","https://i.ytimg.com/vi/H14bBuluwB8/hqdefault.jpg","https://i.ytimg.com/vi/gUV5DJb6KGs/hqdefault.jpg","https://i.ytimg.com/vi/lg48Bi9DA54/hqdefault.jpg","https://i.ytimg.com/vi/dIYmzf21d1g/hqdefault.jpg","https://i.ytimg.com/vi/Hu4Yvq-g7_Y/hqdefault.jpg"};

    static List<List<String>> expectedVideos = Arrays.asList(Arrays.asList("Networking Doesn't Have to Feel Gross | Daniel Hallak | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=2LkDU0iKaro","https://www.youtube.com/@https://www.youtube.com/@TED","Networking doesn't always have to feel like a self-serving transaction, says executive coach Daniel Hallak. Highlighting the ...","https://i.ytimg.com/vi/2LkDU0iKaro/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("How to hack your brain for better focus | Sasha Hamdani | TEDxKC","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=PBaFURjVrm0","https://www.youtube.com/@https://www.youtube.com/@TEDx Talks","The modern world constantly fragments our attention. In this funny, insightful talk, Dr. Hamdani, a psychiatrist and ADHD expert, ...","https://i.ytimg.com/vi/PBaFURjVrm0/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q"),Arrays.asList("How to Speak So That People Want to Listen | Julian Treasure | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=eIho2S0ZahI","https://www.youtube.com/@https://www.youtube.com/@TED","Have you ever felt like you're talking, but nobody is listening? Here's Julian Treasure to help you fix that. As the sound expert ...","https://i.ytimg.com/vi/eIho2S0ZahI/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("The Problem With Being ΓÇ£Too NiceΓÇ¥ at Work | Tessa West | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=HrCbXNRP7eg","https://www.youtube.com/@https://www.youtube.com/@TED","Are you \"too nice\" at work? Social psychologist Tessa West shares her research on how people attempt to mask anxiety with ...","https://i.ytimg.com/vi/HrCbXNRP7eg/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("How to stay calm when you know you&#39;ll be stressed | Daniel Levitin | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=8jPQjjsBbIc","https://www.youtube.com/@https://www.youtube.com/@TED","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","https://i.ytimg.com/vi/8jPQjjsBbIc/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("What Nobody Tells You About Your Twenties | Livi Redden | TEDxBayonne","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=O9pD6LTF4Bk","https://www.youtube.com/@https://www.youtube.com/@TEDx Talks","The fact of the matter is many of the biggest decisions that leave a long-lasting impact on our lives generally occur in our teens ...","https://i.ytimg.com/vi/O9pD6LTF4Bk/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q"),Arrays.asList("Inside the Mind of a Master Procrastinator | Tim Urban | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=arj7oStGLkU","https://www.youtube.com/@https://www.youtube.com/@TED","Tim Urban knows that procrastination doesn't make sense, but he's never been able to shake his habit of waiting until the last ...","https://i.ytimg.com/vi/arj7oStGLkU/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("Everything you think you know about addiction is wrong | Johann Hari | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=PY9DcIMGxMs","https://www.youtube.com/@https://www.youtube.com/@TED","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","https://i.ytimg.com/vi/PY9DcIMGxMs/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("A simple way to break a bad habit | Judson Brewer | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=-moW9jvvMr4","https://www.youtube.com/@https://www.youtube.com/@TED","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized Talk recommendations and more.","https://i.ytimg.com/vi/-moW9jvvMr4/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("Grit: The Power of Passion and Perseverance | Angela Lee Duckworth | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=H14bBuluwB8","https://www.youtube.com/@https://www.youtube.com/@TED","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","https://i.ytimg.com/vi/H14bBuluwB8/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"));

    private static Map<String, Object> responseData = new HashMap<>(Map.of("data", expectedVideos, "senti", ":|"));

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
    public void testGetSearchSource() {
        // Create a TestKit probe for simulating actor behavior
        TestKit testProbe = new TestKit(system);

        // Mock the TagsDataActor
        ActorRef userActor = testProbe.getRef();

        // Mock HTTP RequestHeader with a videoId parameter
        Http.RequestHeader mockRequest = Mockito.mock(Http.RequestHeader.class);
        when(mockRequest.getQueryString("searchTerms")).thenReturn("ted talks");

        // Test API key and timeout
        String apiKey = "AIzaSyBhVg3undEJFND54BtUkJ17gSJCuK9CAvI";
        Duration timeout = Duration.ofSeconds(5);

        UserActor.SearchData mockResponse = new UserActor.SearchData(responseData);

        // Call the method under test
        Source<String, NotUsed> source = SupervisorActor.getSearchSource(
                system, mockRequest, apiKey, userActor, timeout);

        // Test the source to verify it emits the expected result
        CompletionStage<String> result = source.runWith(Sink.head(), materializer);

        // Simulate the actor response to FetchVideoData
        UserActor.FetchSearchData fetchMessage = testProbe.expectMsgClass(UserActor.FetchSearchData.class);

        testProbe.reply(mockResponse);

        // Assert the emitted string
        result.toCompletableFuture().thenAccept(searchInfo -> {
            assertEquals("{\"data\":[[\"Networking Doesn't Have to Feel Gross | Daniel Hallak | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=2LkDU0iKaro\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Networking doesn't always have to feel like a self-serving transaction, says executive coach Daniel Hallak. Highlighting the ...\",\"https://i.ytimg.com/vi/2LkDU0iKaro/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"How to hack your brain for better focus | Sasha Hamdani | TEDxKC\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=PBaFURjVrm0\",\"https://www.youtube.com/@https://www.youtube.com/@TEDx Talks\",\"The modern world constantly fragments our attention. In this funny, insightful talk, Dr. Hamdani, a psychiatrist and ADHD expert, ...\",\"https://i.ytimg.com/vi/PBaFURjVrm0/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q\"],[\"How to Speak So That People Want to Listen | Julian Treasure | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=eIho2S0ZahI\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Have you ever felt like you're talking, but nobody is listening? Here's Julian Treasure to help you fix that. As the sound expert ...\",\"https://i.ytimg.com/vi/eIho2S0ZahI/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"The Problem With Being ΓÇ£Too NiceΓÇ¥ at Work | Tessa West | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=HrCbXNRP7eg\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Are you \\\"too nice\\\" at work? Social psychologist Tessa West shares her research on how people attempt to mask anxiety with ...\",\"https://i.ytimg.com/vi/HrCbXNRP7eg/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"How to stay calm when you know you&#39;ll be stressed | Daniel Levitin | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=8jPQjjsBbIc\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.\",\"https://i.ytimg.com/vi/8jPQjjsBbIc/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"What Nobody Tells You About Your Twenties | Livi Redden | TEDxBayonne\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=O9pD6LTF4Bk\",\"https://www.youtube.com/@https://www.youtube.com/@TEDx Talks\",\"The fact of the matter is many of the biggest decisions that leave a long-lasting impact on our lives generally occur in our teens ...\",\"https://i.ytimg.com/vi/O9pD6LTF4Bk/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q\"],[\"Inside the Mind of a Master Procrastinator | Tim Urban | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=arj7oStGLkU\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Tim Urban knows that procrastination doesn't make sense, but he's never been able to shake his habit of waiting until the last ...\",\"https://i.ytimg.com/vi/arj7oStGLkU/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"Everything you think you know about addiction is wrong | Johann Hari | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=PY9DcIMGxMs\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.\",\"https://i.ytimg.com/vi/PY9DcIMGxMs/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"A simple way to break a bad habit | Judson Brewer | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=-moW9jvvMr4\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized Talk recommendations and more.\",\"https://i.ytimg.com/vi/-moW9jvvMr4/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"Grit: The Power of Passion and Perseverance | Angela Lee Duckworth | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=H14bBuluwB8\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.\",\"https://i.ytimg.com/vi/H14bBuluwB8/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"]],\"senti\":\":|\"}", searchInfo); // Expected JSON
        }).toCompletableFuture().join(); // Ensure test waits for completion
    }

    @Test
    public void testGetTagSource() {
        // Create a TestKit probe for simulating actor behavior
        TestKit testProbe = new TestKit(system);

        // Mock the TagsDataActor
        ActorRef tagsDataActor = testProbe.getRef();

        // Mock HTTP RequestHeader with a videoId parameter
        Http.RequestHeader mockRequest = Mockito.mock(Http.RequestHeader.class);
        when(mockRequest.getQueryString("videoId")).thenReturn("0BjlBnfHcHM");


        // Test API key and timeout
        String apiKey = "AIzaSyBhVg3undEJFND54BtUkJ17gSJCuK9CAvI";
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
        String videoId = "0BjlBnfHcHM";
        String apiKey = "AIzaSyBhVg3undEJFND54BtUkJ17gSJCuK9CAvI";
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

        String videoId = "0BjlBnfHcHM";
        String apiKey = "AIzaSyBhVg3undEJFND54BtUkJ17gSJCuK9CAvI";
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

    @Test
    public void testSearchIndexSuccess() {
        // Create a TestKit probe for the actor system
        TestKit probe = new TestKit(system);

        // Get a reference to the mock actor
        ActorRef mockUserActor = probe.getRef();

        // Define test inputs
        String query = "ted talks";
        String apiKey = "AIzaSyBhVg3undEJFND54BtUkJ17gSJCuK9CAvI";
        Duration timeout = Duration.ofSeconds(5);

        // Simulate a successful response with mock data
        UserActor.SearchData mockResponse = new UserActor.SearchData(responseData);

        // Call the method under test
        CompletionStage<String> result = SupervisorActor.searchIndex(query, apiKey, mockUserActor, timeout);

        // Ensure the FetchVideoData message was sent
        UserActor.FetchSearchData fetchMessage = probe.expectMsgClass(UserActor.FetchSearchData.class);
        assertEquals(query, fetchMessage.query);
        assertEquals(apiKey, fetchMessage.apiKey);

        // Simulate a reply from the actor
        probe.reply(mockResponse);

        // Verify the response
        result.toCompletableFuture().thenAccept(searchInfo -> {
            assertEquals(mockResponse.getVideos(), searchInfo); // Compare expected and actual output
        }).toCompletableFuture().join(); // Ensure completion for test
    }

    @Test
    public void testSearchIndexError() {
        TestKit probe = new TestKit(system);
        ActorRef mockUserActor = probe.getRef();

        String query = "ted talks";
        String apiKey = "AIzaSyBhVg3undEJFND54BtUkJ17gSJCuK9CAvI";
        Duration timeout = Duration.ofSeconds(5);

        // Call the method
        CompletionStage<String> result = SupervisorActor.searchIndex(query, apiKey, mockUserActor, timeout);

        // Simulate an error response
        UserActor.FetchSearchData fetchMessage = probe.expectMsgClass(UserActor.FetchSearchData.class);
        probe.reply(new Status.Failure(new Exception("Mock error")));

        // Assert the result
        result.toCompletableFuture().thenAccept(tagInfo -> {
            assertEquals("{}", tagInfo); // Expected on error
        });
    }
}
