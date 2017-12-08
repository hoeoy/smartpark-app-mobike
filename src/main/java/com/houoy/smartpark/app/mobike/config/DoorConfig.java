package com.houoy.smartpark.app.mobike.config;

import com.houoy.smartpark.app.mobike.domain.Device;
import io.vertx.core.Vertx;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by andyzhao on 2017/12/3.
 */
@Component("DoorConfig")
@Configuration
@ConfigurationProperties(prefix = "door")
@EnableConfigurationProperties
@Data
@NoArgsConstructor
public class DoorConfig {

    // @Value("${door}")
    public List<Device> device;

    private Integer tcpServerPort;

    private String weifaIP;

    private Integer weifaPort;

    @Bean("Vertx")
    public Vertx getVertx() {
        Vertx vertx = Vertx.vertx();
        return vertx;
    }
}
