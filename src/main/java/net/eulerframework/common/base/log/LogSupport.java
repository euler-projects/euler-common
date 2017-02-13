package net.eulerframework.common.base.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LogSupport {    
    protected final Logger logger = LogManager.getLogger(this.getClass());
}
