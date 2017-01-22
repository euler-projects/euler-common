package net.eulerframework.common.util;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyReader {
    
    protected final Logger logger = LogManager.getLogger();

    private PropertySource propertySource;
    
    private String configFile = "/config.properties";
    
    public PropertyReader() {
        this.logger.warn("No config file path defined, use '"+ this.configFile +"' for default.");
        this.loadData();
    }
    
    /**
     * 
     * @param configFile {@link Class#getResource}
     */
    public PropertyReader(String configFile) {
        this.configFile = configFile;
        this.loadData();
    }

    public void refresh() {
        logger.info("Refresh File Config");
        this.loadData();
    }
    
    private void loadData() {
        try {
            propertySource = new PropertySource(this.configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String get(String property) throws PropertyReadException {
        try {
        	String value = (String) propertySource.getProperty(property);
            logger.info("Load config: " + property + "=" + value);
            return value;
        } catch (NullValueException e) {
            throw new PropertyReadException(e);
        }
    }
    
    public String get(String property, String defaultValue) {
        try {
            return get(property);
        } catch (PropertyReadException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }    

    public int getIntValue(String property, int defaultValue) {
        try {
            return Integer.parseInt(get(property));
        } catch (PropertyReadException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }
    
    public long getLongValue(String property, long defaultValue) {
        try {
            return Long.parseLong(get(property));
        } catch (PropertyReadException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }
    

    public double getDoubleValue(String property, double defaultValue) {
        try {
            return Double.parseDouble(get(property));
        } catch (PropertyReadException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }

    public boolean getBooleanValue(String property, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(get(property));
        } catch (PropertyReadException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }
    
    /**
     * 读取枚举类型的配置
     * @param property 参数名
     * @param defaultValue 默认值，在读不到的时候返回此值
     * @param toUpperCase 是否将读取到的字符串转为大写后再转为对应的Enum
     * @return 配置了正确的参数按配置返回，未配置或配置参数不正确返回默认值
     */
    public <T extends Enum<T>> T getEnumValue(String property, T defaultValue, boolean toUpperCase) {
        try {
            String configValue = get(property);
            
            if(toUpperCase)
                configValue = configValue.toUpperCase();
            
            return T.valueOf(defaultValue.getDeclaringClass(), configValue);
        } catch (PropertyReadException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        } catch (IllegalArgumentException e) {
            logger.warn(property +" was configed as a wrong value , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }
}
