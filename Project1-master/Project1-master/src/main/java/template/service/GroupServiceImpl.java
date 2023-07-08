package template.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import template.persistence.dto.Group;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static template.api_config.config.getAccessToken;

public class GroupServiceImpl implements GroupService{
    private String token = getAccessToken();

    public GroupServiceImpl() throws IOException, InterruptedException {
        // TODO document why this constructor is empty
    }

    public void createGroup(Group newGroup,String ownerId, List<String> userId){
        String requestUrl = "https://graph.microsoft.com/v1.0/groups";
        String requestBody = "{\n" +
                "  \"description\": \"" + newGroup.getDescription() +"\",\n" +
                "  \"displayName\": \"" + newGroup.getDisplayName() +"\",\n" +
                "  \"groupTypes\": [],\n" +
                "  \"mailEnabled\": false,\n" +
                "  \"mailNickname\": \"" + newGroup.getMailNickname() +"\",\n" +
                "  \"securityEnabled\": true,\n" +
                "  \"owners@odata.bind\": [\n" +
                "    \"https://graph.microsoft.com/v1.0/users/" + ownerId +"\"\n" +
                "  ],\n" +
                "  \"members@odata.bind\": [\n" ;
        for (int i = 0; i < userId.size(); i++) {
            requestBody += "    \"https://graph.microsoft.com/v1.0/users/" + userId.get(i) + "\"";
            if (i != userId.size() - 1) {
                requestBody += ",";
            }
            requestBody += "\n";
        }
        requestBody += "  ]\n" +
                "}";
        try {
            // Create a new HttpClient object
            HttpClient httpClient = HttpClientBuilder.create().build();
            // Create a new HttpPost object
            HttpPost httpPost = new HttpPost(requestUrl);
            // Set the HTTP request headers
            httpPost.addHeader("Authorization", "Bearer " + token);
            httpPost.addHeader("Content-Type", "application/json");
            // Set the HTTP request body
            httpPost.setEntity(new StringEntity(requestBody));
            // Execute the HTTP request
            HttpResponse response = httpClient.execute(httpPost);
            // Read the HTTP response body
            Gson gson = new GsonBuilder().create();
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            if (response.getStatusLine().getStatusCode() >= 400) {
                // Parse the JSON string
                JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                // Get the value of the "message" property
                String message = jsonObject.getAsJsonObject("error").get("message").getAsString();
                // Print the error message
                System.out.println("Error: " + message);
            } else {
                System.out.println("Create group success");
                System.out.println(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createTeam(String groupId) {
        String requestBody = "{"
                + "\"template@odata.bind\": \"https://graph.microsoft.com/v1.0/teamsTemplates('standard')\","
                + "\"group@odata.bind\": \"https://graph.microsoft.com/v1.0/groups/" + groupId + "\""
                + "}";

        // Set up the request URL
        String requestUrl = "https://graph.microsoft.com/v1.0/teams";
        try {
            // Create a new HttpClient object
            HttpClient httpClient = HttpClientBuilder.create().build();
            // Create a new HttpPost object
            HttpPost httpPost = new HttpPost(requestUrl);
            // Set the HTTP request headers
            httpPost.addHeader("Authorization", "Bearer " + token);
            httpPost.addHeader("Content-Type", "application/json");
            // Set the HTTP request body
            httpPost.setEntity(new StringEntity(requestBody));
            // Execute the HTTP request
            HttpResponse response = httpClient.execute(httpPost);
            // Read the HTTP response body
            Gson gson = new GsonBuilder().create();
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);


            if (response.getStatusLine().getStatusCode() >= 400) {
                JSONObject json = new JSONObject(responseBody);
                JSONObject errorJson = json.getJSONObject("error");
                String innerErrorMessage = errorJson.getJSONObject("innerError").getString("message");
                System.out.println(innerErrorMessage);
            } else {
                System.out.println("Create team success");
                System.out.println(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteTeam(String groupId) {
        // Set up the request URL
        String requestUrl = "https://graph.microsoft.com/v1.0/groups/" + groupId;
        try {
            // Create a new HttpClient object
            HttpClient httpClient = HttpClientBuilder.create().build();
            // Create a new HttpDelete object
            HttpDelete httpDelete = new HttpDelete(requestUrl);
            // Set the HTTP request headers
            httpDelete.addHeader("Authorization", "Bearer " + token);
            // Execute the HTTP request
            HttpResponse response = httpClient.execute(httpDelete);
            // Read the HTTP response body
            Gson gson = new GsonBuilder().create();
            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() >= 400) {
                String responseBody = EntityUtils.toString(entity);
                // Parse the JSON string
                JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                if (jsonObject.getAsJsonObject("error").get("code").getAsString().equals("Request_ResourceNotFound")){
                    System.out.println("Group does not exist");
                }
                // Get the value of the "message" property
                String message = jsonObject.getAsJsonObject("error").get("message").getAsString();
                // Print the error message
                System.out.println("Error: " + message);
            } else {
                System.out.println("Delete team success");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addMemberToTeam(String groupId,List<String> userIds){
        String requestUrl = "https://graph.microsoft.com/v1.0/groups/" + groupId;
        List<String> memberIds = List.of("3eb3c149-d420-4fb3-a00a-f46217a15920", "5d22e230-ac9a-4d26-9ed8-ebc401454827");

        // Build the request body JSON string
        StringBuilder membersJson = new StringBuilder("\"members@odata.bind\": [");
        for (String memberId : memberIds) {
            membersJson.append("\"https://graph.microsoft.com/v1.0/directoryObjects/").append(memberId).append("\",");
        }
        membersJson.deleteCharAt(membersJson.length() - 1);
        membersJson.append("]");
        String requestBody = "{" + membersJson.toString() + "}";

        try {
            // Create a new HttpClient object
            HttpClient httpClient = HttpClientBuilder.create().build();
            // Create a new HttpPatch object
            HttpPatch httpPatch = new HttpPatch(requestUrl);
            // Set the HTTP request headers
            httpPatch.addHeader("Authorization", "Bearer " + token);
            httpPatch.addHeader("Content-Type", "application/json");
            // Set the HTTP request body (if requestBody is not null)
            if (requestBody != null) {
                httpPatch.setEntity(new StringEntity(requestBody));
            }
            // Execute the HTTP request
            HttpResponse response = httpClient.execute(httpPatch);
            // Read the HTTP response body
            Gson gson = new GsonBuilder().create();
            if (response.getStatusLine().getStatusCode() >= 400) {
                String responseBody = EntityUtils.toString(response.getEntity());
                // Parse the JSON string
                JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                // Get the value of the "message" property
                String message = jsonObject.getAsJsonObject("error").get("message").getAsString();
                // Print the error message
                System.out.println("Error: " + message);
            } else {
                System.out.println("Update group members success");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
