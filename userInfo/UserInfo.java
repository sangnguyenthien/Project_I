package userInfo;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class UserInfo {

    //private static final String AUTH_TOKEN = "Bearer eyJ0eXAiO ... 0X2tnSQLEANnSPHY0gKcgw";
    private static final String GRAPH_API_ENDPOINT = "https://graph.microsoft.com/v1.0/users";

    public static void main(String[] args) throws IOException {
        //String response = makeGraphApiRequest();
        //System.out.println(response);
    }

    private static String makeGraphApiRequest(String accessToken) throws IOException {
        String AUTH_TOKEN = "Bearer " + accessToken;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(GRAPH_API_ENDPOINT);
        request.setHeader("Authorization", AUTH_TOKEN);

        HttpResponse httpResponse = httpClient.execute(request);
        String response = EntityUtils.toString(httpResponse.getEntity());

        httpClient.close();

        return response;
    }
}
