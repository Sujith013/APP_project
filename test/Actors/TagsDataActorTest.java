package Actors;

import Actors.TagsDataActor.TagsData;
import Actors.TagsDataActor.FetchVideoData;
import Models.YoutubeService;
import com.google.api.services.youtube.model.*;
import com.google.api.services.youtube.YouTube;

import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.testkit.javadsl.TestKit;

import org.junit.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class TagsDataActorTest {
    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("TestSystem");
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void FetchVideoDataTest() throws GeneralSecurityException, IOException {
        TagsDataActor.FetchVideoData inputData = new TagsDataActor.FetchVideoData(YoutubeService.getService(),"sampleId","sampleAPI");
        assertNotNull(inputData);
        assertEquals("sampleId",inputData.videoId);
        assertEquals("sampleAPI",inputData.apiKey);
    }

    @Test
    public void tagsData() {
        TagsDataActor.TagsData inputData = new TagsDataActor.TagsData("sampleVideoId","sampleVideoTitle","sampleChannelTitle","sampleChannelId","sampleDescription","sampleThumbnailURL","sampleTags");
        assertNotNull(inputData);
        assertEquals("sampleVideoId",inputData.videoId);
        assertEquals("sampleChannelId",inputData.channelId);
        assertEquals("sampleVideoTitle",inputData.videoTitle);
        assertEquals("sampleChannelTitle",inputData.channelTitle);
        assertEquals("sampleDescription",inputData.description);
        assertEquals("sampleThumbnailURL",inputData.thumbnail);
        assertEquals("sampleTags",inputData.tags);

        assertNotNull(inputData.getTagsInformation());
        assertEquals("{\"thumbnail\":\"sampleThumbnailURL\",\"description\":\"sampleDescription\",\"videoId\":\"sampleVideoId\",\"videoTitle\":\"sampleVideoTitle\",\"channelId\":\"sampleChannelId\",\"channelTitle\":\"sampleChannelTitle\",\"tags\":\"sampleTags\"}",inputData.getTagsInformation());
    }

    @Test
    public void testHandleFetchVideoDataSuccess() throws IOException {
        YouTube youtubeMock = mock(YouTube.class);
        YouTube.Videos videosMock = mock(YouTube.Videos.class);
        YouTube.Videos.List videosListMock = mock(YouTube.Videos.List.class);

        VideoListResponse videoListResponseMock = new VideoListResponse();
        Video videoMock = new Video();
        VideoSnippet snippetMock = new VideoSnippet();
        Thumbnail thumbnailMock = new Thumbnail();
        ThumbnailDetails thumbnailDetailsMock = new ThumbnailDetails();

        // Mock video snippet data
        snippetMock.setTitle("Test Video");
        snippetMock.setChannelTitle("Test Channel");
        snippetMock.setChannelId("Channel123");
        snippetMock.setDescription("Test Description");
        snippetMock.setTags(Collections.singletonList("Tag1"));
        thumbnailMock.setUrl("http://example.com/thumbnail.jpg");
        thumbnailDetailsMock.setHigh(thumbnailMock);
        snippetMock.setThumbnails(thumbnailDetailsMock);
        videoMock.setSnippet(snippetMock);
        videoListResponseMock.setItems(Collections.singletonList(videoMock));

        when(youtubeMock.videos()).thenReturn(videosMock);
        when(videosMock.list(Collections.singletonList("snippet,contentDetails,statistics"))).thenReturn(videosListMock);
        when(videosListMock.setId(Collections.singletonList("0BjlBnfHcHM"))).thenReturn(videosListMock);
        when(videosListMock.setKey("AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVEkPI")).thenReturn(videosListMock);
        when(videosListMock.execute()).thenReturn(videoListResponseMock);

        // Act and Assert
        new TestKit(system) {{
            ActorRef tagsDataActor = system.actorOf(TagsDataActor.props());
            FetchVideoData fetchVideoData = new FetchVideoData(youtubeMock, "Video123", "API_KEY");

            tagsDataActor.tell(fetchVideoData, getRef());

            TagsData response = expectMsgClass(TagsData.class);
            Assert.assertEquals("Test Video", response.videoTitle);
            Assert.assertEquals("Test Channel", response.channelTitle);
            Assert.assertEquals("Channel123", response.channelId);
            Assert.assertEquals("Test Description", response.description);
            Assert.assertEquals("http://example.com/thumbnail.jpg", response.thumbnail);
            Assert.assertEquals("Tag1", response.tags);
        }};
    }

    @Test
    public void testHandleFetchVideoDataFailure() throws IOException {
        // Arrange
        YouTube youtubeMock = mock(YouTube.class);
        YouTube.Videos videosMock = mock(YouTube.Videos.class);
        YouTube.Videos.List videosListMock = mock(YouTube.Videos.List.class);

        when(youtubeMock.videos()).thenReturn(videosMock);
        when(videosMock.list(Collections.singletonList("snippet,contentDetails,statistics"))).thenReturn(videosListMock);

        when(videosListMock.setId(Collections.singletonList(""))).thenThrow(new NullPointerException("ID error - VideoID not present"));
        when(videosListMock.setId(Collections.singletonList("video"))).thenThrow(new IllegalArgumentException("ID error - VideoID too short"));
        when(videosListMock.setId(Collections.singletonList("video123456789"))).thenThrow(new IllegalArgumentException("ID error - VideoID too long"));
        when(videosListMock.setId(Collections.singletonList("video@@**&&"))).thenThrow(new IllegalArgumentException("ID error - VideoID too long"));

        when(videosListMock.setKey("")).thenThrow(new NullPointerException("API error - API key not present"));
        when(videosListMock.setKey("API_KEY")).thenThrow(new IllegalArgumentException("API error - API key too short"));
        when(videosListMock.setKey("AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVEabcdbdefgh")).thenThrow(new IllegalArgumentException("API error - API key too long"));
        when(videosListMock.setKey("AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVE-@I")).thenThrow(new IllegalArgumentException("API error - illegal characters present in API"));

        // Act and Assert
        new TestKit(system) {{
            ActorRef tagsDataActor = system.actorOf(TagsDataActor.props());
            FetchVideoData fetchVideoData = new FetchVideoData(youtubeMock, "Video123", "");

            try {
                tagsDataActor.tell(fetchVideoData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }

            fetchVideoData = new FetchVideoData(youtubeMock, "Video123", "API_KEY");

            try {
                tagsDataActor.tell(fetchVideoData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }

            fetchVideoData = new FetchVideoData(youtubeMock, "Video123", "AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVEabcdbdefgh");

            try {
                tagsDataActor.tell(fetchVideoData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }

            fetchVideoData = new FetchVideoData(youtubeMock, "Video123", "AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVE-@I");

            try {
                tagsDataActor.tell(fetchVideoData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }

            fetchVideoData = new FetchVideoData(youtubeMock, "", "AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVEkPI");

            try {
                tagsDataActor.tell(fetchVideoData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }

            fetchVideoData = new FetchVideoData(youtubeMock, "video", "AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVEkPI");

            try {
                tagsDataActor.tell(fetchVideoData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }

            fetchVideoData = new FetchVideoData(youtubeMock, "video123456789", "AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVEkPI");

            try {
                tagsDataActor.tell(fetchVideoData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }

            fetchVideoData = new FetchVideoData(youtubeMock, "video@@**&&", "AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVEkPI");

            try {
                tagsDataActor.tell(fetchVideoData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }
        }};
    }
}
