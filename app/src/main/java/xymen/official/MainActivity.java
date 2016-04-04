package xymen.official;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.vending.licensing.AESObfuscator;
//import com.google.android.vending.licensing.AESObfuscator;
//import com.google.android.vending.licensing.LicenseChecker;
//import com.google.android.vending.licensing.LicenseCheckerCallback;
//import com.google.android.vending.licensing.Policy;
//import com.google.android.vending.licensing.ServerManagedPolicy;
//import com.google.android.vending.licensing.util.Base64DecoderException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

public class MainActivity extends AppCompatActivity implements InputDialog.Communicator, SwipeRefreshLayout.OnRefreshListener {
    Boolean exit = false;
    SwipeRefreshLayout SRL1;
    SwipeRefreshLayout SRL2;
    WebView WV;
    WebView WV2;
    Resources res;
    WebSettings webSettings;
    WebSettings webSettings2;
    ListView navigationDrawer;
    RecyclerView selectionDrawer;
    DrawerLayout mDrawerLayout;
    View navigationDrawerView;
    View selectionDrawerView;
    ActionBarDrawerToggle mDrawerToggle;
    ListAdapter navigationDrawerAdapter;
    int lastPosition;
    SelectionDrawerAdapter selectionAdapter;
    Toolbar mToolbar;
    //    ProgressBar progressBar;
    Handler handler;
    ConnectionDetector connectionDetector;
    int versionIncluded, versionDownloaded;

    //For LVL

    Runnable r;
    LicenseCheckerCallback mLicenseCheckerCallback;
    LicenseChecker mChecker;
    private static final byte[] SALT = new byte[]{
            -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95,
            -45, 77, -117, -36, -113, -11, 32, -64, 89
    };
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlIfTUJ0nyNtMXUNbeAY2RcGJmZ/gdEreDauBrVrYLw77FW1Dvv7bLDgeWopNQxSoqycp7mL7VMyCzbj1Ti3M0WaCYsVL0yse5rqeWVaxf15iTnU/ScKxPeEwpgMC6aeE2J3JZBg6UPfQLPZkpm1wozDJoFXnPtG5NPLN8D250TiTOFoczEP2FprJbOe1tQlFig0QehGJDT571Zt3gpH+/tufpbcadenUwfQgtirOEovPB4sgQIpnokkBWaNuJPwD9YcLob/CV1wt4aRwKNq6ZTpHYbRaTbvk5iLEEA2uUA+Mlhzhvc5wJrzsOMJv4KO7V8LkEnU+m6QMNPmW362p2QIDAQAB";
    String deviceId;
    long checkLicenseDelay = 90 * 1000;
    Boolean firstTimeLicense = true;
    //For LVL


    //For Ads
    AdView mAdView;
    private boolean UserClickedAd = false;
    private int counter = 0;
    private RelativeLayout adContainer;
    private RelativeLayout.LayoutParams params;
    //    int show_interstitial_time=60*1000;
    RelativeLayout.LayoutParams paramsForIV;
    RelativeLayout.LayoutParams paramsForIV2;
    private InterstitialAd mInterstitialAd;
    int show_interstitial_time = 5 * 60 * 1000;
    ImageView IV;
    ImageView IV2;
    Runnable show_interstitial;
    //For Ads

    Boolean isPro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isPro = (BuildConfig.APPLICATION_ID.equals("xymen.official"));
        //Pro Version
        if (isPro) {
            if (readFromPreference(getApplicationContext(), "0", false + "").equals("false"))
                saveToPreference(this, "0", "Client 1");
        }else
                saveToPreference(this, "0", "Client 1");
        //Pro Version
        handler = new Handler();
        res = getResources();
        if (readFromPreference(getApplicationContext(), "filesVersionSaved", false + "").equals("false"))
            saveToPreference(this, "filesVersionSaved", String.valueOf(res.getInteger(R.integer.files_version_included)));


        MyAsyncTask myAsyncTask = new MyAsyncTask(this);
        myAsyncTask.execute(getUrlArray());


        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT > 13)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        connectionDetector = new ConnectionDetector(this);

        SRL1 = (SwipeRefreshLayout) findViewById(R.id.SRL1);
        SRL2 = (SwipeRefreshLayout) findViewById(R.id.SRL2);
        WV = (WebView) findViewById(R.id.webView);
        WV2 = (WebView) findViewById(R.id.webView_for_whatsapp);
        WV.setBackgroundColor(Color.TRANSPARENT);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webSettings = WV.getSettings();
        webSettings2 = WV2.getSettings();
        navigationDrawerView = findViewById(R.id.navigation_drawer);
        navigationDrawer = (ListView) navigationDrawerView;
        selectionDrawerView = findViewById(R.id.selection_drawer);
        selectionDrawer = (RecyclerView) selectionDrawerView;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerLayout);


        setupSwipeRefreshLayout(SRL1);
        setupSwipeRefreshLayout(SRL2);
        setupDrawerLayout();
        setupWebSettings(webSettings);
        setupWebSettings(webSettings2);
        setupWebView(WV);
        setupWebView(WV2);
        setupNavigationDrawer();
        if (!isPro) {
            setupBannerAd();
            setupInterstitialAd();
        }

        versionIncluded = res.getInteger(R.integer.files_version_included);
        versionDownloaded = Integer.parseInt(readFromPreference(this, "filesVersionSaved", false + ""));

        String URL;
        if (versionDownloaded > versionIncluded)
            URL = "file://" + getFilesDir() + "/" + res.getString(R.string.welcome_file_name);
        else
            URL = res.getString(R.string.welcome_url_when_no_internet_present);
//        if (connectionDetector.isConnectingToInternet())
//            URL = res.getString(R.string.server_path) + res.getString(R.string.app_name) + "/v" + BuildConfig.VERSION_CODE + "/" + res.getString(R.string.welcome_file_name_when_internet_present);
//        else

        Log.i("isInterPrsent", "Value of isInternetPresent is " + connectionDetector.isConnectingToInternet() + ". Now loading url: " + URL);
        saveToPreference(getApplicationContext(), "TITLE", res.getString(R.string.app_name));
        WV.loadUrl(URL);
        WV2.loadUrl(getString(R.string.whatsappWebURL));
        selectionAdapter = new SelectionDrawerAdapter(SRL1, SRL2, getSupportActionBar(), getApplicationContext(), getData(), WV, WV2, webSettings, mDrawerLayout, getFragmentManager());
        SlideInRightAnimator LA = new SlideInRightAnimator(new AccelerateInterpolator());
        LA.setAddDuration(300);
        selectionDrawer.setItemAnimator(LA);
        selectionDrawer.setAdapter(selectionAdapter);
        selectionDrawer.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        SRL2.setEnabled(false);

        //Pro Version
        if (isPro) {
            //For LVL
            mLicenseCheckerCallback = new MyLicenseCheckerCallback();

            deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.i("LVL", "Device ID is " + deviceId);
            mChecker = new LicenseChecker(
                    this, new ServerManagedPolicy(this,
                    new AESObfuscator(SALT, getPackageName(), deviceId)),
                    BASE64_PUBLIC_KEY  // Your public licensing key.
            );


            r = new Runnable() {
                @Override
                public void run() {
                    checkLicense();
                }
            };
            handler.postDelayed(r, checkLicenseDelay);
        } else {
            //FreeVersion
            show_interstitial = new Runnable() {
                @Override
                public void run() {
                    showAd();
                    handler.postDelayed(show_interstitial, show_interstitial_time);

                }
            };
            handler.postDelayed(show_interstitial, show_interstitial_time);
        }
        //Free Version
        //For LVL
        //Pro Version

    }

    @Override
    protected void onStop() {
        if (!isPro) {
            Log.i("OnStop", "OnStop Executed");
            if (UserClickedAd) {
                UserClickedAd = false;
            } else {
                Log.i("UserClickedAd", "User has not Clicked the ad because UserClickedAd is " + UserClickedAd + "");
                counter++;
            }
            Log.i("Counter", "Counter is " + counter);
            super.onStop();
            handler.removeCallbacks(show_interstitial);
        }
        super.onStop();
    }

    //Pro Version
    private String[] getUrlArray() {
        int string_size = 4;
        if (isPro)
            string_size++;
        String urls[] = new String[string_size];
        for (int i = 0; i < string_size; ++i) {
            urls[i] = res.getString(R.string.server_path) + res.getString(R.string.app_name) + "/v" + BuildConfig.VERSION_CODE + "/";
            switch (i) {
                case 0:
                    urls[i] += res.getString(R.string.welcome_file_name);
                    break;
                case 1:
                    urls[i] += res.getString(R.string.how_to_use_file_name);
                    break;
                case 2:
                    urls[i] += res.getString(R.string.faq_file_name);
                    break;
                case 3:
                    urls[i] += res.getString(R.string.about_file_name);
                    break;
                case 4:
                    urls[i] += getString(R.string.pro_file_name);
                    break;
                default:
                    break;
            }
        }
        for (int j = 0; j < string_size; ++j) {
            urls[j] = urls[j].replaceAll(" ", "%20");
        }
        return urls;
    }
    //Pro Verison

    private void setupSwipeRefreshLayout(SwipeRefreshLayout SRL) {
        SRL.setOnRefreshListener(this);
        SRL.setColorSchemeResources(R.color.SRL_1, R.color.SRL_2, R.color.SRL_3, R.color.SRL_4);
    }

    private void setupNavigationDrawer() {
        //Pro Verison
        if (isPro)
            navigationDrawerAdapter = new ArrayAdapter<String>(this, R.layout.list_navigation, getResources().getStringArray(R.array.navigation_drawer_list_pro));
        else
            navigationDrawerAdapter = new ArrayAdapter<String>(this, R.layout.list_navigation, getResources().getStringArray(R.array.navigation_drawer_list_free));
        //Pro Version
        navigationDrawer.setAdapter(navigationDrawerAdapter);
        navigationDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Item = (String) navigationDrawerAdapter.getItem(position);
                switch (Item) {
                    case "Welcome":
                        String welcomeURL;
                        if (versionDownloaded > versionIncluded)
                            welcomeURL = "file://" + getFilesDir() + "/" + res.getString(R.string.welcome_file_name);
                        else
                            welcomeURL = res.getString(R.string.welcome_url_when_no_internet_present);
                        saveToPreference(getApplicationContext(), "TITLE", getString(R.string.app_name));
                        WV.loadUrl(welcomeURL);
                        MakeVisible(SRL1, SRL2);

                        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case "How to Use?":
                        String howToUseURL;
                        if (versionDownloaded > versionIncluded)
                            howToUseURL = "file://" + getFilesDir() + "/" + res.getString(R.string.how_to_use_file_name);
                        else
                            howToUseURL = res.getString(R.string.how_to_use_url_when_no_intenet);

                        saveToPreference(getApplicationContext(), "TITLE", "How To Use");

                        WV.loadUrl(howToUseURL);
                        MakeVisible(SRL1, SRL2);
                        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case "Hide/Show Action Bar":

                        ActionBar ab = getSupportActionBar();
                        if (ab.isShowing())
                            ab.hide();
                        else
                            ab.show();
                        break;
                    case "Hide/Show Status Bar":
                        boolean fullScreen;
                        fullScreen = (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
                        if (fullScreen) {
                            Log.i("FULLSCREEN", "App is in Fullscreen");
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        } else {
                            Log.i("FULLSCREEN", "App is not in Fullscreen");
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        }
                        break;


                    case "FAQ":
                        String FAQ;
                        if (versionDownloaded > versionIncluded)
                            FAQ = "file://" + getFilesDir() + "/" + res.getString(R.string.faq_file_name);
                        else
                            FAQ = res.getString(R.string.faq_url_when_no_intenet);

                        saveToPreference(getApplicationContext(), "TITLE", "FAQ");
                        WV.loadUrl(FAQ);
                        MakeVisible(SRL1, SRL2);

                        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case "Share This App":
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.setAction(Intent.ACTION_SEND);
                        String temp = getString(R.string.app_name) + " " + getString(R.string.TextToShare) + BuildConfig.APPLICATION_ID;
                        Log.i("Share", "Share This App is clicked and sharing text " + temp);
                        i.putExtra(Intent.EXTRA_TEXT, temp);
                        startActivity(Intent.createChooser(i, "Share using"));
                        break;
                    case "Pro Version":
                        String pro;
                        if (versionDownloaded > versionIncluded)
                            pro = "file://" + getFilesDir() + "/" + res.getString(R.string.pro_file_name);
                        else
                            pro = res.getString(R.string.pro_url_when_no_internet_present);
                        saveToPreference(getApplicationContext(), "TITLE", "Pro Version");
                        WV.loadUrl(pro);
                        MakeVisible(SRL1, SRL2);
//                        saveToPreference(getApplicationContext(), "HTML", pro);
                        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case "More Apps":
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.FBPage))));
                        /*try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://" + getString(R.string.MoreApps))));
                        } catch (ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/" + getString(R.string.MoreApps))));
                        }*/
                        break;
                    case "FeedBack":
                        Intent LaunchFeedBack = new Intent(getApplicationContext(), FeedBack.class);
                        startActivity(LaunchFeedBack);
                        break;
                    case "Rate This App":
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
                        } catch (ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                        }
                        break;
                    case "About Us":
                        String About;
                        if (versionDownloaded > versionIncluded)
                            About = "file://" + getFilesDir() + "/" + res.getString(R.string.about_file_name);
                        else
                            About = res.getString(R.string.about_url_when_no_intenet);
                        saveToPreference(getApplicationContext(), "TITLE", "About Us");
                        WV.loadUrl(About);
                        MakeVisible(SRL1, SRL2);

                        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
            }
        });

    }

    private void setupDrawerLayout() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                if (drawerView == navigationDrawerView) {
                    if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
                        mDrawerLayout.closeDrawer(GravityCompat.END);
                    super.onDrawerOpened(drawerView);
                }
                if (drawerView == selectionDrawerView) {
                    if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (drawerView == navigationDrawerView)
                    super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (drawerView == navigationDrawerView)
                    super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    void MakeVisible(SwipeRefreshLayout SRL1, SwipeRefreshLayout SRL2) {
        if (SRL1.getVisibility() == View.GONE)
            SRL1.setVisibility(View.VISIBLE);
        if (SRL2.getVisibility() == View.VISIBLE)
            SRL2.setVisibility(View.GONE);
    }

    SwipeRefreshLayout whoVisible(SwipeRefreshLayout srl1, SwipeRefreshLayout srl2) {
        if (srl1.getVisibility() == View.VISIBLE)
            return srl1;
        else return srl2;
    }

    private void setupWebSettings(WebSettings WS) {
        WS.setJavaScriptEnabled(true);
        WS.setDomStorageEnabled(true);
        WS.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT > 10) {
            WS.setDisplayZoomControls(false);

        }
    }

    private void setupWebView(final WebView webView) {
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("Override", "shouldOverrideUrlLoading is called with url: " + url);
                if (url.equals("https://play.google.com/store/apps/details?id=xymen.official")) {
                    Intent mIntent;
                    try {
                        mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=xymen.official"));
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mIntent);
                    } catch (android.content.ActivityNotFoundException anf) {
                        mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=xymen.official"));
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mIntent);
                    }
                    return true;
                } else if (url.equals("http://www.google.co.in/")) {
                    webView.loadUrl(readFromPreference(getApplicationContext(), "HTML", getString(R.string.welcome_url_when_no_internet_present)));
                    return true;
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.i("Error", "onReceivedError is called");
                webView.loadUrl(res.getString(R.string.no_internet_url));
                Toast.makeText(getApplicationContext(), "Please check your Internet Connection.", Toast.LENGTH_SHORT);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i("onPageStarted", "onPageStarted is called.Loading URL: " + url);
                super.onPageStarted(view, url, favicon);
                whoVisible(SRL1, SRL2).setRefreshing(true);
//                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("onPageFinished", "onPageFinished is called");
                Log.i("onPageFinished", "URL is " + url);

                whoVisible(SRL1, SRL2).setRefreshing(false);
//                progressBar.setVisibility(View.GONE);

                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(readFromPreference(getApplicationContext(), "TITLE", false + ""));


                if (url.equals("https://web.whatsapp.com/")) {
                    Log.i("onPageFinished", "WhatsApp is Opened. Setting Zoom...");
                    webView.zoomIn();
                    webView.zoomIn();
                }
            }
        });
        if (webView.getId() == R.id.webView_for_whatsapp) {
            Log.i("UserAgent Before", webView.getSettings().getUserAgentString());
            webView.getSettings().setUserAgentString(res.getString(R.string.user_agent));
            Log.i("UserAgent After", webView.getSettings().getUserAgentString());
            if (res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.i("ORIENTATION", "Orientation is Landscape");
                webView.setInitialScale(res.getInteger(R.integer.landscape_scale));
            } else {
                Log.i("ORIENTATION", "Orientation is Portrait");
                webView.setInitialScale(res.getInteger(R.integer.portrait_scale));
            }
            if (isPro) {
                webView.setDownloadListener(new DownloadListener() {
                    @Override
                    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                    File FilesDir = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));

                        Log.i("Download URL: ", url);
                        File FilesDir = new File("/" + getString(R.string.app_name));
                        if (!FilesDir.exists())
                            FilesDir.mkdir();
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        // in order for this if to run, you must use the android 3.2 to compile your app
                        if (Build.VERSION.SDK_INT >= 11) {
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        }
                        request.setDestinationInExternalPublicDir(FilesDir.toString(), Uri.parse(url).getLastPathSegment());
//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Uri.parse(url).getLastPathSegment());
                        Log.i("Downloaded File Path: ", FilesDir.toString());
                        Log.i("Downloaded File Name: ", Uri.parse(url).getLastPathSegment());
                        // get download service and enqueue file
                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                    }
                });
            }

        }
    }

    public List<Information> getData() {


        List<Information> data = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            Information item = new Information();
            item.client = readFromPreference(this, String.valueOf(i), false + "");
            if (item.client.equals("false")) {
                Log.i("LIST", "Client " + (i + 1) + " equals false so stopped adding data.");
                break;
            }
            data.add(item);
        }
        return data;
    }


    public void addNew(View view) {

        lastPosition = selectionAdapter.getItemCount() + 1;
        Information itemToBeAdded = new Information();
        itemToBeAdded.client = "Client " + lastPosition;
        saveToPreference(getApplicationContext(), String.valueOf(selectionAdapter.getItemCount()), itemToBeAdded.client);
        Toast.makeText(getApplicationContext(), itemToBeAdded.client + " Added", Toast.LENGTH_SHORT).show();
        Log.i("Item Addition", "Add New button is clicked and adding item " + itemToBeAdded.client);
        selectionAdapter.data.add(itemToBeAdded);
        selectionAdapter.notifyItemInserted(selectionAdapter.getItemCount());

    }

    public void delete(View view) {
        int remove = selectionAdapter.getItemCount() - 1;
        Log.i("Item Deleted", "Client " + (remove + 1) + " deleted.");
        if (remove != 0) {
            selectionAdapter.data.remove(remove);
            Toast.makeText(getApplicationContext(), readFromPreference(getApplicationContext(), String.valueOf(remove), false + "") + " Deleted", Toast.LENGTH_SHORT).show();
            saveToPreference(getApplicationContext(), String.valueOf(remove), "false");
            selectionAdapter.notifyDataSetChanged();
        } else
            Toast.makeText(getApplicationContext(), "Cannot delete the last client.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void sendString(String message, int position) {
        if (isPro) {
            //Pro Version
            Information itemToBeAdded = new Information();
            itemToBeAdded.client = message;
            if (message.equals(""))
                Toast.makeText(getApplicationContext(), "Cannot rename to a blank. Please enter some text.", Toast.LENGTH_SHORT).show();
            else {
                if (message.equals("false"))
                    Log.i("InputDialog", "User has pressed cancel");
                else {
                    saveToPreference(getApplicationContext(), String.valueOf(position), itemToBeAdded.client);
                    Toast.makeText(getApplicationContext(), "Client " + (position + 1) + " renamed to " + itemToBeAdded.client, Toast.LENGTH_SHORT).show();
                    Log.i("Item Renamed", "Client " + (position + 1) + " renamed to " + itemToBeAdded.client);
                    selectionAdapter.data.remove(position);
                    selectionAdapter.data.add(position, itemToBeAdded);
                    selectionAdapter.notifyDataSetChanged();
                }
            }
            //Pro Version
        } else
            Toast.makeText(this, "Cannot rename in Free Version.", Toast.LENGTH_LONG);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("onConfig", "onConfiguration is called.");
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("ORIENTATION", "Orientation is changed to Landscape");
            WV2.setInitialScale(res.getInteger(R.integer.landscape_scale));
        } else {
            Log.i("ORIENTATION", "Orientation is changed to Portrait");
            WV2.setInitialScale(res.getInteger(R.integer.portrait_scale));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_select_chapter:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                else
                    mDrawerLayout.openDrawer(GravityCompat.END);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START) || mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                mDrawerLayout.closeDrawer(GravityCompat.START);
            else if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
                mDrawerLayout.closeDrawer(GravityCompat.END);
        } else
            mDrawerLayout.openDrawer(GravityCompat.START);
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }


    @Override
    public void onRefresh() {
        if (SRL1.getVisibility() == View.VISIBLE)
            WV.reload();
        else if (SRL2.getVisibility() == View.VISIBLE)
            new AlertDialog.Builder(this)
                    .setTitle("Refresh")
                    .setMessage("Are you sure you want to refresh the page?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            WV2.reload();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            SRL2.setRefreshing(false);
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            SRL2.setRefreshing(false);
                        }
                    })
                    .show();

    }

    //Pro Verison
    //LVL
    private void checkLicense() {
        Log.i("LVL", "Check License is called");
        try {
            mChecker.checkAccess(mLicenseCheckerCallback);
        } catch (Base64DecoderException e) {
            e.printStackTrace();
        }
    }

    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {

        @Override
        public void allow(int reason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // Should allow user access.

            Log.i("LVL", "Allow - Reason is " + reason);
            saveToPreference(getApplicationContext(), "LVL_Counter", "0");
        }

        @Override
        public void dontAllow(int reason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            Log.i("LVL", "Dont Allow - Reason is " + reason);
            if (firstTimeLicense) {
                Log.i("LVL", "First Time License Checked Result " + reason);
                handler.postDelayed(r, checkLicenseDelay);
                firstTimeLicense = false;
            } else {
                switch (reason) {
                    case Policy.NOT_LICENSED:
                        Log.i("LVL", "Policy says Not Licensed");
                        Toast.makeText(getApplicationContext(), "Please purchase the app.", Toast.LENGTH_LONG).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        }, 4000);

                        break;
                    case 291:
                        Log.i("LVL", "Policy says Error Contacting Server");

                        int lvl_counter = Integer.parseInt(readFromPreference(getApplicationContext(), "LVL_Counter", "0"));
                        lvl_counter++;
                        saveToPreference(getApplicationContext(), "LVL_Counter", String.valueOf(lvl_counter));
                        if (lvl_counter > 5) {
                            Log.i("LVL", "Exiting the app because can't verify license and lvl_counter is " + lvl_counter);
                            Toast.makeText(getApplicationContext(), "Can't verify your license :(", Toast.LENGTH_LONG).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            }, 4000);
                        } else {
                            handler.postDelayed(r, checkLicenseDelay);
                            Log.i("LVL", "lvl_counter after increment is " + lvl_counter);
                            Toast.makeText(getApplicationContext(), "License Verification Failed", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        }

        @Override
        public void applicationError(int errorCode) {
            Log.i("LVL", "ApplicationError - Reason is " + errorCode);

            switch (errorCode) {
                case 3:
                    Log.i("LVL", "Policy says Error Not Market Managed ");
                    Toast.makeText(getApplicationContext(), "Application is not available on PLAY STORE yet :(", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    }, 4000);
                    break;
                case 1:
                    Log.i("LVL", "Policy says Invalid Package Name");
                    break;
                case 2:
                    Log.i("LVL", "Policy says Error Over Quota ");
                    break;

            }

        }
    }
    //LVL
    //Pro Version


    public class MyAsyncTask extends AsyncTask<String, Void, String> {
        Context context;
        Resources res;

        int versionAvailable;

        int versionSaved;

        public MyAsyncTask(Context context) {
            this.context = context;
            res = context.getResources();
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            HttpURLConnection connection = null;

            byte buffer[] = new byte[4096];

            Log.i("Async", "doInBackground Started");


            versionSaved = Integer.parseInt(readFromPreference(context, "filesVersionSaved", false + ""));
            versionAvailable = versionSaved;
            Log.i("Async", "Before Checking: Version Saved: " + versionSaved + ",Version Available: " + versionAvailable);
            try {
                String version = res.getString(R.string.server_path) + res.getString(R.string.app_name) + "/v" + BuildConfig.VERSION_CODE + "/" + res.getString(R.string.version_available_file_name);
                version = version.replaceAll(" ", "%20");
                url = new URL(version);

                BufferedReader in = new BufferedReader((new InputStreamReader(url.openStream())));
                versionAvailable = Character.getNumericValue(in.readLine().charAt(0));
                Log.i("Async", "Version Available on Server is " + versionAvailable);
            } catch (Exception e) {
                return e.toString();
            }
            if (versionAvailable > versionSaved) {
                Log.i("Async", "Newer version is available. Downloading...");
                for (int i = 0; i < 4; ++i) {
                    try {
                        url = new URL(params[i]);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            return "Server returned HTTP " + connection.getResponseCode()
                                    + " " + connection.getResponseMessage();
                        }
                        inputStream = connection.getInputStream();

                        fileOutputStream = new FileOutputStream(context.getFilesDir() + "/" + Uri.parse(params[i]).getLastPathSegment());

                        int read;
                        while ((read = inputStream.read(buffer)) != -1) {
                            // allow canceling with back button
                            if (isCancelled()) {
                                inputStream.close();
                                return null;
                            }
                            fileOutputStream.write(buffer, 0, read);
                        }
                    } catch (Exception e) {
                        return e.toString();
                    } finally {
                        try {
                            if (fileOutputStream != null)
                                fileOutputStream.close();
                            if (inputStream != null)
                                inputStream.close();
                        } catch (IOException ignored) {
                        }

                        if (connection != null)
                            connection.disconnect();
                    }
                }
                return null;
            } else
                return "Latest Version is already saved";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null)
                Log.i("Async", "Download error: " + result);
            else {
                Log.i("Async", "Files downloaded");
                versionDownloaded = versionAvailable;
                saveToPreference(context, "filesVersionSaved", String.valueOf(versionAvailable));
            }
        }
    }

    //Pro Version
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPro) {
            //LVL
            mChecker.onDestroy();
            //LVL
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isPro) {
            //LVL
            handler.post(r);
            //LVL
        } else {
            mAdView.resume();
            Log.i("OnResume", "OnResume Started");
            if (findViewById(R.id.bannerAd) == null) {
                Log.i("bannerAd", "Banner Ad is not found");
                if (counter == -1) {
                    Log.i("Counter", "Counter is -1");
                    Toast.makeText(getApplicationContext(), "Ads are disabled now :)", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("Counter", "Counter is not -1");
                    Log.i("Counter", "Counter is " + counter);
                    adContainer.addView(mAdView, params);
//                if (findViewById(R.id.iv_1) == null)
//                    adContainer.addView(IV, paramsForIV);
//                if (findViewById(R.id.iv_2) == null)
//                    adContainer.addView(IV2, paramsForIV2);
                    UserClickedAd = false;
                    loadBannerAd();
                }
            }
            super.onResume();
            if (handler != null && show_interstitial != null)
                handler.postDelayed(show_interstitial, show_interstitial_time);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isPro) {
            //LVL
            handler.removeCallbacks(r);
            //LVL
        } else {
            mAdView.pause();
        }
    }
    //Pro Verison

    private void setupBannerAd() {
        IV = new ImageView(this);
        IV2 = new ImageView(this);
        IV2.setImageResource(R.drawable.close);
        IV.setImageResource(R.drawable.close);
        IV.setId(R.id.iv_1);
        IV2.setId(R.id.iv_2);
        mAdView = new AdView(this);
        mAdView.setAdUnitId("ca-app-pub-8654654469412109/8130779272");
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setId(R.id.bannerAd);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (findViewById(R.id.iv_1) == null)
                    adContainer.addView(IV, paramsForIV);
                if (findViewById(R.id.iv_2) == null)
                    adContainer.addView(IV2, paramsForIV2);

            }

            @Override
            public void onAdLeftApplication() {
                UserClickedAd = true;
                counter = -1;
                adContainer.removeView(IV);
                adContainer.removeView(IV2);
                adContainer.removeView(mAdView);
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                loadBannerAd();
                super.onAdFailedToLoad(errorCode);
            }
        });
        adContainer = (RelativeLayout) findViewById(R.id.relative_layout_2);
        // Placement of the ad view and IV.
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramsForIV = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsForIV2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsForIV.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        paramsForIV2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        paramsForIV.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramsForIV2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramsForIV.addRule(RelativeLayout.ALIGN_LEFT, R.id.bannerAd);
        paramsForIV2.addRule(RelativeLayout.ALIGN_RIGHT, R.id.bannerAd);


        loadBannerAd();
    }

    private void setupInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8654654469412109/9607512478");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                LoadInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                LoadInterstitial();
                super.onAdFailedToLoad(errorCode);
            }
        });
        LoadInterstitial();
    }

    private void showAd() {
        Log.i("Ad", "Checking for Ad loaded or not");
        if (mInterstitialAd.isLoaded())
            mInterstitialAd.show();
    }

    private void LoadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("161A2617C05FEAAF11436707215C1995").build();
        if (connectionDetector.isConnectingToInternet())
            mInterstitialAd.loadAd(adRequest);
    }

    private void loadBannerAd() {
        if (!UserClickedAd) {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("161A2617C05FEAAF11436707215C1995").build();
            if (connectionDetector.isConnectingToInternet())
                mAdView.loadAd(adRequest);
        }
    }
}