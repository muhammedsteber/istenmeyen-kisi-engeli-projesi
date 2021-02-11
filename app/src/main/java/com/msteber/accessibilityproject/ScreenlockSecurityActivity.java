package com.msteber.accessibilityproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class ScreenlockSecurityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenlock_security);

        SharedPreferences mSharedPreferences = getSharedPreferences("screenlocksecurity",MODE_PRIVATE);
        final SharedPreferences.Editor editor = mSharedPreferences.edit();

        Switch screenlockSecuritySwitch = findViewById(R.id.switch_screenlocksecurity);

        //sheredpreferences'a göre switch işaretleniyor
        screenlockSecuritySwitch.setChecked(mSharedPreferences.getBoolean("isChecked",false));

        screenlockSecuritySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //switch'in durumundaki değişikliğe göre ilgili sharedpreferences güncelleniyor
                if(isChecked){
                    editor.putBoolean("isChecked",true);
                    editor.apply();
                }else{
                    editor.putBoolean("isChecked",false);
                    editor.apply();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //geri tuşunın tıklanmasıyla bu activity sonlandırılıyor
        this.finish();
    }
}
