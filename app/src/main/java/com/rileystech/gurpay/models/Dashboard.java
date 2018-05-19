package com.rileystech.gurpay.models;

import java.util.Date;
import java.util.HashMap;

public class Dashboard {

    int myBillCount;
    int myUnpaidBillCount;

    int myBillsPayerCount;
    int myBillsPayersPaid;

    Double myBillsPayerPaidTotal;
    Double myBillsPayerPaidToDate;

    Double payTotal;
    Double payTotalToDate;

    int payTotalCount;
    int payTotalCountToDate;

    Date nextDueDate;

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
