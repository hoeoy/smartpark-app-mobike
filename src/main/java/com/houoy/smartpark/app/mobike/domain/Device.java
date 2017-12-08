package com.houoy.smartpark.app.mobike.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by andyzhao on 2017/12/8.
 */
@Data
@NoArgsConstructor
public class Device {
    private String id;//唯一编码。1,2,3,4.。。。
    private String ip;
    private Integer port;
    private String sn;
}
