package com.example.firestoreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private Button save;
    private Button load;
    private Button update;
    private Button delete;
    private TextView result;

    private FirebaseFirestore db;
    private CollectionReference cref;
    private DocumentReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        cref = db.collection("Users");
        ref = cref.document("Friends");

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        save = findViewById(R.id.save);
        load = findViewById(R.id.load);
        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);
        result = findViewById(R.id.results);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        load.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                loadData();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });
    }

    private void saveData(){
        String n = name.getText().toString();
        String e = email.getText().toString();
        if(!n.equals("") && !e.equals("")) {
            Friend f = new Friend(n, e);

            cref.add(f).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String docId = documentReference.getId();
                    Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                }
            });
        } else Toast.makeText(MainActivity.this,"Fields shouldn't be empty",Toast.LENGTH_SHORT).show();
    }

    private void loadData(){
        cref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data="";
                for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                    Friend f = snapshot.toObject(Friend.class);
                    data+="Name: "+f.getName()+" Email: "+f.getEmail()+"\n";
                }
                result.setText(data);
                Toast.makeText(MainActivity.this, "Loaded!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateData(){
        cref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data="";
                String n = name.getText().toString();
                String e = email.getText().toString();
                if(!n.equals("") && !e.equals("")) {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Friend f = snapshot.toObject(Friend.class);
                        if (f.getName().equals(n)) {
                            DocumentReference r = cref.document(snapshot.getId());
                            r.update("email", e);
                        }
                        if(f.getEmail().equals(e)){
                            DocumentReference r = cref.document(snapshot.getId());
                            r.update("name", n);
                        }
                    }
                    loadData();
                    Toast.makeText(MainActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(MainActivity.this,"Fields shouldn't be empty",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteData(){
        cref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data="",msg="Deleted";
                String n = "";n=name.getText().toString();
                String e = "";e=email.getText().toString();
                int flag =0;
//                Toast.makeText(MainActivity.this, n+" : "+e, Toast.LENGTH_SHORT).show();
                if(!n.equals("") && !e.equals("")) {
//                    Toast.makeText(MainActivity.this, "iunside", Toast.LENGTH_SHORT).show();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Friend f = snapshot.toObject(Friend.class);
                        if (f.getName().equals(n) && f.getEmail().equals(e)) {
                            Toast.makeText(MainActivity.this, "del", Toast.LENGTH_SHORT).show();
                            DocumentReference r = cref.document(snapshot.getId());
                            flag =1;
                            r.delete();
                        }
                    }
                    loadData();
                    if(flag == 1) msg = "No record suitable to delete";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(MainActivity.this,"Fields shouldn't be empty",Toast.LENGTH_SHORT).show();
            }
        });
    }
}