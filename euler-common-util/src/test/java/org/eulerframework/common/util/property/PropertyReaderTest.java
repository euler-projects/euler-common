package org.eulerframework.common.util.property;

import junit.framework.Assert;
import org.eulerframework.common.util.type.DurationStyle;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class PropertyReaderTest {

    private static PropertyReader propertyReader;

    static {
        try {
            propertyReader = new PropertyReader(new FilePropertySource("/config.properties"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void get() throws PropertyNotFoundException {
        Assert.assertEquals(DurationStyle.SIMPLE, propertyReader.get("enum.durationStyle", DurationStyle.class));
        Assert.assertEquals(DurationStyle.SIMPLE, propertyReader.get("enum.notExists", DurationStyle.SIMPLE));
        Assert.assertEquals(DurationStyle.SIMPLE, propertyReader.get("enum.notExists", DurationStyle.class, DurationStyle.SIMPLE));
    }
}