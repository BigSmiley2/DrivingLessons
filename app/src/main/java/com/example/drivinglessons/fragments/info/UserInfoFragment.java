package com.example.drivinglessons.fragments.info;

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
import com.example.drivinglessons.fragments.view.RatingViewFragment;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;

import java.time.Period;
import java.util.Calendar;
import java.util.Locale;

public class UserInfoFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "user info", ID = "id", IS_STUDENT = "is student", STUDENT_FRAGMENT = "student fragment", TEACHER_FRAGMENT = "teacher fragment", RATING_FRAGMENT = "rating fragment";

    private FirebaseManager fm;

    private String id;
    private boolean isStudent;
    private StudentInfoFragment studentFragment;
    private TeacherInfoFragment teacherFragment;
    private RatingViewFragment ratingFragment;

    private ConstraintLayout layout;
    private ImageView image;
    private TextView name, owner, email, birthdate, age;

    public UserInfoFragment() {}

    @NonNull
    public static UserInfoFragment newInstance(String id, boolean isStudent)
    {
        return newInstance(id, isStudent, StudentInfoFragment.newInstance(), TeacherInfoFragment.newInstance(), RatingViewFragment.newInstance(id));
    }

    @NonNull
    public static UserInfoFragment newInstance(String id, boolean isStudent, StudentInfoFragment studentFragment, TeacherInfoFragment teacherFragment, RatingViewFragment ratingFragment)
    {
        UserInfoFragment fragment = new UserInfoFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putBoolean(IS_STUDENT, isStudent);
        args.putParcelable(STUDENT_FRAGMENT, studentFragment);
        args.putParcelable(TEACHER_FRAGMENT, teacherFragment);
        args.putParcelable(RATING_FRAGMENT, ratingFragment);
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
            studentFragment = args.getParcelable(STUDENT_FRAGMENT);
            teacherFragment = args.getParcelable(TEACHER_FRAGMENT);
            ratingFragment = args.getParcelable(RATING_FRAGMENT);
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
        age = view.findViewById(R.id.textViewFragmentUserInfoAgeData);

        FirebaseRunnable success = new FirebaseRunnable()
        {
            @Override
            public void run(User user)
            {
                Period period = Constants.periodBetween(user.birthdate, Calendar.getInstance().getTime());

                Glide.with(requireContext()).load(fm.getStorageReference(user.imagePath))
                        .thumbnail(Glide.with(requireContext()).load(R.drawable.loading).circleCrop())
                        .circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(image);
                owner.setVisibility(Constants.isOwner(user.email) ? View.VISIBLE : View.GONE);
                name.setText(user.name);
                email.setText(user.email);
                birthdate.setText(Constants.DATE_FORMAT.format(user.birthdate));
                age.setText(String.format(Locale.ROOT, "%d.%d", period.getYears(), period.getMonths()));
                setVisible();
            }
        };

        if (isStudent)
        {
            replaceInfoFragment(studentFragment);
            fm.getStudentChanged(id, new FirebaseRunnable()
            {
                @Override
                public void run(User user)
                {

                    if (user == null) return;

                    success.run(user);
                    studentFragment.update((Student) user);
                }
            });
        }
        else
        {
            replaceInfoFragment(teacherFragment);
            fm.getTeacherChanged(id, new FirebaseRunnable()
            {
                @Override
                public void run(User user)
                {

                    if (user == null) return;

                    success.run(user);
                    teacherFragment.update((Teacher) user);
                }
            });
        }
        replaceRatingFragment(ratingFragment);

    }

    private void setVisible()
    {
        layout.setVisibility(View.VISIBLE);
    }

    private void replaceInfoFragment(Fragment fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.FrameLayoutFragmentUserInfo, fragment).commit();
    }

    private void replaceRatingFragment(Fragment fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.FrameLayoutFragmentUserInfoAddRating, fragment).commit();
    }

    protected UserInfoFragment(@NonNull Parcel in)
    {
        id = in.readString();
        isStudent = in.readByte() == 1;
        studentFragment = in.readParcelable(StudentInfoFragment.class.getClassLoader());
        teacherFragment = in.readParcelable(TeacherInfoFragment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeByte((byte) (isStudent ? 1 : 0));
        dest.writeParcelable(studentFragment, flags);
        dest.writeParcelable(teacherFragment, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<UserInfoFragment> CREATOR = new Creator<UserInfoFragment>()
    {
        @Override
        public UserInfoFragment createFromParcel(Parcel in)
        {
            return new UserInfoFragment(in);
        }

        @Override
        public UserInfoFragment[] newArray(int size)
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
