package com.hzih.hotbat.service.client;

import com.hzih.hotbat.service.Service;
import com.hzih.hotbat.service.entity.ServiceResponse;
import com.hzih.hotbat.utils.NetUtils;
import com.hzih.hotbat.utils.ServiceUtils;
//import com.hzih.hotbat.utils.StpService;
import com.hzih.hotbat.utils.SystemService;
import com.hzih.hotbat.utils.StringUtils;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 13-5-9
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class ActiveClientService extends Thread {
    private final Logger logger = Logger.getLogger(ActiveClientService.class);
    public void init() {
        Properties prop = new Properties();
        try {
            prop.load(ActiveClientService.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            logger.error("加载配置文件config.properties错误",e);
        }
        StringUtils.webPort = Integer.parseInt(prop.getProperty("webport"));
        StringUtils.webUrl = prop.getProperty("weburl");
    }

    public boolean isRunning() {
        return Service.isActiveClientService;
    }




    public void run() {
        int count = 0;
        while (isRunning()) {
            if(NetUtils.isNetStatOk(StringUtils.serverPort)) {//判断状态
                StringUtils.setLocalSystemStart();//设置本地状态为启动
            } else {
                StringUtils.setLocalSystemStop();//设置本地状态为停止
            }
            try {
                String[][] params = new String[][] {
                        { "SERVICEREQUESTTYPE", "SERVICECONTROLPOST" },
                        { StringUtils.Str_MonitorCommand, StringUtils.Str_Command_Active },
                        { StringUtils.Str_LocalStatus, String.valueOf(StringUtils.getLocalStatus())}
                };
                ServiceResponse response = ServiceUtils.callService(params);
                if (response != null ){
                    if(response.getCode() == 200  ){
                        String data = response.getData();
                        if(data.length()<1){
                            continue;
                        }
                        int remoteStatus = Integer.parseInt(data);
                        Service.remoteSystemStatus = remoteStatus;
                        if (!StringUtils.isMainSystem()) {//如果不是主设备
                            switch (remoteStatus) {//主设备状态
                                case 0:
                                    StringUtils.setLocalSystemStart();//副设备需要启动
                                    break;//主设备已关闭
                                case 1:
                                    StringUtils.setLocalSystemStop();//副设备需要停止
                                    break;//主设备已启动
                                default:
                            }
                        }
                    }
                }
            }  catch (Exception e) {
                logger.error("发送心跳错误"+e.getMessage()+count);
                count++;
                if(count >= 20){//发送心跳20次失败
                    count = 0;
                    StringUtils.setLocalSystemStart();
                }
            }
            if(Service.localSystemStatus == 1) {
                if(!NetUtils.isNetStatOk(StringUtils.serverPort)){
                    SystemService.serviceCommand(StringUtils.startService);
                    SystemService.serviceCommand(StringUtils.startVpnService);
                    logger.info((StringUtils.isMainSystem()?"主机":"副机") + "启动本机SSLVPN服务...");
                    sleepLocal(StringUtils.sleep_stp_start);
                }
            } else if(Service.localSystemStatus == 0) {
                if(NetUtils.isNetStatOk(StringUtils.serverPort)){
                    SystemService.serviceCommand(StringUtils.stopService);
                    SystemService.serviceCommand(StringUtils.stopVpnService);
                    sleepLocal(StringUtils.sleep_stp_stop);
                    logger.info((StringUtils.isMainSystem()?"主机":"副机") + "停止本机SSLVPN服务...");
                }
            }
            if(StringUtils.isMainSystem()){
                logger.info((StringUtils.isMainSystem()?"主机":"副机") +" 状态 : "+ StringUtils.getLocalStatusStr() + " "
                        + (!StringUtils.isMainSystem()?"主机":"副机") +" 状态 : "+ StringUtils.getRemoteStatusStr());
            } else {
                logger.info((!StringUtils.isMainSystem()?"主机":"副机") +" 状态 : "+ StringUtils.getRemoteStatusStr() + " "
                        + (StringUtils.isMainSystem()?"主机":"副机") +" 状态 : "+ StringUtils.getLocalStatusStr());
            }
            sleepLocal(StringUtils.sleep_active );
        }
    }

    public void sleepLocal(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    public void close() {
        Service.isActiveClientService = false;
    }
}
