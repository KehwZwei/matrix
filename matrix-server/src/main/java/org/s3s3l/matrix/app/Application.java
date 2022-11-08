package org.s3s3l.matrix.app;

import java.io.File;
import java.util.Properties;

import org.s3s3l.matrix.utils.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ContextIdApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * <p>
 * </p>
 * ClassName:Application <br>
 * Date: Aug 30, 2017 5:04:33 PM <br>
 * 
 * @author kehw_zwei
 * @version 1.0.0
 * @since JDK 1.8
 */
@EnableAutoConfiguration
@ComponentScan({ "org.s3s3l.matrix" })
@SpringBootApplication
public class Application extends ContextIdApplicationContextInitializer {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static ConfigurableApplicationContext ctx;

    private static final String[] LOG4J_CONFIG = new String[] { "file:config/log4j/log4j2.yml",
            "file:conf/log4j/log4j2.yml", "file:log4j2.yml", "classpath:config/log4j/log4j2.yml",
            "classpath:conf/log4j/log4j2.yml", "classpath:log4j2.yml" };

    public static void main(String[] args) throws Throwable {
        configureLog4j();
        ctx = SpringApplication.run(Application.class, args);

        logger.info("Started.");
    }

    /**
     *
     * 配置log4j2
     *
     * @since JDK 1.8
     */
    private static void configureLog4j() {
        Properties props = System.getProperties();
        props.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        File configFile = FileUtils.getFirstExistFile(LOG4J_CONFIG);
        if (configFile != null) {
            props.setProperty("log4j.configurationFile", configFile.getAbsolutePath());
        }
    }
}