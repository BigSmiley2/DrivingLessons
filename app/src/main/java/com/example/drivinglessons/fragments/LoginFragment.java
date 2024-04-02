package com.example.drivinglessons.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivinglessons.InputActivity;
import com.example.drivinglessons.MainActivity;
import com.example.drivinglessons.R;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.services.NotificationService;
import com.example.drivinglessons.util.SharedPreferencesManager;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;
import com.example.drivinglessons.util.validation.Permission;
import com.example.drivinglessons.util.validation.TextListener;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE = "login", EMAIL = "email", PASSWORD = "password";
    private String email, password;

    private TextInputLayout emailInputLayout, passwordInputLayout;
    private EditText emailInput, passwordInput;
    private View login, signup, forgot;

    private FirebaseManager fm;
    private SharedPreferencesManager spm;

    private ActivityResultLauncher<Intent> startActivity;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public LoginFragment() {}

    @NonNull
    public static LoginFragment newInstance()
    {
        return newInstance("", "");
    }

    @NonNull
    public static LoginFragment newInstance(String email, String password)
    {
        LoginFragment fragment = new LoginFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(PASSWORD, password);
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
            email = args.getString(EMAIL);
            password = args.getString(PASSWORD);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        startActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), r ->
        {
            if (!Permission.checkNotify(requireContext()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            else startService();
            ((MainActivity<?>) requireActivity()).refresh();
        });
        requestPermissionLauncher = registerForActivityResult( new ActivityResultContracts.RequestPermission(), isGranted ->
        {
            if (isGranted)
            {
                startService();
            }
        });

        fm = FirebaseManager.getInstance(requireContext());
        spm = SharedPreferencesManager.getInstance(requireContext());

        emailInputLayout = view.findViewById(R.id.textInputLayoutFragmentLoginEmail);
        emailInput = view.findViewById(R.id.editTextFragmentLoginEmail);
        passwordInputLayout = view.findViewById(R.id.textInputLayoutFragmentLoginPassword);
        passwordInput = view.findViewById(R.id.editTextFragmentLoginPassword);
        login = view.findViewById(R.id.buttonFragmentLogin);
        signup = view.findViewById(R.id.textViewFragmentLoginSignup);
        forgot = view.findViewById(R.id.textViewFragmentLoginForgotPassword);

        emailInput.addTextChangedListener((TextListener) s ->
        {
            String str = emailInput.getText().toString().trim();

            if (str.isEmpty())
                emailInputLayout.setError(null);
            else if (!Patterns.EMAIL_ADDRESS.matcher(str).matches())
                emailInputLayout.setError("invalid email address");
            else emailInputLayout.setError(null);

            email = str;
        });

        passwordInput.addTextChangedListener((TextListener) s ->
        {
            String str = passwordInput.getText().toString();

            if (str.isEmpty())
                passwordInputLayout.setError(null);
            else if (str.length() < 6)
                passwordInputLayout.setError("password must contain at least 6 characters");
            else if (str.length() > 127)
                passwordInputLayout.setError("password must contain at most 127 characters");
            else passwordInputLayout.setError(null);

            password = str;
        });

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                login.setOnClickListener(null);
                View.OnClickListener listener = this;

                if (email.isEmpty())
                    emailInputLayout.setError("email mustn't be empty");
                if (password.isEmpty())
                    passwordInputLayout.setError("password mustn't be empty");

                if (passwordInputLayout.getError() != null || emailInputLayout.getError() != null)
                    login.setOnClickListener(listener);
                else if (!Constants.isNetworkAvailable(requireContext()))
                {
                    Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    login.setOnClickListener(listener);
                }
                else fm.signIn(requireContext(), email, password, new FirebaseRunnable()
                {
                    @Override
                    public void run()
                    {
                        if (!Permission.checkNotify(requireContext()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        else startService();
                        ((MainActivity<?>) requireActivity()).signIn();
                    }
                }, new FirebaseRunnable() {
                    @Override
                    public void run() {
                        login.setOnClickListener(listener);
                    }
                });
            }
        });

        signup.setOnClickListener(v ->
        {
            Intent intent = new Intent(requireActivity(), InputActivity.class);

            intent.putExtra(InputActivity.EMAIL, email);
            intent.putExtra(InputActivity.PASSWORD, password);

            startActivity.launch(intent);
        });

        forgot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (email.isEmpty())
                    emailInputLayout.setError("email mustn't be empty");

                if (emailInputLayout.getError() == null)
                {
                    forgot.setOnClickListener(null);
                    View.OnClickListener listener = this;

                    fm.sendPasswordReset(requireContext(), email, new FirebaseRunnable()
                    {
                        @Override
                        public void run()
                        {
                            forgot.setOnClickListener(listener);
                        }
                    });
                }
                else Toast.makeText(getContext(), R.string.enter_email_first, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startService()
    {
        Intent intent = new Intent(requireActivity(), NotificationService.class);
        intent.putExtra(NotificationService.ID, fm.getCurrentUid());
        intent.putExtra(NotificationService.IS_STUDENT, spm.getIsStudent());

        requireActivity().startService(intent);
    }

    protected LoginFragment(@NonNull Parcel in)
    {
        email = in.readString();
        password = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(email);
        dest.writeString(password);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<LoginFragment> CREATOR = new Creator<LoginFragment>()
    {
        @Override
        public LoginFragment createFromParcel(Parcel in)
        {
            return new LoginFragment(in);
        }

        @Override
        public LoginFragment[] newArray(int size)
        {
            return new LoginFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return FRAGMENT_TITLE;
    }
}
