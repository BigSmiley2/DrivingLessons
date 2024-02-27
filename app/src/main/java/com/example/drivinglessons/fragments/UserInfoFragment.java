package com.example.drivinglessons.fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.Teacher;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.SharedPreferencesManager;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;
import com.example.drivinglessons.util.fragments.FragmentUpdate;

public class UserInfoFragment <T extends Fragment & Parcelable & FragmentUpdate> extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "user info", ID = "id", IS_STUDENT = "is student", ADDITIONAL_FRAGMENT = "additional fragment";

    private FirebaseManager fm;
    private SharedPreferencesManager spm;

    private String id;
    private boolean isStudent;
    private T additionalFragment;

    private ConstraintLayout layout;
    private ImageView image;
    private TextView name, owner, email, birthdate;

    public UserInfoFragment() {}

    @NonNull
    public static <T extends Fragment & Parcelable & FragmentUpdate> UserInfoFragment<T> newInstance(String id, boolean isStudent, T additionalFragment)
    {
        UserInfoFragment<T> fragment = new UserInfoFragment<>();

        /* saving data state */
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putBoolean(IS_STUDENT, isStudent);
        args.putParcelable(ADDITIONAL_FRAGMENT, additionalFragment);
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
            id = args.getString(ID);
            isStudent = args.getBoolean(IS_STUDENT);
            additionalFragment = args.getParcelable(ADDITIONAL_FRAGMENT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fm = FirebaseManager.getInstance(requireContext());

        layout = view.findViewById(R.id.ConstraintLayoutFragmentUserInfo);
        image = view.findViewById(R.id.imageViewFragmentUserInfo);
        name = view.findViewById(R.id.textViewFragmentUserInfoFullName);
        owner = view.findViewById(R.id.textViewFragmentUserInfoOwner);
        email = view.findViewById(R.id.textViewFragmentUserInfoEmailData);
        birthdate = view.findViewById(R.id.textViewFragmentUserInfoBirthdateData);

        FirebaseRunnable success = new FirebaseRunnable()
        {
            @Override
            public void run(User user)
            {
                Glide.with(requireContext()).load(fm.getStorageReference(user.imagePath))
                        .thumbnail(Glide.with(requireContext()).load(R.drawable.loading).circleCrop())
                        .circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(image);
                owner.setVisibility(Constants.isOwner(user.email) ? View.VISIBLE : View.GONE);
                name.setText(user.name);
                email.setText(user.email);
                birthdate.setText(Constants.DATE_FORMAT.format(user.birthdate));
                setVisible();

            }
        };

        if (isStudent) fm.getStudentChanged(id, new FirebaseRunnable()
        {
            @Override
            public void run(User user)
            {
                success.run(user);
                additionalFragment.update((Student) user);
            }
        });
        else fm.getTeacherChanged(id, new FirebaseRunnable()
        {
            @Override
            public void run(User user)
            {
                success.run(user);
                additionalFragment.update((Teacher) user);
            }
        });

        replaceFragment(additionalFragment);
    }

    private void setVisible()
    {
        layout.setVisibility(View.VISIBLE);
    }

    private void replaceFragment(Fragment fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.FrameLayoutFragmentUserInfo, fragment).commit();
    }

    protected UserInfoFragment(Parcel in)
    {
        id = in.readString();
        isStudent = in.readByte() == 1;
        additionalFragment = in.readParcelable(Parcelable.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeByte((byte) (isStudent ? 1 : 0));
        dest.writeParcelable(additionalFragment, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<UserInfoFragment<?>> CREATOR = new Creator<UserInfoFragment<?>>()
    {
        @Override
        public UserInfoFragment<?> createFromParcel(Parcel in)
        {
            return new UserInfoFragment<>(in);
        }

        @Override
        public UserInfoFragment<?>[] newArray(int size)
        {
            return new UserInfoFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
