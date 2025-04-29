package hr.iisbackend.XmlRpc;
import org.w3c.dom.*;
import org.springframework.stereotype.Component;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class WeatherServiceImpl implements WeatherService {

    @Override
    public List<Map<String, String>> getTemperature(String cityPart) {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            URL url = new URL("https://vrijeme.hr/hrvatska_n.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(url.openStream());
            NodeList cities = doc.getElementsByTagName("Grad");

            for (int i = 0; i < cities.getLength(); i++) {
                Element cityElement = (Element) cities.item(i);
                String cityName = cityElement.getElementsByTagName("GradIme").item(0).getTextContent();
                String temperature = cityElement.getElementsByTagName("Temp").item(0).getTextContent();

                if (cityName.toLowerCase().contains(cityPart.toLowerCase())) {
                    Map<String, String> cityTemp = new HashMap<>();
                    cityTemp.put("city", cityName);
                    cityTemp.put("temperature", temperature);
                    result.add(cityTemp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
