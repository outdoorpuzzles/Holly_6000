package cz.FCBcoders.holly6000;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.Collator;
import java.util.HashMap;
import java.util.Map;

public class Holly6000TextDisplayFragment extends Fragment {
    int mIndex = 0;
    int lastNewLineStart = 0;
    Runnable characterAdder = null;
    Handler mHandler = null;
    String holly0000DisplayTextWithNewLine;
    Holly6000ViewModel holly6000ViewModel;
    TextView textDisplayTV, promptTV;
    EditText promptET;
    ScrollView textDisplaySV;
    String appScriptURL;
    String jmenoTymu;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Holly6000TextDisplayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Holly6000TextDisplayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Holly6000TextDisplayFragment newInstance(String param1, String param2) {
        Holly6000TextDisplayFragment fragment = new Holly6000TextDisplayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_holly6000_text_display, container, false);

        //Toast.makeText(getActivity(),"Fragment zobrazen", Toast.LENGTH_SHORT).show();

        textDisplayTV = (TextView) view.findViewById(R.id.holly6000TextDisplayTV);
        promptTV = (TextView) view.findViewById(R.id.holly6000PromptTV);
        promptET = (EditText) view.findViewById(R.id.holly6000PromptET);
        textDisplaySV = (ScrollView) view.findViewById(R.id.holly6000TextDisplaySV);

        holly6000ViewModel = new ViewModelProvider(requireActivity()).get(Holly6000ViewModel.class);
        appScriptURL = holly6000ViewModel.getAppScriptURL();
        jmenoTymu = holly6000ViewModel.getTeamName();

        promptET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String submittedText = promptET.getText().toString().trim();

                    Collator collatorInstance = Collator.getInstance();
                    collatorInstance.setStrength(Collator.PRIMARY);

                    int planetNum = holly6000ViewModel.getLastPlanetNum();
                    String planetName = holly6000ViewModel.getGameData()[planetNum][MainActivity.PLANET_NAME_COLUMN];
                    String newTextToDisplay = "";

                    switch (holly6000ViewModel.getCurrentAction()) {
                        case MainActivity.ACTION_LOG_PLANET:
                            String nextPlanetName = holly6000ViewModel.getGameData()[planetNum+1][MainActivity.PLANET_NAME_COLUMN];
                            String nextPlanetCode = holly6000ViewModel.getGameData()[planetNum+1][MainActivity.PLANET_CODE_COLUMN];
                            if (collatorInstance.equals(submittedText, nextPlanetCode)) {
                                Toast.makeText(getActivity(),"Zadáno správné řešení", Toast.LENGTH_SHORT).show();

                                logAction("logPlanet", nextPlanetName, submittedText);
                                holly6000ViewModel.setLastPlanetNum(planetNum+1);

                            } else {
                                Toast.makeText(getActivity(),"Blbě, vole! :)", Toast.LENGTH_SHORT).show();

                                String displayText = holly6000ViewModel.getDisplayText();
                                holly6000ViewModel.setDisplayText(displayText + "\n" + jmenoTymu + "> " + submittedText);

                                newTextToDisplay = getResources().getString(R.string.invalidPlanetCodeText);
                                newTextToDisplay = newTextToDisplay + nextPlanetName;
                                holly6000ViewModel.setUserInputAwaited(true);
                                retroComputerTextAnimation(newTextToDisplay);
                            }

                            break;
                        case MainActivity.ACTION_REQUEST_HELP:

                            break;
                        case MainActivity.ACTION_REGUEST_SOLUTION:

                            break;
                        case MainActivity.ACTION_COMMIT_SOLUTION:

                            break;
                        case MainActivity.ACTION_COMMIT_B_CODE:

                            break;
                        case MainActivity.ACTION_GET_COORDINATES:

                            break;
                        case MainActivity.ACTION_REQUEST_TREASURE_HELP:

                            break;
                        case MainActivity.ACTION_COMMIT_TREASURE_SOLUTION:

                            break;
                        case MainActivity.ACTION_LOG_TREASURE:

                            break;
                        case MainActivity.ACTION_GET_NEWS:

                            break;
                    }

                    return true;
                }
                return false;
            }
        });

        textDisplayTV.post(new Runnable() {
            @Override
            public void run() {
                retroComputerTextAnimation(holly6000ViewModel.getNewTextToDisplay());
                //retroComputerTextAnimation(holly6000TextDisplaySV, holly6000TextDisplayTV, holly6000PromptTV, holly6000PromptET, "Kubikula", "Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... new");
                //retroComputerTextAnimation(holly6000TextDisplaySV, holly6000TextDisplayTV, holly6000PromptTV, "Ahoj, já jsem Holly a mám IQ 6000... abcdefghijklmnopqrstuvwxyz Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... new");
                //retroComputerTextAnimation(holly6000TextDisplayTV, holly6000PromptTV, "Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... new");
                //retroComputerTextAnimation(holly6000TextDisplaySV, holly6000TextDisplayTV, holly6000PromptTV, "Kuba Kubikula","kuk bude sfghs Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Integer vulputate sem a nibh rutrum consequat. Suspendisse sagittis ultrices augue. Integer rutrum, orci vestibulum ullamcorper ultricies, lacus quam ultricies odio, vitae placerat pede sem sit amet enim. Mauris tincidunt sem sed arcu. Nulla turpis magna, cursus sit amet, suscipit a, interdum id, felis. Etiam posuere lacus quis dolor. Suspendisse nisl. Duis viverra diam non justo. Pellentesque arcu. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. In enim a arcu imperdiet malesuada. Aliquam ante.");
            }
        });
        //holly6000TextDisplayTV.setText("Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... new");
        //holly6000TextDisplayTV.setText("Small text... Small text... Small text... Small text... Small text... Small text... Small text...");
        //holly6000TextDisplayTV.setVisibility(View.VISIBLE); holly6000TextDisplayTV.setText("Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... Large text... ");
        //holly6000TextDisplayTV.setVisibility(View.VISIBLE); holly6000TextDisplayTV.setText("Příliš žluťoučký kůň úpěl ďábelské ódy");
        //holly6000TextDisplayTV.setVisibility(View.VISIBLE); holly6000TextDisplayTV.setText("aaaaaaaaaaaaaaaaaaaaaaaaaa bbbbbbbbbbbbbbbbbbbbbbbbbb cccccccccccccccccccccccccc");
        //holly6000PromptTV.setText("Pokus");
        //holly6000TextDisplayTV.setMovementMethod(new ScrollingMovementMethod());
        //holly6000TextDisplayTV.setMovementMethod(new ScrollingMovementMethod());


        //retroComputerTextAnimation(holly6000TextDisplayTV,"Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... new");


        return view;
    }

    public void retroComputerTextAnimation(String message) {
        long mDelay = 10;
        int firstCursorBlinks = 3;

        TextPaint textPaint;
        int letterWidth, textViewWidth;
        if (textDisplayTV.getVisibility() != View.GONE) {
            textPaint = textDisplayTV.getPaint();
            letterWidth = (int) textPaint.measureText("a");
            textViewWidth = textDisplayTV.getWidth();
        } else {
            textPaint = promptTV.getPaint();
            letterWidth = (int) textPaint.measureText("a");
            textViewWidth = promptTV.getWidth();
        }
        int lettersPerLine = textViewWidth/letterWidth;

        String startingText = getResources().getString(R.string.holly6000_monitor_holly_prompt);
        int startingTextLength = startingText.length();
        promptTV.setText(startingText);
        promptET.setText("");

        ViewGroup.LayoutParams promptTVLayoutParams = promptTV.getLayoutParams();
        promptTVLayoutParams.width = 0;
        promptTV.setLayoutParams(promptTVLayoutParams);
        promptET.setVisibility(View.GONE);

        String holly0000DisplayText = holly6000ViewModel.getDisplayText();
        if (!holly0000DisplayText.equals("")) {
            holly6000ViewModel.setDisplayText(holly0000DisplayText + "\n" + startingText + message);
            textDisplayTV.setVisibility(View.VISIBLE);
            textDisplayTV.setText(holly0000DisplayText);
            textDisplaySV.post(() -> textDisplaySV.fullScroll(View.FOCUS_DOWN));
        }
        else
            holly6000ViewModel.setDisplayText(startingText + message);

        /* vyřešeno v předchozím if
        if (!holly0000DisplayText.equals("")) {
            textDisplayTV.setVisibility(View.VISIBLE);
            textDisplayTV.setText(holly0000DisplayText);
            textDisplaySV.post(() -> textDisplaySV.fullScroll(View.FOCUS_DOWN));
        }*/

        String wholeString = promptTV.getText() + message;
        int wholeStringLength = wholeString.length();
        char[] wholeStringCharArray = wholeString.toCharArray();


        int lastSpacePos = 0;
        int posInLine = 0;
        for (int i = 0; i < wholeStringCharArray.length; i++) {
            if (wholeStringCharArray[i] == ' ')
                lastSpacePos = i;
            if (++posInLine > lettersPerLine) {
                if (wholeStringCharArray[lastSpacePos] == '\n') {
                    char[] pom = new char[wholeStringCharArray.length+1];
                    System.arraycopy(wholeStringCharArray, 0, pom, 0, lastSpacePos+lettersPerLine+1);
                    System.arraycopy(wholeStringCharArray, lastSpacePos+lettersPerLine+1, pom, lastSpacePos+lettersPerLine+2, wholeStringCharArray.length-(lastSpacePos+lettersPerLine+1));
                    wholeStringCharArray = pom;
                    lastSpacePos = lastSpacePos+lettersPerLine+1;
                }
                wholeStringCharArray[lastSpacePos] = '\n';
                posInLine = i - lastSpacePos + 1;
            }
        }

        final String wholeMessageWithBreakLines = String.valueOf(wholeStringCharArray) + "\n";
        mIndex = startingTextLength - firstCursorBlinks;

        mHandler = new Handler();
        characterAdder = new Runnable() {
            @Override
            public void run() {
                String promptTVText = promptTV.getText().toString();
                if (Character.compare(wholeMessageWithBreakLines.charAt(mIndex), '\n') == 0) {
                    // tohle if je tam jenom kvůli tomu, aby to na konci zprávy neodřádkovalo, pokud se nečeká input od uživatele
                    // (posední řádek by tam byl 2x - jedno v textDisplayTV a podruhé by zůstal v promptTV - nebyl by přepsán "Kuba> "
                    if ((mIndex < wholeMessageWithBreakLines.length()-1) || (holly6000ViewModel.isUserInputAwaited())) {
                        if (textDisplayTV.getVisibility() == View.GONE)
                            textDisplayTV.setVisibility(View.VISIBLE);
                        if (!holly0000DisplayText.equals(""))
                            holly0000DisplayTextWithNewLine = holly0000DisplayText + "\n" + wholeMessageWithBreakLines.subSequence(0, mIndex++);
                        else
                            holly0000DisplayTextWithNewLine = wholeMessageWithBreakLines.subSequence(0, mIndex++).toString();
                        textDisplayTV.setText(holly0000DisplayTextWithNewLine);

                        textDisplaySV.post(() -> textDisplaySV.fullScroll(View.FOCUS_DOWN));
                    } else {
                        promptTVText = promptTVText.substring(0, promptTVText.length() - 1);
                        promptTV.setText(promptTVText);
                        mIndex++;
                    }

                    if (mIndex < wholeMessageWithBreakLines.length()) {
                        promptTVText = "_";
                        promptTV.setText(promptTVText);
                    } else if (holly6000ViewModel.isUserInputAwaited()) {
                        promptTVText = jmenoTymu + "> ";
                        promptTV.setText(promptTVText);

                        ViewGroup.LayoutParams params = promptTV.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        promptTV.setLayoutParams(params);
                        promptET.setVisibility(View.VISIBLE);

                        return;
                    } else
                        return;
                }
                if (promptTVText.endsWith("_")) {
                    promptTVText = promptTVText.substring(0,promptTVText.length()-1);
                    promptTV.setText(promptTVText);
                    mHandler.postDelayed(characterAdder, mDelay);
                } else {
                    if (mIndex < startingTextLength) {
                        promptTVText= promptTVText + "_";
                        mIndex++;
                    } else {
                        promptTVText= promptTVText + wholeMessageWithBreakLines.charAt(mIndex++) + "_";
                    }
                    promptTV.setText(promptTVText);
                    mHandler.postDelayed(characterAdder, mDelay);
                }

            }
        };

        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);


    }

    private void logAction(String currentAction, String planet, String submittedText) {

        final Activity activity = getActivity();
        final ProgressDialog loading;

        if (!holly6000ViewModel.isInternetAvailable()) {
            Log.d("Log Planet", "noInternetConnectionWarning (logAction -> začátek)");
            MainActivity myActivity = (MainActivity) getActivity();
            myActivity.noInternetConnectionWarning();
            return;
        }

        //loading =  ProgressDialog.show(activity,"Loading","please wait",false,true);

        holly6000ViewModel.setDisplayText(holly6000ViewModel.getDisplayText() + "\n" + jmenoTymu + "> " + submittedText);
        textDisplayTV.setText(holly6000ViewModel.getDisplayText());
        promptTV.setText(getResources().getString(R.string.holly6000_monitor_data_loading));
        promptTV.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.holly6000_monitor_data_loading_animation));
        promptET.setText("");
        promptET.setVisibility(View.INVISIBLE);
        textDisplaySV.post(() -> textDisplaySV.fullScroll(View.FOCUS_DOWN));
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(promptET.getWindowToken(), 0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        holly6000ViewModel.setDisplayText(holly6000ViewModel.getDisplayText() +
                                "\n" + getResources().getString(R.string.holly6000_monitor_data_loading));
                        textDisplayTV.setText(holly6000ViewModel.getDisplayText());
                        //promptTV.setText(getResources().getString(R.string.holly6000_monitor_holly_prompt));
                        promptTV.clearAnimation();
                        promptET.setText("");
                        //promptET.setVisibility(View.VISIBLE);
                        textDisplaySV.post(() -> textDisplaySV.fullScroll(View.FOCUS_DOWN));
                        holly6000ViewModel.setUserInputAwaited(false);
                        retroComputerTextAnimation(getResources().getString(R.string.holly6000_monitor_planet_logged));

                        //loading.dismiss();
                        //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();

                        String errorSubString = "<!DOC";
                        int substringLengt = Math.min(response.length(), errorSubString.length());
                        if ((response.equals("")) || (response.substring(0,substringLengt).equals(errorSubString.substring(0,substringLengt)))) {
                            Log.d("Log Planet", "error message (logAction -> onResponse): " + response);
                            MainActivity myActivity = (MainActivity) getActivity();
                            myActivity.networkProblemWarning();
                            return;
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), "Spojení vypršelo", Toast.LENGTH_LONG).show();
                        //loading.dismiss();
                        Log.d("Log Planet", "networkProblemWarning (logAction -> onErrorResponse)" + error.toString());
                        //error.printStackTrace();
                        MainActivity myActivity = (MainActivity) getActivity();
                        myActivity.networkProblemWarning();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "logAction");
                parmas.put("currentAction", currentAction);
                parmas.put("team", jmenoTymu);
                parmas.put("planet", planet);

                return parmas;
            }
        };

        int socketTimeOut = 5000;// u can change this .. here it is 50 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(stringRequest);

    }

    @Override
    public void onDestroyView() {
        //Toast.makeText(getActivity(),"Fragment zničen", Toast.LENGTH_SHORT).show();
        if (characterAdder != null)
            mHandler.removeCallbacks(characterAdder);

        super.onDestroyView();
    }
}