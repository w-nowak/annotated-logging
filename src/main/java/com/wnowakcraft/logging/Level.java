package com.wnowakcraft.logging;

import org.slf4j.Logger;

public enum Level {
    ERROR {
        void log(Logger logger, String message, Object... parameters) {
            logger.error(message, parameters);
        }
    },

    WARNING {
        void log(Logger logger, String message, Object... parameters) {
            logger.warn(message, parameters);
        }
    },
    INFO {
        void log(Logger logger, String message, Object... parameters) {
            logger.info(message, parameters);
        }
    },
    DEBUG {
        void log(Logger logger, String message, Object... parameters) {
            logger.debug(message, parameters);
        }
    };

    abstract void log(Logger logger, String message, Object... parameters);
}
