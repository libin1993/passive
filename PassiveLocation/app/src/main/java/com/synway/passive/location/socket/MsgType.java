package com.synway.passive.location.socket;

/**
 * Author：Libin on 2020/8/10 09:34
 * Email：1993911441@qq.com
 * Describe：
 */
public class MsgType {
    public final static short SEND_SERVER_HEART_BEAT = 0x01; // 终端→设备终端发送心跳
    public final static short RCV_SERVER_HEART_BEAT = 0xA1; // 设备→终端心跳反馈
    public final static short RCV_SERVER_STATUS = 0xA2; // 设备→终端服务层状态上报
    public final static short RCV_DEVICE_STATUS = 0xA3; // 设备→终端设备状态上报
    public final static short RCV_DEVICE_LOG = 0xA4; // 设备→终端设备运行LOG上报
    public final static short SEND_IMPORT_PERMISSIONCODE = 0x05; // 终端→设备终端请求导入授权码
    public final static short RCV_IMPORT_PERMISSIONCODE = 0xA5; // 设备→终端导入授权码请求反馈
    public final static short SEND_DEVICE_PERMISSION = 0x06; // 终端→设备终端请求授权情况
    public final static short RCV_DEVICE_PERMISSION = 0xA6; // 设备→终端授权情况请求反馈
    public final static short RCV_BASEBAND_STATUS = 0xA7; // 设备→终端基带设备状态请求反馈
    public final static short SEND_SHOW_VERSION = 0x08;// 终端→设备终端请求设备版本信息
    public final static short RCV_SHOW_VERSION = 0xA8;//设备→终端设备版本信息请求反馈
    public final static short SEND_CELL_SEARCH = 0x09; // 终端→设备终端请求小区搜索
    public final static short RCV_CELL_SEARCH = 0xA9;// 设备→终端小区搜索请求反馈
    public final static short RCV_CELL_INFO = 0xA0; // 设备→终端小区数据上报
    public final static short SEND_LOCATION_CMD = 0x11; // 终端→设备终端请求目标定位
    public final static short RCV_LOCATION_CMD = 0xB1;// 设备→终端目标定位请求反馈
    public final static short RCV_LOCATION_INFO = 0xB2; // 设备→终端目标定位数据上报
    public final static short RCV_LTE_STATISTICS = 0xB3; // 设备→终端LTE统计信息上报
    public final static short RCV_GSM_STATISTICS = 0xB4; // 设备→终端 GSM统计信息上报
    public final static short RCV_CDMA_STATISTICS =0xB5; // 设备→终端CDMA统计信息上报
    public final static short RCV_WCDMA_STATISTICS =0xB6; // 设备→终端WCDMA统计信息上报
    public final static short RCV_TDSCDMA_STATISTICS =0xB7; // 设备→终端TDSCDMA统计信息上报
    public final static short SEND_MONITOR_CMD = 0x18; // 终端→设备终端请求目标监控
    public final static short RCV_MONITOR_CMD = 0xB8;// 设备→终端目标监控请求反馈
    public final static short RCV_SMS_INFO = 0xB9; // 设备→终端短信信息上报
    public final static short RCV_VOICE_INFO = 0xB0; // 设备→终端语音文件路径上报
    public final static short SEND_VOICE_STOP = 0x21; // 终端→设备停止语音监控
    public final static short RCV_VOICE_STOP = 0xC1; // 设备→终端停止语音监控反馈
    public final static short SEND_SET_POWERLEV = 0x22; // 终端→设备增益设置
    public final static short RCV_SET_POWERLEV = 0xC2; // 设备→终端增益设置反馈
    public final static short RCV_CRNTI_VALUE = 0xC3; // 设备→终端 CRNTI值上报
    public final static short RCV_CTY_VALUE = 0xC4; //设备→终端 CTY连续捕获状态值
    public final static short SEND_DT_MODE_CONF = 0x25; // 终端→设备 设置数传模式
    public final static short RCV_DT_MODE_CONF = 0xC5; // 设备→终端 设置数传确认消息
    public final static short RCV_DT_HEARTBEAT = 0xC6; // 设备→终端 数传信号强度
    public final static short SEND_CRNTI_LOCK = 0x27; // 终端→设备 锁定CRNTI
    public final static short RCV_CRNTI_LOCK = 0xC7; // 设备→终端 锁定CRNTI应答
    public final static short SEND_CRNTI_UNLOCK = 0x28; // 终端→设备 解锁CRNTI
    public final static short RCV_CRNTI_UNLOCK = 0xC8; // 设备→终端 解锁CRNTI应答
    public final static short SEND_BS_CRNTI_LOCK = 0x29; // 终端→设备 锁定伪基站CRNTI
    public final static short RCV_BS_CRNTI_LOCK = 0xC9; // 设备→终端 锁定伪基站CRNTI应答
    public final static short SEND_BS_CRNTI_UNLOCK = 0x30; // 终端→设备 解锁伪基站CRNTI
    public final static short RCV_BS_CRNTI_UNLOCK = 0xC0; // 设备→终端 解锁伪基站CRNTI应答
    public final static short SEND_TRIGGER_START = 0x31; // 终端→设备 诱发开始命令
    public final static short SEND_TRIGGER_END = 0x32; // 终端→设备 诱发结束命令
    public final static short RCV_TRIGGER_ACK = 0xD3; //设备→终端 诱发反馈ACK
    public final static short SEND_SET_VOLTE_STATUS = 0x34; // 终端→设备 设置VOLTE状态
    public final static short RCV_SET_VOLTE_STATUS_ACK = 0xD4; //设备→终端 设置VOLTE状态应答
    public final static short SEND_CELL_CONT_SEARCH = 0x35;// 终端→设备 发送连续扫网开关
    public final static short RCV_CELL_CONT_SEARCH_ACK = 0xD5;  //设备→终端发送连续扫网开关应答
    public final static short SEND_TARGET_CELL_SET = 0x36; // 终端→设备 发送锁定目标小区
    public final static short RCV_TARGET_CELL_SET_ACK = 0xD6;// 设备→终端发送锁定目标小区应答
    public final static short SEND_OPEN_SYSINFORPT = 0x37; // 终端→设备 发送打开邻小区上报
    public final static short RCV_OPEN_SYSINFORPT_ACK = 0xD7; // 设备→终端打开邻小区上报应答
    public final static short RCV_GSM_NEIGHBORCELLS = 0xD8; // "设备→终端 上报GSM邻小区",
    public final static short RCV_LTE_SYSINFO = 0xD9;// "设备→终端 上报LTE系统信息",
    public final static short SEND_SET_LTEREFVALUES = 0x40; // "终端→设备 下发LTE参考值"),
    public final static short RCV_SET_LTEREFVALUES_ACK = 0xE0;// "设备→终端 下发LTE参考值应答",
    public final static short RCV_RPT_LTEREFVALUES = 0xE1;// "设备→终端 上报LTE参考值",
    public final static short SEND_SET_DBCELLNUM = 0x41;//  终端→设备 设置单兵8小区开关,
    public final static short RCV_SET_DBCELLNUM_ACK = 0xF1;//设备→终端 设置单兵8小区开关应答
    public final static short SEND_MANUAL_TARGET_CELL_SET = 0x42;// 终端→设备 手动锁定小区（预留）
    public final static short RCV_MANUAL_TARGET_CELL_SET_ACK = 0xF2; // 设备→终端 手动锁定小区应答（预留）
}
