package com.example.uberclone.include;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.uberclone.R;

public class MyToolbar {

    //metodo que recibe una actividad
    public static void show(AppCompatActivity activity, String title, boolean upButtom){
        Toolbar mToolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButtom);

    }
}
