package com.sahdeepsingh.Bop.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahdeepsingh.Bop.Activities.SettingActivity;
import com.sahdeepsingh.Bop.CustomViews.ColorView;
import com.sahdeepsingh.Bop.CustomViews.Theme;
import com.sahdeepsingh.Bop.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.MyViewHolder> {

    private List<Theme> themeList;

    public ThemeAdapter(List<Theme> themeList) {
        this.themeList = themeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_theme, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Theme theme = themeList.get(position);
        holder.themeView.addColors(theme);

        if (SettingActivity.selectedTheme == position) {
            holder.themeView.setActivated(true);
        } else {
            holder.themeView.setActivated(false);
        }

    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ColorView themeView;

        public MyViewHolder(View view) {
            super(view);
            themeView = view.findViewById(R.id.themeView);
            themeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingActivity.selectedTheme = getAdapterPosition();
                    themeView.setActivated(true);
                    ThemeAdapter.this.notifyDataSetChanged();
                }
            });
        }

    }
}
