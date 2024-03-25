package com.example.drivinglessons.firebase.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Rating
{
    private static final String ID = "id", FROM_ID = "fromId", TO_ID = "toId", FROM_NAME = "fromName", FROM_IMAGE = "fromImage", MESSAGE = "message", DATE = "date", RATE = "rate";

    public String id, fromId, toId, fromName, fromImage, message;
    public Date date;
    public Double rate;

    public Rating() {}

    public Rating(String id, String fromId, String toId, String fromName, String fromImage, String message, Date date, Double rate)
    {
        this.id = id;
        this.toId = toId;
        this.fromId = fromId;
        this.fromName = fromName;
        this.fromImage = fromImage;
        this.message = message;
        this.date = date;
        this.rate = rate;
    }

    public Map<String, Object> toMap()
    {
        Map<String, Object> map = new HashMap<>();

        if (id != null) map.put(ID, id);
        if (toId != null) map.put(TO_ID, toId);
        if (fromId != null) map.put(FROM_ID, fromId);
        if (fromName != null) map.put(FROM_NAME, fromName);
        if (fromImage != null) map.put(FROM_IMAGE, fromImage);
        if (message != null) map.put(MESSAGE, message);
        if (date != null) map.put(DATE, date);
        if (rate != null) map.put(RATE, rate);

        return map;
    }
}
