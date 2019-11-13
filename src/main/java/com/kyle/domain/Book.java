package com.kyle.domain;



import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Table(name = "book")
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bid;

    private Integer cid;

    private Integer aid;

    private String bname;

    private String introduce;

    private BigDecimal nummoney;

    private Integer scount;

    private String bpic;

    private BigDecimal bprice;

//    private Integer btickets;


    private static final long serialVersionUID = 1L;

    public BigDecimal getBprice() {
        return bprice;
    }

    public void setBprice(BigDecimal bprice) {
        this.bprice = bprice;
    }

//    public Integer getBtickets() {
//        return btickets;
//    }
//
//    public void setBtickets(Integer btickets) {
//        this.btickets = btickets;
//    }

    public Integer getBid() {
        return bid;
    }

    public void setBid(Integer bid) {
        this.bid = bid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname == null ? null : bname.trim();
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce == null ? null : introduce.trim();
    }

    public BigDecimal getNummoney() {
        return nummoney;
    }

    public void setNummoney(BigDecimal nummoney) {
        this.nummoney = nummoney;
    }

    public Integer getScount() {
        return scount;
    }

    public void setScount(Integer scount) {
        this.scount = scount;
    }

    public String getBpic() {
        return bpic;
    }

    public void setBpic(String bpic) {
        this.bpic = bpic == null ? null : bpic.trim();
    }

}