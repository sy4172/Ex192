package com.example.ex192_vaccineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 *  * @author		Shahar Yani
 *  * @version  	1.0
 *  * @since		19/03/2021
 *
 *  * This MainActivity.class displays the opening activity
 *    and a menu move to the whole activities.
 *  *
 *  */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent si;

        if (id == R.id.Input){
            si = new Intent(this, InputActivity.class);
            startActivity(si);
        }
        else if (id == R.id.UpgradeData){
            si = new Intent(this, UpdateActivity.class);
            startActivity(si);
        }
        else if (id == R.id.sort){
            si = new Intent(this, SortActivity.class);
            startActivity(si);
        }
        else if (id == R.id.Credits){
            si = new Intent(this, CreditsActivity.class);
            startActivity(si);
        }

        return super.onOptionsItemSelected(item);
    }
}