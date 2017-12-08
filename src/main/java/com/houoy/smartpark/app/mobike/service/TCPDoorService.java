package com.houoy.smartpark.app.mobike.service;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * Created by andyzhao on 2017/12/8.
 */
public interface TCPDoorService {
    Boolean openDoor(Vertx vertx,Boolean door1, Boolean door2, Handler<Boolean> responseHandler);
    Boolean weifaRequest(Vertx vertx,String vgdecoderresult,Handler<String> responseHandler,Handler<String> errorHandler);
}
