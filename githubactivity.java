import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class githubactivity {

    public static void ReformatJSON(String body) {

        body = body.replace("[","").replace("]","").replace("{","").replace("}","");

        HashMap<String, ArrayList<String>> events = new HashMap<>();

        Integer ith = 0;

        String typeName = "";
        String repoName = "";
        Integer ID = 0;

        for (String s : body.split(",")) {

            ith++;
            //System.out.println(ith + " - " + s);

            if (typeName.equals("") && s.contains("type")) {
                typeName = s.replace("\"type\":\"","").replace("\"","");
            }
            if (repoName.equals("") && s.contains("https://api.github.com/repos/")) {
                repoName = s.replace("\"url\":\"https://api.github.com/repos/","").replace("\"","");
            }

            if (ith == 19) {

                if (!events.containsKey(repoName)) {
                    events.put(repoName,new ArrayList<String>());
                }

                ArrayList<String> list = events.get(repoName);

                list.add(typeName);

                events.replace(repoName,list);

                typeName = "";
                repoName = "";

                ith = 0;
                ID++;
            }
        }

        for (String key : events.keySet()) {

            ArrayList<String> types = events.get(key);

            Integer pushes = 0;
            Integer watches = 0;

            for (String type : types) {
                switch (type) {
                    case "PushEvent":
                        pushes++;
                        break;
                    case "WatchEvent":
                        watches++;
                        break;
                }
            }

            if (pushes>0) {
                System.out.println("-Pushed " + pushes + " commits to " + key);
            }
            if (watches>0) {
                System.out.println("-Starred " + key);
            }

        }

    }

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

            ReformatJSON(response.body());

        } catch (Exception e) {
            System.out.println("Failed to fetch user");
        }

    }

}
