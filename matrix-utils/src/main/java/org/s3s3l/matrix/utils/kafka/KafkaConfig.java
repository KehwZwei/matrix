package org.s3s3l.matrix.utils.kafka;

import java.util.Properties;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.s3s3l.matrix.utils.bean.exception.ResourceNotFoundException;
import org.s3s3l.matrix.utils.file.FileUtils;
import org.s3s3l.matrix.utils.stuctural.jackson.JacksonHelper;
import org.s3s3l.matrix.utils.stuctural.jackson.JacksonUtils;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class KafkaConfig implements InitializingBean {
    private static final String[] EXTRA_CONFIG_FILES = new String[] { "kafka.conf", "config/kafka.conf",
            "conf/kafka.conf" };
    private String bootstrapServers;
    private short replicationFactor = 1;
    // ms
    private int timeout = 3_000;
    private String adminConfigFileLocation;
    private String producerConfigFileLocation;
    private String consumerConfigFileLocation;
    private Properties extraConfig = new Properties();

    public static Properties DEFAULT_CONFIG = new Properties();

    static {
        DEFAULT_CONFIG.put(
                org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getCanonicalName());// key反序列化方式
        DEFAULT_CONFIG.put(
                org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                ByteArraySerializer.class.getCanonicalName());// value反系列化方式
    }

    @Data
    public static class ConsumerConfig {
        public static Properties DEFAULT_CONFIG = new Properties();

        static {
            DEFAULT_CONFIG.put(
                    org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    StringDeserializer.class.getCanonicalName());// key反序列化方式
            DEFAULT_CONFIG.put(
                    org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    ByteArrayDeserializer.class.getCanonicalName());// value反系列化方式
            DEFAULT_CONFIG.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);// 自动提交
            DEFAULT_CONFIG.put(org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);// 每次最大的拉取数
            DEFAULT_CONFIG.put(org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1_800_000);// 每次拉取最大的处理时长
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        JacksonHelper helper = JacksonUtils.create(new YAMLFactory());
        try {
            helper.update(this, FileUtils.getFirstExistResource(EXTRA_CONFIG_FILES));
        } catch (ResourceNotFoundException e) {
            // ignore
        } catch (Exception e) {
            log.warn("kafka独立配置文件加载失败: {}", EXTRA_CONFIG_FILES, e);
        }
        log.info("kafka配置： {}", helper.prettyPrinter().toStructuralString(this));
    }
}
