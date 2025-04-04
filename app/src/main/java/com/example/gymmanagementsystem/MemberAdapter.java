package com.example.gymmanagementsystem;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<Member> memberList;
    private Context context;

    public MemberAdapter(Context context, List<Member> memberList) {
        this.context = context;
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);
        holder.name.setText("Name: " + member.getName());
        holder.phone.setText("Phone: " + member.getPhone());
        holder.joiningDate.setText("Joining Date: " + member.getJoiningDate());
        holder.subscription.setText("Subscription: " + member.getSubscriptionType());
        holder.endDate.setText("End Date : " + member.getEndDate());

        // Delete member on button click using Firestore
        holder.button.setOnClickListener(v -> {
            removeMember(position);  // NEW: Remove using Firestore
        });

        // Background color logic based on subscription end date
        holder.itemView.setBackgroundColor(Color.WHITE); // Reset to default

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDateStr = sdf.format(new Date());

        try {
            Date currentDate = sdf.parse(currentDateStr);
            Date endDate = sdf.parse(member.getEndDate());

            if (endDate != null && currentDate != null) {
                long diff = endDate.getTime() - currentDate.getTime();
                long daysLeft = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                if (daysLeft <= 7 && daysLeft > 0) {
                    holder.itemView.setBackgroundColor(Color.YELLOW);
                } else if (daysLeft <= 0) {
                    holder.itemView.setBackgroundColor(Color.RED);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, joiningDate, subscription, endDate;
        Button button;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.memberName);
            phone = itemView.findViewById(R.id.memberPhone);
            joiningDate = itemView.findViewById(R.id.memberJoiningDate);
            subscription = itemView.findViewById(R.id.memberSubscription);
            endDate = itemView.findViewById(R.id.enddate);
            button = itemView.findViewById(R.id.btnDelete);
        }
    }

    // NEW: Remove member from Firestore instead of SharedPreferences
    private void removeMember(int position) {
        Member member = memberList.get(position);
        String docId = member.getId();

        FirebaseFirestore.getInstance().collection("members")
                .document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    memberList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, memberList.size());
                    Toast.makeText(context, "Member Deleted!", Toast.LENGTH_SHORT).show();
                    // Optionally, if the context is ViewMembersActivity, refresh the list.
                    if (context instanceof ViewMembersActivity) {
                        ((ViewMembersActivity) context).refreshMemberList();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error deleting member", Toast.LENGTH_SHORT).show();
                });
    }
}
