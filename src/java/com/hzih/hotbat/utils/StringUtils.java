package com.hzih.hotbat.utils;

import com.hzih.hotbat.service.Service;
import com.inetec.common.exception.Ex;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 13-5-8
 * Time: 下午7:24
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {
    private final static Logger logger = Logger.getLogger(StringUtils.class);

    public final static String homePath = "/usr/app/sslvpn/hotbat";
    public final static String hotConfig = "/usr/app/sslvpn/data/backup/hotconfig.xml";

    public final static String startService = "service sslvpn start";
    public final static String startVpnService = "service openvpn start";
    public final static String stopService = "service sslvpn stop";
    public final static String stopVpnService = "service openvpn stop";


    public final static String config = "/usr/app/sslvpn/server_config/server.conf";
    public final static String sql = "/usr/app/sslvpn/sslvpn_backup.sql";

    public static long sleep_active = 1000 * 5;//心跳发送间隔
    public static long sleep_config = 1000 * 5 * 60;//心跳发送间隔
    public static long sleep_stp_stop = 1000 * 10;//sslvpn服务停止所需时间
    public static long sleep_stp_start = 1000 * 90;//sslvpn服务启动所需时间

    public static int webPort = 8088; //本系统服务端口
    public static String webUrl = "/hotbat/service"; //本系统服务端口

    public static final int serverPort = 80; //本系统服务端口

    public static final String HDR_ServiceRequestType = "SERVICEREQUESTTYPE";
    public static final String STR_REQTP_ServiceDataPost = "SERVICEDATAPOST";
    public static final String STR_REQTP_ServiceControlPost = "SERVICECONTROLPOST";
    public static final String Str_MonitorCommand = "Command";
    public static final String Str_Command_Active = "active";
    public static final String Str_Command_Sql = "sql";
    public static final String Str_Command_Config = "config";
    public static final String Str_LocalStatus = "localStatus";


    /**
     * 判断是否主要设备
     * @return
     */
    public static boolean isMainSystem() {
        return Service.backUp.isMainSystem();
    }

    /**
     * 获取远程热备设备IP
     * @return
     */
    public static String remoteIp() {
        if(StringUtils.isMainSystem()) {
            return Service.backUp.getBackupIp();
        } else {
            return Service.backUp.getMainIp();
        }
    }

    /**
     * 获取远程热备设备监听端口
     * @return
     */
    public static int remotePort() {
        if(StringUtils.isMainSystem()) {
            return Service.backUp.getBackupPort();
        } else {
            return Service.backUp.getMainPort();
        }
    }

    /**
     * 本机是否启动:true启动;false关闭
     * @return
     */
    public static boolean isLocalSystemStart() {
        if(Service.localSystemStatus == 1
                || Service.localSystemStatus == 2) {
            return true;
        }
        return false;
    }

    /**
     * 设置本机为 停止 状态
     */
    public static void setLocalSystemStop() {
        Service.localSystemStatus = 0;
    }

    /**
     * 设置本机为 启动 状态
     */
    public static void setLocalSystemStart() {
        Service.localSystemStatus = 1;
    }

    /**
     * 设置本机为 启动中 状态
     */
    public static void setLocalSystemStarting() {
        Service.localSystemStatus = 2;
    }

    /**
     * 设置本机为 停止中 状态
     */
    public static void setLocalSystemStopping() {
        Service.localSystemStatus = 3;
    }

    public static String getServiceUrl() {
        return "http://"+StringUtils.remoteIp()+":"+StringUtils.webPort + StringUtils.webUrl;
    }

    public static boolean isLocalStarting() {
        return Service.localSystemStatus == 2;
    }

    public static boolean isLocalStop() {
        return Service.localSystemStatus == 0;
    }

    public static boolean isLocalStopping() {
        return Service.localSystemStatus == 3;
    }

    public static String getLocalStatusStr()  {
        String status = null;
        switch (Service.localSystemStatus){
            case 0 : status = "已停止";break;
            case 1 : status = "已启动";break;
            case 2 : status = "启动中";break;
            case 3 : status = "关闭中";break;
        }
        return status;
    }

    public static String getRemoteStatusStr()  {
        String status = null;
        switch (Service.remoteSystemStatus){
            case 0 : status = "已停止";break;
            case 1 : status = "已启动";break;
            case 2 : status = "启动中";break;
            case 3 : status = "关闭中";break;
        }
        return status;
    }

    public static int getLocalStatus() {
        return Service.localSystemStatus;
    }

}
