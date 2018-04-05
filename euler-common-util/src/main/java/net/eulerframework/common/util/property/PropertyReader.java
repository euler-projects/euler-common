package net.eulerframework.common.util.property;

import java.io.IOException;
import java.net.URISyntaxException;

import net.eulerframework.common.base.log.LogSupport;
import net.eulerframework.common.util.ArrayUtils;

public class PropertyReader extends LogSupport {

    private PropertySource propertySource;
    
    private String[] configFile;
    
    /**
     * 初始化读取器，使用classpath根目录作为搜索位置，config.properties作为文件名
     */
    public PropertyReader() {
        this("/config.properties");
        this.logger.warn("No config file path defined, use '"+ this.configFile +"' for default.");
    }

    /**
     * 初始化读取器, 并指定多个uri作为搜索位置, 如存在相同的属性, 后面的会覆盖前面的
     * @param configFileUri 配置文件URI
     */
    public PropertyReader(String... configFileUri) {
        this.configFile = configFileUri;
        this.loadData();
    }
    
    /**
     * 向读取器追加新的配置文件URI
     * @param configFileUri 配置文件URI
     */
    public void addConfigFile(String... configFileUri) {
        this.configFile = ArrayUtils.concat(this.configFile, configFileUri);
        try {
            this.propertySource.loadProperties(configFileUri);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh() {
        logger.info("Refresh File Config");
        this.loadData();
    }
    
    private void loadData() {
        try {
            propertySource = new PropertySource(this.configFile);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String get(String property) throws PropertyNotFoundException {
        String value = (String) propertySource.getProperty(property);
        logger.info("Load config: " + property + "=" + value);
        return value;
    }
    
    public String get(String property, String defaultValue) {
        try {
            return get(property);
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }    

    public int getIntValue(String property, int defaultValue) {
        try {
            return Integer.parseInt(get(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }
    
    public long getLongValue(String property, long defaultValue) {
        try {
            return Long.parseLong(get(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }
    

    public double getDoubleValue(String property, double defaultValue) {
        try {
            return Double.parseDouble(get(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }

    public boolean getBooleanValue(String property, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(get(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }
    
    /**
     * 读取枚举类型的配置
     * 
     * @param <T> 待读取的枚举类
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
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        } catch (IllegalArgumentException e) {
            logger.error(property +" was configed as a wrong value.");
            throw new EnumPropertyReadException(e);
        }
    }
}
