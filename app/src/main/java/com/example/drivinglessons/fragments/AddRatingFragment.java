package com.example.drivinglessons.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.google.android.material.textfield.TextInputLayout;

public class AddRatingFragment extends Fragment
{
    private static final String FRAGMENT_TITLE = "add rating", ID = "id";

    private String id, message;
    private double rate;

    private FirebaseManager fm;

    private EditText messageInput, rateInput;
    private TextInputLayout messageInputLayout, rateInputLayout;
    private ImageView image;


    public AddRatingFragment() {}

    @NonNull
    public static AddRatingFragment newInstance(String id)
    {
        AddRatingFragment fragment = new AddRatingFragment();

        Bundle args = new Bundle();
        args.putString(ID, id);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fm = FirebaseManager.getInstance(requireContext());

        Bundle args = getArguments();
        if (args != null)
        {
            id = args.getString(ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_add_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);


    }

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
