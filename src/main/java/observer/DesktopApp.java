package observer;

import org.json.JSONObject;

public class DesktopApp implements Observer {
    private final String name;

    public DesktopApp(String name) {
        this.name = name;
    }

    @Override
    public void update(JSONObject weatherData) {
        String cityName = weatherData.getString("name");
        double temperature = weatherData.getJSONObject("main").getDouble("temp");
        String weatherDescription = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
        int humidity = weatherData.getJSONObject("main").getInt("humidity");

        System.out.println(name + " muestra el clima actual:");
        System.out.println("Ciudad: " + cityName);
        System.out.println("Temperatura: " + temperature + "°C");
        System.out.println("Descripción: " + weatherDescription);
        System.out.println("Humedad: " + humidity + "%");
    }
}
