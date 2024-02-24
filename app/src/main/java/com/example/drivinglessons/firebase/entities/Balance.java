package com.example.drivinglessons.firebase.entities;

import java.util.Date;

public class Balance
{
    public Date date;
    public Double amount;

    public Balance() {}

    public Balance(Double amount, Date date)
    {
        this.amount = amount;
        this.date = date;
    }
}
