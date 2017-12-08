package com.houoy.smartpark.app.mobike.controller;

import com.google.gson.Gson;
import com.houoy.smartpark.app.mobike.service.TCPDoorService;
import com.houoy.smartpark.app.mobike.vo.ResultVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 权限管理
 * User: Mr.zheng
 * Date: 2017/5/18
 * Time: 7:14
 */
@RestController
@RequestMapping("/api")
public class DoorController {
    private Logger logger = LoggerFactory.getLogger(DoorController.class);

    @Autowired
    private TCPDoorService tcpDoorService;

    @Autowired
    private Vertx vertx;

//    @ApiOperation(value = "模拟微发的接口：门禁开门询问访客系统接口 ")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "qrcode", value = "二维码字符串(访客系统的二维码code)格式QR_十到十三位数字",
//                    required = true, dataType = "string", paramType = "query"),
//            @ApiImplicitParam(name = "opentime", value = "开门时间戳返回值", required = true, dataType = "string", paramType = "query")
//    })
//    @GetMapping(value = "/door", produces = "application/json;charset=UTF-8")
//    public String path(String qrcode, String opentime) throws IOException {
//        return "0";
//    }

    @ApiOperation(value = "测试开门接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "door1", value = "是否开门1,true or false", required = true, dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "door2", value = "是否开门2,true or false", required = true, dataType = "boolean", paramType = "query")
    })
    @PostMapping(value = "/openDoor", produces = "application/json;charset=UTF-8")
    public Boolean openDoor(Boolean door1, Boolean door2) throws IOException {
        return tcpDoorService.openDoor(vertx, door1, door2, null);
    }

    @ApiOperation(value = "测试第三方二维码服务接口")
    @ApiImplicitParam(name = "vgdecoderresult", value = "二维码，如：QR_1511854297716", required = true, dataType = "string", paramType = "query")
    @PostMapping(value = "/testWeiFa", produces = "application/json;charset=UTF-8")
    public String testWeiFa(String vgdecoderresult) throws IOException {
        Gson gson = new Gson();
        tcpDoorService.weifaRequest(vertx,vgdecoderresult,result->{
            ResultVO resultVO = gson.fromJson(result, ResultVO.class);//对于javabean直接给出class实例
            if (resultVO != null && resultVO.getResult().equals("0")) {//验证通过
                logger.info(resultVO.getMsg() + ": " + vgdecoderresult);
            } else {
                logger.info(resultVO.getMsg() + ": " + vgdecoderresult);
            }
        },error->{

        });
        return "";
    }
}
