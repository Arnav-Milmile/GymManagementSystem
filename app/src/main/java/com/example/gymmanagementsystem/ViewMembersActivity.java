package com.example.gymmanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewMembersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MemberAdapter adapter;
    private List<Member> memberList;
    private Button addMemberBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);

        recyclerView = findViewById(R.id.recyclerView);
        addMemberBtn = findViewById(R.id.addMemberBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        memberList = loadMembers();
        adapter = new MemberAdapter(this, memberList);
        recyclerView.setAdapter(adapter);

        addMemberBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ViewMembersActivity.this, regform.class);
            startActivityForResult(intent, 1);
        });
    }

    private List<Member> loadMembers() {
        List<Member> list = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("GymMembers", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String[] data = entry.getValue().toString().split(",");

            if (data.length == 4) {
                String name = entry.getKey();
                String phone = data[0];
                String joiningDate = data[1];
                String subscriptionType = data[2];
                String endDate = data[3];

                list.add(new Member(name, phone, joiningDate, subscriptionType, endDate));
            }
        }
        return list;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean shouldUpdate = data.getBooleanExtra("updateList", false);
            if (shouldUpdate) {
                refreshMemberList();
            }
        }
    }

    void refreshMemberList() {
        memberList.clear(); // ✅ Purani list clear karo
        memberList.addAll(loadMembers()); // ✅ SharedPreferences se wapas load karo
        adapter.notifyDataSetChanged(); // ✅ RecyclerView update karo
    }
}

