package observer;

import org.json.JSONObject;

public interface Observer {
    void update(JSONObject weatherData);
}
