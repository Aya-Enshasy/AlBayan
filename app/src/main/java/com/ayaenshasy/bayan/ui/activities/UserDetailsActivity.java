package com.ayaenshasy.bayan.ui.activities;

import static com.ayaenshasy.bayan.utils.Constant.USER_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.adapter.UserAdapter;
import com.ayaenshasy.bayan.base.BaseActivity;
import com.ayaenshasy.bayan.databinding.ActivitySoraBinding;
import com.ayaenshasy.bayan.databinding.ActivityUserDetailsBinding;
import com.ayaenshasy.bayan.model.user.User;
import com.ayaenshasy.bayan.ui.fragments.BaseFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UserDetailsActivity extends BaseActivity {
    Context context = this;
    ActivityUserDetailsBinding binding;
    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toast.makeText(context,getIntent().getStringExtra(USER_ID)+ "", Toast.LENGTH_SHORT).show();

        binding.rvUser.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        UserAdapter userAdapter = new UserAdapter(users, context);
        binding.rvUser.setAdapter(userAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        Query query = usersRef.orderByChild("responsible_id").equalTo(getIntent().getStringExtra(USER_ID));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve the user data
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                    // Do something with the user data
                    // For example, print the user's name
                    System.out.println(user.getName());
                }
                userAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
                System.out.println("Error: " + databaseError.getMessage());
            }
        });

    }
}