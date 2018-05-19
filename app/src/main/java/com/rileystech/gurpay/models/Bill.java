package com.rileystech.gurpay.models;

import java.util.Date;
import java.util.HashMap;

public class Bill {

    int id;
    int owner_id;
    String name;
    Double total;
    Date date_assigned;
    Date date_paid;
    Date date_due;
    Boolean is_archive;

    //detail bill stuff
    Double subtotal;
    Double split_cost;
    HashMap<User,Boolean> payers;

    public Bill(int id, int owner_id, String name, Date dateAssigned, Date datePaid, Date dateDue){
        this(id,owner_id,name,dateAssigned,datePaid,dateDue,false);

    }

    public Bill(int id, int owner_id, String name, Date dateAssigned, Date datePaid, Date dateDue, boolean isArchive){
        this.id = id;
        this.owner_id = owner_id;
        this.name = name;
        this.date_assigned = dateAssigned;
        this.date_due = dateDue;
        this.date_paid = datePaid;
        this.is_archive = isArchive;
    }
}
