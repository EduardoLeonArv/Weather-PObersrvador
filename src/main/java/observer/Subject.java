package observer;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
    private final List<Observer> observers = new ArrayList<>();

    public void attach(Observer o) {
        observers.add(o);
    }

    public void detach(Observer o) {
        observers.remove(o);
    }

    protected void notifyObservers(JSONObject weatherData) {
        for (Observer o : observers) {
            o.update(weatherData);
        }
    }
}
