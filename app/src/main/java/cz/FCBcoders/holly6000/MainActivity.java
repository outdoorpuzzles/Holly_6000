package cz.FCBcoders.holly6000;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private boolean hollyMsg = true;
    private final int FINISH_APP_TIME_INTERVAL = 2000;
    private long mLastBackPressedTime;
    Handler digitDisplayHandler = null;
    Runnable digitDisplayRunnable = null;
    Holly6000ViewModel holly6000ViewModel;
    private String appScriptURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button logStanovisteBtn = (Button) findViewById(R.id.logStanovisteBtn);
        Button napovedaBtn = (Button) findViewById(R.id.napovedaBtn);
        Button reseniBtn = (Button) findViewById(R.id.reseniBtn);

        ImageView smallHolly6000Monitor = (ImageView) findViewById(R.id.holly6000ConsoleSmallHolly6000Monitor);

        holly6000ViewModel = new ViewModelProvider(this).get(Holly6000ViewModel.class);
        appScriptURL = holly6000ViewModel.getAppScriptURL();

        animateHolly6000ConsoleItems();

        AppCompatImageButton logPlanetBtn = (AppCompatImageButton) findViewById(R.id.holly6000ConsoleLogPlanetBtn);
        logPlanetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(),
                        R.animator.holly6000_console_controls_animator);
                set.setTarget(findViewById(R.id.holly6000ConsoleRedSquareControl_1));
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {

                    }
                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        set.start();
                    }
                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {

                    }
                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {

                    }
                });
                set.start();*/
            }
        });

        smallHolly6000Monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Holly6000VideoFragment holly6000VideoFragment = (Holly6000VideoFragment) fm.findFragmentByTag("Holly6000VideoFragment");

                if (holly6000VideoFragment != null && holly6000VideoFragment.isVisible()) {
                    fm.beginTransaction()
                            .replace(R.id.holly6000_monitor_fragment_container, Holly6000TextDisplayFragment.class, null, "Holly6000TextDisplayFragment")
                            .setReorderingAllowed(true)
                            .addToBackStack("Holly6000TextDisplayFragment") // Name can be null
                            .commit();
                } else {
                    fm.beginTransaction()
                            .replace(R.id.holly6000_monitor_fragment_container, Holly6000VideoFragment.class, null, "Holly6000VideoFragment")
                            .setReorderingAllowed(true)
                            .addToBackStack("Holly6000VideoFragment") // Name can be null
                            .commit();
                }
            }
        });

        // Ověří konektivitu internetu
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                holly6000ViewModel.setInternetAvailable(true);
                super.onAvailable(network);
            }

            @Override
            public void onLost(@NonNull Network network) {
                holly6000ViewModel.setInternetAvailable(false);
                super.onLost(network);
            }
        };

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mLastBackPressedTime + FINISH_APP_TIME_INTERVAL < System.currentTimeMillis()) {
                    Toast.makeText(getBaseContext(), "Opětovným stiskem zpět ukončíte aplikaci Holly 6000", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                    System.exit(0);
                }
                mLastBackPressedTime = System.currentTimeMillis();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    protected void onStart() {
        super.onStart();

        runConsoleDigitDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*Hiding Status Bar*/
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (digitDisplayRunnable != null)
            digitDisplayHandler.removeCallbacks(digitDisplayRunnable);
    }

    private void getLastPlanet() {

        final ProgressDialog loading;

        if (!holly6000ViewModel.isInternetAvailable()) {
            noInternetConnectionWarning();
            return;
        }

        loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String lastPlanet = response;
                        String nextPlanet;
                        loading.dismiss();

                        //Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
                        String errorSubString = "<!DOC";
                        int substringLengt = Math.min(response.length(), errorSubString.length());
                        if ((response.equals("")) || (response.substring(0,substringLengt).equals(errorSubString.substring(0,substringLengt)))) {
                            networkProblemWarning();
                            return;
                        }

                        String[][] planetCodes = holly6000ViewModel.getPlanetCodes();
                        if (lastPlanet.equals("Planeta nenalezena")) {
                            nextPlanet = planetCodes[0][0];
                        } else {
                            byte i;
                            for (i = 0; i < planetCodes[0].length; i++)
                                if (planetCodes[0][i].equals(lastPlanet)) {
                                    break;
                                }
                            nextPlanet = planetCodes[0][i + 1];
                        }
                        Log.d("Log Planet", "Obdrzel jsem jmeno dalsi planety " + nextPlanet);
                        //displayLogInstructions(nextPlanet);
                        holly6000ViewModel.setNextPlanet(nextPlanet);


                        byte i;
                        for (i = 0; i < planetCodes[0].length; i++)
                            if (planetCodes[0][i].equals(nextPlanet)) {
                                Log.d("Log Planet", "Porovnavam planetu " + planetCodes[0][i]);
                                Log.d("Log Planet", "S planetou " + nextPlanet);
                                break;
                            }
                        Log.d("Log Planet", "Dalsi planeta je na pozici " + i);
                        holly6000ViewModel.setCorrectPlanetPSW(planetCodes[1][i]);
                        Log.d("Log Planet", "Kod dalsi planety je " + planetCodes[1][i]);
                        //Toast.makeText(MainActivity.this, holly6000ViewModel.getCorrectPlanetPSW(), Toast.LENGTH_LONG).show();
                        FragmentManager fm = getSupportFragmentManager();
                        PlanetLoginDialogFragment planetLoginDialogFragment = new PlanetLoginDialogFragment();
                        planetLoginDialogFragment.show(fm, "Planet Login Dialog Fragment");
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        noInternetConnectionWarning();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "getLastPlanet");
                //parmas.put("action", "findLastPlanet");
                parmas.put("team", holly6000ViewModel.getTeamName());
                Log.d("Log Planet", "Posilam jmeno tymu " + holly6000ViewModel.getTeamName());

                return parmas;
            }
        };

        int socketTimeOut = 5000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    //private void writeToDatabase() {
    /*private void writeToDatabase() {

        final ProgressDialog loading = ProgressDialog.show(this, "Adding Item", "Please wait");
        final String planetLogCodeTVtext = planetLogCodeET.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        //startActivity(intent);
                        String errorSubString = "<!DOC";
                        int substringLengt = Math.min(response.length(), errorSubString.length());
                        if ((response.equals("")) || (response.substring(0,substringLengt).equals(errorSubString.substring(0,substringLengt)))) {
                            networkProblemWarning();
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        MainActivity myActivity = (MainActivity) getActivity();
                        myActivity.noInternetConnectionWarning();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "logPlanet");
                //parmas.put("action", "getLastPlanet");
                parmas.put("team", "FCB");
                parmas.put("planet", planetLogCodeTVtext);

                return parmas;
            }
        };

        int socketTimeOut = 5000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }*/

    public void noInternetConnectionWarning() {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog))
                .setTitle("Holly 6000 unavilable")
                .setMessage("Spojení s mateřskou lodí nebylo navázáno. Zkuste to prosím později.")
                .setPositiveButton("Rozumím", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void networkProblemWarning() {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog))
                .setTitle("Holly 6000 error")
                .setMessage("Holly hlásí, že je někde problém. Zkuste to prosím později. Když to nepůjde, kontatujte mateřskou loď.")
                .setPositiveButton("Rozumím", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void animateHolly6000ConsoleItems()
    {
        // Console controls animations
        /*ImageView holly6000ConsoleRedSquareControl_1 = (ImageView) findViewById(R.id.holly6000ConsoleRedSquareControl_1);
        ImageView holly6000ConsoleBlueSquareControl_1 = (ImageView) findViewById(R.id.holly6000ConsoleBlueSquareControl_1);

        Animation holly6000ConsoleRedSquareControl_1Animation = AnimationUtils.loadAnimation(this, R.anim.holly6000_console_controls_animation);
        Animation holly6000ConsoleBlueSquareControl_1Animation = AnimationUtils.loadAnimation(this, R.anim.holly6000_console_controls_animation);

        holly6000ConsoleRedSquareControl_1Animation.setStartOffset(3000);
        holly6000ConsoleBlueSquareControl_1Animation.setStartOffset(500);

        holly6000ConsoleRedSquareControl_1.startAnimation(holly6000ConsoleRedSquareControl_1Animation);
        holly6000ConsoleBlueSquareControl_1.startAnimation(holly6000ConsoleBlueSquareControl_1Animation);*/

        // Console controls animators
        AnimatorSet holly6000ConsoleRedSquareControl_1_AS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_red_square_1);
         holly6000ConsoleRedSquareControl_1_AS.setTarget(findViewById(R.id.holly6000ConsoleRedSquareControl_1));
         holly6000ConsoleRedSquareControl_1_AS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                 holly6000ConsoleRedSquareControl_1_AS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
         holly6000ConsoleRedSquareControl_1_AS.start();

        AnimatorSet holly6000ConsoleRedSquareControl_2_AS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_red_square_2);
        holly6000ConsoleRedSquareControl_2_AS.setTarget(findViewById(R.id.holly6000ConsoleRedSquareControl_2));
        holly6000ConsoleRedSquareControl_2_AS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleRedSquareControl_2_AS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleRedSquareControl_2_AS.start();

        AnimatorSet holly6000ConsoleBlueSquareControl_1_AS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_blue_square_1);
        holly6000ConsoleBlueSquareControl_1_AS.setTarget(findViewById(R.id.holly6000ConsoleBlueSquareControl_1));
        holly6000ConsoleBlueSquareControl_1_AS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleBlueSquareControl_1_AS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleBlueSquareControl_1_AS.start();

        AnimatorSet holly6000ConsoleBlueSquareControl_2_AS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_blue_square_2);
        holly6000ConsoleBlueSquareControl_2_AS.setTarget(findViewById(R.id.holly6000ConsoleBlueSquareControl_2));
        holly6000ConsoleBlueSquareControl_2_AS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleBlueSquareControl_2_AS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleBlueSquareControl_2_AS.start();

        AnimatorSet holly6000ConsoleGreenSquareControlAS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_green_square);
        holly6000ConsoleGreenSquareControlAS.setTarget(findViewById(R.id.holly6000ConsoleGreenSquareControl));
        holly6000ConsoleGreenSquareControlAS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleGreenSquareControlAS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleGreenSquareControlAS.start();

        AnimatorSet holly6000ConsoleYellowSquareControlAS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_yellow_square);
        holly6000ConsoleYellowSquareControlAS.setTarget(findViewById(R.id.holly6000ConsoleYellowSquareControl));
        holly6000ConsoleYellowSquareControlAS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleYellowSquareControlAS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleYellowSquareControlAS.start();

        AnimatorSet holly6000ConsoleBlueCircleControlAS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_blue_circle);
        holly6000ConsoleBlueCircleControlAS.setTarget(findViewById(R.id.holly6000ConsoleBlueCircleControl));
        holly6000ConsoleBlueCircleControlAS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleBlueCircleControlAS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleBlueCircleControlAS.start();

        AnimatorSet holly6000ConsoleGreenCircleControlAS = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_green_circle);
        holly6000ConsoleGreenCircleControlAS.setTarget(findViewById(R.id.holly6000ConsoleGreenCircleControl));
        holly6000ConsoleGreenCircleControlAS.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleGreenCircleControlAS.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleGreenCircleControlAS.start();

        AnimatorSet holly6000ConsoleYellowCircleControl = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.holly6000_console_controls_animator_yellow_circle);
        holly6000ConsoleYellowCircleControl.setTarget(findViewById(R.id.holly6000ConsoleYellowCircleControl));
        holly6000ConsoleYellowCircleControl.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                holly6000ConsoleYellowCircleControl.start();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        holly6000ConsoleYellowCircleControl.start();


        //Console Green Bar animation
        ImageView holly6000ConsoleGreenBar = (ImageView) findViewById(R.id.holly6000ConsoleGreenBar);
        Animation holly6000ConsoleGreenBarAnimation = AnimationUtils.loadAnimation(this, R.anim.holly6000_console_green_bar_animation);
        holly6000ConsoleGreenBar.startAnimation(holly6000ConsoleGreenBarAnimation);
    }

    public void runConsoleDigitDisplay() {
        Random random = new Random();
        TextView holly6000ConsoleDigitDisplayTV = (TextView) findViewById(R.id.holly6000ConsoleDigitDisplay);
        /*ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        holly6000ConsoleDigitDisplayTV.setText(String.format(Locale.GERMANY, "%04d", random.nextInt(10000)));
                    }
                }, 0, 2, TimeUnit.SECONDS);*/
        final int[] randomNumberForDigitDisplay = new int[1];
        digitDisplayHandler = new Handler();
        digitDisplayRunnable = new Runnable() {
            @Override
            public void run() {
                randomNumberForDigitDisplay[0] = random.nextInt(10000);
                holly6000ConsoleDigitDisplayTV.setText(String.format(Locale.GERMANY, "%04d", randomNumberForDigitDisplay[0]));
                digitDisplayHandler.postDelayed(digitDisplayRunnable, randomNumberForDigitDisplay[0]);
            }
        };
        digitDisplayHandler.removeCallbacks(digitDisplayRunnable);
        digitDisplayHandler.post(digitDisplayRunnable);
    }
}