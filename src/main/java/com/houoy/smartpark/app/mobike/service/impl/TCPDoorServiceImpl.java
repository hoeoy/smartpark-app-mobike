package com.houoy.smartpark.app.mobike.service.impl;

import com.houoy.smartpark.app.mobike.config.DoorConfig;
import com.houoy.smartpark.app.mobike.domain.Device;
import com.houoy.smartpark.app.mobike.service.TCPDoorService;
import com.iandtop.common.driver.DoorDriver;
import com.iandtop.common.driver.door.DriverTCPClient;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by andyzhao on 2017/12/8.
 */
@Service("TCPDoorService")
public class TCPDoorServiceImpl implements TCPDoorService {
    private static final Logger logger = LoggerFactory.getLogger(TCPDoorServiceImpl.class);

    @Autowired
    private DoorConfig doorConfig;

    @Override
    public void openDoor(Vertx vertx, String id, Boolean door1, Boolean door2, Handler<Boolean> responseHandler) {
        Device device = getDeviceById(null, id);
//        Device device = getDeviceById(doorConfig.device, id);
        if (device == null) {
            logger.info("Can't find door device by sn=" + device.getSn() + " in application.properties");
            responseHandler.handle(false);
        }

        try {
            DriverTCPClient tcpClient = DoorDriver.getInstance(vertx, device.getIp(), device.getPort(), device.getSn());
            try {
                tcpClient.openDoor(door1, door1, false, false, success -> {
                    if (success) {
                        logger.info("Success open door" + device.getIp() + " " + device.getPort() + " " + device.getSn());
                    } else {
                        logger.error("Fail open door" + device.getIp() + " " + device.getPort() + " " + device.getSn());
                    }

                    if (responseHandler != null) {
                        responseHandler.handle(success);
                    }
                });
            } catch (IOException e) {
                logger.error("DriverTCPClient open door fail " + device.getIp() + " " + device.getPort() + " " + device.getSn(), e);
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
        vertx.createHttpClient().getNow(doorConfig.getWeifaPort(), doorConfig.getWeifaIP(), path, resp -> {
            resp.bodyHandler(body -> {
                responseHandler.handle(body.toString());
            });
        });
    }

    private Device getDeviceById(List<Device> deviceList, String id) {
        if (StringUtils.isEmpty(id) || CollectionUtils.isEmpty(deviceList)) {
            return null;
        }

        for (Device device : deviceList) {
            if (!StringUtils.isEmpty(device.getSn())
                    && device.getSn().equals(id)) {
                return device;
            }
        }

        return null;
    }
}
