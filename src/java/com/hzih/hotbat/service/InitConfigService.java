package com.hzih.hotbat.service;

import com.hzih.hotbat.utils.StringUtils;
import com.inetec.common.exception.Ex;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 13-5-12
 * Time: 下午6:52
 * To change this template use File | Settings | File Templates.
 */
public class InitConfigService extends Thread {
    private static final Logger logger = Logger.getLogger(InitConfigService.class);
    private boolean isStartService = false;//hotbat 主要线程启动状态
    public void init() {

    }

    public void run() {
        while (isRunning()) {
            try {
                if(isStartService) {
                    if(!Service.backUp.isActive()) {
                        logger.debug("......init...stop");
                        isStartService = Service.stopService();
                    }
                } else {
                    if(Service.backUp.isActive()) {  //未启动服务,且有了启动动作
                        logger.debug("......init...start");
                        isStartService = Service.startService();
                    }
                }
                try {
                    Thread.sleep(StringUtils.sleep_active * 10);
                } catch (InterruptedException e) {
                }
                if(Service.backUp == null) {
                    Service.backUp = Service.backUp.readBackUp();
                }
            } catch (Ex ex) {
                logger.error("加载配置文件错误",ex);
            }

        }
    }

    public boolean isRunning() {
        return Service.isInitConfigService;
    }

    public void close() {
        Service.isInitConfigService = false;
    }
}
