package com.example.drivinglessons;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.fragments.BalanceViewFragment;
import com.example.drivinglessons.fragments.info.UserInfoFragment;

public class BalanceActivity extends AppCompatActivity
{
    public final static String ID = "id";

    private String id;

    private FragmentContainerView container;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        container = findViewById(R.id.fragmentContainerViewActivityBalance);

        Intent intent = getIntent();

        if (intent != null) id = intent.getStringExtra(ID);

        if (savedInstanceState == null) createAndLinkFragment();
    }

    private void replaceFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(container.getId(), fragment).commit();
    }

    private void createAndLinkFragment()
    {
        replaceFragment(BalanceViewFragment.newInstance(id));
    }
}