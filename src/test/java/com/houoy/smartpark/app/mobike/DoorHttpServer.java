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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 门禁前置机  httpserver服务  uap请求此服务
 *
 * @author andyzhao
 */
@Component
public class DoorHttpServer implements CommandLineRunner {

    public static void main(String[] args) {
        try {
            DoorHttpServer server = new DoorHttpServer();
            server.doorConfig = new DoorConfig();
            server.doorConfig.ip = "192.168.0.150";
            server.doorConfig.port = 8000;
            server.doorConfig.sn = "CA-3220T27010046";
            server.doorConfig.httpServerPort = 8081;

            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private DoorConfig doorConfig;

    @Override
    public void run(String... strings) throws Exception {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

//        //初始化门禁信息并启动
//        router.route(HttpMethod.POST, "/path/" +
//                ":" + "a" ).handler(routingContext -> {
//            String a = routingContext.request().getParam("a");
        //初始化门禁信息并启动


        router.route(HttpMethod.POST, "/path").handler(routingContext -> {
            String b = routingContext.request().getParam("b");
            String c = routingContext.request().getParam("c");
            String d = routingContext.request().getParam("d");


            DriverTCPClient tcpClient = DoorDriver.getInstance(vertx, doorConfig.ip, doorConfig.port, doorConfig.sn);
            try {
                tcpClient.openDoor(true, true, false, false, success -> {
                    Gson gson = new Gson();
                    String rs = gson.toJson(new ResultVO());
                    routingContext.response().putHeader("content-type", "text/json").end(rs);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.requestHandler(router::accept).listen(doorConfig.httpServerPort);
    }
}
