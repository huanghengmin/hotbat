package com.hzih.hotbat.utils;

import com.inetec.common.exception.Ex;
import com.inetec.common.net.Ping;
import com.inetec.common.net.Telnet;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 13-5-13
 * Time: 上午10:57
 * To change this template use File | Settings | File Templates.
 */
public class NetUtils {
    private static final Logger logger = Logger.getLogger(NetUtils.class);

    public static boolean isPingOk(String ip,int count) {
        try {
            String pingStr = Ping.exec(ip, count);
            return getResult(pingStr);
        } catch (Ex ex) {
            logger.error("ping 错误",ex);
        }
        return false;
    }

    private static boolean getResult(String result) {
        if(( result.indexOf("ttl")>-1 && result.indexOf("time")>-1 )
                ||(result.indexOf("bytes=")>-1 && result.indexOf("time")>-1
                                                 && result.indexOf("TTL")>-1)){
            return true;
        } else {
            return false;
        }
	}

    public static boolean isTelnetOk(String ip,int port) {
        boolean isTelnet = false;
        try {
            isTelnet = Telnet.exec(ip, port);
        } catch (Ex ex) {
            logger.error("telnet 错误",ex);
        }
        return isTelnet;
    }

    public static boolean isNetStatOk(int port){
        Runtime rt = Runtime.getRuntime();
        Process p = null;// call "top" command in linux
        String[] cmd = { "sh", "-c", "netstat -an |grep "+port };
        try {
            p = rt.exec(cmd);
        } catch (IOException e) {
            return false;
        }
        if(p!=null) {
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str = null;
            try {
                while ((str = in.readLine()) != null) {
                    String regex = "\\s+";
                    String [] rex = str.split(regex);
                    if(rex!=null){
                        if (rex.length == 6) {
                            if (rex[5].equals("LISTEN")) {
                                if (rex[3].contains(String.valueOf(":"))) {
                                    String[] listen = rex[3].split(":");
                                    if (listen.length >= 2) {
                                        if (listen[1].equals(String.valueOf(port))) {
                                            logger.info("Service sslvpn is Runing");
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                return false;
            }
            return false;
        }
        return false;
    }
}
