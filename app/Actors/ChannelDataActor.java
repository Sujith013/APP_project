package Actors;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import org.apache.pekko.actor.Props;
import org.apache.pekko.actor.AbstractActor;

import com.google.api.services.youtube.YouTube;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChannelDataActor extends AbstractActor {

    // Message class for channel data fetch request
    public static class FetchChannelData {
        public final String channelId;
        public final YouTube youtube;
        public final String apiKey;

        public FetchChannelData(YouTube youtube, String channelId, String apiKey) {
            this.channelId = channelId;
            this.youtube = youtube;
            this.apiKey = apiKey;
        }
    }

    public static Props props() {
        return Props.create(ChannelDataActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(FetchChannelData.class, this::handleFetchChannelData)
                .build();
    }

    private void handleFetchChannelData(FetchChannelData request) {
        try {
            // Fetch Channel Data
            YouTube.Channels.List channelRequest = request.youtube.channels().list(Collections.singletonList("snippet,statistics"));
            channelRequest.setId(Collections.singletonList(request.channelId));
            channelRequest.setKey(request.apiKey);
            ChannelListResponse channelResponse = channelRequest.execute();

            // Fetch Recent Videos
            YouTube.PlaylistItems.List playlistRequest = request.youtube.playlistItems().list(Collections.singletonList("snippet"));
            playlistRequest.setPlaylistId("UU" + request.channelId.substring(2)); // Channel's uploads playlist ID
            playlistRequest.setMaxResults(20L); // Fetch up to 20 videos
            playlistRequest.setKey(request.apiKey);
            PlaylistItemListResponse playlistResponse = playlistRequest.execute();

            // Extract Channel Details
            String channelTitle = channelResponse.getItems().get(0).getSnippet().getTitle();
            String description = channelResponse.getItems().get(0).getSnippet().getDescription();
            String subscriberCount = channelResponse.getItems().get(0).getStatistics().getSubscriberCount().toString();
            String thumbnailUrl = channelResponse.getItems().get(0).getSnippet().getThumbnails().getDefault().getUrl();
            String publishedAt = channelResponse.getItems().get(0).getSnippet().getPublishedAt().toString(); // Extract published date

            // Extract Recent Videos (limit to 10 videos)
            List<List<String>> recentVideos = playlistResponse.getItems().stream()
                    .limit(10) // Only take the first 10 videos
                    .map(item -> List.of(
                            item.getSnippet().getTitle(),
                            "https://www.youtube.com/watch?v=" + item.getSnippet().getResourceId().getVideoId(),
                            item.getSnippet().getPublishedAt().toString(), // Published date for the video
                            "https://www.youtube.com/@" + item.getSnippet().getChannelTitle(),
                            item.getSnippet().getDescription(),
                            item.getSnippet().getThumbnails().getHigh().getUrl(),
                            "https://www.youtube.com/channel/" + item.getSnippet().getChannelId()
                    ))
                    .toList();

            // Debugging
            System.out.println(channelTitle);

            // Send response back to sender
            getSender().tell(new ChannelDataResponse(channelTitle, description, subscriberCount, thumbnailUrl, publishedAt, recentVideos), getSelf());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching channel data", e);
        }
    }




    // Response class to send back the channel data
    public static class ChannelDataResponse {
        public final String channelTitle;
        public final String description;
        public final String subscriberCount;
        public final String thumbnailUrl;
        public final String publishedAt; // Published date for the channel
        public final List<List<String>> recentVideos;

        public ChannelDataResponse(String channelTitle, String description, String subscriberCount, String thumbnailUrl, String publishedAt, List<List<String>> recentVideos) {
            this.channelTitle = channelTitle;
            this.description = description;
            this.subscriberCount = subscriberCount;
            this.thumbnailUrl = thumbnailUrl;
            this.publishedAt = publishedAt;
            this.recentVideos = recentVideos;
        }

        public String getChannelDataJson() {
            try {
                HashMap<String, Object> response = new HashMap<>();
                response.put("channelTitle", this.channelTitle);
                response.put("description", this.description);
                response.put("subscriberCount", this.subscriberCount);
                response.put("thumbnailUrl", this.thumbnailUrl);
                response.put("publishedAt", this.publishedAt); // Include publishedAt in the JSON response
                response.put("recentVideos", this.recentVideos);

                // Convert to JSON and return
                return new ObjectMapper().writeValueAsString(response);

            } catch (Exception e) {
                e.printStackTrace();
                return "{\"error\":\"Failed to serialize data\"}";
            }
        }
    }

}