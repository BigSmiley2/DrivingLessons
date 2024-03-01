package com.example.drivinglessons;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;

import com.example.drivinglessons.fragments.InputFragment;

public class InputActivity extends AppCompatActivity
{
    private FragmentContainerView container;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        container = findViewById(R.id.fragmentContainerViewActivityInput);

        if (savedInstanceState == null) createAndLinkFragment();
    }

    private void replaceFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(container.getId(), fragment).commit();
    }

    private void createAndLinkFragment()
    {
        replaceFragment(InputFragment.newInstance());
    }
}