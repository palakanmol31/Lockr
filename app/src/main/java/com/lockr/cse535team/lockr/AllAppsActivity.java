package com.lockr.cse535team.lockr;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by rkotwal2 on 11/2/2016.
 */

public class AllAppsActivity extends Activity {
        private PackageManager packageManager = null;
        private List<ApplicationInfo> applist = null;
        private ApplicationAdapter listadaptor = null;

        public AllAppsActivity() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.list_all);
            packageManager = getPackageManager();
            new LoadApplications().execute();
        }

        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.my_options_menu, menu);

            return true;
        }

        public boolean onOptionsItemSelected(MenuItem item) {
            boolean result = true;

            switch (item.getItemId()) {
                case R.id.nav_all_apps: {
                    displayAboutDialog();
                    break;
                }
                default: {
                    result = super.onOptionsItemSelected(item);

                    break;
                }
            }

            return result;
        }

        private void displayAboutDialog() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("App Locker-Group27");
            builder.setMessage("You are currently seeing all the installed apps on this phone");

                /*builder.setPositiveButton("Know More", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://stacktips.com"));
                        startActivity(browserIntent);
                dialog.cancel();
            }
        });*/
            builder.setNegativeButton("No Thanks!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//
//        ApplicationInfo app = applist.get(position);
//        try {
//            Intent intent = packageManager
//                    .getLaunchIntentForPackage(app.packageName);
//
//            if (null != intent) {
//                startActivity(intent);
//            }
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(AllAppsActivity.this, e.getMessage(),
//                    Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            Toast.makeText(AllAppsActivity.this, e.getMessage(),
//                    Toast.LENGTH_LONG).show();
//        }
//    }

        private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
            ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
            for (ApplicationInfo info : list) {
                try {
                    if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                        applist.add(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return applist;
        }

        private class LoadApplications extends AsyncTask<Void, Void, Void> {
            private ProgressDialog progress = null;

            @Override
            protected Void doInBackground(Void... params) {
                applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
                listadaptor = new ApplicationAdapter(AllAppsActivity.this,
                        R.layout.snippet_list_row, applist);

                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

//        @Override
//        protected void onPostExecute(Void result) {
//            setListAdapter(listadaptor);
//            progress.dismiss();
//            super.onPostExecute(result);
//        }

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(AllAppsActivity.this, null,
                        "Loading application info...");
                super.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }
        }
}
