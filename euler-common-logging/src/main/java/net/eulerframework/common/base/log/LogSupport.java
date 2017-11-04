package net.eulerframework.common.base.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LogSupport {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
}
