package hr.iisbackend.XmlRpc;

import java.util.List;
import java.util.Map;

public interface WeatherService {
    List<Map<String, String>> getTemperature(String cityPart);
}
