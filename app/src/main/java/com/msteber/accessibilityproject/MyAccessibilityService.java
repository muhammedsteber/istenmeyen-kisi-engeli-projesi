package com.msteber.accessibilityproject;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";
    //açılması engellenecek uygulamaların packagename'lerini tutan arraylist
    public static ArrayList<String> packageNames;


    @Override
    protected void onServiceConnected() {
        //accessibilityservice'in aktif edilmesiyle ilk ayarlar yapılıyor
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        //info.packageNames = packageNames;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.notificationTimeout = 50;
        setServiceInfo(info);

        //accessibilityservice aktif edildiğinde daha önce belirlenmiş olan engellenecek uygulamalar varsa onlar alınıp packageNames listine ekleniyor
        SharedPreferences mSharedPrefs = getSharedPreferences("CheckedBoxes",Context.MODE_PRIVATE);
        Set<String> sharedPrefsPackageNames = mSharedPrefs.getStringSet("checkBoxListSharedPrefs",new HashSet<String>());
        packageNames = new ArrayList<>();
        if(sharedPrefsPackageNames != null){
            packageNames.addAll(sharedPrefsPackageNames);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();

        if(source == null){
            Log.e(TAG,"SOURCE IS NULL");
            return;
        }

        if(packageNames != null){

            if(packageNameControl(packageNames,event)){

                //engelli uygulamalardan birisinin açılması durumunda kullanıcı ana ekrana yönlendiriliyor
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(startMain);

                //açılmaya çalışılan uygulamanın processleri kill ediliyor.
                ActivityManager activityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.killBackgroundProcesses(source.getPackageName().toString());
            }
        }
    }

    //açılan uygulamanın kullanıcı tarafından belirlenen engelli uygulamalar arasında olup olmadığını kontrol ederek
    //ona göre boolean değer döndüren method
    private boolean packageNameControl(ArrayList<String> packageNamesToBlock, AccessibilityEvent event){

        //onAccessibilityEvent methodundan alınan eventteki packagename, engelli uygulamaların packagename'leriyle karşılaştırılıyor
        for(String packageName : packageNamesToBlock){
            if(event.getPackageName().toString().equals(packageName)){
                return true;
            }
        }
        return false;
    }


    @Override
    public void onInterrupt() {

    }
}

