package com.houoy.smartpark.app.mobike.config;

import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by andyzhao on 2017/12/3.
 */
@Component("DoorConfig")
@Configuration
public class DoorConfig {
    @Value("${door.ip}")
    public String ip;
    @Value("${door.port}")
    public Integer port;
    @Value("${door.sn}")
    public String sn;
    @Value("${door.tcpServerPort}")
    public Integer tcpServerPort;

    @Value("${weifa.ip}")
    public String weifaIP;
    @Value("${weifa.port}")
    public Integer weifaPort;

    @Bean("Vertx")
    public Vertx getVertx() {
        Vertx vertx = Vertx.vertx();
        return vertx;
    }

}
