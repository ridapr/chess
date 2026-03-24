package client;

import model.AuthData;
import model.GameData;

import com.google.gson.Gson;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {
    private String baseUrl;
    private Gson gson = new Gson();


    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
    }



    public  AuthData register(String username, String password, String email) throws ClientException {
        var body = Map.of("username", username, "password", password, "email", email);
        // make helper
    }

    public AuthData login() throws ClientException {

    }

    public AuthData logout() throws ClientException {

    }



    public Collection<GameData> listGames() throws ClientException {

    }

    public int createGame() throws ClientException {

    }

    public void joinGame() throws ClientException {


    }

    public void clear() throws ClientException {

    }




}
