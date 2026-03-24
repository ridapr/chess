package client;

import model.AuthData;
import model.GameData;

import com.google.gson.Gson;
import java.util.Collection;
import java.util.Map;
import java.net.*;
import java.io.*;

public class ServerFacade {
    private String baseUrl;
    private Gson gson = new Gson();


    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
    }



    public  AuthData register(String username, String password, String email) throws ClientException {
        var body = Map.of("username", username, "password", password, "email", email);
        return request("POST", "/user", body, null, AuthData.class);
    }

    public AuthData login() throws ClientException {
        return null;
    }

    public AuthData logout() throws ClientException {
        return null;
    }



    public Collection<GameData> listGames() throws ClientException {
        return null;
    }

    public int createGame() throws ClientException {
        return 0;
    }

    public void joinGame() throws ClientException {


    }

    public void clear() throws ClientException {

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
