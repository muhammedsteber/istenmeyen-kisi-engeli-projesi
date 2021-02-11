package com.msteber.accessibilityproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CardViewHolder> {
    private Context mContext;
    //recyclerview'da gösterilen checkboxları ve ilgili verileri tutan list
    private List<MyCheckBox> mCheckBoxList;


    public RVAdapter(Context context, List<MyCheckBox> checkBoxList) {
        this.mContext = context;

        SharedPreferences mSharedPreferences = context.getSharedPreferences("CheckedBoxes",Context.MODE_PRIVATE);

        Set<String> checkBoxListSharedPrefs = mSharedPreferences.getStringSet("checkBoxListSharedPrefs",new HashSet<String>());

        if(checkBoxListSharedPrefs != null){

            //sharedpreferences'daki önceden seçilmiş uygulamaların packagename'lerine göre ilgili checkboxlar işaretleniyor
            for (String checkedBoxPackageName : checkBoxListSharedPrefs){
                for(int i = 0; i < checkBoxList.size(); i++){
                    if(checkBoxList.get(i).getPackageName().equals(checkedBoxPackageName)){
                        checkBoxList.get(i).setChecked(true);
                    }
                }
            }
        }

        this.mCheckBoxList = checkBoxList;
    }

    public List<MyCheckBox> getCheckBoxList(){
        return mCheckBoxList;
    }

    //recyclerview'da gösterilen cardviewların sınıfı
    public class CardViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private CardView cardView;
        private CheckBox checkBox;

        public CardViewHolder(View view){
            super(view);
            cardView = view.findViewById(R.id.cardView);
            checkBox = view.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int adapterPosition = getAdapterPosition();
            //işaretlenen veya işareti kaldırılan checkbox'ların durumu mCheckBoxList listesinde güncelleniyor
            mCheckBoxList.get(adapterPosition).setChecked(isChecked);
        }
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //ilgili cardview dizaynı recyclerview'da kullanılmak üzere inflate ediliyor
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_design,parent,false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        //recyclerview'daki gösterilen öğeler mCheckBoxList listesindeki verilere göre güncelleniyor
        holder.checkBox.setText(mCheckBoxList.get(position).getAppName());
        holder.checkBox.setChecked(mCheckBoxList.get(position).isChecked());
    }

    @Override
    public int getItemCount() {
        if(mCheckBoxList == null){
            return 0;
        }
        return mCheckBoxList.size();
    }
}
