package com.msteber.accessibilityproject;

import java.util.Comparator;

//Recyclerview'de gösterilecek olan uygulama isimlerini, packagename'leri ve checkbox'ları tutan sınıf

public class MyCheckBox {
    private String packageName, appName;
    private boolean isChecked;


    public MyCheckBox(String packageName, String appName, boolean isChecked) {
        this.packageName = packageName;
        this.isChecked = isChecked;
        this.appName = appName;
    }

    //yüklü uygulamarın isimlerini alfabetik olarak sıralamaya yarayan interface implementasyonu
    public static final Comparator<MyCheckBox> BY_NAME_ALPHABETICAL = new Comparator<MyCheckBox>() {
        @Override
        public int compare(MyCheckBox o1, MyCheckBox o2) {
            return o1.appName.compareTo(o2.appName);
        }
    };

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
