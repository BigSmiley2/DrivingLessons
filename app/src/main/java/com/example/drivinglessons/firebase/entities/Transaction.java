package com.example.drivinglessons.firebase.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Transaction
{
    private static final String ID = "id", FROM_ID = "fromId", FROM_NAME = "fromName", TO_ID = "toId", TO_NAME = "toName", DATE = "date", AMOUNT = "amount", IS_CONFIRMED = "isConfirmed";

    public String id, fromId, toId, fromName, toName;
    public Date date;
    public Double amount;
    public Boolean isConfirmed;

    public Transaction() {}

    public Transaction(String id, String fromId, String toId, String fromName, String toName, Date date, Double amount, Boolean isConfirmed)
    {
        this.id = id;
        this.fromId = fromId;
        this.fromName = fromName;
        this.toId = toId;
        this.toName = toName;
        this.date = date;
        this.amount = amount;
        this.isConfirmed = isConfirmed;
    }

    public Map<String, Object> toMap()
    {
        Map<String, Object> map = new HashMap<>();

        if (id != null) map.put(ID, id);
        if (fromId != null) map.put(FROM_ID, fromId);
        if (fromName != null) map.put(FROM_NAME, fromName);
        if (toId != null) map.put(TO_ID, toId);
        if (toName != null) map.put(TO_NAME, toName);
        if (date != null) map.put(DATE, date);
        if (amount != null) map.put(AMOUNT, amount);
        if (isConfirmed != null) map.put(IS_CONFIRMED, isConfirmed);

        return map;
    }
}
