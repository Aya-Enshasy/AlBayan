package com.ayaenshasy.AlBayan.ui.activities;

import static com.ayaenshasy.AlBayan.utils.Constant.USER_ID;
import static com.ayaenshasy.AlBayan.utils.Constant.USER_NAME;
import static com.ayaenshasy.AlBayan.utils.Constant.USER_ROLE;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ayaenshasy.AlBayan.R;
import com.ayaenshasy.AlBayan.adapter.UserAdapter;
import com.ayaenshasy.AlBayan.base.BaseActivity;
import com.ayaenshasy.AlBayan.databinding.ActivitySoraBinding;
import com.ayaenshasy.AlBayan.databinding.ActivityUserDetailsBinding;
import com.ayaenshasy.AlBayan.model.Role;
import com.ayaenshasy.AlBayan.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class UserDetailsActivity extends BaseActivity {
    Context context = this;
    ActivityUserDetailsBinding binding;
    List<User> users = new ArrayList<>();

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backArrow.setOnClickListener(v -> {
            finish();
        });
        binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        binding.tvName.setText("الاسم : " + getIntent().getStringExtra(USER_NAME));
        binding.tvId.setText("رقم الهوية : " + getIntent().getStringExtra(USER_ID));

        UserAdapter userAdapter = new UserAdapter(users, context);
        binding.rvUser.setAdapter(userAdapter);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef;
        String userRole = getIntent().getStringExtra(USER_ROLE);
        if (userRole != null && userRole.equals(Role.TEACHER.name())) {
            usersRef = db.collection("students");
        } else {
            usersRef = db.collection("users");
        }

        String userId = getIntent().getStringExtra(USER_ID);
        if (userId != null) {
            Query query = usersRef.whereEqualTo("responsible_id", userId);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    users.clear(); // Clear the previous user list
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Retrieve the user data
                        User user = document.toObject(User.class);
                        users.add(user);
                    }
                    // Notify the adapter that the data has changed
                    userAdapter.notifyDataSetChanged();
                } else {
                    // Handle any errors that occur
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            });
        }
    }
}
