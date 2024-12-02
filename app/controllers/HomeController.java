/**
 * @author Sujith Manikandan
 * @author Tharun Balaji
 * @author Thansil Mohamed Syed Hamdulla
 * @author Prakash Yuvaraj
 * @version 1.0
 * @since 01-11-2024
 * */
package controllers;
import play.mvc.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

/**
 * This is the one and only main controller for the project that handles the HTTP requests and renders
 * pages for the entire application.
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * @author Sujith Manikandan
     * @author Tharun Balaji
     * @author Thansil Mohamed Syed Hamdulla
     * @author Prakash Yuvaraj
     * @return returns the view of the index page containing the html content for the main page of our application
     * */
    public Result index() {
        return ok(views.html.index.render());
    }



     /**
      * @author Sujith Manikandan
      * @param videoId the id of the video for which the content is required
      * @return A wrapped object containing all the data about the video such as title,channel,description,thumbnails and tags of the video.
      * */
     public CompletionStage<Result>tagIndex(String videoId) {
     return CompletableFuture.supplyAsync(() -> {
             return ok(views.html.tags.render(videoId));
       });
     }




    /**
     * @author Thansil Mohammed Syed Hamdulla
     * Method to handle requests for displaying the channel profile page.
     * @param channelId The ID of the YouTube channel to display.
     * @return A Result containing the channel profile page.
     */
    public CompletionStage<Result> channelProfile(String channelId) {
        return CompletableFuture.supplyAsync(() -> {
                return ok(views.html.channel.render(channelId));
        });
    }
}