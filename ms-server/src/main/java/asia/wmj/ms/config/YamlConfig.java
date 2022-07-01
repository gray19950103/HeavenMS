package asia.wmj.ms.config;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.*;
import java.net.URL;
import java.util.List;


public class YamlConfig {

    public static final YamlConfig config = fromFile("config.yaml");
    
    public List<WorldConfig> worlds;
    public ServerConfig server;

    public static YamlConfig fromFile(String filename) {
        try {
            InputStream inputStream = YamlConfig.class.getClassLoader().getResourceAsStream(filename);
            if (inputStream == null) throw new FileNotFoundException();
            YamlReader reader = new YamlReader(new InputStreamReader(inputStream));
            YamlConfig config = reader.read(YamlConfig.class);
            reader.close();
            return config;
        } catch (FileNotFoundException e) {
            String message = "Could not read config file " + filename + ": " + e.getMessage();
            throw new RuntimeException(message);
        } catch (IOException e) {
            String message = "Could not successfully parse config file " + filename + ": " + e.getMessage();
            throw new RuntimeException(message);
        }
    }
}
