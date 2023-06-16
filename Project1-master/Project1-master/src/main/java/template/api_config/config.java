package template.api_config;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import template.persistence.dto.User;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class config {
    private static final String CLIENT_ID = "55bc4573-5e32-463d-ae68-70b9a9c28eec";
    private static final String CLIENT_SECRET = "8~58Q~E5ieqWemmZpgJNLFzv2ssYDy_puXd6DcfW";
    private static final String TENANT_ID = "6e83b1ce-3e89-4213-a156-3cbca5875266";

    public static void main(String[] args) throws IOException, InterruptedException {
        // Step 1: Get an access token
        String accessToken = getAccessToken();
        // Step 2: Make an HTTP request to the Microsoft Graph API
        // Step 2: Create a new user
        String userJson = "{\r\n" +
                "  \"accountEnabled\": true,\r\n" +
                "  \"displayName\": \"John Doe\",\r\n" +
                "  \"mailNickname\": \"johndoe\",\r\n" +
                "  \"userPrincipalName\": \"johndoe@3pjv85.onmicrosoft.com\",\r\n" +
                "  \"passwordProfile\": {\r\n" +
                "    \"forceChangePasswordNextSignIn\": true,\r\n" +
                "    \"password\": \"P@ssw0rd123\"\r\n" +
                "  }\r\n" +
                "}";
        URL url = new URL("https://graph.microsoft.com/v1.0/users");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.getOutputStream().write(userJson.getBytes(StandardCharsets.UTF_8));

        // Step 3: Print the response to the console
        int responseCode = con.getResponseCode();
        Gson gson = new GsonBuilder().create();
        BufferedReader in;
        if (responseCode >= 400) {
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                errorResponse.append(line);
            }
            // Parse the JSON string
            JsonObject jsonObject = gson.fromJson(errorResponse.toString(), JsonObject.class);
            // Get the value of the "message" property
            String message = jsonObject.getAsJsonObject("error").get("message").getAsString();
            // Print the error message
            System.out.println("Error: " + message);
        } else {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            System.out.println("Create user success");
            System.out.println(in.readLine());
        }

        in.close();
    }




    public static String getAccessToken() throws IOException, InterruptedException {
        String tokenEndpoint = "https://login.microsoftonline.com/" + TENANT_ID + "/oauth2/v2.0/token";
        String encodedCredentials = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + encodedCredentials)
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&scope=https://graph.microsoft.com/.default"))
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new GsonBuilder().create();
        String accessToken = gson.fromJson(response.body(), Map.class).get("access_token").toString();

        return accessToken;
    }
}
