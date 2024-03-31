package com.example.drivinglessons;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.drivinglessons.firebase.entities.Lesson;
import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.fragments.AddRatingFragment;
import com.example.drivinglessons.fragments.info.UserInfoFragment;
import com.example.drivinglessons.fragments.view.UserViewFragment;

public class SelectActivity extends AppCompatActivity
{
    public static final String LESSON = "lesson";

    private FragmentContainerView container;

    private Lesson lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        container = findViewById(R.id.fragmentContainerViewActivitySelect);

        Intent intent = getIntent();

        if (intent != null) lesson = intent.getParcelableExtra(LESSON);

        if (savedInstanceState == null) createAndLinkFragment();
    }

    private void replaceFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(container.getId(), fragment).commit();
    }

    private void createAndLinkFragment()
    {
        replaceFragment(UserViewFragment.newInstance(false, true, false, lesson));
    }
}