package com.example.drivinglessons.firebase.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Transaction
{
    private static final String ID = "id", FROM_ID = "fromId", TO_ID = "toId", DATE = "date", AMOUNT = "amount";
    public String id, fromId, toId;
    public Date date;
    public Double amount;

    public Transaction() {}

    public Transaction(String id, String fromId, String toId, Date date, Double amount)
    {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.date = date;
        this.amount = amount;
    }

    public Map<String, Object> toMap()
    {
        Map<String, Object> map = new HashMap<>();

        if (id != null) map.put(ID, id);
        if (fromId != null) map.put(FROM_ID, fromId);
        if (toId != null) map.put(TO_ID, toId);
        if (date != null) map.put(DATE, date);
        if (amount != null) map.put(AMOUNT, amount);

        return map;
    }
}
