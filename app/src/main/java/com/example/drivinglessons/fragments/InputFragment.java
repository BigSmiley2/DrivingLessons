package com.example.drivinglessons.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Balance;
import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.Teacher;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.firebase.FirebaseManager;
import com.example.drivinglessons.util.firebase.FirebaseRunnable;
import com.example.drivinglessons.util.validation.Permission;
import com.example.drivinglessons.util.validation.TextListener;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class InputFragment extends Fragment implements Parcelable
{
    private static final String FRAGMENT_TITLE_REGISTER = "register", FRAGMENT_TITLE_EDIT = "edit", IS_REGISTER = "is register", USER = "user", IMAGE = "image", IS_STUDENT = "is student", STUDENT_FRAGMENT = "student fragment", TEACHER_FRAGMENT = "teacher fragment";

    private FirebaseManager fm;
    private ActivityResultLauncher<Intent> startFile;

    private InputStudentFragment studentFragment;
    private InputTeacherFragment teacherFragment;
    private boolean isRegister;
    private boolean isStudent;
    private Bitmap image;
    private Uri imageUri;
    private User user;

    private TextView title;
    private ImageView cameraInput, imageView, galleryInput;
    private TextInputLayout fullNameInputLayout, emailInputLayout, passwordInputLayout, confirmPasswordInputLayout, birthdateInputLayout;
    private EditText fullNameInput, emailInput, passwordInput, confirmPasswordInput, birthdateInput;
    private RadioGroup roleInput;
    private Button buttonInput;

    public InputFragment() {}

    @NonNull
    public static InputFragment newInstance()
    {
        return newInstance(InputStudentFragment.newInstance(), InputTeacherFragment.newInstance());
    }

    @NonNull
    public static InputFragment newInstance(InputStudentFragment studentFragment, InputTeacherFragment teacherFragment)
    {
        return newInstance(null, true, studentFragment, teacherFragment);
    }

    @NonNull
    public static InputFragment newInstance(User user, boolean isStudent, InputStudentFragment studentFragment, InputTeacherFragment teacherFragment)
    {
        InputFragment fragment = new InputFragment();

        /* saving data state */
        Bundle args = new Bundle();
        args.putBoolean(IS_REGISTER, user == null);
        args.putParcelable(USER, user == null ? User.emptyUser() : user);
        args.putBoolean(IS_STUDENT, isStudent);
        args.putParcelable(STUDENT_FRAGMENT, studentFragment);
        args.putParcelable(TEACHER_FRAGMENT, teacherFragment);
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
            isRegister = args.getBoolean(IS_REGISTER);
            user = args.getParcelable(USER);
            isStudent = args.getBoolean(IS_STUDENT);
            studentFragment = args.getParcelable(STUDENT_FRAGMENT);
            teacherFragment = args.getParcelable(TEACHER_FRAGMENT);
        }
        if (savedInstanceState != null)
        {
            image = savedInstanceState.getParcelable(IMAGE);
            savedInstanceState.clear();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(IMAGE, image);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fm = FirebaseManager.getInstance(requireContext());

        title = view.findViewById(R.id.textViewFragmentInputTitle);
        imageView = view.findViewById(R.id.imageViewFragmentInput);
        galleryInput = view.findViewById(R.id.imageViewFragmentInputGallery);
        cameraInput = view.findViewById(R.id.imageViewFragmentInputCamera);
        fullNameInputLayout = view.findViewById(R.id.textInputLayoutFragmentInputFullName);
        fullNameInput = view.findViewById(R.id.editTextFragmentInputFullName);
        emailInputLayout = view.findViewById(R.id.textInputLayoutFragmentInputEmail);
        emailInput = view.findViewById(R.id.editTextFragmentInputEmail);
        passwordInputLayout = view.findViewById(R.id.textInputLayoutFragmentInputPassword);
        passwordInput = view.findViewById(R.id.editTextFragmentInputPassword);
        confirmPasswordInputLayout = view.findViewById(R.id.textInputLayoutFragmentInputConfirmPassword);
        confirmPasswordInput = view.findViewById(R.id.editTextFragmentInputConfirmPassword);
        birthdateInputLayout = view.findViewById(R.id.textInputLayoutFragmentInputBirthdate);
        birthdateInput = view.findViewById(R.id.editTextFragmentInputBirthdate);
        roleInput = view.findViewById(R.id.radioGroupFragmentInputRole);
        buttonInput = view.findViewById(R.id.buttonFragmentInput);

        if (!isRegister)
        {
            title.setText(R.string.edit);
            buttonInput.setText(R.string.edit);

            fullNameInput.setText(user.name);
            emailInput.setText(user.email);
            passwordInput.setText(user.password);
            confirmPasswordInput.setText(user.password);
            birthdateInput.setText(Constants.DATE_FORMAT.format(user.birthdate));
            roleInput.check(isStudent ? R.id.radioButtonFragmentInputStudent : R.id.radioButtonFragmentInputTeacher);

            emailInputLayout.setVisibility(View.GONE);
            passwordInputLayout.setVisibility(View.GONE);
            confirmPasswordInputLayout.setVisibility(View.GONE);
            roleInput.setFocusable(false);
            fullNameInput.setOnEditorActionListener((v, actionId, event) ->
                    (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) && user.birthdate == null && createBirthdateDialog());
        }
        else
        {
            title.setText(R.string.register);
            buttonInput.setText(R.string.register);
            confirmPasswordInput.setOnEditorActionListener((v, actionId, event) ->
                    (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) && user.birthdate == null && createBirthdateDialog());
        }

        replaceFragment();

        if (image == null && !isRegister)
            Glide.with(requireContext()).load(fm.getStorageReference(user.imagePath))
                    .thumbnail(Glide.with(requireContext()).load(R.drawable.loading).circleCrop()).circleCrop()
                    .listener(new RequestListener<Drawable>()
                    {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)
                        {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource)
                        {
                            if (resource instanceof BitmapDrawable) image = ((BitmapDrawable) resource).getBitmap();
                            return false;
                        }
                    }).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
        else if (image != null) Glide.with(requireContext()).load(image).circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);

        startFile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), r ->
        {
            if (r.getResultCode() != Activity.RESULT_OK)
            {
               imageUri = null;
                return;
            }
            if (imageUri == null && r.getData() != null) imageUri = r.getData().getData();

            try
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize=8;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                image = BitmapFactory.decodeStream(new ByteArrayInputStream(bitmapToBytes(bitmap, 50)), null, options);
            }
            catch (Exception e) { Toast.makeText(requireActivity(), R.string.went_wrong_error, Toast.LENGTH_SHORT).show(); }

            Glide.with(requireContext()).load(image).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
        });

        fullNameInput.addTextChangedListener((TextListener) s ->
        {
            String str = fullNameInput.getText().toString().trim().toLowerCase();
            if (str.isEmpty())
                fullNameInputLayout.setError(null);
            else if (str.length() < 2)
                fullNameInputLayout.setError("name must contain at least 2 letters");
            else if (str.length() > 32)
                fullNameInputLayout.setError("name must contain at most 32 letters");
            else if (!Pattern.matches(" *[a-z]+([ -][a-z]+)+ *", str))
                fullNameInputLayout.setError("name must be separated by a dash or a space");
            else fullNameInputLayout.setError(null);

            user.name = Constants.fixName(str);
        });

        emailInput.addTextChangedListener((TextListener) s ->
        {
            String str = emailInput.getText().toString().trim();

            if (str.isEmpty())
                emailInputLayout.setError(null);
            else if (!Patterns.EMAIL_ADDRESS.matcher(str).matches())
                emailInputLayout.setError("invalid email address");
            else emailInputLayout.setError(null);

            user.email = str;
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

            confirmPasswordInput.setText(confirmPasswordInput.getText());
            user.password = str;
        });

        confirmPasswordInput.addTextChangedListener((TextListener) s ->
        {
            String str = confirmPasswordInput.getText().toString();
            if (str.isEmpty())
                confirmPasswordInputLayout.setError(null);
            else if (str.length() < 6)
                confirmPasswordInputLayout.setError("password must contain at least 6 characters");
            else if (str.length() > 127)
                confirmPasswordInputLayout.setError("password must contain at most 127 characters");
            else if (!str.equals(user.password))
                confirmPasswordInputLayout.setError("confirm password must equal to password");
            else confirmPasswordInputLayout.setError(null);
        });
        birthdateInput.addTextChangedListener((TextListener) s -> birthdateInputLayout.setError(null));
        birthdateInput.setOnClickListener(v -> createBirthdateDialog());

        roleInput.setOnCheckedChangeListener((radioGroup, i) ->
        {
            isStudent = i == R.id.radioButtonFragmentInputStudent;
            replaceFragment();
        });

        galleryInput.setOnClickListener(v ->
        {
            if (!Permission.isAllowed(requireActivity())) return;

            Intent intent = new Intent();
            imageUri = null;
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startFile.launch(intent);
        });

        cameraInput.setOnClickListener(v ->
        {
            if (!Permission.isAllowed(requireActivity())) return;

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "new picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "from camera");
            imageUri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startFile.launch(intent);
        });

        buttonInput.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                InputTeacherFragment.Data teacherData = teacherFragment.getData();
                InputStudentFragment.Data studentData = studentFragment.getData();

                if (user.name.isEmpty())
                    fullNameInputLayout.setError("full name mustn't be empty");
                if (user.email.isEmpty())
                    emailInputLayout.setError("email mustn't be empty");
                if (user.password.isEmpty())
                    passwordInputLayout.setError("password mustn't be empty");
                if (confirmPasswordInput.getText().toString().isEmpty())
                    confirmPasswordInputLayout.setError("confirm password mustn't be empty");
                if (!isStudent && teacherData.cost == null)
                    teacherFragment.addCostError("cost mustn't be empty");
                if (user.birthdate == null)
                    birthdateInputLayout.setError("birthdate mustn't be empty");
                if (image == null) Toast.makeText(requireContext(), R.string.enter_image_first, Toast.LENGTH_SHORT).show();

                if (fullNameInputLayout.getError() != null || emailInputLayout.getError() != null || passwordInputLayout.getError() != null || confirmPasswordInputLayout.getError() != null || birthdateInputLayout.getError() != null || (!isStudent && teacherData.cost == null) || image == null) return;

                buttonInput.setOnClickListener(null);
                View.OnClickListener listener = this;

                Date now = Calendar.getInstance().getTime();

                Balance balance = isRegister ? new Balance(0.0, now) : new Balance();

                byte[] bytes = bitmapToBytes(image);

                if (isStudent)
                {
                    Student student = new Student(user, studentData.theory);
                    fm.saveStudent(requireContext(), student, balance, bytes, new FirebaseRunnable()
                    {
                        @Override
                        public void run()
                        {
                            requireActivity().finish();
                        }
                    }, new FirebaseRunnable()
                    {
                        @Override
                        public void run()
                        {
                            buttonInput.setOnClickListener(listener);
                        }
                    });
                }
                else
                {
                    Teacher teacher = new Teacher(user, teacherData.manual, teacherData.tester, teacherData.cost, isRegister ? now : null);
                    fm.saveTeacher(requireContext(), teacher, balance, bytes, new FirebaseRunnable()
                    {
                        @Override
                        public void run()
                        {
                            requireActivity().finish();
                        }
                    }, new FirebaseRunnable()
                    {
                        @Override
                        public void run()
                        {
                            buttonInput.setOnClickListener(listener);
                        }
                    });
                }
            }
        });
    }

    private boolean createBirthdateDialog()
    {
        clearFocus();

        Calendar c = Calendar.getInstance();

        if (user.birthdate != null) c.setTime(user.birthdate);

        int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (view, year, month, dayOfMonth) ->
        {
            c.set(year, month, dayOfMonth);
            user.birthdate = c.getTime();
            birthdateInput.setText(Constants.DATE_FORMAT.format(user.birthdate));
        }, y, m, d);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.YEAR, -16);
        calendar.add(Calendar.MONTH, -6);

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();

        return true;
    }

    @NonNull
    private static byte[] bitmapToBytes(Bitmap image)
    {
        return bitmapToBytes(image, 100);
    }

    @NonNull
    private static byte[] bitmapToBytes(@NonNull Bitmap image, int quality)
    {
        //noinspection SpellCheckingInspection
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    private void clearFocus()
    {
        View view = requireActivity().getCurrentFocus();
        if (view != null)
        {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void replaceFragment()
    {
        if (isStudent) replaceFragment(studentFragment);
        else replaceFragment(teacherFragment);
    }

    private void replaceFragment(Fragment fragment)
    {
        getChildFragmentManager().beginTransaction().replace(R.id.FrameLayoutFragmentInput, fragment).commit();
    }

    protected InputFragment(@NonNull Parcel in)
    {
        studentFragment = in.readParcelable(InputStudentFragment.class.getClassLoader());
        teacherFragment = in.readParcelable(InputTeacherFragment.class.getClassLoader());
        isRegister = in.readByte() == 1;
        isStudent = in.readByte() == 1;
        image = in.readParcelable(Bitmap.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeParcelable(studentFragment, flags);
        dest.writeParcelable(teacherFragment, flags);
        dest.writeByte((byte) (isRegister ? 1 : 0));
        dest.writeByte((byte) (isStudent ? 1 : 0));
        dest.writeParcelable(image, flags);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<InputFragment> CREATOR = new Creator<InputFragment>()
    {
        @Override
        public InputFragment createFromParcel(Parcel in)
        {
            return new InputFragment(in);
        }

        @Override
        public InputFragment[] newArray(int size) {
            return new InputFragment[size];
        }
    };

    @NonNull
    @Override
    public String toString()
    {
        return isRegister ? FRAGMENT_TITLE_REGISTER : FRAGMENT_TITLE_EDIT;
    }
}
