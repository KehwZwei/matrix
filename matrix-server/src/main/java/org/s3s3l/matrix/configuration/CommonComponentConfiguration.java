package org.s3s3l.matrix.configuration;

import org.s3s3l.matrix.utils.common.SnowFlake;
import org.s3s3l.matrix.utils.http.HttpHelper;
import org.s3s3l.matrix.utils.http.OkHttpHelper;
import org.s3s3l.matrix.utils.verify.CommonVerifier;
import org.s3s3l.matrix.utils.verify.Verifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import okhttp3.OkHttpClient;

@Configuration
public class CommonComponentConfiguration {

    @Bean
    public Verifier verifier() {
        return new CommonVerifier();
    }

    @Bean
    public HttpHelper http() {
        return new OkHttpHelper(new OkHttpClient());
    }

    @Bean
    public SnowFlake snowFlake() {
        return new SnowFlake(0, 0);
    }
}
