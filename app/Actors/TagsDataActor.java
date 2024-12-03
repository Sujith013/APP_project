package Actors;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

import com.google.api.services.youtube.model.VideoListResponse;
import org.apache.pekko.actor.Props;
import org.apache.pekko.actor.AbstractActor;

import com.google.api.services.youtube.YouTube;
import com.fasterxml.jackson.databind.ObjectMapper;


public class TagsDataActor extends AbstractActor {

    public static class FetchVideoData {
        public final String videoId;
        public final YouTube youtube;
        public final String apiKey;

        /**
         * @author Sujith Manikandan
         * @param youtube the youtube object
         * @param videoId the video Id
         * @param apiKey*/
        public FetchVideoData(YouTube youtube, String videoId, String apiKey) {
            this.videoId = videoId;
            this.youtube = youtube;
            this.apiKey = apiKey;
        }
    }

    public static Props props() {
        return Props.create(TagsDataActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(FetchVideoData.class, this::handleFetchVideoData)
                .build();
    }

    /**
     * The main function for the computation
     * @param request the request which is the videoData object*/
    private void handleFetchVideoData(FetchVideoData request) {
            try {
                YouTube.Videos.List videoList = request.youtube.videos().list(Collections.singletonList("snippet,contentDetails,statistics"));
                videoList.setId(Collections.singletonList(request.videoId));
                videoList.setKey(request.apiKey);

                VideoListResponse response = videoList.execute();

                String videoTitle = response.getItems().get(0).getSnippet().getTitle();
                String channelTitle = response.getItems().get(0).getSnippet().getChannelTitle();
                String channelId = response.getItems().get(0).getSnippet().getChannelId();
                String description = response.getItems().get(0).getSnippet().getDescription();
                String thumbnail = response.getItems().get(0).getSnippet().getThumbnails().getHigh().getUrl();
                String tags = response.getItems().get(0).getSnippet().getTags() != null ?
                        String.join("+", response.getItems().get(0).getSnippet().getTags()) : "";

                getSender().tell(new TagsData(request.videoId,videoTitle, channelTitle, channelId, description, thumbnail, tags), getSelf());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    public static class TagsData {
        public final String videoId;
        public final String videoTitle;
        public final String channelTitle;
        public final String channelId;
        public final String description;
        public final String thumbnail;
        public final String tags;

        /**
         * @author Sujith Manikandan
         * @param videoId the id of the video
         * @param videoTitle the title of the video
         * @param channelTitle the title of the channel
         * @param description the description of the video
         * @param  thumbnail the thumbnail url
         * @param tags the tags list response*/
        public TagsData(String videoId, String videoTitle, String channelTitle, String channelId, String description, String thumbnail, String tags) {
            this.videoId = videoId;
            this.videoTitle = videoTitle;
            this.channelTitle = channelTitle;
            this.channelId = channelId;
            this.description = description;
            this.thumbnail = thumbnail;
            this.tags = tags;
        }

        /**
         * @author sujith manikandan
         * @returns the complete information about the tags as a string*/
        public String getTagsInformation()
        {
            try {
                java.util.HashMap<String, Object> response = new HashMap<>();

                response.put("videoId",this.videoId);
                response.put("videoTitle",this.videoTitle);
                response.put("channelTitle",this.channelTitle);
                response.put("description",this.description);
                response.put("thumbnail",this.thumbnail);
                response.put("tags",this.tags);
                response.put("channelId",this.channelId);

                // Convert the Java object to JSON string
                return new ObjectMapper().writeValueAsString(response);

            } catch (Exception e) {
                // Handle any potential errors during serialization
                e.printStackTrace();
                return "{\"error\":\"Failed to serialize data\"}";
            }
        }
    }
}
