package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final String PROPERTIES_FILE = "application.properties";
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("No se pudo encontrar el archivo " + PROPERTIES_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error cargando el archivo de configuración", e);
        }
    }

    public static String getApiKey() {
        return properties.getProperty("api.key");
    }

    public static String getBaseUrl() {
        return properties.getProperty("api.base_url");
    }
}