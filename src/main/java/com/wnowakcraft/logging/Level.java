package com.wnowakcraft.logging;

import org.slf4j.Logger;

/**
 * Class which defines possible levels for log statements.
 */
public enum Level {
    /** Error level */
    ERROR {
        void log(Logger logger, String message, Object... parameters) {
            logger.error(message, parameters);
        }
    },

    /** Warning level */
    WARNING {
        void log(Logger logger, String message, Object... parameters) {
            logger.warn(message, parameters);
        }
    },

    /** Info level */
    INFO {
        void log(Logger logger, String message, Object... parameters) {
            logger.info(message, parameters);
        }
    },

    /** Debug level */
    DEBUG {
        void log(Logger logger, String message, Object... parameters) {
            logger.debug(message, parameters);
        }
    };

    abstract void log(Logger logger, String message, Object... parameters);
}
