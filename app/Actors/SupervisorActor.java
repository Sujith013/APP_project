package Actors;

import Models.YoutubeService;
import org.apache.pekko.NotUsed;
import play.mvc.Http.RequestHeader;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.pattern.Patterns;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.stream.javadsl.Source;

import java.time.Duration;
import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;
import java.security.GeneralSecurityException;

public class SupervisorActor {

    public static Source<String, NotUsed> getSearchSource(ActorSystem actorSystem, RequestHeader request, String API_KEY, ActorRef UserActor, Duration TIMEOUT)
    {
        return Source.<String>actorRef(10, org.apache.pekko.stream.OverflowStrategy.fail())
                .mapMaterializedValue(ref -> {
                    actorSystem.scheduler().scheduleAtFixedRate(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(10),
                            () -> {
                                searchIndex(request.getQueryString("searchTerms"),API_KEY,UserActor,TIMEOUT).thenAccept(response -> {
                                    ref.tell(response, ActorRef.noSender());
                                }).exceptionally(ex -> {
                                    ref.tell("Error fetching data: " + ex.getMessage(), ActorRef.noSender());
                                    return null;
                                });
                            },
                            actorSystem.dispatcher()
                    );
                    return NotUsed.getInstance();
                });
    }

    public static Source<String, NotUsed> getTagSource(ActorSystem actorSystem, RequestHeader request, String API_KEY, ActorRef tagsDataActor, Duration TIMEOUT)
    {
        return Source.<String>actorRef(10, org.apache.pekko.stream.OverflowStrategy.fail())
                .mapMaterializedValue(ref -> {
                    actorSystem.scheduler().scheduleAtFixedRate(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(10),
                            () -> {
                                tagIndex(request.getQueryString("videoId"),API_KEY,tagsDataActor,TIMEOUT).thenAccept(response -> {
                                    ref.tell(response, ActorRef.noSender());
                                }).exceptionally(ex -> {
                                    ref.tell("Error fetching data: " + ex.getMessage(), ActorRef.noSender());
                                    return null;
                                });
                            },
                            actorSystem.dispatcher()
                    );
                    return NotUsed.getInstance();
                });
    }

    public static CompletionStage<String> tagIndex(String videoId, String API_KEY,ActorRef tagsDataActor, Duration TIMEOUT) {
        try {
            TagsDataActor.FetchVideoData message = new TagsDataActor.FetchVideoData(YoutubeService.getService(), videoId, API_KEY);
            CompletionStage<Object> responseStage = Patterns.ask(tagsDataActor, message, TIMEOUT);

            // Handle the response asynchronously
            return responseStage.thenApply(response -> {
                        if (response instanceof TagsDataActor.TagsData tagsData) {
                            // Process the response if it's of the expected type
                            return tagsData.getTagsInformation();
                        } else {
                            // Log unexpected response type and return empty object
                            System.out.println("Unexpected response type");
                            return "{}";
                        }
                    })
                    .exceptionally(ex -> {
                        // Handle exception separately
                        System.out.println("Error occurred in responseStage: " + ex.getMessage());
                        return "{}";  // Return an empty object on error
                    });
        }catch(IOException | GeneralSecurityException e)
        {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Error fetching YouTube data");
        }
    }

    public static CompletionStage<String> searchIndex(String query, String API_KEY,ActorRef userDataActor, Duration TIMEOUT) {
        try {
            UserActor.FetchSearchData message = new UserActor.FetchSearchData(YoutubeService.getService(), query, API_KEY);
            CompletionStage<Object> responseStage = Patterns.ask(userDataActor, message, TIMEOUT);

            // Handle the response asynchronously
            return responseStage.thenApply(response -> {
                        if (response instanceof UserActor.SearchData searchData) {
                            // Process the response if it's of the expected type
                            return searchData.getVideos();
                        } else {
                            // Log unexpected response type and return empty object
                            System.out.println("Unexpected response type");
                            return "{}";
                        }
                    })
                    .exceptionally(ex -> {
                        // Handle exception separately
                        System.out.println("Error occurred in responseStage: " + ex.getMessage());
                        return "{}";  // Return an empty object on error
                    });
        }catch(IOException | GeneralSecurityException e)
        {
            e.printStackTrace();
            return CompletableFuture.completedFuture("Error fetching YouTube data");
        }
    }
}
