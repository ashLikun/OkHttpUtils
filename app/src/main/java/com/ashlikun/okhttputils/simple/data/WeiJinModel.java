package com.ashlikun.okhttputils.simple.data;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/12/27　13:59
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public class WeiJinModel {


    /**
     * marketId : 61129
     * creditorId : 119332
     * investId : 8696
     * investType : 个人消费借款
     * investCode : V170600010083
     * investLevel : B
     * creditorStatus : 0
     * transferAPR : 0.11
     * residualPeriod : 30
     * transferAmt : 855.28
     * investmentBaoName : 保
     * transferDate : 00:00:22
     * hold : false
     */

    private int marketId;
    private int creditorId;
    private int investId;
    private String investType;
    private String investCode;
    private String investLevel;
    private String creditorStatus;
    private double transferAPR;
    private int residualPeriod;
    private double transferAmt;
    private String investmentBaoName;
    private String transferDate;
    private boolean hold;

    public int getMarketId() {
        return marketId;
    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    public int getCreditorId() {
        return creditorId;
    }

    public void setCreditorId(int creditorId) {
        this.creditorId = creditorId;
    }

    public int getInvestId() {
        return investId;
    }

    public void setInvestId(int investId) {
        this.investId = investId;
    }

    public String getInvestType() {
        return investType;
    }

    public void setInvestType(String investType) {
        this.investType = investType;
    }

    public String getInvestCode() {
        return investCode;
    }

    public void setInvestCode(String investCode) {
        this.investCode = investCode;
    }

    public String getInvestLevel() {
        return investLevel;
    }

    public void setInvestLevel(String investLevel) {
        this.investLevel = investLevel;
    }

    public String getCreditorStatus() {
        return creditorStatus;
    }

    public void setCreditorStatus(String creditorStatus) {
        this.creditorStatus = creditorStatus;
    }

    public double getTransferAPR() {
        return transferAPR;
    }

    public void setTransferAPR(double transferAPR) {
        this.transferAPR = transferAPR;
    }

    public int getResidualPeriod() {
        return residualPeriod;
    }

    public void setResidualPeriod(int residualPeriod) {
        this.residualPeriod = residualPeriod;
    }

    public double getTransferAmt() {
        return transferAmt;
    }

    public void setTransferAmt(double transferAmt) {
        this.transferAmt = transferAmt;
    }

    public String getInvestmentBaoName() {
        return investmentBaoName;
    }

    public void setInvestmentBaoName(String investmentBaoName) {
        this.investmentBaoName = investmentBaoName;
    }

    public String getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
    }

    public boolean isHold() {
        return hold;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }
}
