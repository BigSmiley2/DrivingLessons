package com.example.drivinglessons.util.firebase;

import android.content.Context;
import android.widget.Toast;


import com.example.drivinglessons.R;
import com.example.drivinglessons.firebase.entities.Balance;
import com.example.drivinglessons.firebase.entities.Lesson;
import com.example.drivinglessons.firebase.entities.Student;
import com.example.drivinglessons.firebase.entities.Teacher;
import com.example.drivinglessons.firebase.entities.User;
import com.example.drivinglessons.util.Constants;
import com.example.drivinglessons.util.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FirebaseManager
{
    private static FirebaseManager fm;
    private final SharedPreferencesManager spm;
    private final FirebaseAuth auth;
    private final FirebaseDatabase db;
    private final FirebaseStorage st;

    private FirebaseManager(Context context)
    {
        spm = SharedPreferencesManager.getInstance(context);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        st = FirebaseStorage.getInstance();
    }

    public static FirebaseManager getInstance(Context context)
    {
        return fm == null ? fm = new FirebaseManager(context) : fm;
    }

    public void saveStudent(Context c, Student student, Balance balance, byte[] image, FirebaseRunnable success, FirebaseRunnable complete)
    {
        FirebaseRunnable failure = new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                FirebaseRunnable.super.run(e);
                toastS(c, R.string.went_wrong_error);
                complete.runAll(e);
            }
        };
        FirebaseRunnable onward = new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                student.imagePath = getPath(auth.getUid());
                saveInStorage(student.imagePath, image, new FirebaseRunnable()
                {
                    @Override
                    public void run()
                    {
                        saveBalanceInDatabase(student.id, balance, new FirebaseRunnable()
                        {
                            @Override
                            public void run()
                            {
                                saveStudentInDatabase(student, new FirebaseRunnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        signInSharedPreferences(student, new FirebaseRunnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                success.runAll();
                                                complete.runAll();
                                            }
                                        }, failure);
                                    }
                                }, failure);
                            }
                        }, failure);
                    }
                }, failure);
            }
        };
        if (isSigned()) deleteInStorage(student.imagePath, onward, failure);
        else registerToAuth(student, onward, failure);
    }

    public  void saveTeacher(Context c, Teacher teacher, Balance balance, byte[] image, FirebaseRunnable success, FirebaseRunnable complete)
    {
        FirebaseRunnable failure = new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                FirebaseRunnable.super.run(e);
                toastS(c, R.string.went_wrong_error);
                complete.runAll(e);
            }
        };
        FirebaseRunnable onward = new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                teacher.imagePath = getPath(auth.getUid());
                saveInStorage(teacher.imagePath, image, new FirebaseRunnable()
                {
                    @Override
                    public void run()
                    {
                        saveBalanceInDatabase(teacher.id, balance, new FirebaseRunnable()
                        {
                            @Override
                            public void run()
                            {
                                saveTeacherInDatabase(teacher, new FirebaseRunnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        signInSharedPreferences(teacher, new FirebaseRunnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                success.runAll();
                                                complete.runAll();
                                            }
                                        }, failure);
                                    }
                                }, failure);
                            }
                        }, failure);
                    }
                }, failure);
            }
        };
        if (isSigned()) deleteInStorage(teacher.imagePath, onward, failure);
        else registerToAuth(teacher, onward, failure);
    }

    private void registerToAuth(User user, FirebaseRunnable success, FirebaseRunnable failure)
    {
        auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnSuccessListener(authResult -> success.runAll())
                .addOnFailureListener(failure::runAll);
    }

    public void saveBalanceInDatabase(String id, Balance balance, FirebaseRunnable success, FirebaseRunnable failure)
    {
        db.getReference("balance").child(id).updateChildren(balance.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    private void saveStudentInDatabase(Student student, FirebaseRunnable success, FirebaseRunnable failure)
    {
        db.getReference("student").child(student.id).updateChildren(student.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    private void saveInStorage(String path, byte[] image, FirebaseRunnable success, FirebaseRunnable failure)
    {
        st.getReference(path).putBytes(image)
                .addOnSuccessListener(taskSnapshot -> success.runAll())
                .addOnFailureListener(failure::runAll);
    }

    public void deleteInStorage(String path, FirebaseRunnable success, FirebaseRunnable failure)
    {
        st.getReference(path).delete()
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    private void saveTeacherInDatabase(Teacher teacher, FirebaseRunnable success, FirebaseRunnable failure)
    {
        db.getReference("teacher").child(teacher.id).updateChildren(teacher.toMap())
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    public void sendPasswordReset(Context c, String email, FirebaseRunnable complete)
    {
        sendPasswordReset(email, new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                toastS(c, R.string.reset_email_success);
                complete.runAll();
            }
        }, new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                FirebaseRunnable.super.run(e);
                toastS(c, R.string.reset_email_failure);
                complete.runAll();
            }
        });
    }

    private void sendPasswordReset(String email, FirebaseRunnable success, FirebaseRunnable failure)
    {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(success::runAll)
                .addOnFailureListener(failure::runAll);
    }

    private void signInAuth(String email, String password, FirebaseRunnable success, FirebaseRunnable failure)
    {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> success.runAll())
                .addOnFailureListener(failure::runAll);
    }

    public void signIn(Context c, String email, String password, FirebaseRunnable success, FirebaseRunnable complete)
    {
        fm.signInAuth(email, password, new FirebaseRunnable()
        {
            @Override
            public void run()
            {
                fm.getCurrentUserFromDatabase(new FirebaseRunnable()
                {
                    @Override
                    public void run(User user)
                    {
                        signInSharedPreferences(user, new FirebaseRunnable()
                        {
                            @Override
                            public void run(User user)
                            {
                                success.runAll(user);
                                complete.runAll(user);
                            }
                        },  new FirebaseRunnable()
                        {
                            @Override
                            public void run(Exception e)
                            {
                                FirebaseRunnable.super.run(e);
                                toastS(c, R.string.went_wrong_error);
                                complete.runAll(e);
                            }
                        });
                    }
                }, new FirebaseRunnable()
                {
                    @Override
                    public void run(Exception e)
                    {
                        FirebaseRunnable.super.run(e);
                        toastS(c, R.string.went_wrong_error);
                        complete.runAll(e);
                    }
                });
            }
        }, new FirebaseRunnable()
        {
            @Override
            public void run(Exception e)
            {
                FirebaseRunnable.super.run(e);
                toastS(c, R.string.email_or_password_incorrect);
                complete.runAll(e);
            }
        });
    }

    private void signInSharedPreferences(User user, FirebaseRunnable success, FirebaseRunnable failure)
    {
        boolean isStudent = user instanceof Student, isOwner = user.email.equals(Constants.OWNER_EMAIL);

        if (isStudent)
        {
            Student student = (Student) user;

            boolean hasTeacher = student.teacherId != null;

            spm.put(true, hasTeacher, null, isOwner);

            getStudentCanTest(user, success, failure);
        }
        else
        {
            spm.put(false, null, null, isOwner);
            success.runAll(user);
        }
    }

    public void getStudentCanTest(User user, FirebaseRunnable success, FirebaseRunnable failure)
    {
        getStudentLessons(user, new FirebaseRunnable()
        {
            @Override
            public void run(List<Lesson> lessons)
            {
                Calendar calendar = Calendar.getInstance();
                double time = 0;

                for (Lesson lesson : lessons)
                    if (lesson.isConfirmed && lesson.end.getTime() < calendar.getTime().getTime())
                        time += lesson.getDuration();

                spm.putCanTest(Constants.TEST_TIME < time);

                success.runAll(user);
            }
        }, failure);
    }

    public void getStudentLessons(User user, FirebaseRunnable success, FirebaseRunnable failure)
    {
        getStudentLessons(user.id, success, failure);
    }

    public void getStudentLessons(String id, FirebaseRunnable success, FirebaseRunnable failure)
    {
        db.getReference("lesson").orderByChild("studentId").equalTo(id).get()
                .addOnSuccessListener(dataSnapshot ->
                {
                    List<Lesson> lessons = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        lessons.add(snapshot.getValue(Lesson.class));

                    success.runAll(lessons);
                })
                .addOnFailureListener(failure::runAll);
    }

    public void getTeacherLessons(Context c, User user, FirebaseRunnable success)
    {
        getTeacherLessons(c, user.id, success);
    }

    public void getTeacherLessons(Context c, String id, FirebaseRunnable success)
    {
        db.getReference("lesson").orderByChild("teacherId").equalTo(id).get()
                .addOnSuccessListener(dataSnapshot ->
                {
                    List<Lesson> lessons = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        lessons.add(snapshot.getValue(Lesson.class));

                    success.runAll(lessons);
                })
                .addOnFailureListener(e -> new FirebaseRunnable()
                {
                    @Override
                    public void run(Exception e)
                    {
                        toastS(c, R.string.went_wrong_error);
                    }
                });
    }

    public void getUserFromDatabase(String id, FirebaseRunnable success, FirebaseRunnable failure)
    {
        getStudentFromDatabase(id, success, failure);

        getTeacherFromDatabase(id, success, failure);
    }

    public void getStudentFromDatabase(String id, FirebaseRunnable success, FirebaseRunnable failure)
    {
        db.getReference("student").orderByKey().equalTo(id).limitToFirst(1).get()
                .addOnSuccessListener(dataSnapshot ->
                {
                    Student student = dataSnapshot.getValue(Student.class);

                    if (student != null) success.runAll(student);
                })
                .addOnFailureListener(failure::runAll);
    }

    public void getTeacherFromDatabase(String id, FirebaseRunnable success, FirebaseRunnable failure)
    {
        db.getReference("teacher").orderByKey().equalTo(id).limitToFirst(1).get()
                .addOnSuccessListener(dataSnapshot ->
                {
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);

                    if (teacher != null) success.runAll(teacher);
                })
                .addOnFailureListener(failure::runAll);
    }

    public void getCurrentUserFromDatabase(FirebaseRunnable success, FirebaseRunnable failure)
    {
        getUserFromDatabase(auth.getUid(), success, failure);
    }

    public String getCurrentUid()
    {
        return auth.getUid();
    }

    public boolean isSigned()
    {
        return getCurrentUid() != null;
    }

    private String getPath(String id)
    {
        Date now = Calendar.getInstance().getTime();
        return "image/" + id + "/" + Constants.FILE_FORMAT.format(now) + ".png";
    }

    private void toastS(Context c, int message)
    {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    private void toastS(Context c, String message)
    {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    private void toastL(Context c, int message)
    {
        Toast.makeText(c, message, Toast.LENGTH_LONG).show();
    }

    private void toastL(Context c, String message)
    {
        Toast.makeText(c, message, Toast.LENGTH_LONG).show();
    }
}
