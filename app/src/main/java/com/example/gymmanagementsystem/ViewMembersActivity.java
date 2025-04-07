package com.example.gymmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewMembersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MemberAdapter adapter;
    private List<Member> memberList;
    private List<Member> filteredList; // NEW: Filtered list for search
    private Button addMemberBtn;
    private EditText searchMember; // NEW: Search bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);

        recyclerView = findViewById(R.id.recyclerView);
        addMemberBtn = findViewById(R.id.addMemberBtn);
        searchMember = findViewById(R.id.searchMember); // NEW: Search bar reference

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        memberList = new ArrayList<>();
        filteredList = new ArrayList<>(); // NEW: Initialize filtered list
        adapter = new MemberAdapter(this, filteredList); // Use filteredList instead
        recyclerView.setAdapter(adapter);

        addMemberBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ViewMembersActivity.this, regform.class);
            startActivity(intent);
        });

        loadMembersFromFirestore();

        // NEW: Implement search functionality
        searchMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadMembersFromFirestore() {
        FirebaseFirestore.getInstance().collection("members")
                .whereEqualTo("ownerId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    memberList.clear();
                    filteredList.clear(); // Also clear filtered list

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Member member = doc.toObject(Member.class);
                        if (member != null) {
                            member.setId(doc.getId());
                            memberList.add(member);
                        }
                    }

                    filteredList.addAll(memberList); // Show all members initially
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewMembersActivity.this, "Error loading members: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void filterList(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(memberList);
        } else {
            for (Member member : memberList) {
                if (member.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(member);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    public void refreshMemberList() {
        loadMembersFromFirestore();
    }
}
