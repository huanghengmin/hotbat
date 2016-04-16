package com.hzih.hotbat.service;

import com.hzih.hotbat.service.client.ActiveClientService;
import com.hzih.hotbat.service.client.DataPostClientService;
import com.hzih.hotbat.service.entity.BackUp;
import com.hzih.hotbat.service.mysql.MysqlBakUtils;
import com.hzih.hotbat.utils.FileUtil;
import com.hzih.hotbat.utils.StringUtils;
import com.inetec.common.client.util.EChange;
import com.inetec.common.exception.Ex;
import com.inetec.common.i18n.Message;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 13-5-8
 * Time: 下午7:29
 * To change this template use File | Settings | File Templates.
 */
public class Service extends HttpServlet {
    public static final Logger logger = Logger.getLogger(Service.class);

    public static int localSystemStatus = 3;  //主设备stp状态:1已启动;0已关闭;2启动中;3关闭中
    public static int remoteSystemStatus = 3; //热备stp状态:1已启动;0已关闭;2启动中;3关闭中

    public static ActiveClientService activeClientService = new ActiveClientService();
    public static DataPostClientService dataPostClientService = new DataPostClientService();
    public static boolean isActiveClientService = false;
    public static boolean isDoPostClientService = false;

    public static InitConfigService initConfigService = new InitConfigService();
    public static boolean isInitConfigService = false;
    public static BackUp backUp = new BackUp();

    public void init() {
        try {
            Service.backUp = Service.backUp.readBackUp();
        } catch (Ex ex) {
            logger.error("加载配置文件错误", ex);
        }
        runInitConfigService();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Monitor  Service Page</title>");
        writer.println("</head>");
        writer.println("<body bgcolor=white>");
        writer.println("<table border=\"0\">");
        writer.println("<tr>");
        writer.println("<td>");
        writer.println("<h1>Fertec Service Status Page</h1>");
        writer.println("<P>Hotbat service is running.<P><BR>");
        writer.println("</td>");
        writer.println("</tr>");
        writer.println("</table>");
        writer.println("</body>");
        writer.println("</html>");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String reqType = request.getParameter(StringUtils.HDR_ServiceRequestType);
            if (reqType == null) {
                reqType = request.getHeader(StringUtils.HDR_ServiceRequestType);
            }
            if (reqType == null)
                throw new Ex().set(EChange.E_UNKNOWN, new Message("Service Request commond is null."));

            if (reqType.equalsIgnoreCase(StringUtils.STR_REQTP_ServiceDataPost)) {
                //数据上传命令
                String commandBody = request.getParameter(StringUtils.Str_MonitorCommand);
                if (commandBody == null) {
                    commandBody = request.getHeader(StringUtils.Str_MonitorCommand);
                }
                if (commandBody == null) {
                    commandBody = "";
                }
                if (commandBody.equalsIgnoreCase("")) {
                    logger.debug("Data Control bad Request commandBody:" + commandBody);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }

                if (StringUtils.Str_Command_Sql.equalsIgnoreCase(commandBody)) {
                    try {
                        boolean fg = MysqlBakUtils.backup();
                        if (fg) {
                            File file = new File(StringUtils.sql);
                            if (file.exists())
                                response = FileUtil.copy(file, response);
                        }
                    } catch (IOException e) {
                        logger.info(e.getMessage(), e);
                    }
                }


                if (StringUtils.Str_Command_Config.equalsIgnoreCase(commandBody)) {
                    File file = new File(StringUtils.config);
                    if (file.exists())
                        response = FileUtil.copy(file, response);
                }

            } else if (reqType.equalsIgnoreCase(StringUtils.STR_REQTP_ServiceControlPost)) {
                String commandBody = request.getParameter(StringUtils.Str_MonitorCommand);
                if (commandBody == null) {
                    commandBody = request.getHeader(StringUtils.Str_MonitorCommand);
                }
                if (commandBody == null) {
                    commandBody = "";
                }
                if (commandBody.equalsIgnoreCase("")) {
                    logger.debug("Data Control bad Request commandBody:" + commandBody);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
                if (StringUtils.Str_Command_Active.equalsIgnoreCase(commandBody)) {
                    Service.remoteSystemStatus = Integer.parseInt(request.getParameter(StringUtils.Str_LocalStatus));
                    byte[] buff = String.valueOf(Service.localSystemStatus).getBytes();
                    response.setContentLength(buff.length);
                    response.getOutputStream().write(buff);
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
            response.flushBuffer();

        } catch (Ex Ex) {    // Server error; report to client.

            logger.info("Could not process the request from " + Ex.getMessage());

            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                    , " - Could not process the request from " +
                    ": " + Ex.getMessage());

            logger.error("" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR +
                    " - Could not process the request: ", Ex);

        } catch (RuntimeException Ex) {
            logger.error("Run-time exception is caught:- ", Ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                    , " - Could not process the request from " +
                    ": " + Ex.getMessage());
        }
    }

    public static boolean startService() {
        runActiveClientService();
        runDataPostClientService();
        logger.info("启动了所有业务线程..");
        return true;
    }

    public static boolean stopService() {
        activeClientService.close();
        dataPostClientService.close();
        logger.info("停止了所有业务线程..");
        return true;
    }

    private void runInitConfigService() {
        if (Service.isInitConfigService) {
            return;
        }
        Service.isInitConfigService = true;
        Service.initConfigService.init();
        Service.initConfigService.start();
        logger.info("启动配置文件加载线程..");
    }

    /**
     * 完成发送心跳消息
     */
    private static void runActiveClientService() {
        if (Service.isActiveClientService) {
            return;
        }
        Service.isActiveClientService = true;
        Service.activeClientService.init();
        Service.activeClientService.start();
        logger.info("启动心跳发送线程...");
    }

    private static void runDataPostClientService() {
        if (Service.isDoPostClientService) {
            return;
        }
        Service.isDoPostClientService = true;
        Service.dataPostClientService.init();
        Service.dataPostClientService.start();
        logger.info("启动心跳发送线程...");
    }


    public static void main(String args[]) throws Exception {
        String ss = "tcp6       0      0 172.16.10.117:80        :::*                    LISTEN";
        String regex = "\\s+";
        String[] rex = ss.split(regex);
        if (rex != null) {
            if (rex.length == 6) {
                if (rex[5].equals("LISTEN")) {
                    if (rex[3].contains(String.valueOf(":"))) {
                        String[] listen = rex[3].split(":");
                        if (listen.length >= 2) {
                            if (listen[1].equals(String.valueOf(80))) {
                                System.out.print("Listing");
                            }
                        }
                    }
                }
            }
        }
    }
}
