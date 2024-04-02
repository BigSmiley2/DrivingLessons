package com.example.drivinglessons;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.fragments.AddRatingFragment;
import com.example.drivinglessons.fragments.info.UserInfoFragment;
import com.example.drivinglessons.util.SharedPreferencesManager;
import com.example.drivinglessons.util.firebase.FirebaseManager;

public class InfoActivity extends AppCompatActivity
{
    public final static String USER = "user";

    private User user;
    private FirebaseManager fm;

    private FragmentContainerView containerInfo, containerAddRating;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        fm = FirebaseManager.getInstance(this);

        containerInfo = findViewById(R.id.fragmentContainerViewActivityInfoInfo);
        containerAddRating = findViewById(R.id.fragmentContainerViewActivityInfoAddRating);

        Intent intent = getIntent();

        if (intent != null) user = intent.getParcelableExtra(USER);

        if (savedInstanceState == null) createAndLinkFragment();
    }

    private void replaceFragment(@NonNull FragmentContainerView container, Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(container.getId(), fragment).commit();
    }

    private void createAndLinkFragment()
    {
        replaceFragment(containerInfo, UserInfoFragment.newInstance(user.id, user instanceof Student));
        if (fm.isSigned() && !user.id.equals(fm.getCurrentUid())) replaceFragment(containerAddRating, AddRatingFragment.newInstance(user.id, fm.getCurrentUid(), user instanceof Student));
    }
}