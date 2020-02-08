import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Spotify {

    String authorization;
    Object userId;
    Object playListId;

    @BeforeMethod
    public void setUp() {
        authorization = "Bearer BQClNKz7GpJelkkp-80wESxxA94TpZ7bn2paZIOgL5oH4ZM_X02vrrY0svGnBGLVLVVtbSERHHsA7jrS7lnwJX1yn0QJlXpN_lvI-iG5Gi24UoxFNWsoOwX9RVHvpt9JklO6RXn329810E4_ceLFy12Cq_e71qLPd65Vf_GgbloYpbCj38hrQGWI4oOYP93Ads_q0s_2LlD3KeixB6Wp_rgFvPAyAz1RvpqohvwiLbyGU3tjXmobVyytBLmMSC1Sg96JLay95bKLPGhWkMqbR5nmZPbjbwJDpg\"";
    }

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
        playlist.put("name", "My new list new songs");
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
