package com.houoy.smartpark.app.mobike.service;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * Created by andyzhao on 2017/12/8.
 */
public interface TCPDoorService {
    void openDoor(Vertx vertx,String id,Boolean door1, Boolean door2, Handler<Boolean> responseHandler);
    void weifaRequest(Vertx vertx,String vgdecoderresult,Handler<String> responseHandler,Handler<String> errorHandler);
}
