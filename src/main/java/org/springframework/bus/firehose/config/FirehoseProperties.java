package org.springframework.bus.firehose.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by vcarvalho on 6/11/15.
 */
@Component
@ConfigurationProperties(prefix = "firehose")
public class FirehoseProperties {

    private String dopplerUrl;
    private String cfDomain;
    private String authenticationUrl;
    private String username;
    private String password;
    private String dopplerEvents;
    private String dopplerSubscription;



    public String getDopplerUrl() {
        return dopplerUrl;
    }

    public void setDopplerUrl(String dopplerUrl) {
        this.dopplerUrl = dopplerUrl;
    }

    public String getCfDomain() {
        return cfDomain;
    }

    public void setCfDomain(String cfDomain) {
        this.cfDomain = cfDomain;
    }

    public String getAuthenticationUrl() {
        return authenticationUrl;
    }

    public void setAuthenticationUrl(String authenticationUrl) {
        this.authenticationUrl = authenticationUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDopplerEvents() {
        return dopplerEvents;
    }

    public void setDopplerEvents(String dopplerEvents) {
        this.dopplerEvents = dopplerEvents;
    }

    public String getDopplerSubscription() {
        return dopplerSubscription;
    }

    public void setDopplerSubscription(String dopplerSubscription) {
        this.dopplerSubscription = dopplerSubscription;
    }


}
