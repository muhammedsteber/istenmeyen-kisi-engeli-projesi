package com.msteber.accessibilityproject;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RVAdapter rvAdapter;
    private SharedPreferences.Editor editor;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences sharedPrefsSecurity = getSharedPreferences("screenlocksecurity",MODE_PRIVATE);

        //uygulamanın açılışında ekran kilidi özelliğinin aktifliği kontrol ediliyor
        if(sharedPrefsSecurity.getBoolean("isChecked",false)){
            KeyguardManager keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);

            //kullanıcının bir ekran kilidine sahip olup olmadığı kontrol edilip ona göre işlem yapılıyor
            if(keyguardManager.isKeyguardSecure()){
                Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("Kimlik Doğrulama", "Lütfen uygulamaya erişmek için ekran kilidinizi açın:");
                startActivityForResult(intent,CODE_AUTHENTICATION_VERIFICATION);
            }else{
                Toast.makeText(this, "Lütfen açılış güvenliği özelliğini kullanmak için bir ekran kilidi belirleyin", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Ayarlar > Güvenlik > Ekran Kilidi", Toast.LENGTH_LONG).show();
            }
        }


        Button button_apply = findViewById(R.id.button_Apply);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_appNames);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerview'de gösterilecek olan checkboxları tutan arraylist
        ArrayList<MyCheckBox> checkBoxList = new ArrayList<>();

        final PackageManager pm = getPackageManager();
        //cihazda yüklü uygulamalar alınıyor
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);


        for (ApplicationInfo packageInfo : packages) {
            String packageName = packageInfo.packageName;
            String appName = packageInfo.loadLabel(pm).toString();
            //uygulamamız hariç tutularak yüklü uygulamaların bilgileri checkboxlist'e aktarılıyor
            if(!packageName.equals("com.msteber.accessibilityproject"))
                checkBoxList.add(new MyCheckBox(packageName,appName,false));

            //Log.e(TAG, "Installed package :" + packageInfo.packageName);
            //Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
            //Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }

        //recyclerview'daki, kullanıcı tarafından daha önce işaretlenmiş olan checkboxların durumunu tutan sharedpreferences
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckedBoxes", MODE_PRIVATE);
        editor = mSharedPreferences.edit();

        Set<String> checkBoxListSharedPrefs = mSharedPreferences.getStringSet("checkBoxListSharedPrefs", new HashSet<String>());

        //yüklü uygulamalar adaptera verilmeden önce alfabetik olarak sıralanıyor
        Collections.sort(checkBoxList,MyCheckBox.BY_NAME_ALPHABETICAL);

        rvAdapter = new RVAdapter(this, checkBoxList);
        recyclerView.setAdapter(rvAdapter);

        button_apply.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(!myAccessibilityServiceIsEnabled()){
            Toast.makeText(MainActivity.this, "Lütfen önce erişilebilirlik hizmetini açın..", Toast.LENGTH_SHORT).show();

            //ilgili accessibilityservice'in aktif olmaması durumunda kullanıcı ayarlara yönlendiriliyor
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
            if (launchIntent != null) {
                startActivity(launchIntent);
            }

            Toast.makeText(MainActivity.this, "Erişilebilirlik > İstenmeyen Kişi Engeli > Aç", Toast.LENGTH_LONG).show();
        }else{
            ArrayList<String> packageNamesToSend = new ArrayList<>();
            Set<String> checkBoxListSharedPrefsUpdate = new HashSet<>();

            //recyclerview'da işaretlenen checkboxlar accessiblityservice'e gönderilmek üzere alınıyor
            for(MyCheckBox checkBoxes : rvAdapter.getCheckBoxList()){
                if(checkBoxes.isChecked()){
                    packageNamesToSend.add(checkBoxes.getPackageName());
                    checkBoxListSharedPrefsUpdate.add(checkBoxes.getPackageName());
                }
            }

            //yukarıda alınan işaretli checkbox'lara ait packagename'ler sharedpreferences'a ekleniyor
            editor.putStringSet("checkBoxListSharedPrefs",checkBoxListSharedPrefsUpdate);
            editor.apply();
            //yukarıda alınan işaretli checkboxlar accessibilityservice'e gönderiliyor
            MyAccessibilityService.packageNames = packageNamesToSend;
        }
    }

    //uygulamamızın kullandığı accessbilityservice'in aktif olup olmadığını kontrol eden method
    private boolean myAccessibilityServiceIsEnabled(){
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for(AccessibilityServiceInfo asi : enabledServices){
            //ServiceInfo enabledServiceInfo = asi.getResolveInfo().serviceInfo;
            if(asi.getResolveInfo().serviceInfo.packageName.equals("com.msteber.accessibilityproject")){
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //uygulamaya erişilmeye çalışılırken 'cancel' seçeneğinin tıklanması durumunda uygulama kapatılıyor
        if(!(resultCode == RESULT_OK && requestCode == CODE_AUTHENTICATION_VERIFICATION))
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //screenlock_security.xml dosyasında oluşturulan menü actionbar'a ekleniyor
        getMenuInflater().inflate(R.menu.screenlock_security,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Actionbar'daki seçeneğin tıklanmasıyla, ilgili activity'i açılıyor
        Intent intent = new Intent(MainActivity.this, ScreenlockSecurityActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        }
        else {
            super.finish();
        }
    }

}
