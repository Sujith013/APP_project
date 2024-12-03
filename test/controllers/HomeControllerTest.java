package controllers;

import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.Application;
import play.test.WithApplication;
import play.inject.guice.GuiceApplicationBuilder;

import static org.junit.Assert.*;
import static play.test.Helpers.GET;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;


public class HomeControllerTest extends WithApplication {


    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }


    /**
     * checks whether the main page (index) is properly rendered with a success result status
     * @author Sujith Manikandan
     * @author Tharun Balaji
     * @author Thansil Mohamed Syed Hamdulla
     * @author Prakash Yuvaraj
     * */
    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * checks whether the tag page is properly rendered with a success result status
     * @author Sujith Manikandan
     * */
    @Test
    public void testTagIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/tags/0BjlBnfHcHM");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }
}
