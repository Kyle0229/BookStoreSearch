package com.kyle.domain;


import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "bookstore")
public class Bookstore implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bsid;

    private Integer uid;

    private Integer bid;

    private static final long serialVersionUID = 1L;

    public Integer getBsid() {
        return bsid;
    }

    public void setBsid(Integer bsid) {
        this.bsid = bsid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getBid() {
        return bid;
    }

    public void setBid(Integer bid) {
        this.bid = bid;
    }
}