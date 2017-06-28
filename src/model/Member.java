/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author asus
 */
public class Member {
    private SimpleStringProperty ip;
    private SimpleStringProperty pin;
    private SimpleStringProperty name;

    public Member(String ip, String pin, String name) {
        this.ip = new SimpleStringProperty(ip);
        this.pin = new SimpleStringProperty(pin);
        this.name = new SimpleStringProperty(name);
    }

    Member() {
        this.ip = null;
        this.pin = null;
        this.name = null;
    }

    public String getIP() {
        return ip.getValue();
    }

    public void setIP(SimpleStringProperty ip) {
        this.ip = ip;
    }

    public String getPIN() {
        return pin.getValue();
    }

    public void setPIN(SimpleStringProperty pin) {
        this.pin = pin;
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(SimpleStringProperty name) {
        this.name = name;
    }
}
