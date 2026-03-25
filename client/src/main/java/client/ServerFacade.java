package client;

import model.AuthData;
import model.GameData;

import com.google.gson.Gson;
import java.util.Collection;
import java.util.Map;
import java.net.*;
import java.io.*;

public class ServerFacade {
    private final String baseUrl;
    private final Gson gson = new Gson();


    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
    }



    public  AuthData register(String username, String password, String email) throws ClientException {
        var body = Map.of("username", username, "password", password, "email", email);
        return request("POST", "/user", body, null, AuthData.class);
    }

    public AuthData login(String username, String password) throws ClientException {
        var body = Map.of("username", username, "password", password);
        return request("POST", "/session", body, null, AuthData.class);
    }

    public void logout(String authToken) throws ClientException {
        request("DELETE", "/session", null, authToken, Void.class);
    }



    public Collection<GameData> listGames(String authToken) throws ClientException {
        record ListGamesResponse(Collection<GameData> games) {}
        var response = request("GET", "/game", null, authToken, ListGamesResponse.class);
        return response.games();
    }

    public int createGame(String gameName, String authToken) throws ClientException {
        var body = Map.of("gameName", gameName);
        record CreateGameResponse(int gameID) {}
        var response = request("POST", "/game", body, authToken, CreateGameResponse.class);
        return response.gameID();
    }

    public void joinGame() throws ClientException {


    }

    public void clear() throws ClientException {
        request("DELETE", "/db", null, null, Void.class);
    }





    private <T> T request(String method, String path, Object body, String authToken, Class<T> responseClass) throws ClientException {
        try {
            URL url = new URI(baseUrl + path).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type",  "application/json");
            if (authToken != null) {
                conn.setRequestProperty("Authorization", authToken);
            }
            if (body != null) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(gson.toJson(body).getBytes());
                }
            }

            conn.connect();
            int status = conn.getResponseCode();
            // get just first number of code
            InputStream stream = (status / 100 ==2) ? conn.getInputStream() : conn.getErrorStream();
            String responseBody = stream == null ? "{}" : new String(stream.readAllBytes());

            if (status / 100 != 2) {
                record ErrorResponse(String message) {
                }
                var err = gson.fromJson(responseBody, ErrorResponse.class);
                String message2 = (err != null && err.message() != null) ? err.message() : "Error: HTTP " + status;
                throw new ClientException(message2);
            }

            if (responseClass == Void.class || responseBody.isBlank()) {
                return null;
            }

            return gson.fromJson(responseBody, responseClass);

        } catch (ClientException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ClientException("Error: " + ex.getMessage());
        }
    }


}
