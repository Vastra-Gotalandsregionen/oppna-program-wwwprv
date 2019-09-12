package se.vgregion.portal.wwwprv.backingbean;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.logging.Slf4jLogger;
import org.springframework.stereotype.Component;

@Component
public class Hacks {

    static {
        // To make CXF log by SLF4J.
        LogUtils.setLoggerClass(Slf4jLogger.class);
    }

}
