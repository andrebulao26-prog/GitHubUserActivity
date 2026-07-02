import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class githubactivity {

    static void main(String[] Args) {

        if (Args.length < 1) {
            return;
        }

        String UserName = Args[0];
        String URL = "https://api.github.com/users/" + UserName + "/events";

        String JsonBody = "{\n" +"username: \"" + UserName + "\"\n" +"}";

        try {

            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(URL))
                    .method("GET", HttpRequest.BodyPublishers.ofString(JsonBody))
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());

        } catch (Exception e) {
            System.out.println("Failed to fetch user");
        }

    }

}
