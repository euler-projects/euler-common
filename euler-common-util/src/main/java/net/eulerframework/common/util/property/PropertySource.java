package net.eulerframework.common.util.property;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import net.eulerframework.common.base.log.LogSupport;

public class PropertySource extends LogSupport {

    private Properties props;

    /**
     * 新建Properties文件数据源
     * @param configFile Properties文件路径，具体搜索规则参考{@link Class#getResource}
     * @param callerClass 调用者的Class，用来确定搜索位置
     * @throws IOException config file cannot be found
     */
    protected PropertySource(String configFile, Class<?> callerClass) throws IOException {
        InputStream inputStream = null;
        try {
            URL url = callerClass.getResource(configFile);
            inputStream = url.openStream();
            this.logger.info("Load property file: " + url.toString());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.props = new Properties();
            this.props.load(bufferedReader);
        } catch (NullPointerException e) {
            throw new IOException("Property file \"" + configFile + "\" read error. Does this file exist?", e);
        } catch (IOException e) {
            throw e;
        } finally {
            if(inputStream != null)
                inputStream.close();
        }
    }

    public Object getProperty(String key) throws PropertyNotFoundException {
        Object value = this.props.get(key);
        if (value == null) {
            throw new PropertyNotFoundException("Property not found: " + key);
        }
        return value;
    }

}
