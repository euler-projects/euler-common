package net.eulerframework.common.util.property;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Properties;

import net.eulerframework.common.base.log.LogSupport;
import net.eulerframework.common.util.io.file.FileUtils;

public class PropertySource extends LogSupport {

    private Properties props;
    
    /**
     * 新建空Properties文件数据源
     */
    protected PropertySource() {
        this.props = new Properties();
    }

    /**
     * 新建Properties文件数据源, 并读取一个uri列表中的数据, 后读的会覆盖先读的
     * @param uri
     * @throws IOException
     * @throws URISyntaxException
     */
    protected PropertySource(String... uri) throws IOException, URISyntaxException {
        this();
        this.loadProperties(uri);
    }
    
    protected void loadProperties(String... uri) throws URISyntaxException, IOException {
        for(String each : uri) {
            this.loadProperties(each);
        }
    }
    
    protected void loadProperties(String uri) throws URISyntaxException, IOException {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = FileUtils.getInputStreamFromUri(uri);
            this.logger.info("Load property file: " + uri);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.props.load(bufferedReader);
        } finally {
            if(inputStream != null)
                inputStream.close();
            if(bufferedReader != null)
                bufferedReader.close();
        }
    }

    protected Object getProperty(String key) throws PropertyNotFoundException {
        Object value = this.props.get(key);
        if (value == null) {
            throw new PropertyNotFoundException("Property not found: " + key);
        }
        return value;
    }

}
