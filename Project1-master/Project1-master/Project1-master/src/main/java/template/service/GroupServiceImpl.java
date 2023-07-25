package template.service;

import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import template.jsonUtil.JsonTool;
import template.persistence.dto.Group;
import template.team_config.Config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static template.team_config.Config.getAccessToken;

public class GroupServiceImpl implements GroupService {
    private String token = getAccessToken();
    public static final JsonArray tableSetting = JsonParser.parseString("[\n" +
            "  {\n" +
            "    \"name\": \"id\",\n" +
            "    \"type\": \"singleLineText\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"@odata.type\",\n" +
            "    \"type\": \"singleLineText\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"roles\",\n" +
            "    \"type\": \"singleLineText\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"displayName\",\n" +
            "    \"type\": \"singleLineText\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"visibleHistoryStartDateTime\",\n" +
            "    \"type\": \"singleLineText\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"userId\",\n" +
            "    \"type\": \"singleLineText\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"email\",\n" +
            "    \"type\": \"singleLineText\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"tenantId\",\n" +
            "    \"type\": \"singleLineText\"\n" +
            "  }\n" +
            "]\n").getAsJsonArray();


    public GroupServiceImpl() throws IOException, InterruptedException {
        // TODO document why this constructor is empty
    }

    public String createGroup(Group newGroup, String ownerId, List<String> userId) {
        String groupId = null;
        String requestUrl = "https://graph.microsoft.com/v1.0/groups";
        String requestBody = "{\n" +
                "  \"description\": \"" + newGroup.getDescription() + "\",\n" +
                "  \"displayName\": \"" + newGroup.getDisplayName() + "\",\n" +
                "  \"groupTypes\": [],\n" +
                "  \"mailEnabled\": false,\n" +
                "  \"mailNickname\": \"" + newGroup.getMailNickname() + "\",\n" +
                "  \"securityEnabled\": true,\n" +
                "  \"owners@odata.bind\": [\n" +
                "    \"https://graph.microsoft.com/v1.0/users/" + ownerId + "\"\n" +
                "  ],\n" +
                "  \"members@odata.bind\": [\n";
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
                JSONObject json = new JSONObject(responseBody);
                groupId = json.getString("id");
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
        return groupId;
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
            System.out.println("Error");
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
                if (jsonObject.getAsJsonObject("error").get("code").getAsString().equals("Request_ResourceNotFound")) {
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
            System.out.println("Error");
        }
    }

    public void addMemberToTeam(String groupId, List<String> userIds) {
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
            System.out.println("Error");
        }
    }

    public static List<JsonObject> listUsersAsJson(String group_id, String token) throws IOException, InterruptedException {
        String graphEndpoint = "https://graph.microsoft.com/v1.0/teams/" + group_id + "/members";
        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
        try {
            HttpGet httpGet = new HttpGet(graphEndpoint);
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("Cannot list users of Team has id: " + group_id);
                return null;
            }

            String body = EntityUtils.toString(response.getEntity());
            JsonObject bodyJson = JsonParser.parseString(body).getAsJsonObject();

            JsonArray jsonArray = new JsonArray();

            if (bodyJson.has("value")) {
                jsonArray = bodyJson.get("value").getAsJsonArray();
            }

            List<JsonObject> result = new ArrayList<>();

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                if (jsonObject.has("roles")) {
                    // Get the value of "roles" as a comma-separated String
                    String rolesString = jsonObject.get("roles").getAsJsonArray().toString();

                    // Update the JsonObject with the new value of "roles"
                    jsonObject.addProperty("roles", rolesString);

                    result.add(jsonObject);
                }

                if (jsonObject.has("businessPhones")) {
                    // Get the value of "roles" as a comma-separated String
                    String rolesString = jsonObject.get("businessPhones").getAsJsonArray().toString();

                    // Update the JsonObject with the new value of "roles"
                    jsonObject.addProperty("businessPhones", rolesString);
                }

            }

            return result;


        } catch (IOException e) {
            System.out.println("Cannot list users of Team has id: " + group_id);
            return null;
        }
    }

    public void listIDsGroup() throws IOException, InterruptedException {
        String accessToken = token;

        // Set the request headers
        HttpGet request = new HttpGet("https://graph.microsoft.com/v1.0/groups");
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Send the request and get the response
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Check the response status code
                if (response.getStatusLine().getStatusCode() == 200) {
                    // Get the response body as a string
                    String responseBody = EntityUtils.toString(response.getEntity());

                    // Parse the JSON response
                    JSONObject responseJson = new JSONObject(responseBody);

                    // Get the array of groups from the response
                    JSONArray groupsJson = responseJson.getJSONArray("value");

                    // Print the IDs of the groups
                    for (int i = 0; i < groupsJson.length(); i++) {
                        JSONObject groupJson = groupsJson.getJSONObject(i);
                        String groupId = groupJson.getString("id");
                        System.out.println(groupId);
                    }
                } else {
                    System.out.printf("Failed to list groups. Status code: %d, Error message: %s", response.getStatusLine().getStatusCode(), response.getEntity().getContent().toString());
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to list groups");
        }
    }

    public List<String[]> listIDsTeam() throws UnsupportedEncodingException {
        // Set the request headers
        HttpGet request = new HttpGet("https://graph.microsoft.com/v1.0/groups?$filter=" +
                URLEncoder.encode("resourceProvisioningOptions/Any(x:x eq 'Team')", StandardCharsets.UTF_8.name()));
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        try {

            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                //System.out.println("Error: Could not list tables");
                return null;
            }
            //System.out.println("Listed tables");
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);

            JsonObject responseJsonObject = JsonParser.parseString(responseString).getAsJsonObject();

            JsonArray valueJsonArray = new JsonArray();

            if (responseJsonObject.has("value"))
            {
                valueJsonArray = responseJsonObject.get("value").getAsJsonArray();
            }

            List<String[]> idAndNameList = new ArrayList<>();

            for (JsonElement element : valueJsonArray) {
                JsonObject teamObject = element.getAsJsonObject();
                String id = teamObject.get("id").getAsString();
                String displayName = teamObject.get("displayName").getAsString();
                String[] idAndName = {id, displayName};
                idAndNameList.add(idAndName);
            }
            return idAndNameList;
        }
        catch (IOException e)
        {
            return null;
        }
    }
    public String createLinkToTeam(String groupId) throws InterruptedException, UnsupportedEncodingException {
        String TENANT_ID = JsonTool.getAccessInfo(configAzure).get("TENANT_ID").getAsString();

        List<String[]> idAndNameList = listIDsTeam();

        boolean isTeams = false;

        for(String[] idAndName : idAndNameList)
        {
            if (groupId.equals(idAndName[0]))
            {
                isTeams = true;
                break;
            }
        }

        if (isTeams)
        {
            String channelsString = ChannelService.listAllChannels(groupId);
            JsonObject channelsJsonObject = JsonParser.parseString(channelsString).getAsJsonObject();

            JsonArray channelsJsonArray = new JsonArray();

            if (channelsJsonObject.has("value"))
            {
                channelsJsonArray = channelsJsonObject.get("value").getAsJsonArray();
            }

            for (JsonElement element : channelsJsonArray) {
                JsonObject channelObject = element.getAsJsonObject();
                String channelId = channelObject.get("id").getAsString();
                String displayName = channelObject.get("displayName").getAsString();
                if (displayName.equals("General"))
                {
                    //return link to team
                    String linkToTeam = "https://teams.microsoft.com/l/team/" + channelId + "/conversations?groupId=" + groupId +"&tenantId=" + TENANT_ID;
                    return "https://teams.microsoft.com/l/team/" + URLEncoder.encode(channelId, StandardCharsets.UTF_8) + "/conversations?groupId=" + groupId +"&tenantId=" + TENANT_ID;
                }
            }
            if (channelsJsonArray.size()>0)
            {
                JsonObject channelObject = channelsJsonArray.get(0).getAsJsonObject();
                String channelId = channelObject.get("id").getAsString();
                String displayName = channelObject.get("displayName").getAsString();
                return "https://teams.microsoft.com/l/team/" + URLEncoder.encode(channelId, StandardCharsets.UTF_8) + "/conversations?groupId=" + groupId +"&tenantId=" + TENANT_ID;
            }
        }
        return "Cannot create link to team";
    }

}
