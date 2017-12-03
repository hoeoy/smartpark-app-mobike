package com.houoy.smartpark.app.mobike;

import com.google.gson.Gson;
import com.houoy.smartpark.app.mobike.config.DoorConfig;
import com.houoy.smartpark.app.mobike.vo.ResultVO;
import com.iandtop.common.driver.DoorDriver;
import com.iandtop.common.driver.door.DriverTCPClient;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;

import java.io.IOException;

@SpringBootApplication
@EnableAutoConfiguration
@EnableAsync
public class SmartparkAppMobikeApplication {
    private static final Logger logger = LoggerFactory.getLogger(SmartparkAppMobikeApplication.class);

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(SmartparkAppMobikeApplication.class, args);
        DoorConfig doorConfig = (DoorConfig) applicationContext.getBean("DoorConfig");

        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        Gson gson = new Gson();
        router.route(HttpMethod.POST, "/path").handler(routingContext -> {
            String vgdecoderresult = routingContext.request().getParam("vgdecoderresult");
            String devicenumber = routingContext.request().getParam("devicenumber");
            String otherparams = routingContext.request().getParam("otherparams");

            //发送http请求，验证此二维码是否合法，可以开门
            String path = "/api/door?qrcode=" + vgdecoderresult + "&opentime=" + System.currentTimeMillis();
            vertx.createHttpClient().getNow(doorConfig.weifaPort, doorConfig.weifaIP, path, resp -> {
                resp.bodyHandler(body -> {
                    try {
                        String isRight = body.toString("UTF-8");
                        if (!StringUtils.isEmpty(isRight) && isRight.equals("0")) {
                            DriverTCPClient tcpClient = DoorDriver.getInstance(vertx, doorConfig.ip, doorConfig.port, doorConfig.sn);
                            try {
                                tcpClient.openDoor(true, true, false, false, success -> {
                                    String rs = gson.toJson(new ResultVO());
                                    routingContext.response().putHeader("content-type", "text/json").end(rs);
                                });
                            } catch (IOException e) {
                                logger.error("DriverTCPClient open door fail " + doorConfig.ip + " " + doorConfig.port + " " + doorConfig.sn, e);
                                String rs = gson.toJson(new ResultVO("0001", "DriverTCPClient open door fail " + doorConfig.ip + " " + doorConfig.port + " " + doorConfig.sn));
                                routingContext.response().putHeader("content-type", "text/json").end(rs);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Get isRight info fail" + path, e);
                        String rs = gson.toJson(new ResultVO("0001", "Get isRight info fail" + path));
                        routingContext.response().putHeader("content-type", "text/json").end(rs);
                    }
                });
            });
        });

        server.requestHandler(router::accept).listen(doorConfig.httpServerPort);
        logger.info("app init success");
    }
}
