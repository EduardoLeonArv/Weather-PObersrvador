import observer.*;
import api.WeatherAPIClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        String apiKey = "73effaf7600879ce57e1a2adf0a23017"; // Tu clave API
        WeatherAPIClient apiClient = new WeatherAPIClient(apiKey);

        WeatherAPIStation station = new WeatherAPIStation(apiClient);

        // Crear y registrar observadores
        Observer desktopApp = new DesktopApp("Aplicación de Escritorio");
        station.attach(desktopApp);

        // Crear un programador para consultas periódicas
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Consultar clima cada 10 segundos para Madrid y Londres alternadamente
        scheduler.scheduleAtFixedRate(() -> {
            try {
                String city = (System.currentTimeMillis() / 10000) % 2 == 0 ? "Madrid" : "London";
                System.out.println("\nConsultando clima para " + city + "...");
                station.fetchWeather(city);
            } catch (Exception e) {
                System.out.println("Error en la consulta: " + e.getMessage());
            }
        }, 0, 10, TimeUnit.SECONDS);

        // Mantener el programa corriendo
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nFinalizando consultas...");
            scheduler.shutdown();
        }));
    }
}
