package com.houoy.smartpark.app.mobike;

import com.google.gson.Gson;
import com.houoy.smartpark.app.mobike.config.DoorConfig;
import com.houoy.smartpark.app.mobike.vo.ResultVO;
import com.iandtop.common.driver.DoorDriver;
import com.iandtop.common.driver.door.DriverTCPClient;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
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
//        HttpServer server = vertx.createHttpServer();
//        Router router = Router.router(vertx);

//        Gson gson = new Gson();
//        router.route(HttpMethod.POST, "/path").handler(routingContext -> {
//            String vgdecoderresult = routingContext.request().getParam("vgdecoderresult");
//            String devicenumber = routingContext.request().getParam("devicenumber");
//            String otherparams = routingContext.request().getParam("otherparams");
//
//            //发送http请求，验证此二维码是否合法，可以开门
//            String path = "/api/door?qrcode=" + vgdecoderresult;// + "&opentime=" + System.currentTimeMillis();
//            vertx.createHttpClient().getNow(doorConfig.weifaPort, doorConfig.weifaIP, path, resp -> {
//                resp.bodyHandler(body -> {
//                    String rs = gson.toJson(new ResultVO());
//                    routingContext.response().putHeader("content-type", "text/json").end(rs);
//                    try {
//                        String isRight = body.toString("UTF-8");
//                        if (!StringUtils.isEmpty(isRight) && isRight.equals("0")) {
//                            DriverTCPClient tcpClient = DoorDriver.getInstance(vertx, doorConfig.ip, doorConfig.port, doorConfig.sn);
//                            try {
//                                tcpClient.openDoor(true, true, false, false, success -> {
//                                    String rs = gson.toJson(new ResultVO());
//                                    routingContext.response().putHeader("content-type", "text/json").end(rs);
//                                });
//                            } catch (IOException e) {
//                                logger.error("DriverTCPClient open door fail " + doorConfig.ip + " " + doorConfig.port + " " + doorConfig.sn, e);
//                                String rs = gson.toJson(new ResultVO("0001", "DriverTCPClient open door fail " + doorConfig.ip + " " + doorConfig.port + " " + doorConfig.sn));
//                                routingContext.response().putHeader("content-type", "text/json").end(rs);
//                            }
//                        }
//                    } catch (Exception e) {
//                        logger.error("Get isRight info fail" + path, e);
//                        String rs = gson.toJson(new ResultVO("0001", "Get isRight info fail" + path));
//                        routingContext.response().putHeader("content-type", "text/json").end(rs);
//                    }
//                });
//            });
//        });
//        server.requestHandler(router::accept).listen(doorConfig.httpServerPort);


        NetServer tcpserver = vertx.createNetServer();
        //pos机采用的是短连接，一个连接一个报文交易，交易后链接立即关闭。
        //新的交易来到后会新开启一个链接，所以不用考虑多条消息报文交叉接收的情况，不用考虑粘包,拆包
        tcpserver.connectHandler(socket -> {
            socket.handler(buffer -> {
                String qrString = buffer.toString();
                String qr = "";
                Boolean openDoor1 = false;
                Boolean openDoor2 = false;

                if (!StringUtils.isEmpty(qrString)) {
                    if (qrString.startsWith("door")) {
                        if (qrString.contains("door1_")) {
                            qr = qrString.split("door1_")[1];
                            openDoor1 = true;
                        } else if (qrString.contains("door2_")) {
                            qr = qrString.split("door2_")[1];
                            openDoor2 = true;
                        }
                    } else {
                        logger.info("系统外非法格式二维码：" + qrString);
                    }
                }
                final Boolean finalOpenDoor = openDoor2;
                final Boolean finalOpenDoor1 = openDoor1;
                String vgdecoderresult = qr;

                Gson gson = new Gson();
                //发送http请求，验证此二维码是否合法，可以开门
                String path = "/api/door?qrcode=" + vgdecoderresult;// + "&opentime=" + System.currentTimeMillis();
                vertx.createHttpClient().getNow(doorConfig.weifaPort, doorConfig.weifaIP, path, resp -> {
                    resp.bodyHandler(body -> {
                        ResultVO resultVO = gson.fromJson(body.toString(), ResultVO.class);//对于javabean直接给出class实例
                        if (resultVO != null && resultVO.getResult().equals("0")) {//验证通过
                            try {
                                DriverTCPClient tcpClient = DoorDriver.getInstance(vertx, doorConfig.ip, doorConfig.port, doorConfig.sn);
                                try {
                                    tcpClient.openDoor(finalOpenDoor1, finalOpenDoor, false, false, success -> {
//                                            String rs = gson.toJson(new ResultVO());
//                                            routingContext.response().putHeader("content-type", "text/json").end(rs);
                                    });
                                } catch (IOException e) {
                                    logger.error("DriverTCPClient open door fail " + doorConfig.ip + " " + doorConfig.port + " " + doorConfig.sn, e);
//                                        String rs = gson.toJson(new ResultVO("0001", "DriverTCPClient open door fail " + doorConfig.ip + " " + doorConfig.port + " " + doorConfig.sn));
//                                        routingContext.response().putHeader("content-type", "text/json").end(rs);
                                }
                            } catch (Exception e) {
                                logger.error("Get isRight info fail" + path, e);
//                                String rs = gson.toJson(new ResultVO("0001", "Get isRight info fail" + path));
//                                routingContext.response().putHeader("content-type", "text/json").end(rs);
                            }
                        } else {
                            logger.info(resultVO.getMsg() + ": " + vgdecoderresult);
                        }
                    });
                });

            });
            socket.closeHandler(v -> {
                logger.info("本次链接关闭");
            });

        }).listen(doorConfig.httpServerPort, res -> {
            if (res.succeeded()) {
                logger.info("监听成功,监听端口号："+doorConfig.httpServerPort);
            } else {
                logger.error("监听端口"+doorConfig.httpServerPort+"失败",res.cause());
            }
        });

        logger.info("app init success");
    }
}
