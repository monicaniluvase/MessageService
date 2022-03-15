package services.impl;

import config.Configuration;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import services.ConfigService;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigServiceImpl implements ConfigService {
    private Configuration configuration;

    public ConfigServiceImpl() throws IOException {
        initializeConfig();
    }
    private void initializeConfig() throws IOException {
        String config = getConfigFromFileLocation();
        configuration =  new Yaml(new Constructor(Configuration.class)).load(config);
    }

    private String getConfigFromFileLocation() throws IOException {
        File file = new File("src/main/resources/config.yml");
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    @Override
    public Configuration getConfig() {
        return configuration;
    }

}
