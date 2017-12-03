package com.houoy.smartpark.app.mobike.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
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
public class AuthManagerController {
    private Logger logger = LoggerFactory.getLogger(AuthManagerController.class);

    @ApiOperation(value = "模拟微发的接口：门禁开门询问访客系统接口 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qrcode", value = "二维码字符串(访客系统的二维码code)格式QR_十到十三位数字",
                    required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "opentime", value = "开门时间戳返回值", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/door", produces = "application/json;charset=UTF-8")
    public String path(String qrcode, String opentime) throws IOException {
        return "0";
    }
}
