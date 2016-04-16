package com.hzih.hotbat.service.entity;

import com.hzih.hotbat.utils.Configuration;
import com.hzih.hotbat.utils.StringUtils;
import com.inetec.common.exception.Ex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 13-5-12
 * Time: 下午3:43
 * To change this template use File | Settings | File Templates.
 */
public class BackUp {

    private boolean isActive;
    private boolean isMainSystem;
    private String mainIp;
    private String backupIp;
    private int mainPort;
    private int backupPort;
    private String mainStatus;
    private String backupStatus;
    List<String> pings = new ArrayList<String>();
    List<String> telnets = new ArrayList<String>();
    List<String> others = new ArrayList<String>();

    public boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public boolean isMainSystem() {
        return isMainSystem;
    }

    public void setMainSystem(Boolean mainSystem) {
        isMainSystem = mainSystem;
    }

    public String getMainIp() {
        return mainIp;
    }

    public void setMainIp(String mainIp) {
        this.mainIp = mainIp;
    }

    public String getBackupIp() {
        return backupIp;
    }

    public void setBackupIp(String backupIp) {
        this.backupIp = backupIp;
    }

    public int getMainPort() {
        return mainPort;
    }

    public void setMainPort(int mainPort) {
        this.mainPort = mainPort;
    }

    public int getBackupPort() {
        return backupPort;
    }

    public void setBackupPort(int backupPort) {
        this.backupPort = backupPort;
    }

    public String getMainStatus() {
        return mainStatus;
    }

    public void setMainStatus(String mainStatus) {
        this.mainStatus = mainStatus;
    }

    public String getBackupStatus() {
        return backupStatus;
    }

    public void setBackupStatus(String backupStatus) {
        this.backupStatus = backupStatus;
    }

    public List<String> getPings() {
        return pings;
    }

    public void setPings(List<String> pings) {
        this.pings = pings;
    }

    public List<String> getTelnets() {
        return telnets;
    }

    public void setTelnets(List<String> telnets) {
        this.telnets = telnets;
    }

    public List<String> getOthers() {
        return others;
    }

    public void setOthers(List<String> others) {
        this.others = others;
    }

    public BackUp readBackUp() throws Ex {
        Configuration config = new Configuration(StringUtils.hotConfig);
        return config.initBackUp();
    }

    public void updateBase(BackUp backUp) throws Ex {
        Configuration config = new Configuration(StringUtils.hotConfig);
        config.editBackUpBase(backUp);
    }

}
