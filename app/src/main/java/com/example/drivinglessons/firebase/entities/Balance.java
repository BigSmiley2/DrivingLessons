package com.example.drivinglessons.firebase.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Balance
{
    private static final String DATE = "date", AMOUNT = "amount";

    public Date date;
    public Double amount;

    public Balance() {}

    public Balance(Double amount, Date date)
    {
        this.amount = amount;
        this.date = date;
    }

    public Map<String, Object> toMap()
    {
        Map<String, Object> map = new HashMap<>();
        if (date != null) map.put(DATE, date);
        if (amount != null) map.put(AMOUNT, amount);
        return map;
    }
}
