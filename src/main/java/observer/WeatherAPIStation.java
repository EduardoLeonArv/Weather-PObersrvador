package observer;

import api.WeatherAPIClient;
import org.json.JSONObject;

public class WeatherAPIStation extends Subject {
    private final WeatherAPIClient apiClient;
    private JSONObject weatherData;

    public WeatherAPIStation(WeatherAPIClient apiClient) {
        this.apiClient = apiClient;
    }

    public void fetchWeather(String city) {
        try {
            JSONObject newWeatherData = apiClient.getWeatherData(city);
            if (weatherData == null || !newWeatherData.similar(weatherData)) {
                weatherData = newWeatherData;
                notifyObservers(weatherData);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener datos de la API: " + e.getMessage());
        }
    }
}
