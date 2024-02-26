package com.example.drivinglessons;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.os.Parcelable;

import com.example.drivinglessons.fragments.LoginFragment;
import com.example.drivinglessons.fragments.MainOfflineFragment;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.fragments.PagerFragment;

import java.util.ArrayList;

public class MainActivity <T extends Fragment & Parcelable> extends AppCompatActivity
{

    private FirebaseManager fm;
    private PagerFragment<T> pagerFragment;
    private FragmentContainerView container;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = FirebaseManager.getInstance(this);

        //fm.signOut();

        container = findViewById(R.id.fragmentContainerViewActivityMain);

        setSupportActionBar(findViewById(R.id.toolbarActivityMain));

        if (savedInstanceState == null) createAndLinkFragments();
    }

    public void createAndLinkFragments()
    {
        createFragments();
        replaceFragments();
    }

    private void replaceFragments()
    {
        getSupportFragmentManager().beginTransaction().replace(container.getId(),pagerFragment).commit();
    }

    private void createFragments()
    {
        ArrayList<T> fragments = new ArrayList<>();

        if (fm.isSigned()) createOnline(fragments);
        else createOffline(fragments);

        pagerFragment = PagerFragment.newInstance(fragments);
    }

    @SuppressWarnings("unchecked")
    private void createOffline(ArrayList<T> fragments)
    {
        fragments.add((T) MainOfflineFragment.newInstance(LoginFragment.newInstance()));
    }

    private void createOnline(ArrayList<T> fragments)
    {
        // add here
    }
}