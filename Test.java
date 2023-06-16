import graphAccessToken.GetAccessTokenVer2;
import UserInfo.GetUserInfo;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws Exception
    {
        String tenantId = "865f9941-ebea-433d-9979-ffa06829d5e6";
        String clientId = "e9b191e7-b5fc-4ec1-ae09-2be5d19c0c97";
        String clientSecret = "Lq-8Q~yicdD92gpPliiPdiHYPwBtQJimNUYhrcP5";
        try {
            String accessToken = GetAccessTokenVer2.getAccessToken(tenantId, clientId, clientSecret);
            
            String response = GetUserInfo.makeGraphApiRequest(accessToken);

            System.out.println(response);

            //System.out.println("Access token: " + accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
