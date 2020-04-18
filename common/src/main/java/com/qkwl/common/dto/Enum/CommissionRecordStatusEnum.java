package com.qkwl.common.dto.Enum;

public class   CommissionRecordStatusEnum {

    /**
     * 为发放
     */
    public static final int NORMAL = 1;

    /**
     * 已发放
     */
    public static final int SUCCESS = 2;

    /**
     * 失败
     */
    public static final int FAIL = 3;

    public static String getStatusName(int status){
        String name = "";
        switch (status){
            case NORMAL:
                name = "未发放";
                break;
            case SUCCESS:
                name = "已发放";
                break;
            case FAIL:
                name = "发放失败";
                break;
        }
        return name;
    }


}
