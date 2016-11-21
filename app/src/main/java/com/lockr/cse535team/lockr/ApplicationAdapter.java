package com.lockr.cse535team.lockr;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.lockr.cse535team.lockr.Singleton.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by rkotwal2 on 11/2/2016.
 */

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
    private static final String TAG = ApplicationAdapter.class.getSimpleName();
    private List<ApplicationInfo> appsList = null;
    private ArrayList<String> apps = null;
    private Context context;
    private PackageManager packageManager;
    static TextView packageName;
    MyApplication myApplication;

    public ApplicationAdapter(Context context, int textViewResourceId,
                              List<ApplicationInfo> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        this.appsList = appsList;
        packageManager = context.getPackageManager();
        myApplication = MyApplication.getInstance();
        apps = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.snippet_list_row, null);
        }

        final ApplicationInfo applicationInfo = appsList.get(position);
        if (null != applicationInfo) {
            TextView appName = (TextView) view.findViewById(R.id.app_name);
            packageName = (TextView) view.findViewById(R.id.app_paackage);
            ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);

            appName.setText(applicationInfo.loadLabel(packageManager));
            packageName.setText(applicationInfo.packageName);
            iconview.setImageDrawable(applicationInfo.loadIcon(packageManager));
        }
        // final TableRow Tr=(TableRow) view.findViewById(R.id.nav_allApps);
        final ToggleButton toggleButton = (ToggleButton) view.findViewById(R.id.togglebutton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Log.d(TAG, "onClick: Package selected:" + applicationInfo.packageName);
                    apps.add(applicationInfo.packageName.toString());
                }else{
                    Log.d(TAG, "onClick: Package name is:" + applicationInfo.packageName);
                    apps.remove(applicationInfo.packageName.toString());
                }
                myApplication.saveToPreferences(getContext(),"Locked",apps);
                ArrayList<String> x = myApplication.readFromPreferences(getContext(),"Locked","NULL");
            }
        });
        return view;
    }


};