package graphAccessToken;

import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class ExamplePostRequest {
    public static void main(String[] args) throws Exception {
        // Replace {tenant} with your tenant ID or name
        String tenant = "f8cdef31-a31e-4b4a-93e4-5f571e91255a";
        // Replace {client_id} with your application's client ID
        String clientId = "d7f2dff8-0aca-48ae-9a09-89b3d939adad";
        // Replace {client_secret} with your application's client secret
        String clientSecret = "54A8Q~1w14sm20Z1AxERTMCfPEhxHoTuOJN6ua-~";
        // Set the scope for the request
        String scope = "https://graph.microsoft.com/.default";

        // Create a new HttpClient
        HttpClient client = HttpClients.createDefault();

        // Create a new HttpPost with the URL
        HttpPost post = new HttpPost("https://login.microsoftonline.com/" + tenant + "/oauth2/v2.0/token");

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
        
        System.out.println(access_token);
        }
}
