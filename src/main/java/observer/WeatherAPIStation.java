package observer;

import api.WeatherAPIClient;
import org.json.JSONObject;

public class WeatherAPIStation extends Subject {
    private final WeatherAPIClient apiClient;
    private JSONObject weatherData;

    public WeatherAPIStation(WeatherAPIClient apiClient) {
        this.apiClient = apiClient;
    }

    public void fetchWeather(String city) throws Exception {
        try {
            JSONObject newWeatherData = apiClient.getWeatherData(city);

            // Validar que los datos contengan información relevante
            if (!newWeatherData.has("name") || !newWeatherData.has("main")
                    || !newWeatherData.has("weather")) {
                throw new Exception("Datos incompletos o no disponibles para la ciudad: " + city);
            }

            // Verificar si los datos son nuevos y notificar a los observadores
            if (weatherData == null || !newWeatherData.similar(weatherData)) {
                weatherData = newWeatherData;
                notifyObservers(weatherData);
            }
        } catch (Exception e) {
            if (e.getMessage().contains("404")) {
                throw new Exception("Ciudad no encontrada: " + city);
            }
            throw new Exception("Error al obtener datos de la API: " + e.getMessage());
        }
    }

}
