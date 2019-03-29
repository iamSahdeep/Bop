package com.sahdeepsingh.Bop.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.sahdeepsingh.Bop.Adapters.ThemeAdapter;
import com.sahdeepsingh.Bop.BopUtils.ExtraUtils;
import com.sahdeepsingh.Bop.BopUtils.ThemeUtil;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.settings.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SettingActivity extends BaseActivity {

    public static List<Theme> mThemeList = new ArrayList<>();
    public static int selectedTheme = 0;
    LinearLayout mode, theme;
    MaterialCheckBox pauseHeadphoneUnplugged, resumeHeadphonePlugged, headphoneControl, saveRecent, savePlaylist, saveCount;
    LinearLayout llBottomSheet;
    private RecyclerView mRecyclerView;
    private ThemeAdapter mAdapter;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ExtraUtils.getThemedIcon(this, getDrawable(R.drawable.ic_backarrow)));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mode = findViewById(R.id.settingsMode);
        theme = findViewById(R.id.settingsTheme);
        pauseHeadphoneUnplugged = findViewById(R.id.pauseHeadphoneUnplugged);
        resumeHeadphonePlugged = findViewById(R.id.resumeHeadphonePlugged);
        headphoneControl = findViewById(R.id.headphoneControl);
        saveRecent = findViewById(R.id.saveRecent);
        saveCount = findViewById(R.id.saveCount);
        savePlaylist = findViewById(R.id.savePlaylist);

        llBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        setupCheckBoxes();
        setListeners();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.resetSetting:
                resetDialog();
                return true;

        }
        return false;

    }

    private void resetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Reset App Settings");
        builder.setMessage("It will reset In-App Settings. Also Recent Songs, Counts and Last Played Playlist will be Deleted!");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Main.settings.reset();
                Toast.makeText(SettingActivity.this, "Reset Complete", Toast.LENGTH_SHORT).show();
                recreate();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setupCheckBoxes() {
        pauseHeadphoneUnplugged.setChecked(Main.settings.get("pauseHeadphoneUnplugged", true));
        resumeHeadphonePlugged.setChecked(Main.settings.get("resumeHeadphonePlugged", true));
        headphoneControl.setChecked(Main.settings.get("headphoneControl", true));
        saveRecent.setChecked(Main.settings.get("saveRecent", true));
        saveCount.setChecked(Main.settings.get("saveCount", true));
        savePlaylist.setChecked(Main.settings.get("savePlaylist", true));
    }

    private void setListeners() {

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModeDialog();
            }
        });

        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTheme();
            }
        });

        pauseHeadphoneUnplugged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Main.settings.set("pauseHeadphoneUnplugged", true);
                } else {
                    Main.settings.set("pauseHeadphoneUnplugged", false);
                }
            }
        });

        resumeHeadphonePlugged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Main.settings.set("resumeHeadphonePlugged", true);
                } else {
                    Main.settings.set("resumeHeadphonePlugged", false);
                }

            }
        });

        headphoneControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Main.settings.set("headphoneControl", true);
                } else {
                    Main.settings.set("headphoneControl", false);
                }

            }
        });

        saveRecent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Main.settings.set("saveRecent", true);
                } else {
                    Main.settings.set("saveRecent", false);
                }

            }
        });

        saveCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Main.settings.set("saveCount", true);
                } else {
                    Main.settings.set("saveCount", false);
                }
            }
        });

        savePlaylist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Main.settings.set("savePlaylist", true);
                } else {
                    Main.settings.set("savePlaylist", false);
                }

            }
        });

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void changeTheme() {
        selectedTheme = ThemeUtil.getCurrentActiveTheme();

        openBottomSheet();
        prepareThemeData();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void openBottomSheet() {

        mRecyclerView = findViewById(R.id.recyclerViewBottomSheet);

        mAdapter = new ThemeAdapter(mThemeList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void prepareThemeData() {
        mThemeList.clear();
        mThemeList.addAll(ThemeUtil.getThemeList());
        mAdapter.notifyDataSetChanged();
    }

    private void showModeDialog() {
        CharSequence[] values = {"Day Mode", "Nigh Mode"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Set Day/Night Mode");
        int checkeditem = Main.settings.get("modes", "Day").equals("Day") ? 0 : 1;
        int[] newcheckeditem = {checkeditem};
        builder.setSingleChoiceItems(values, checkeditem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        switch (item) {
                            case 0:
                                newcheckeditem[0] = 0;
                                break;
                            case 1:
                                newcheckeditem[0] = 1;
                                break;

                        }
                    }
                }
        );

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkeditem == newcheckeditem[0]) {
                    dialog.dismiss();
                } else {
                    if (newcheckeditem[0] == 1) {
                        Main.settings.set("modes", "Night");
                    } else {
                        Main.settings.set("modes", "Day");
                    }
                    Toast.makeText(SettingActivity.this, "Changes Made", Toast.LENGTH_SHORT).show();
                    recreate();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settingsmenu, menu);
        return true;
    }

    public void sendFeedback(View view) {
        ExtraUtils.sendFeedback(SettingActivity.this);
    }

    public void gotoFAQ(View view) {
        ExtraUtils.openCustomTabs(SettingActivity.this, "https://github.com/iamSahdeep/Bop/blob/master/FAQs.md");
    }

    public void gotoPP(View view) {
        ExtraUtils.openCustomTabs(SettingActivity.this, "https://github.com/iamSahdeep/Bop/blob/master/privacy_policy.md");
    }

    public void gotoGithub(View view) {
        ExtraUtils.openCustomTabs(SettingActivity.this, "https://github.com/iamSahdeep/Bop");
    }

    public void cancelTheme(View view) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void saveTheme(View view) {
        Main.settings.set("themes", getResources().getStringArray(R.array.themes_values)[selectedTheme]);
        Toast.makeText(SettingActivity.this, "Changes Made", Toast.LENGTH_SHORT).show();
        recreate();
    }
}
