package example.micronaut;

import static org.junit.Assert.assertEquals;

import example.micronaut.domain.Genre;
import example.micronaut.genre.GenreSaveCommand;
import example.micronaut.genre.GenreUpdateCommand;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class GenreControllerTest {

    private static EmbeddedServer server; // <1>
    private static HttpClient client; // <2>

    @BeforeClass
    public static void setupServer() {
        server = ApplicationContext.run(EmbeddedServer.class); // <1>
        client = server.getApplicationContext().createBean(HttpClient.class, server.getURL()); // <2>
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.stop();
        }
    }

    @Test
    public void testGenreCrudOperations() {
        HttpRequest request = HttpRequest.POST("/genres", new GenreSaveCommand("Microservices")); // <3>
        HttpResponse<Genre> response = client.toBlocking().exchange(request, Argument.of(Genre.class));

        assertEquals(HttpStatus.CREATED, response.getStatus());

        Long id = response.getBody().get().getId();
        request = HttpRequest.GET("/genres/" + id);
        Genre genre = client.toBlocking().retrieve(request, Genre.class); // <4>

        assertEquals("Microservices", genre.getName());

        request = HttpRequest.PUT("/genres/", new GenreUpdateCommand(id, "Micro-services"));
        response = client.toBlocking().exchange(request);  // <5>

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        request = HttpRequest.GET("/genres/" + id);
        genre = client.toBlocking().retrieve(request, Genre.class);
        assertEquals("Micro-services", genre.getName());

        request = HttpRequest.GET("/genres");
        List<Genre> genres = client.toBlocking().retrieve(request, Argument.of(List.class, Genre.class));

        assertEquals(1, genres.size());

        // cleanup:
        request = HttpRequest.DELETE("/genres/" + id);
        response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }
}
