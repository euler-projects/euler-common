package net.eulerframework.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertySource {

    protected final Logger logger = LogManager.getLogger();

    private Properties props;

    /**
     * 
     * @param configFile {@link Class#getResource}
     * @throws IOException config file cannot be found
     */
    public PropertySource(String configFile) throws IOException {
        try {
            URL url = this.getClass().getResource(configFile);
            InputStream inputStream = url.openStream();
            this.logger.info("Load property file: " + url.toString());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.props = new Properties();
            this.props.load(bufferedReader);
        } catch (NullPointerException e) {
            throw new IOException("Property file \"" + configFile + "\" read error. Does this file exist?", e);
        }
    }

    public PropertySource(InputStream inputStream) throws IOException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.props = new Properties();
            this.props.load(bufferedReader);
        } catch (NullPointerException e) {
            throw new IOException("Property file \"" + "\" read error. Does this file exist?", e);
        }
    }

    public Object getProperty(String key) throws NullValueException {
        Object value = this.props.get(key);
        if (value == null) {
            throw new NullValueException("Key read error, no such key: " + key);
        }
        return value;
    }

}
