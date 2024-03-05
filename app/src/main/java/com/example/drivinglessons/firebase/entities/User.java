package com.example.drivinglessons.firebase.entities;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable
{
    private static final String ID = "id", NAME = "name", EMAIL = "email", PASSWORD = "password", IMAGE_PATH = "imagePath", BIRTHDATE = "birthdate";

    public String id, name, email, password, imagePath;
    public Date birthdate;

    public static User emptyUser()
    {
        return new User(null, "", "", "", null, null);
    }

    public User() {}

    public User(String id, String name, String email, String password, String imagePath, Date birthdate)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.imagePath = imagePath;
        this.birthdate = birthdate;
    }

    public User(User user)
    {
        this(user.id, user.name, user.email, user.password, user.imagePath, user.birthdate);
    }

    protected User(Parcel in)
    {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        password = in.readString();
        imagePath = in.readString();
        birthdate = (Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(imagePath);
        dest.writeSerializable(birthdate);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>()
    {
        @Override
        public User createFromParcel(Parcel in)
        {
            return new User(in);
        }

        @Override
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };

    public Map<String, Object> toMap()
    {
        Map<String, Object> map = new HashMap<>();

        if (id != null) map.put(ID, id);
        if (name != null) map.put(NAME, name);
        if (email != null) map.put(EMAIL, email);
        if (password != null) map.put(PASSWORD, password);
        if (imagePath != null) map.put(IMAGE_PATH, imagePath);
        if (birthdate != null) map.put(BIRTHDATE, birthdate);

        return map;
    }
}
