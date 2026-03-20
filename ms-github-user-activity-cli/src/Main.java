import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws Exception {

        // Step 1: Validate input
        if (args.length == 0) {
            System.out.println("Please enter github username");
            return;
        }

        String username = args[0];

        // Step 2: Fetch data
        String json = fetchGitHubEvents(username);

        if (json == null) {
            System.out.println("Failed to fetch data.");
            return;
        }

        // Step 3: Parse and display
        parseAndDisplayEvents(json);
    }

    // -----------------------------------
    // Method to fetch data from GitHub API
    // -----------------------------------
    public static String fetchGitHubEvents(String username) throws Exception {

        String apiUrl = "https://api.github.com/users/" + username + "/events";
        URL url = new URL(apiUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int statusCode = connection.getResponseCode();

        if (statusCode != 200) {
            System.out.println("Error: HTTP " + statusCode);
            return null;
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );

        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        return response.toString();
    }

    // -----------------------------------
    // Method to parse JSON and print output
    // -----------------------------------
    public static void parseAndDisplayEvents(String json) {

        // Better split (handles formatting more safely)
        String[] events = json.split("\\},\\{");

        for (String event : events) {

            // Extract repo name per event (reset each loop)
            String repoName = extractRepoName(event);

            // Normalize event type detection
            if (event.contains("\"type\":\"PushEvent\"")) {
                System.out.println("- Pushed commits to " + repoName);

            } else if (event.contains("\"type\":\"WatchEvent\"")) {
                System.out.println("- Starred " + repoName);

            } else if (event.contains("\"type\":\"IssuesEvent\"")) {
                System.out.println("- Opened an issue in " + repoName);
            }
        }
    }

    // -----------------------------------
    // Method to extract repository name
    // -----------------------------------
    public static String extractRepoName(String event) {

        String repoName = "unknown repo";

        int repoIndex = event.indexOf("\"name\":\"");

        if (repoIndex != -1) {
            int start = repoIndex + 8;
            int end = event.indexOf("\"", start);

            if (end != -1) {
                repoName = event.substring(start, end);
            }
        }

        return repoName;
    }
}