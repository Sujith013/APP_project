package Actors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.Props;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class UserActor extends AbstractActor {
    public ArrayList<String> queries = new ArrayList<>();
    public static class FetchSearchData {
        public final String query;
        public final YouTube youtube;
        public final String apiKey;

        public FetchSearchData(YouTube youtube, String query, String apiKey) {
            this.query = query;
            this.youtube = youtube;
            this.apiKey = apiKey;
        }
    }

    public static Props props() {
        return Props.create(UserActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UserActor.FetchSearchData.class, this::handleFetchSearchData)
                .build();
    }

    private void handleFetchSearchData(UserActor.FetchSearchData request) {
        try {
            if (request.apiKey.isEmpty())
                throw new NullPointerException();
            else if (request.apiKey.length() < 39)
                throw new IllegalArgumentException("API key length too short");
            else if (request.apiKey.length() > 39)
                throw new IllegalArgumentException("API key length too long");

            boolean f = true;

            for (int i = 0; i < request.apiKey.length(); i++)
                if (!Character.isLetterOrDigit(request.apiKey.charAt(i)) && request.apiKey.charAt(i) != '_' && request.apiKey.charAt(i) != '-')
                    f = false;

            if (!f)
                throw new IllegalArgumentException("API key must only contain alphanumeric characters with - and _");

            String query = URLEncoder.encode(request.query, StandardCharsets.UTF_8);

            YouTube.Search.List search = request.youtube.search().list(Collections.singletonList("snippet"));
            search.setQ(query);
            search.setMaxResults(50L);
            search.setKey(request.apiKey);

            // Execute the search request and get the response
            SearchListResponse response = search.execute();

            // Filter and map the response to extract video details
            List<List<String>> videos = response.getItems().stream()
                        .filter(video -> "youtube#video".equals(video.getId().getKind()))
                        .map(video -> Arrays.asList(
                                video.getSnippet().getTitle(),
                                "https://www.youtube.com/watch?v=" + video.getId().getVideoId(),
                                "https://www.youtube.com/@" + video.getSnippet().getChannelTitle(),
                                video.getSnippet().getDescription(),
                                video.getSnippet().getThumbnails().getHigh().getUrl(),
                                "/channel/" + video.getSnippet().getChannelId()))
                        .limit(10)
                        .collect(Collectors.toList());
            getSender().tell(new UserActor.SearchData(videos), getSelf());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class SearchData
    {
        private final List<List<String>> videos;

        public SearchData(List<List<String>> videos)
        {
            this.videos = videos;
        }

        public String getVideos()
        {
            try {
                return new ObjectMapper().writeValueAsString(videos);
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"error\":\"Failed to serialize data\"}";
            }
        }
    }
}
