package xymen.official;

import android.app.DownloadManager;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;


public class SelectionDrawerAdapter extends RecyclerView.Adapter<SelectionDrawerAdapter.SelectionDrawerViewHolder> {
    private DrawerLayout mDrawerLayout;
    LayoutInflater inflater;
    List<Information> data = Collections.emptyList();
    ConnectionDetector cd;
    WebView WV;
    WebView WV2;
    Context context;
    WebSettings webSettings;
    Resources res;
    ActionBar actionBar;
    FragmentManager manager;
    SwipeRefreshLayout SRL1;
    SwipeRefreshLayout SRL2;


    Boolean isPro;
//    private int previousPosition = 0;

    public SelectionDrawerAdapter(SwipeRefreshLayout SRL1, SwipeRefreshLayout SRL2, ActionBar actionBar, Context context, List<Information> data, WebView WV, WebView WV2, WebSettings webSettings, DrawerLayout mDrawerLayout, FragmentManager manager) {
        inflater = LayoutInflater.from(context);
        this.manager = manager;
        this.SRL1 = SRL1;
        this.SRL2 = SRL2;
        this.data = data;
        this.actionBar = actionBar;
        cd = new ConnectionDetector(context);
        this.WV = WV;
        this.WV2 = WV2;
        this.mDrawerLayout = mDrawerLayout;
        this.context = context;
        this.webSettings = webSettings;
        res = context.getResources();

        isPro = BuildConfig.APPLICATION_ID.equals("xymen.official");


    }

    @Override
    public SelectionDrawerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.list_selection, viewGroup, false);
        return new SelectionDrawerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectionDrawerViewHolder viewHolder, final int i) {


//        if (i > previousPosition) {
//            MyAnimationUtils.animate(viewHolder, true);
//        } else {
//            MyAnimationUtils.animate(viewHolder, false);
//        }
//        previousPosition = i;
        Information current = data.get(i);
        viewHolder.mTextView.setText(current.client);
        viewHolder.ripple.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInputDialog(i);
                return true;
            }
        });
        viewHolder.ripple.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     if (cd.isConnectingToInternet()) {
                                                         if (i == 0) {
//                            Log.i("UserAgent Before", webSettings.getUserAgentString());
//                            webSettings.setUserAgentString(res.getString(R.string.user_agent));
//                            Log.i("UserAgent After", webSettings.getUserAgentString());

                                                             saveToPreference(context, "TITLE", readFromPreference(context, i + "", false + ""));
                                                             if (actionBar != null)
                                                                 actionBar.setTitle(readFromPreference(context, i + "", false + ""));
                                                             if (SRL1.getVisibility() == View.VISIBLE)
                                                                 SRL1.setVisibility(View.GONE);
                                                             if (SRL2.getVisibility() == View.GONE)
                                                                 SRL2.setVisibility(View.VISIBLE);

                                                             // WV.loadUrl(res.getString(R.string.whatsappWebURL));
                                                             if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
                                                                 mDrawerLayout.closeDrawer(GravityCompat.END);
                                                         } else {
                                                             if (isPro) {
                                                                 int temp_i = i + 1;
                                                                 Intent mIntent;
                                                                 Log.i("CLIENT", "Client " + (i + 1) + " Activated.");
                                                                 String intent = "xymen.remotewhatsappproclient" + temp_i + ".MainActivity";
                                                                 mIntent = new Intent(intent);
                                                                 try {
                                                                     mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                     context.startActivity(mIntent);
                                                                 } catch (android.content.ActivityNotFoundException anfe) {
                                                                     Log.i("CLIENT", "Client " + temp_i + " Activity Not Found.");
                                                                     Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();
                                                                     String client_url = context.getString(R.string.client_server_path) + "v" + BuildConfig.VERSION_CODE + "/WebClient" + temp_i + ".apk";
                                                                     DownloadManager.Request request = new DownloadManager.Request(Uri.parse(client_url));
                                                                     request.setTitle("Client " + temp_i);
                                                                     // in order for this if to run, you must use the android 3.2 to compile your app
                                                                     if (Build.VERSION.SDK_INT >= 11) {
                                                                         request.allowScanningByMediaScanner();
                                                                         request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                                                     }
                                                                     request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "WebClient " + temp_i + ".apk");

                                                                     // get download service and enqueue file
                                                                     DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                                                     manager.enqueue(request);
                                                                 }
                                                             } else {
                                                                 Intent mIntent;
                                                                 try {
                                                                     mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=xymen.official"));
                                                                     mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                     context.startActivity(mIntent);
                                                                 } catch (android.content.ActivityNotFoundException anf) {
                                                                     mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=xymen.official"));
                                                                     mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                     context.startActivity(mIntent);
                                                                 }
                                                             }
//                                mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(client_url));
//                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                context.startActivity(mIntent);
//                                try {
//                                    mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=xymen.whatsappwebclient" + temp_i));
//                                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    context.startActivity(mIntent);
//                                } catch (android.content.ActivityNotFoundException anf) {
//                                    mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=xymen.whatsappwebclient" + temp_i));
//                                    String temp = context.getString(R.string.client_server_path) + "v" + BuildConfig.VERSION_CODE + "/WebClient" + temp_i+".apk";
//                                    mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(temp));
//                                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    context.startActivity(mIntent);
//
//                                }
                                                         }
                                                     } else
                                                         Toast.makeText(context, "Sorry, No Internet Connection :(", Toast.LENGTH_LONG).

                                                                 show();

                                                 }
                                             }

        );

    }

    private void showInputDialog(int position) {

        InputDialog inputDialog = InputDialog.newInstance(position);
        inputDialog.show(manager, "Rename_Client");

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SelectionDrawerViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        View ripple;

        public SelectionDrawerViewHolder(View itemView) {
            super(itemView);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (cd.isConnectingToInternet()) {
//                        if (getLayoutPosition() == 0) {
////                            Log.i("UserAgent Before", webSettings.getUserAgentString());
////                            webSettings.setUserAgentString(res.getString(R.string.user_agent));
////                            Log.i("UserAgent After", webSettings.getUserAgentString());
//
//                            saveToPreference(context, "TITLE", readFromPreference(context, getLayoutPosition() + "", false + ""));
//                            if (actionBar != null)
//                                actionBar.setTitle(readFromPreference(context, getLayoutPosition() + "", false + ""));
//                            if (SRL1.getVisibility() == View.VISIBLE)
//                                SRL1.setVisibility(View.GONE);
//                            if (SRL2.getVisibility() == View.GONE)
//                                SRL2.setVisibility(View.VISIBLE);
//
//                            // WV.loadUrl(res.getString(R.string.whatsappWebURL));
//                            if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
//                                mDrawerLayout.closeDrawer(GravityCompat.END);
//                        } else {
//                            int temp_i = getLayoutPosition() + 1;
//                            Intent mIntent;
//                            Log.i("CLIENT", "Client " + (getLayoutPosition() + 1) + " Activated.");
//                            String intent = "xymen.whatsappwebclient" + temp_i + ".MainActivity";
//                            mIntent = new Intent(intent);
//                            try {
//                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                context.startActivity(mIntent);
//                            } catch (android.content.ActivityNotFoundException anfe) {
//                                Log.i("CLIENT", "Client " + temp_i + " Activity Not Found.");
//                                Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();
//                                String client_url = context.getString(R.string.client_server_path) + "v" + BuildConfig.VERSION_CODE + "/WebClient" + temp_i + ".apk";
//                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(client_url));
//                                request.setTitle("Client " + temp_i);
//                                // in order for this if to run, you must use the android 3.2 to compile your app
//                                if (Build.VERSION.SDK_INT >= 11) {
//                                    request.allowScanningByMediaScanner();
//                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                                }
//                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "WebClient " + temp_i + ".apk");
//
//                                // get download service and enqueue file
//                                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                                manager.enqueue(request);
//
//
////                                mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(client_url));
////                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                context.startActivity(mIntent);
////                                try {
////                                    mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=xymen.whatsappwebclient" + temp_i));
////                                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                    context.startActivity(mIntent);
////                                } catch (android.content.ActivityNotFoundException anf) {
////                                    mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=xymen.whatsappwebclient" + temp_i));
////                                    String temp = context.getString(R.string.client_server_path) + "v" + BuildConfig.VERSION_CODE + "/WebClient" + temp_i+".apk";
////                                    mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(temp));
////                                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                    context.startActivity(mIntent);
////
////                                }
//                            }
//                        }
//                    } else
//                        Toast.makeText(context, "Sorry, No Internet Connection :(", Toast.LENGTH_LONG).show();
//
//                }
//            });
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    showInputDialog(getLayoutPosition());
//                    return true;
//                }
//            });
            mTextView = (TextView) itemView.findViewById(R.id.SelectionTextView);
            ripple = itemView.findViewById(R.id.ripple);

        }

    }

    public static void saveToPreference(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreference(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }
}
