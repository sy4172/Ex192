package com.example.ex192_vaccineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.ex192_vaccineapp.FBref.refStudents;

public class UpdateActivity extends AppCompatActivity implements View.OnCreateContextMenuListener {

    ListView lv;
    ArrayList<String> studentNames, statusList, studentTitle;
    CustomAdapter customadp;

    public UpdateActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        lv = findViewById(R.id.lv);

        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnCreateContextMenuListener(this);

        statusList = new ArrayList<String>();
        studentNames = new ArrayList<String>();
        // put the student names to studentNames
        refStudents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                if (dS.exists()){
                    ArrayList<String> studentNames1 = new ArrayList<>();
                    ArrayList<String> statusList1 = new ArrayList<>();
                    ArrayList<String> studentTitle1 = new ArrayList<>();
                    for (DataSnapshot data: dS.getChildren()) {
                        Student temp = data.getValue(Student.class);
                        studentTitle1.add(data.getKey());
                        studentNames1.add(Objects.requireNonNull(temp).getName());
                        if (temp.getIsAllergic()){
                            statusList1.add("Allergic student");
                        }
                        else if (temp.getV2() != null){
                            statusList1.add("All the vaccines were documented");
                        }
                        else{
                            statusList1.add("First vaccination was documented");
                        }
                    }
                    customadp = new CustomAdapter(getApplicationContext(), studentNames1, statusList1);
                    lv.setAdapter(customadp);
                    statusList = statusList1;
                    studentNames = studentNames1;
                    studentTitle = studentTitle1;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        // put the status according the vaccine state to statusList


        // get all the students and display in the listView object
        // making a context menu
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        v.setOnCreateContextMenuListener(this);
        menu.setHeaderTitle("ACTIONS");
        menu.add("Update Details");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo adpInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = adpInfo.position;
        String action = item.getTitle().toString();
        if (action.equals("Update Details")){
            Intent si = new Intent(this, InputActivity.class);
            if (statusList.get(pos).equals("Allergic student")){
                si.putExtra("AllergicStatus", true);
            }
            else{
                si.putExtra("AllergicStatus", false);
            }
            si.putExtra("StudentTitle", studentTitle.get(pos)); // Student name to display in the fields
            startActivity(si);
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


}