import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import java.util.Date;

public class Spotify {

    String authorization = "Bearer BQAzWaBu8ulH5_G2DztbafRtDqcLrpADrAv0sAgSpnQtXPTicqMXLnYQRI9QeXd2_ooHemPh0N7VZQgBEXv9EenYyJajnyHQvv9q3zVVtdJa0Yqnf6gu6Wf2OOvPKwoVX6WYu9hWSVWbHOKJKZpeDU6aZyekJwJVyKXO_" +
            "vgG6HsUmGNaDa62QEwJGdl9YddRNlfxb5uYmTVs0Xsi1RK7MjUX_S-rKE4ean6pzbWOU88suI8YBzS4UNfWwL3bk-hav21O5vTN5usEfl4AAjlNrD7Noip2Fgcokw";
    Object userId;
    Object playListId;

    Date date = new Date();

    @Test
    public void givenURL_WhenOpen_PerformOpeartion() throws ParseException {

        //Get Current User's Profile
        Response response = RestAssured.given()
                .accept("application/json")
                .contentType(ContentType.JSON)
                .header("Authorization", authorization)
                .when()
                .get("https://api.spotify.com/v1/me");
        response.then().assertThat().statusCode(200);

        //Getting id for user
        Response responseForId = RestAssured.given()
                .accept("application/json")
                .contentType(ContentType.JSON)
                .header("Authorization", authorization)
                .when()
                .get("https://api.spotify.com/v1/me");
        response.then().assertThat().statusCode(200);
        JSONObject object = (JSONObject) new JSONParser().parse((responseForId.asString()));
        Object name = object.get("display_name");
        userId = object.get("id");
        System.out.println("User Name :" + name);
        System.out.println("User id   :" + userId);

        //Getting playlist of user
        Response responseForPlaylist = RestAssured.given()
                .accept("application/json")
                .contentType(ContentType.JSON)
                .header("Authorization", authorization)
                .pathParam("user_id", userId)
                .when()
                .get("https://api.spotify.com/v1/users/{user_id}/playlists");
        JSONObject allPlayList = (JSONObject) new JSONParser().parse((responseForPlaylist.asString()));
        Object totalValue = allPlayList.get("total");
        System.out.println("List Of All Playlist : " + totalValue);

        //Creating Playlist for user
        JSONObject playlist = new JSONObject();
        playlist.put("name", "My new list new songs"+date.getTime());
        playlist.put("description", "Songs added successfully");
        playlist.put("public", false);

        Response creatingPlayList = RestAssured.given()
                .accept("application/json")
                .contentType(ContentType.JSON)
                .header("Authorization", authorization)
                .pathParam("user_id", userId)
                .when()
                .body(playlist.toJSONString())
                .post("https://api.spotify.com/v1/users/{user_id}/playlists");
        response.then().assertThat().statusCode(200);
        JSONObject jsonObject = (JSONObject) new JSONParser().parse((responseForPlaylist.asString()));
        playListId = creatingPlayList.path("id");
        System.out.println("Play List Id : " + playListId);

        //Updating Playlist
        JSONObject playlistUpdate = new JSONObject();
        playlistUpdate.put("name", "Kumar Sanu' songs");
        playlistUpdate.put("description", "Updated Done");
        playlistUpdate.put("public", false);

        Response updatePlayList = RestAssured.given()
                .accept("application/json")
                .contentType(ContentType.JSON)
                .header("Authorization", authorization)
                .pathParam("playlist_id", playListId)
                .when()
                .body(playlistUpdate.toJSONString())
                .post("https://api.spotify.com/v1/playlists/{playlist_id}/tracks");
        response.then().assertThat().statusCode(200);
    }
}
