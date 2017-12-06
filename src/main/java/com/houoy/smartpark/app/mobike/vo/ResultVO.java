package com.houoy.smartpark.app.mobike.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by andyzhao on 2017/12/2.
 */
@Data
@NoArgsConstructor
public class ResultVO {
    private String code = "0000";//0000表示成功
    private String desc = "success";
    private String result;
    private String msg ;

    public ResultVO(String _code, String _desc) {
        code = _code;
        desc = _desc;
    }
}
