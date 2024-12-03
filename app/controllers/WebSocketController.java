/**
 * @author Sujith Manikandan
 * @author Tharun Balaji
 * @author Thansil Mohamed Syed Hamdulla
 * @author Prakash Yuvaraj
 * @version 1.0
 * @since 01-12-2024
 * */
package controllers;

import Actors.UserActor;
import Actors.TagsDataActor;
import Actors.ChannelDataActor;
//import Actors.UnifiedWordStatsActor;

import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;
import org.apache.pekko.stream.Materializer;
import org.apache.pekko.stream.javadsl.Flow;
import org.apache.pekko.stream.javadsl.Sink;

import play.mvc.Controller;
import play.mvc.WebSocket;
import javax.inject.Inject;
import java.time.Duration;

import static Actors.SupervisorActor.getSearchSource;
import static Actors.SupervisorActor.getTagSource;
import static Actors.SupervisorActor.getChannelSource;
//import static Actors.SupervisorActor.getWordStatsSource;

public class WebSocketController extends Controller {
    private static final String API_KEY = "AIzaSyAtdsdZUu9Yayn4XBp-aA7-HiGdNnuGJVU";

    private final ActorSystem actorSystem;
    private final Materializer materializer;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final ActorRef tagsDataActor;
    private final ActorRef channelDataActor;
    private final ActorRef userActor;
    //private final ActorRef wordStatsActor;

    /**
     * The constructor of the class
     * @author Sujith Manikandan
     * @author Tharun Balaji
     * @author Thansil Mohamed Syed Hamdulla
     * @author Prakash Yuvaraj
     * */
    @Inject
    public WebSocketController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
        this.tagsDataActor = actorSystem.actorOf(Props.create(TagsDataActor.class), "tagsDataActor");
        this.userActor = actorSystem.actorOf(Props.create(UserActor.class), "userActor");
        this.channelDataActor = actorSystem.actorOf(Props.create(ChannelDataActor.class), "ChannelDataActor");
        //this.wordStatsActor = actorSystem.actorOf(Props.create(UnifiedWordStatsActor.class), "wordStatsActor");
    }

    /**
     * The main socket for the search function
     * @author Sujith Manikandan
     * @author Tharun Balaji
     * @author Thansil Mohamed Syed Hamdulla
     * @author Prakash Yuvaraj
     * */
    public WebSocket searchSocket() {
        return WebSocket.Text.accept(request -> {
            return Flow.fromSinkAndSource(Sink.ignore(), getSearchSource(actorSystem,request,API_KEY,userActor,TIMEOUT));
        });
    }

    /**
     * The main socket for the tag function
     * @author Sujith Manikandan
     * */
    public WebSocket tagsSocket() {
        return WebSocket.Text.accept(request -> {
            return Flow.fromSinkAndSource(Sink.ignore(), getTagSource(actorSystem,request,API_KEY,tagsDataActor,TIMEOUT));
        });
    }
    public WebSocket ChannelSocket() {
        return WebSocket.Text.accept(request -> {
            return Flow.fromSinkAndSource(Sink.ignore(), getChannelSource(actorSystem,request,API_KEY,channelDataActor,TIMEOUT));
        });
    }

/*    public WebSocket statsSocket() {
        return WebSocket.Text.accept(request -> {
            return Flow.fromSinkAndSource(Sink.ignore(), getWordStatsSource(actorSystem, request, wordStatsActor, TIMEOUT));
        });
    }*/
}
