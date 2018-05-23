package com.rileystech.gurpay.models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Bill implements Serializable {

    public int id;
    public int owner_id;
    public String name;
    public Double total;
    public Date date_assigned;
    public Date date_paid;
    public Date date_due;
    public Boolean is_archive;

    //detail bill stuff
    public Double subtotal;
    public Double split_cost;
    public HashMap<User,Boolean> payers = new HashMap<>();

    public Bill(){}

    public Bill(int id, int owner_id, String name,Double total, Date dateAssigned, Date datePaid, Date dateDue){
        this(id,owner_id,name,total,dateAssigned,datePaid,dateDue,false);

    }

    public Bill(int id, int owner_id, String name,Double total, Date dateAssigned, Date datePaid, Date dateDue, boolean isArchive){
        this.id = id;
        this.owner_id = owner_id;
        this.name = name;
        this.total = total;
        this.date_assigned = dateAssigned;
        this.date_due = dateDue;
        this.date_paid = datePaid;
        this.is_archive = isArchive;
    }
}
