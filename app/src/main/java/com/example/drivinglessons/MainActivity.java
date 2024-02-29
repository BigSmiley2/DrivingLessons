package com.example.drivinglessons;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.os.Parcelable;

import com.example.drivinglessons.dialogs.LessonFiltersDialogFragment;
import com.example.drivinglessons.fragments.LessonViewFragment;
import com.example.drivinglessons.fragments.LoginFragment;
import com.example.drivinglessons.fragments.MainOfflineFragment;
import com.example.drivinglessons.fragments.StudentInfoFragment;
import com.example.drivinglessons.fragments.TeacherInfoFragment;
import com.example.drivinglessons.fragments.UserInfoFragment;
import com.example.drivinglessons.fragments.UserViewFragment;
import com.example.drivinglessons.util.SharedPreferencesManager;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.fragments.PagerFragment;

import java.util.ArrayList;

public class MainActivity <T extends Fragment & Parcelable> extends AppCompatActivity
{

    private FirebaseManager fm;
    private SharedPreferencesManager spm;
    private PagerFragment<T> pagerFragment;
    private FragmentContainerView container;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = FirebaseManager.getInstance(this);
        spm = SharedPreferencesManager.getInstance(this);

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

        if (fm.isSigned()) createSignedIn(fragments);
        else createSignedOut(fragments);

        pagerFragment = PagerFragment.newInstance(fragments);
    }

    @SuppressWarnings("unchecked")
    private void createSignedOut(@NonNull ArrayList<T> fragments)
    {
        fragments.add((T) MainOfflineFragment.newInstance(LoginFragment.newInstance()));
    }

    @SuppressWarnings("unchecked")
    private void createSignedIn(@NonNull ArrayList<T> fragments)
    {
        boolean isStudent = spm.getIsStudent();
        fragments.add((T) UserViewFragment.newInstance(!isStudent));
        fragments.add((T) UserInfoFragment.newInstance(fm.getCurrentUid(), isStudent));
        if (spm.getHasTeacher()) fragments.add((T) LessonViewFragment.newInstance());
    }
}