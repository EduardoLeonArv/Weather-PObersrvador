import observer.*;
import api.WeatherAPIClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;

public class MainClearUpdate {
    private static final Map<String, JTextArea> cityTextAreas = new HashMap<>();
    private static final WeatherAPIStation station = new WeatherAPIStation(new WeatherAPIClient("73effaf7600879ce57e1a2adf0a23017"));
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public static void main(String[] args) {
        // Configurar la ventana principal
        JFrame frame = new JFrame("Monitor de Clima (Clear Update)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Panel para agregar ciudades
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        JTextField cityInput = new JTextField();
        JButton addCityButton = new JButton("Agregar Ciudad");

        controlPanel.add(cityInput, BorderLayout.CENTER);
        controlPanel.add(addCityButton, BorderLayout.EAST);

        // Área de visualización
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JScrollPane scrollPane = new JScrollPane(displayPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        frame.setLayout(new BorderLayout());
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Acción del botón para agregar ciudades
        addCityButton.addActionListener(e -> {
            String city = cityInput.getText().trim();
            if (!city.isEmpty() && !cityTextAreas.containsKey(city)) {
                try {
                    // Verificar si la ciudad es válida y tiene datos antes de crear el cuadro
                    station.fetchWeather(city);

                    // Crear el cuadro solo si la ciudad es válida y tiene datos
                    JTextArea cityArea = new JTextArea(5, 15);
                    cityArea.setEditable(false);
                    cityArea.setBorder(BorderFactory.createTitledBorder(city));
                    displayPanel.add(cityArea);
                    cityTextAreas.put(city, cityArea);

                    station.attach(weatherData -> SwingUtilities.invokeLater(() -> {
                        if (weatherData.getString("name").equalsIgnoreCase(city)) {
                            double temp = weatherData.getJSONObject("main").getDouble("temp");
                            String description = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
                            int humidity = weatherData.getJSONObject("main").getInt("humidity");

                            // Actualizar el contenido del área de texto limpiando el contenido previo
                            String newData = String.format("Temperatura: %.2f°C\nDescripción: %s\nHumedad: %d%%\n", temp, description, humidity);
                            cityArea.setText(newData);
                        }
                    }));

                    scheduler.scheduleAtFixedRate(() -> {
                        try {
                            station.fetchWeather(city);
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() -> cityArea.setText("Error: " + ex.getMessage()));
                        }
                    }, 0, 10, TimeUnit.SECONDS);

                    frame.revalidate();
                    frame.repaint();
                    cityInput.setText("");

                } catch (Exception ex) {
                    // Mostrar un mensaje de error si la ciudad no se encuentra o no tiene datos
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (cityTextAreas.containsKey(city)) {
                JOptionPane.showMessageDialog(frame, "La ciudad ya está siendo monitoreada.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Mostrar la ventana principal
        frame.setVisible(true);

        // Actualización automática de todas las ciudades monitoreadas
        scheduler.scheduleAtFixedRate(() -> {
            for (String city : cityTextAreas.keySet()) {
                try {
                    station.fetchWeather(city);
                } catch (Exception ex) {
                    JTextArea cityArea = cityTextAreas.get(city);
                    SwingUtilities.invokeLater(() -> cityArea.setText("Error al actualizar: " + ex.getMessage()));
                }
            }
        }, 0, 10, TimeUnit.SECONDS);

        // Agregar acción para cerrar limpiamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException ex) {
                scheduler.shutdownNow();
            }
        }));
    }
}
