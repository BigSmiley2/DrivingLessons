package com.example.drivinglessons.fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.R;

public class MainOfflineFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "login & register", LOGIN_FRAGMENT = "login fragment";
    private LoginFragment loginFragment;

    public MainOfflineFragment() {}

    @NonNull
    public static MainOfflineFragment newInstance()
    {
        return newInstance(null);
    }

    @NonNull
    public static MainOfflineFragment newInstance(LoginFragment loginFragment)
    {
        MainOfflineFragment fragment = new MainOfflineFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putParcelable(LOGIN_FRAGMENT, loginFragment);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            loginFragment = args.getParcelable(LOGIN_FRAGMENT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_main_offline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (loginFragment != null) replaceFragment(loginFragment);
    }

    private void replaceFragment(Fragment fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.FrameLayoutFragmentMainOffline, fragment).commit();
    }

    protected MainOfflineFragment(@NonNull Parcel in)
    {
        loginFragment = in.readParcelable(LoginFragment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeParcelable(loginFragment, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<MainOfflineFragment> CREATOR = new Creator<MainOfflineFragment>()
    {
        @Override
        public MainOfflineFragment createFromParcel(Parcel in)
        {
            return new MainOfflineFragment(in);
        }

        @Override
        public MainOfflineFragment[] newArray(int size)
        {
            return new MainOfflineFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
