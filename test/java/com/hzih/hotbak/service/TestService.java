package com.hzih.hotbak.service;


import com.hzih.hotbat.service.Service;

import junit.framework.TestCase;



/**
 * Created with IntelliJ IDEA.
 * User: 钱晓盼
 * Date: 13-5-9
 * Time: 上午11:27
 * To change this template use File | Settings | File Templates.
 */
public class TestService extends TestCase {

    public void testInit() {
        Service service = new Service();
        service.init();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }


}
