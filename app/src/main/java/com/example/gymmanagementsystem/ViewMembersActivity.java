package com.example.gymmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

        memberList = new ArrayList<>();
        adapter = new MemberAdapter(this, memberList);
        recyclerView.setAdapter(adapter);

        addMemberBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ViewMembersActivity.this, regform.class);
            startActivity(intent);
        });

        loadMembersFromFirestore(); // NEW: Load members from Firestore
    }

    // NEW: Method to load members from Firestore
    private void loadMembersFromFirestore() {
        FirebaseFirestore.getInstance().collection("members")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    memberList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Member member = doc.toObject(Member.class);
                        if (member != null) {
                            member.setId(doc.getId()); // Set the Firestore document ID
                            memberList.add(member);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewMembersActivity.this, "Error loading members: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // NEW: Refresh list after an update
    public void refreshMemberList() {
        loadMembersFromFirestore();
    }
}
