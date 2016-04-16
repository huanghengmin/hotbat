package com.hzih.hotbat.utils;

import com.hzih.hotbat.service.entity.BackUp;
import com.inetec.common.exception.E;
import com.inetec.common.exception.Ex;
import com.inetec.common.i18n.Message;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;

public class Configuration {
    static Logger logger = Logger.getLogger(Configuration.class);
    private Document document;
    public String confPath;

    public Configuration(Document doc) {
        this.document = doc;
    }

    public Configuration(String path) throws Ex {
        this.confPath = path;
        SAXReader saxReader = new SAXReader();
        try {
            document = saxReader.read(path);
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }

    public Configuration(InputStream is, String path) throws Ex {
        this.confPath = path;
        SAXReader saxReader = new SAXReader();
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            logger.info(e.getMessage());
        }
    }

    public void save() throws Ex {
        XMLWriter output = null;
        try {
            File file = new File(confPath);
            FileInputStream fin = new FileInputStream(file);
            byte[] bytes = new byte[fin.available()];
            while (fin.read(bytes) < 0) fin.close();
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            output = new XMLWriter(new FileOutputStream(file),format);
            if(document != null){
                output.write(document);
            }else{

            }
        } catch (IOException e) {
            throw new Ex().set(E.E_IOException, e, new Message("ccured exception when move Internal configuration To History"));
        } finally {
            try {
                if (output != null)
                    output.close();
            } catch (IOException e) {
                throw new Ex().set(E.E_IOException, e, new Message("Occured exception when close XMLWrite"));
            }
        }
    }

    public BackUp initBackUp(){
        BackUp backUp = new BackUp();
        Element base = (Element)document.selectSingleNode("/backup/base");
        if(base!=null){
            backUp.setActive(Boolean.parseBoolean(base.element("isactive").getText()));
            backUp.setMainSystem(Boolean.parseBoolean(base.element("main").getText()));
            backUp.setMainIp(base.element("mainip").getText());
            backUp.setMainPort(Integer.parseInt(base.element("mainport").getText()));
            backUp.setMainStatus(base.element("mainstatus").getText());
            backUp.setBackupIp(base.element("backupip").getText());
            backUp.setBackupPort(Integer.parseInt(base.element("backupport").getText()));
            backUp.setBackupStatus(base.element("backupstatus").getText());
        }
        List pings = document.selectNodes("/backup/pings/ping");
        if(pings!=null) {
            List<String> _pings = new ArrayList<String>();
            for(Iterator it = pings.iterator();it.hasNext();){
                Element e = (Element) it.next();
                _pings.add(e.getText());
            }
            backUp.setPings(_pings);
        }
        List telnets = document.selectNodes("/backup/telnets/telnet");
        if(telnets!=null) {
            List<String> _telnets = new ArrayList<String>();
            for(Iterator it = telnets.iterator();it.hasNext();){
                Element e = (Element) it.next();
                _telnets.add(e.getText());
            }
            backUp.setTelnets(_telnets);
        }
        List<Element> others = document.selectNodes("/backup/others/other");
        if(others!=null) {
            List<String> _others = new ArrayList<String>();
            for(Iterator it = others.iterator();it.hasNext();){
                Element e = (Element) it.next();
                _others.add(e.getText());
            }
            backUp.setOthers(_others);
        }
        return backUp;
    }

    public synchronized void editBackUpBase(BackUp backUp) throws Ex {
        Element base = (Element)document.selectSingleNode("/backup/base");
        if(base!=null){
            base.element("isactive").setText(String.valueOf(backUp.isActive()));
            base.element("main").setText(String.valueOf(backUp.isMainSystem()));
            base.element("mainip").setText(String.valueOf(backUp.getMainIp()));
            base.element("mainport").setText(String.valueOf(backUp.getMainPort()));
            base.element("mainstatus").setText(String.valueOf(backUp.getMainStatus()));
            base.element("backupip").setText(String.valueOf(backUp.getBackupIp()));
            base.element("backupport").setText(String.valueOf(backUp.getBackupPort()));
            base.element("backupstatus").setText(String.valueOf(backUp.getBackupStatus()));
            save();
        }

    }


}