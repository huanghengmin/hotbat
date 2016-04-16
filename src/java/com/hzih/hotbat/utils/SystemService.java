
package com.hzih.hotbat.utils;

import com.inetec.common.util.OSInfo;
import com.inetec.common.util.Proc;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 13-5-8
 * Time: 下午8:26
 * To change this template use File | Settings | File Templates.
 */

public class SystemService {

/**
     *  启停 stp 服务
     */

    private static final Logger logger = Logger.getLogger(SystemService.class);

    public static void serviceCommand(String command) {
        OSInfo osinfo = OSInfo.getOSInfo();
        if (osinfo.isLinux()) {
            Proc proc = new Proc();
            proc.exec(command);
            logger.info("linux execute : " + command);
        } else {
            logger.info("执行"+command);
        }
    }
}

