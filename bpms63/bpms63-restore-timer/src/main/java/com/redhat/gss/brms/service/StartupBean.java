package com.redhat.gss.brms.service;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class StartupBean {


    @PostConstruct
    public void init() {
        System.setProperty("org.jbpm.ht.callback", "custom");
        System.setProperty("org.jbpm.ht.custom.callback", "com.redhat.gss.brms.service.SimpleUserGroupCallback");
    }
}

