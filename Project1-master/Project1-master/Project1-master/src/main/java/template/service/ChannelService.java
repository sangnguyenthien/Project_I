package template.service;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import template.team_config.Config;

import java.io.IOException;


public class ChannelService {
    public static void createChannel(String teamId, String displayName, String description) throws IOException, InterruptedException
    {
        //String teamId = "7e8dcfe0-1fd3-4226-aeb0-63e1cc394c39";
        String membershipType = "standard";
        String url = "https://graph.microsoft.com/v1.0/teams/" + teamId + "/channels";
        String body = String.format("{\n" +
                "  \"displayName\": \"%s\",\n" +
                "  \"description\": \"%s\",\n" +
                "  \"membershipType\": \"%s\"\n" +
                "}", displayName, description, membershipType);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);

        // Set request headers
        request.setHeader("Content-type", "application/json");


        // Set the Authorization header with the access token
        String accessToken = Config.getAccessToken();
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        // Set request body
        StringEntity params = new StringEntity(body);
        request.setEntity(params);

        // Send request
        HttpResponse response = httpClient.execute(request);

        if (response.getStatusLine().getStatusCode() == 201) {
            System.out.println("Created channel " + displayName + " successfully!");
            /*
            // Read response
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            // Print response
            System.out.println("Response code: " + response.getStatusLine().getStatusCode());
            System.out.println("Response body: " + result.toString());
            */
        }else{
            System.out.printf("Failed to create channel. Status code: %d, Error message: %s", response.getStatusLine().getStatusCode(), response.getEntity().getContent().toString());
        }
    }

    public static String listAllChannels(String groupId) throws InterruptedException
    {
        String endpoint = "https://graph.microsoft.com/v1.0/teams/" + groupId + "/channels";
        HttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
        try {
            HttpGet httpGet = new HttpGet(endpoint);

            String token = Config.getAccessToken();
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != 200) {
                //System.out.println("Error: Could not list tables");
                return null;
            }
            //System.out.println("Listed tables");
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            return responseString;
        } catch (IOException e) {
            System.out.println("Oops! We ran into some problems");
            return null;
        }
    }

}


