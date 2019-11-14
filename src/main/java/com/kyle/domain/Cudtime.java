package com.kyle.domain;

import javax.persistence.*;

@Entity
@Table(name = "cudtime")
public class Cudtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cdid;
    private Integer bid;
    private String msgcode;

    public Integer getCdid() {
        return cdid;
    }

    public void setCdid(Integer cdid) {
        this.cdid = cdid;
    }

    public Integer getBid() {
        return bid;
    }

    public void setBid(Integer bid) {
        this.bid = bid;
    }

    public String getMsgcode() {
        return msgcode;
    }

    public void setMsgcode(String msgcode) {
        this.msgcode = msgcode;
    }
}
