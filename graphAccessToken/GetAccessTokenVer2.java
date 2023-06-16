package graphAccessToken;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class GetAccessTokenVer2 {

    public static void main(String[] args) throws URISyntaxException {
        String tenantId = "865f9941-ebea-433d-9979-ffa06829d5e6";
        String clientId = "e9b191e7-b5fc-4ec1-ae09-2be5d19c0c97";
        String clientSecret = "Lq-8Q~yicdD92gpPliiPdiHYPwBtQJimNUYhrcP5";
        try {
            String accessToken = getAccessToken(tenantId, clientId, clientSecret);
            System.out.println("Access token: " + accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAccessToken(String tenantId, String clientId, String clientSecret) throws IOException, URISyntaxException {
        HttpClient client = HttpClients.createDefault();

        String scope = "https://graph.microsoft.com/.default";

         // Create a new HttpPost with the URL
        HttpPost post = new HttpPost("https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token");

        // Set the headers for the request
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        // Set the request body
        String requestBody = "client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=client_credentials&scope=" + scope;
        post.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

        // Send the POST request and receive the response
        HttpResponse response = client.execute(post);

        // Print the response body
        //System.out.println(EntityUtils.toString(response.getEntity()));
        
     
        JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        String access_token = jsonObject.getString("access_token");

        return access_token;
    }
}
