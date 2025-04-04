package com.example.gymmanagementsystem;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import java.util.Map;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void addMember(Map<String, Object> memberData, OnSuccessListener<DocumentReference> onSuccess, OnFailureListener onFailure) {
        db.collection("members")
                .add(memberData)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public static void deleteMember(String docId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection("members")
                .document(docId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
}

