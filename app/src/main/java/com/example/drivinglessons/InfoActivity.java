package com.example.drivinglessons;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.fragments.InputFragment;
import com.example.drivinglessons.fragments.UserInfoFragment;

public class InfoActivity extends AppCompatActivity
{
    public final static String USER = "user";

    private User user;

    private FragmentContainerView container;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        container = findViewById(R.id.fragmentContainerViewActivityInfo);

        Intent intent = getIntent();

        if (intent != null) user = intent.getParcelableExtra(USER);

        if (savedInstanceState == null) createAndLinkFragment();
    }

    private void replaceFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(container.getId(), fragment).commit();
    }

    private void createAndLinkFragment()
    {
        replaceFragment(UserInfoFragment.newInstance(user.id, user instanceof Student));
    }
}