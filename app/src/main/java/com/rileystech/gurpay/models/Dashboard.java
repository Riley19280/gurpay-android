package com.rileystech.gurpay.models;

import java.util.Date;
import java.util.HashMap;

public class Dashboard {

    public int myBillCount;
    public int myUnpaidBillCount;

    public int myBillsPayerCount;
    public int myBillsPayersPaid;

    public Double myBillsPayerPaidTotal;
    public Double myBillsPayerPaidToDate;

    public Double payTotal;
    public Double payTotalToDate;

    public int payTotalCount;
    public int payTotalCountToDate;

    public Date nextDueDate;

    public Dashboard(int mbc, int mubc,int mbpc,int mbpp,Double mbppt,Double mbpptd,Double pt,Double pttd,int ptc,int ptctd,Date ndd) {
        this.myBillCount = mbc;
        this.myUnpaidBillCount = mubc;

        this.myBillsPayerCount = mbpc;
        this.myBillsPayersPaid = mbpp;

        this.myBillsPayerPaidTotal = mbppt;
        this.myBillsPayerPaidToDate = mbpptd;

        this.payTotal = pt;
        this.payTotalToDate = pttd;

        this.payTotalCount = ptc;
        this.payTotalCountToDate = ptctd;

        this.nextDueDate = ndd;
    }
    

}
