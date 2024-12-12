package api;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WeatherAPIClient {
    private final String apiKey;

    public WeatherAPIClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public JSONObject getWeatherData(String city) throws Exception {
        String[] parts = city.split(",", 2); // Divide en nombre de ciudad y código de país
        String encodedCity;
        if (parts.length == 2) {
            // Si se incluye un código de país, asegúrate de codificarlo correctamente
            encodedCity = URLEncoder.encode(parts[0].trim(), "UTF-8") + "," + parts[1].trim();
        } else {
            // Solo codifica el nombre de la ciudad
            encodedCity = URLEncoder.encode(city.trim(), "UTF-8");
        }

        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&units=metric&appid=" + apiKey;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return new JSONObject(response.toString());
        } else if (responseCode == 404) {
            throw new Exception("Ciudad no encontrada. Por favor, verifica el nombre.");
        } else if (responseCode == 401) {
            throw new Exception("Error de autenticación: API Key inválida.");
        } else if (responseCode == 429) {
            throw new Exception("Límite de solicitudes alcanzado. Inténtalo más tarde.");
        } else {
            throw new Exception("Error al conectar con la API: Código " + responseCode);
        }
    }

}
