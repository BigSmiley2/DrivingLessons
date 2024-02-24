package com.example.drivinglessons.firebase.entities;

import java.util.Date;

public class Transaction
{
    public String fromId, toId;
    public Date date;
    public Double amount;

    public Transaction() {}

    public Transaction(String fromId, String toId, Date date, Double amount)
    {
        this.fromId = fromId;
        this.toId = toId;
        this.date = date;
        this.amount = amount;
    }
}
