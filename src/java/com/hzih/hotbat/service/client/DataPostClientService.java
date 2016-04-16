package com.hzih.hotbat.service.client;

import com.hzih.hotbat.service.Service;
import com.hzih.hotbat.service.mysql.MysqlBakUtils;
import com.hzih.hotbat.utils.NetUtils;
import com.hzih.hotbat.utils.ServiceUtils;
import com.hzih.hotbat.utils.StringUtils;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 13-5-9
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class DataPostClientService extends Thread {
    private final Logger logger = Logger.getLogger(DataPostClientService.class);
    public void init() {
        Properties prop = new Properties();
        try {
            prop.load(DataPostClientService.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            logger.error("加载配置文件config.properties错误",e);
        }
        StringUtils.webPort = Integer.parseInt(prop.getProperty("webport"));
        StringUtils.webUrl = prop.getProperty("weburl");
    }

    public boolean isRunning() {
        return Service.isDoPostClientService;
    }

    public void run() {
        int count = 0;
        while (isRunning()) {
            if(NetUtils.isNetStatOk(StringUtils.serverPort)) {//判断状态
                StringUtils.setLocalSystemStart();//设置本地状态为启动
            } else {
                StringUtils.setLocalSystemStop();//设置本地状态为停止
                try {
                    String[][] params = new String[][] {
                            { "SERVICEDATAPOST", "SERVICEDATAPOST" },
                            { StringUtils.Str_MonitorCommand, StringUtils.Str_Command_Sql}
                    };
                    InputStream response = ServiceUtils.callServiceInputStream(params);
                    if (response != null ){
                        FileOutputStream fos = new FileOutputStream(StringUtils.sql);
                        byte[] b = new byte[1024];
                        while((response.read(b)) != -1){
                            fos.write(b);
                        }
                        response.close();
                        fos.close();
                        MysqlBakUtils.recover();
                    }

                    String[][] params_config = new String[][] {
                            { "SERVICEDATAPOST", "SERVICEDATAPOST" },
                            { StringUtils.Str_MonitorCommand, StringUtils.Str_Command_Config}
                    };
                    InputStream response_config = ServiceUtils.callServiceInputStream(params_config);
                    if (response_config != null ){
                        FileOutputStream fos = new FileOutputStream(StringUtils.config);
                        byte[] b = new byte[1024];
                        while((response.read(b)) != -1){
                            fos.write(b);
                        }
                        response.close();
                        fos.close();
                    }
                }  catch (Exception e) {
                    logger.error("发送配置信息错误"+e.getMessage()+count);
                }
            }
            sleepLocal(StringUtils.sleep_config );
        }
    }

    public void sleepLocal(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    public void close() {
        Service.isDoPostClientService = false;
    }
}
