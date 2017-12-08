package com.houoy.smartpark.app.mobike.service.impl;

import com.houoy.smartpark.app.mobike.config.DoorConfig;
import com.houoy.smartpark.app.mobike.service.TCPDoorService;
import com.iandtop.common.driver.DoorDriver;
import com.iandtop.common.driver.door.DriverTCPClient;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by andyzhao on 2017/12/8.
 */
@Service("TCPDoorService")
public class TCPDoorServiceImpl implements TCPDoorService {
    private static final Logger logger = LoggerFactory.getLogger(TCPDoorServiceImpl.class);
    @Autowired
    private DoorConfig doorConfig;

    @Override
    public void openDoor(Vertx vertx, Boolean door1, Boolean door2, Handler<Boolean> responseHandler) {
        try {
            DriverTCPClient tcpClient = DoorDriver.getInstance(vertx, doorConfig.ip, doorConfig.port, doorConfig.sn);
            try {
                tcpClient.openDoor(door1, door1, false, false, success -> {
                    if (success) {
                        logger.info("Success open door" + doorConfig.ip + " " + doorConfig.port + " " + doorConfig.sn);
                    } else {
                        logger.error("Fail open door" + doorConfig.ip + " " + doorConfig.port + " " + doorConfig.sn);
                    }

                    if (responseHandler != null) {
                        responseHandler.handle(success);
                    }
                });
            } catch (IOException e) {
                logger.error("DriverTCPClient open door fail " + doorConfig.ip + " " + doorConfig.port + " " + doorConfig.sn, e);
                if (responseHandler != null) {
                    responseHandler.handle(false);
                }
            }
        } catch (Exception e) {
            logger.error("Get isRight info fail", e);
            if (responseHandler != null) {
                responseHandler.handle(false);
            }
        }
    }

    @Override
    public void weifaRequest(Vertx vertx, String vgdecoderresult, Handler<String> responseHandler, Handler<String> errorHandler) {
        //发送http请求，验证此二维码是否合法，可以开门
        String path = "/api/door?qrcode=" + vgdecoderresult;// + "&opentime=" + System.currentTimeMillis();
        vertx.createHttpClient().getNow(doorConfig.weifaPort, doorConfig.weifaIP, path, resp -> {
            resp.bodyHandler(body -> {
                responseHandler.handle(body.toString());
            });
        });
    }
}
