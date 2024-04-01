package com.example.drivinglessons;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.example.drivinglessons.dialogs.LessonDialog;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.fragments.view.LessonViewFragment;
import com.example.drivinglessons.fragments.LoginFragment;
import com.example.drivinglessons.fragments.MainOfflineFragment;
import com.example.drivinglessons.fragments.info.UserInfoFragment;
import com.example.drivinglessons.fragments.view.UserViewFragment;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.NetworkChangedReceiver;
import com.example.drivinglessons.util.SharedPreferencesManager;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;
import com.example.drivinglessons.util.fragments.PagerFragment;

import java.util.ArrayList;

public class MainActivity <T extends Fragment & Parcelable> extends AppCompatActivity
{
    private NetworkChangedReceiver receiver;

    private boolean isOwnerMode;

    private FirebaseManager fm;
    private SharedPreferencesManager spm;

    private ActivityResultLauncher<Intent> startActivity;
    private PagerFragment<T> pagerFragment;
    private FragmentContainerView container;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new NetworkChangedReceiver();

        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        isOwnerMode = false;

        fm = FirebaseManager.getInstance(this);
        spm = SharedPreferencesManager.getInstance(this);

        startActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), r ->
        {
            if (r.getResultCode() == Activity.RESULT_OK) refresh();
        });

        container = findViewById(R.id.fragmentContainerViewActivityMain);

        setSupportActionBar(findViewById(R.id.toolbarActivityMain));

        if (fm.isSigned() && spm.getIsStudent()) fm.getStudentCanTest(fm.getCurrentUid(), new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                refresh();
            }
        });

        if (savedInstanceState == null) createAndLinkFragments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main_options, menu);

        MenuItem logout, edit, balance, owner, notOwner, overflow, test;

        overflow = menu.findItem(R.id.menuItemMainOptionsMenuOverflowMenu);
        logout = menu.findItem(R.id.menuItemMainOptionsMenuLogout);
        edit = menu.findItem(R.id.menuItemMainOptionsMenuEdit);
        balance = menu.findItem(R.id.menuItemMainOptionsMenuBalance);
        owner = menu.findItem(R.id.menuItemMainOptionsMenuOwner);
        notOwner = menu.findItem(R.id.menuItemMainOptionsMenuNotOwner);
        test = menu.findItem(R.id.menuItemMainOptionsMenuTest);

        final boolean isSigned = fm.isSigned(), isOwner = spm.getIsOwner(), canTest = spm.getCanTest();

        overflow.setVisible(isSigned); 
        logout.setVisible(isSigned);
        edit.setVisible(isSigned && !isOwnerMode);
        balance.setVisible(isSigned && !isOwnerMode);
        owner.setVisible(isOwner && isOwnerMode);
        notOwner.setVisible(isOwner && !isOwnerMode);
        test.setVisible(canTest && !isOwnerMode);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        final int id = item.getItemId();

        if (id == R.id.menuItemMainOptionsMenuLogout) signOut();

        else if (id == R.id.menuItemMainOptionsMenuEdit) startInputActivity();

        else if (id == R.id.menuItemMainOptionsMenuTest) new LessonDialog(this, fm.getCurrentUid(), true).show();

        else if (id == R.id.menuItemMainOptionsMenuBalance)
        {
            Intent intent = new Intent(this, BalanceActivity.class);
            intent.putExtra(BalanceActivity.ID, fm.getCurrentUid());
            startActivity(intent);
        }

        else if (id == R.id.menuItemMainOptionsMenuOwner)
        {
            isOwnerMode = false;
            refresh();
        }

        else if (id == R.id.menuItemMainOptionsMenuNotOwner)
        {
            isOwnerMode = true;
            refresh();
        }

        else if (id == R.id.menuItemMainOptionsMenuAbout);

        else return false;

        return true;
    }

    private void startInputActivity()
    {
        Intent intent = new Intent(this, InputActivity.class);

        fm.getCurrentUserFromDatabase(new FirebaseRunnable()
        {
            @Override
            public void run(User user)
            {
                intent.putExtra(InputActivity.USER, user);
                startActivity.launch(intent);
            }
        }, new FirebaseRunnable() {});
    }

    private void signOut()
    {
        Constants.createAlertDialog(this, "Are you sure you want to logout?", "Yes", "No", (dialogInterface, i) ->
        {
            if (i == Dialog.BUTTON_POSITIVE) fm.signOut(new FirebaseRunnable()
            {
                @Override
                public void run()
                {
                    refresh();
                }
            });
        });
    }

    public void signIn()
    {
        isOwnerMode = false;
        refresh();
    }

    public void refresh()
    {
        createAndLinkFragments();
        supportInvalidateOptionsMenu();
    }

    public void createAndLinkFragments()
    {
        createFragments();
        replaceFragments();
    }

    private void replaceFragments()
    {
        getSupportFragmentManager().beginTransaction().replace(container.getId(), pagerFragment).commit();
    }

    private void createFragments()
    {
        ArrayList<T> fragments = new ArrayList<>();

        if (fm.isSigned() && isOwnerMode) createOwner(fragments);
        else if (fm.isSigned()) createSignedIn(fragments);
        else createSignedOut(fragments);

        pagerFragment = PagerFragment.newInstance(fragments);
    }

    @SuppressWarnings("unchecked")
    private void createSignedOut(@NonNull ArrayList<T> fragments)
    {
        fragments.add((T) UserViewFragment.newInstance(false, false, false, null));
        fragments.add((T) MainOfflineFragment.newInstance(LoginFragment.newInstance()));
    }

    @SuppressWarnings("unchecked")
    private void createSignedIn(@NonNull ArrayList<T> fragments)
    {
        String id = fm.getCurrentUid();
        boolean isStudent = spm.getIsStudent();

        fragments.add((T) UserViewFragment.newInstance(false, false, !isStudent, null));
        fragments.add((T) UserInfoFragment.newInstance(id, isStudent));
        if (spm.getHasTeacher() || !isStudent) fragments.add((T) LessonViewFragment.newInstance(id, isStudent));
    }

    @SuppressWarnings("unchecked")
    private void createOwner(@NonNull ArrayList<T> fragments)
    {
        String id = fm.getCurrentUid();
        boolean isStudent = spm.getIsStudent();

        fragments.add((T) UserViewFragment.newInstance(true, false, null));
        fragments.add((T) UserInfoFragment.newInstance(id, isStudent));
        fragments.add((T) LessonViewFragment.newInstance());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        unregisterReceiver(receiver);
    }
}