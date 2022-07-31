package com.example.foliage.page.device.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author liujianliang@cecdat.com
 * @date 2022/7/29
 */
public class DeviceDetailDTO {

    private DeviceBean device;
    private long tid;
    private DataBean data;

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DeviceBean {

        private String id;
        private String devcode;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDevcode() {
            return devcode;
        }

        public void setDevcode(String devcode) {
            this.devcode = devcode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class DataBean {

        private String dataway;
        private String 急停指示;
        private String 振动模式;
        private String 行走模式;

        private String 发动机转速;
        private String 水温;
        private String 行车速度;
        private String 振动频率;
        private String 振动频率设定值;
        private String 补油压力;
        private String 工作档行走最大速度设定值;
        private String 行走手柄电位计;
        private String 行驶档行走最大速度设定值;
        private String 水箱水位低报警;
        private String 电源电压报警;
        private String 主界面发动机水箱水位低报警;
        private String 输出开路短路报警;
        private String 液压油位报警;
        private String 发电机故障;
        private String 液压油温高报警;
        private String cAN总线报警;
        private String 补油压力报警;
        private String 发动机_油压低报警;
        private String 空滤阻塞报警;
        private String 燃油位低报警;
        private String 燃油滤清器污染报警;
        private String 发动机油压低报警;
        private String 发动机水温高报警;
        private String 行走手柄故障;
        private String 液压真空度报警;
        private String 发动机工作时间;
        private String 手柄电位计输入;
        private String 液压油温传感器输入;
        private String 液压油位低报警开关输入状态;
        private String 振动手动开关输入状态;
        private String 故障报警灯开关量输出状态;
        private String 左转向灯开关量输出状态;
        private String 制动开关量输出状态;
        private String 水温高开关输入状态;
        private String 液压真空度报警开关输入状态;
        private String 紧急停止开关输入状态;
        private String 机油压力报警开关输入状态;
        private String 应急开关输入状态;
        private String 手柄后退开关输入状态;
        private String 手柄中位开关输入状态;
        private String 手柄前进开关输入状态;
        private String 空滤阻塞报警开关输入状态;
        private String 燃油滤清器报警开关输入状态;
        private String 燃油量输入;
        private String 发动机启动锁开关量输出状态;
        private String 前轮马达输出状态;
        private String 后轮马达输出状态;
        private String 右转向开关输入状态;
        private String 发动机预热;
        private String 左转向开关输入状态;
        private String 右转向输出状态;
        private String _200_0;
        private String 行走泵前进比例阀输出;
        private String 行走泵后退比例阀输出;
        private String 振动泵正转比例阀输出;
        private String 振动泵反转比例阀输出;
        private String _485_4;
        private String 大气压力;
        private String 进气压力;
        private String _485_2;
        private String 燃油温度;
        private String 机油温度;
        private String _200_1;
        private String 进气温度;
        private String 计算一;
        private String 计算二;
        private String 计算三;
        private String 燃油位传感器最小值标定;
        private String 燃油位传感器最大值标定;
        @com.google.gson.annotations.SerializedName("DTC1")
        private String dTC1;
        @com.google.gson.annotations.SerializedName("DTC2")
        private String dTC2;
        @com.google.gson.annotations.SerializedName("DTC3")
        private String dTC3;
        @com.google.gson.annotations.SerializedName("DTC4")
        private String dTC4;
        @com.google.gson.annotations.SerializedName("DTC5")
        private String dTC5;
        @com.google.gson.annotations.SerializedName("DTC6")
        private String dTC6;
        @com.google.gson.annotations.SerializedName("DTC7")
        private String dTC7;
        @com.google.gson.annotations.SerializedName("DTC8")
        private String dTC8;
        private String 加速度传感器模拟量处理后;
        private String 加速度传感器电压值设定1;
        private String 加速度传感器电压值设定2;
        private String 加速度传感器电压值设定3;
        private String 密实度设定1;
        private String 密实度设定2;
        private String 密实度设定3;
        private String 频率值设定1;
        private String 频率值设定2;
        private String 频率值设定3;
        private String 合格曲线设定值;
        private String 实时曲线值;
        private String 标定编号设定值;
        private String 合格值设定1;
        private String 合格值设定2;
        private String 合格值设定3;
        private String 实际加速度;
        private String 设定密实度;
        private String 振动手动使能开关状态;
        private String 加速度传感器零点标定;
        private String 加速度传感器分辨率标定;
        private String 加速度传感器输入;
        private String 油门大小;
        private String 一次启动时间读取;
        private String 前行走压力;
        private String 振动泵前进最小电流标定;
        private String 振动泵前进最大电流标定;
        private String 振动泵后退最小电流标定;
        private String 振动泵后退最大电流标定;
        private String 制动开启时间;
        private String 制动闭合时间;
        private String 行走向前Accel;
        private String 行走向前Decel;
        private String 行走向后Accel;
        private String 行走向后Decel;
        private String 振动Accel;
        private String 振动Decel;
        private String 行走泵最小转速;
        private String 行走泵最大转速;
        private String 起振点;
        private String 停振点;
        private String 行走马达齿数读取;
        private String 启动锁转速读取;
        private String 发动机飞轮齿数读取;
        private String 振动马达齿数读取;
        private String 发动机转速修正最大;
        private String 燃油位修正最大;
        private String 发动机转速修正最小;
        private String 燃油位修正最小;
        private String 机油压力修正最大;
        private String 水温修正最大;
        private String 机油压力修正最小;
        private String 水温修正最小;
        private String 液压油温修正最大;
        private String 电源电压修正最大;
        private String 液压油温修正最小;
        private String 行走泵前进最小电流标定;
        private String 行走泵前进最大电流标定;
        private String 行走泵后退最小电流标定;
        private String 行走泵后退最大电流标定;
        private String 后行走压力;
        private String 前振动压力;
        private String 后振动压力;
        private String 当前压实值;
        private String 机油压力;
        private String 发动机工作时间高;
        private String 锁车状态;
        private String 供电电压;
        private String _284_0;
        private String _284_2;
        private String _284_4;
        private String _284_6;
        private String 行走泵前进最小电流;
        private String 行走泵前进最大电流;
        private String 行走泵后退最小电流;
        private String 行走泵后退最大电流;
        private String 振动泵前进最小电流;
        private String 振动泵前进最大电流;
        private String 振动泵后退最小电流;
        private String 振动泵后退最大电流;
        private String 燃油位最小值设定;
        private String 燃油位最大值设定;
        private String 制动闭合延时;
        private String 行走马达齿数;
        private String 一次启动时间;
        private String 启动锁转速;
        private String 发动机飞轮齿数;
        @SerializedName("振动马达齿数")
        private String a;
        private String _485_0;
        private String _200_4;
        private String _200_2;
        private String _485_6;
        private String _486_0;
        private String _486_2;
        private String _486_4;
        private String _486_6;
        private String _189_0;
        private String _189_4;

        @SerializedName("燃油位")
        private String fuelLevel;

        private String datatime;
        private String sysdate;

    }

}