
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import template.service.GroupServiceImpl;
import template.service.UserServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static template.api_config.config.getAccessToken;


public class Main {
    public static void main(String[] args) throws Exception{
        UserServiceImpl service = new UserServiceImpl();
        /*
        //test get user
        service.getUser();*/

        //test create user
        /*
        service.createUser("John Doe" , "johndoe" ,"johndoe@3pjv85.onmicrosoft.com" ,"P@ssw0rd123");

         */
        GroupServiceImpl service2 = new GroupServiceImpl();

        //test create group
        /*
        String des = "Group with designated owner and members";
        String display = "My group";
        String mailNick = "phusgroup2023";
        String ownerId = "66ebefba-651f-4fe7-ab39-2b3a29b5643c";
        List<String> userIds = new ArrayList<>();
        userIds.add("6301cd42-30ba-476e-aca5-ad1fae558b0b");
        userIds.add("88ff6ec2-3b5d-4b56-b013-ebd683870604");
        service2.createGroup(des,display,mailNick,ownerId,userIds);
        */

        //test create team
        service2.createTeam("1e7ba4f2-5af5-47df-af68-394a5d62951f");

        /*
        //test create multiple user from csv
        service.createAllUsers("C:\\Users\\LamPhuss\\Downloads\\Project1-master\\users.csv");
        */
        /*
        //test add user to group
        String groupId = "1e7ba4f2-5af5-47df-af68-394a5d62951f"; // Replace with your actual group ID
        List<String> memberIds = List.of("3eb3c149-d420-4fb3-a00a-f46217a15920", "5d22e230-ac9a-4d26-9ed8-ebc401454827");
        service2.addMemberToTeam(groupId,memberIds);*/
    }


}