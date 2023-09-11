package cz.FCBcoders.holly6000;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
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
    final String YES = "Y";
    int mIndex = 0;
    boolean doubleUnderscore = false;
    Runnable characterAdder = null;
    Handler mHandler = null;
    String holly0000DisplayTextWithNewLine;
    Holly6000ViewModel holly6000ViewModel;
    TextView textDisplayTV, promptTV;
    EditText promptET;
    ScrollView textDisplaySV;
    String appScriptURL;
    String jmenoTymu;

    public Holly6000TextDisplayFragment() {
        // Required empty public constructor
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
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(promptET.getWindowToken(), 0);

                    String submittedText = promptET.getText().toString().trim();

                    holly6000ViewModel.setDisplayText(holly6000ViewModel.getDisplayText() + "\n" + jmenoTymu + "> " + submittedText);

                    Collator collatorInstance = Collator.getInstance();
                    collatorInstance.setStrength(Collator.PRIMARY);

                    int planetNum = holly6000ViewModel.getLastPlanetNum();
                    String planetName;
                    /*if (planetNum == -1)
                        planetName = holly6000ViewModel.getGameData()[0][MainActivity.PLANET_NAME_COLUMN];
                    else
                        planetName = holly6000ViewModel.getGameData()[planetNum][MainActivity.PLANET_NAME_COLUMN];*/
                    int treasureGameDataRow = holly6000ViewModel.getGameData().length - 1;
                    String newTextToDisplay = "";

                    switch (holly6000ViewModel.getCurrentAction()) {
                        case MainActivity.ACTION_LOG_PLANET:
                            String nextPlanetName = holly6000ViewModel.getGameData()[planetNum+1][MainActivity.PLANET_NAME_COLUMN];
                            String nextPlanetCode = holly6000ViewModel.getGameData()[planetNum+1][MainActivity.PLANET_CODE_COLUMN];
                            if (collatorInstance.equals(submittedText, nextPlanetCode)) {
                                //Toast.makeText(getActivity(),"Zadáno správné řešení", Toast.LENGTH_SHORT).show();
                                //Log.d("Log Planet", "Last planet num PŘED zalogováním: " + holly6000ViewModel.getLastPlanetNum());
                                logAction();
                                //Log.d("Log Planet", "Last planet num PO zalogování: " + holly6000ViewModel.getLastPlanetNum());

                            } else {
                                //Toast.makeText(getActivity(),"Blbě, vole! :)", Toast.LENGTH_SHORT).show();
                                newTextToDisplay = getResources().getString(R.string.invalid_planet_code_text);
                                newTextToDisplay = newTextToDisplay + nextPlanetName;
                                holly6000ViewModel.setUserInputAwaited(false);
                                retroComputerTextAnimation(newTextToDisplay);
                            }
                            break;

                        case MainActivity.ACTION_REQUEST_HELP:
                            if (collatorInstance.equals(submittedText, YES)) {
                                logAction();
                            } else {
                                newTextToDisplay = getResources().getString(R.string.help_request_refused_text);
                                holly6000ViewModel.setUserInputAwaited(false);
                                retroComputerTextAnimation(newTextToDisplay);
                            }
                            break;

                        case MainActivity.ACTION_REGUEST_SOLUTION:
                            if (collatorInstance.equals(submittedText, YES)) {
                                logAction();
                            } else {
                                newTextToDisplay = getResources().getString(R.string.solution_request_refused_text);
                                holly6000ViewModel.setUserInputAwaited(false);
                                retroComputerTextAnimation(newTextToDisplay);
                            }
                            break;

                        case MainActivity.ACTION_COMMIT_SOLUTION:
                            String correctSolution = holly6000ViewModel.getGameData()[planetNum][MainActivity.SOLUTION_COLUMN];
                            if (collatorInstance.equals(submittedText, correctSolution)) {
                                logAction();
                            } else {
                                newTextToDisplay = getResources().getString(R.string.invalid_solution_text);
                                holly6000ViewModel.setUserInputAwaited(false);
                                retroComputerTextAnimation(newTextToDisplay);
                            }
                            break;

                        case MainActivity.ACTION_COMMIT_B_CODE:

                            break;

                        case MainActivity.ACTION_REQUEST_TREASURE_HELP:
                            if (collatorInstance.equals(submittedText, YES)) {
                                logAction();
                            } else {
                                newTextToDisplay = getResources().getString(R.string.treasure_help_request_refused_text);
                                holly6000ViewModel.setUserInputAwaited(false);
                                retroComputerTextAnimation(newTextToDisplay);
                            }
                            break;

                        case MainActivity.ACTION_COMMIT_TREASURE_SOLUTION:
                            String correctTreasureSolution = holly6000ViewModel.getGameData()[treasureGameDataRow][MainActivity.SOLUTION_COLUMN];
                            if (collatorInstance.equals(submittedText, correctTreasureSolution)) {
                                logAction();
                            } else {
                                newTextToDisplay = getResources().getString(R.string.invalid_treasure_solution_text);
                                holly6000ViewModel.setUserInputAwaited(false);
                                retroComputerTextAnimation(newTextToDisplay);
                            }
                            break;

                        case MainActivity.ACTION_LOG_TREASURE:
                            String treasureCode = holly6000ViewModel.getGameData()[treasureGameDataRow][MainActivity.PLANET_CODE_COLUMN];
                            if (collatorInstance.equals(submittedText, treasureCode)) {
                                logAction();
                            } else {
                                newTextToDisplay = getResources().getString(R.string.invalid_treasure_code_text);
                                holly6000ViewModel.setUserInputAwaited(false);
                                retroComputerTextAnimation(newTextToDisplay);
                            }
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
        doubleUnderscore = false;

        boolean[] currentBtnsState = disableAllButtons();

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

                        restoreButtonsState(currentBtnsState);

                        if (holly6000ViewModel.getCurrentAction().equals(MainActivity.ACTION_LOG_PLANET) &&
                        message.equals(getResources().getString(R.string.planet_logged_text))) {
                            holly6000ViewModel.setDisplayText("");
                            textDisplayTV.setText("");
                            textDisplayTV.setVisibility(View.GONE);
                            promptTV.setText(startingText);
                        }

                        return;
                    } else {
                        restoreButtonsState(currentBtnsState);

                        if (holly6000ViewModel.getCurrentAction().equals(MainActivity.ACTION_LOG_PLANET) &&
                                message.equals(getResources().getString(R.string.planet_logged_text))) {
                            holly6000ViewModel.setDisplayText("");
                            textDisplayTV.setText("");
                            textDisplayTV.setVisibility(View.GONE);
                            promptTV.setText(startingText);
                        }

                        return;
                    }
                }
                if (promptTVText.endsWith("_") && !doubleUnderscore) {
                    doubleUnderscore = (promptTVText.length() > 1) && (Character.compare(promptTVText.charAt(promptTVText.length() - 2), '_') == 0);
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

    private boolean[] disableAllButtons() {
        MainActivity myActivity = (MainActivity) getActivity();
        boolean[] currentBtnsState = new boolean[10];

        currentBtnsState[0] = myActivity.logPlanetBtn.isEnabled();
        currentBtnsState[1] = myActivity.helpRequestBtn.isEnabled();
        currentBtnsState[2] = myActivity.solutionRequestBtn.isEnabled();
        currentBtnsState[3] = myActivity.solutionBtn.isEnabled();
        currentBtnsState[4] = myActivity.bCodeBtn.isEnabled();
        currentBtnsState[5] = myActivity.navigationBtn.isEnabled();
        currentBtnsState[6] = myActivity.treasureHelpRequestBtn.isEnabled();
        currentBtnsState[7] = myActivity.treasureSolutionBtn.isEnabled();
        currentBtnsState[8] = myActivity.logTreasureBtn.isEnabled();
        currentBtnsState[9] = myActivity.hollysJokesBtn.isEnabled();

        myActivity.logPlanetBtn.setEnabled(false);
        myActivity.helpRequestBtn.setEnabled(false);
        myActivity.solutionRequestBtn.setEnabled(false);
        myActivity.solutionBtn.setEnabled(false);
        myActivity.bCodeBtn.setEnabled(false);
        myActivity.navigationBtn.setEnabled(false);
        myActivity.treasureHelpRequestBtn.setEnabled(false);
        myActivity.treasureSolutionBtn.setEnabled(false);
        myActivity.logTreasureBtn.setEnabled(false);
        myActivity.hollysJokesBtn.setEnabled(false);

        return currentBtnsState;
    }

    private void restoreButtonsState(boolean[] buttonsState) {
        MainActivity myActivity = (MainActivity) getActivity();

        myActivity.logPlanetBtn.setEnabled(buttonsState[0]);
        myActivity.helpRequestBtn.setEnabled(buttonsState[1]);
        myActivity.solutionRequestBtn.setEnabled(buttonsState[2]);
        myActivity.solutionBtn.setEnabled(buttonsState[3]);
        myActivity.bCodeBtn.setEnabled(buttonsState[4]);
        myActivity.navigationBtn.setEnabled(buttonsState[5]);
        myActivity.treasureHelpRequestBtn.setEnabled(buttonsState[6]);
        myActivity.treasureSolutionBtn.setEnabled(buttonsState[7]);
        myActivity.logTreasureBtn.setEnabled(buttonsState[8]);
        myActivity.hollysJokesBtn.setEnabled(buttonsState[9]);
    }

    private void logAction() {
        final Activity activity = getActivity();

        if (!holly6000ViewModel.isInternetAvailable()) {
            Log.d("Log Planet", "noInternetConnectionWarning (logAction -> začátek)");
            MainActivity myActivity = (MainActivity) getActivity();
            myActivity.noInternetConnectionWarning();
            return;
        }

        boolean[] currentBtnsState = disableAllButtons();

        startLoadingProgress();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, appScriptURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        stopLoadingProgress();

                        restoreButtonsState(currentBtnsState);

                        MainActivity myActivity = (MainActivity) getActivity();
                        int planetNum = holly6000ViewModel.getLastPlanetNum();
                        int treasureGameDataRow = holly6000ViewModel.getGameData().length - 1;
                        String newTextToDisplay = "";

                        Log.d("Log Planet", "response: " + response);

                        String errorSubString = "<!DOC";
                        int substringLengt = Math.min(response.length(), errorSubString.length());
                        if ((response.equals("")) || (response.substring(0,substringLengt).equals(errorSubString.substring(0,substringLengt)))) {
                            Log.d("Log Planet", "error message (logAction -> onResponse): " + response);
                            myActivity.noInternetConnectionWarning();
                            return;
                        } else {

                            switch (holly6000ViewModel.getCurrentAction()) {
                                case MainActivity.ACTION_LOG_PLANET:
                                    holly6000ViewModel.setLastPlanetNum(holly6000ViewModel.getLastPlanetNum()+1);
                                    holly6000ViewModel.setHelpRequested(false);
                                    holly6000ViewModel.setSolutionRequested(false);
                                    holly6000ViewModel.setSolutionCommitted(false);

                                    myActivity.logPlanetBtn.setEnabled(false);
                                    myActivity.helpRequestBtn.setEnabled(true);
                                    myActivity.solutionRequestBtn.setEnabled(true);
                                    myActivity.solutionBtn.setEnabled(true);
                                    if (!holly6000ViewModel.isTreasureSolutionCommitted() || holly6000ViewModel.isTreasureLogged())
                                        myActivity.navigationBtn.setEnabled(false);
                                    if (holly6000ViewModel.getLastPlanetNum() == 0) {
                                        myActivity.bCodeBtn.setEnabled(true);
                                        myActivity.treasureHelpRequestBtn.setEnabled(true);
                                        myActivity.treasureSolutionBtn.setEnabled(true);
                                    }

                                    retroComputerTextAnimation(getResources().getString(R.string.planet_logged_text));
                                    break;

                                case MainActivity.ACTION_REQUEST_HELP:
                                    holly6000ViewModel.setHelpRequested(true);
                                    newTextToDisplay = getResources().getString(R.string.help_starting_text);
                                    newTextToDisplay = newTextToDisplay + holly6000ViewModel.getGameData()[planetNum][MainActivity.HELP_COLUMN];
                                    retroComputerTextAnimation(newTextToDisplay);
                                    break;

                                case MainActivity.ACTION_REGUEST_SOLUTION:
                                    holly6000ViewModel.setSolutionRequested(true);

                                    myActivity.helpRequestBtn.setEnabled(false);

                                    newTextToDisplay = getResources().getString(R.string.solution_starting_text);
                                    newTextToDisplay = newTextToDisplay + holly6000ViewModel.getGameData()[planetNum][MainActivity.SOLUTION_COLUMN];
                                    retroComputerTextAnimation(newTextToDisplay);
                                    break;

                                case MainActivity.ACTION_COMMIT_SOLUTION:
                                    holly6000ViewModel.setHelpRequested(true);

                                    myActivity.logPlanetBtn.setEnabled(true);
                                    myActivity.helpRequestBtn.setEnabled(false);
                                    myActivity.solutionRequestBtn.setEnabled(false);
                                    myActivity.solutionBtn.setEnabled(false);
                                    myActivity.navigationBtn.setEnabled(true);

                                    retroComputerTextAnimation(getResources().getString(R.string.solution_committed_text));
                                    // TODO odemknout coordinates
                                    break;

                                case MainActivity.ACTION_COMMIT_B_CODE:

                                    break;

                                case MainActivity.ACTION_REQUEST_TREASURE_HELP:
                                    holly6000ViewModel.setTreasureHelpRequested(true);
                                    newTextToDisplay = getResources().getString(R.string.treasure_help_starting_text);
                                    newTextToDisplay = newTextToDisplay + holly6000ViewModel.getGameData()[treasureGameDataRow][MainActivity.HELP_COLUMN];
                                    retroComputerTextAnimation(newTextToDisplay);
                                    break;

                                case MainActivity.ACTION_COMMIT_TREASURE_SOLUTION:
                                    holly6000ViewModel.setTreasureSolutionCommitted(true);

                                    myActivity.navigationBtn.setEnabled(true);
                                    myActivity.treasureHelpRequestBtn.setEnabled(false);
                                    myActivity.treasureSolutionBtn.setEnabled(false);
                                    myActivity.logTreasureBtn.setEnabled(true);

                                    retroComputerTextAnimation(getResources().getString(R.string.treasure_solution_committed_text));
                                    // TODO odemknout coordinates
                                    break;

                                case MainActivity.ACTION_LOG_TREASURE:
                                    holly6000ViewModel.setTreasureLogged(true);

                                    myActivity.treasureHelpRequestBtn.setEnabled(false);
                                    myActivity.treasureSolutionBtn.setEnabled(false);
                                    myActivity.logTreasureBtn.setEnabled(false);
                                    if (!holly6000ViewModel.isSolutionCommitted())
                                        myActivity.navigationBtn.setEnabled(false);

                                    retroComputerTextAnimation(getResources().getString(R.string.treasure_logged_text));
                                    break;
                            }

                        }
                        Log.d("Log Planet", "Číslo poslední planety: " + holly6000ViewModel.getLastPlanetNum() + "; helpRequested: " + holly6000ViewModel.isHelpRequested() + "; solutionRequested: " + holly6000ViewModel.isSolutionRequested() + "; solutionCommitted: " + holly6000ViewModel.isSolutionCommitted() + "; treasureHelpRequested: " + holly6000ViewModel.isTreasureHelpRequested() + "; treasureSolutionCommitted: " + holly6000ViewModel.isTreasureSolutionCommitted() + "; treasureLogged: " + holly6000ViewModel.isTreasureLogged());
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stopLoadingProgress();
                        restoreButtonsState(currentBtnsState);
                        Log.d("Log Planet", "networkProblemWarning (logAction -> onErrorResponse)" + error.toString());
                        //error.printStackTrace();
                        MainActivity myActivity = (MainActivity) getActivity();
                        myActivity.noInternetConnectionWarning();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                int planetNum = holly6000ViewModel.getLastPlanetNum();
                String planetName;
                if (holly6000ViewModel.getCurrentAction().equals(MainActivity.ACTION_LOG_PLANET))
                    planetName = holly6000ViewModel.getGameData()[planetNum+1][MainActivity.PLANET_NAME_COLUMN];
                else
                    planetName = holly6000ViewModel.getGameData()[planetNum][MainActivity.PLANET_NAME_COLUMN];

                //here we pass params
                parmas.put("action", "logAction");
                parmas.put("currentAction", holly6000ViewModel.getCurrentAction());
                parmas.put("team", jmenoTymu);
                parmas.put("planet", planetName);

                return parmas;
            }
        };

        int socketTimeOut = 15000;// u can change this .. here it is 15 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(stringRequest);

    }

    public void startLoadingProgress() {
        textDisplayTV.setText(holly6000ViewModel.getDisplayText());
        promptTV.setText(getResources().getString(R.string.holly6000_monitor_data_loading));
        promptTV.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.holly6000_monitor_data_loading_animation));
        promptET.setText("");
        promptET.setVisibility(View.INVISIBLE);
        textDisplaySV.post(() -> textDisplaySV.fullScroll(View.FOCUS_DOWN));
    }

    public void stopLoadingProgress() {
        holly6000ViewModel.setDisplayText(holly6000ViewModel.getDisplayText() + "\n" + getResources().getString(R.string.holly6000_monitor_data_loading));
        textDisplayTV.setText(holly6000ViewModel.getDisplayText());
        promptTV.clearAnimation();
        promptET.setText("");
        textDisplaySV.post(() -> textDisplaySV.fullScroll(View.FOCUS_DOWN));
        holly6000ViewModel.setUserInputAwaited(false);
    }

    @Override
    public void onDestroyView() {
        //Toast.makeText(getActivity(),"Fragment zničen", Toast.LENGTH_SHORT).show();
        if (characterAdder != null)
            mHandler.removeCallbacks(characterAdder);

        super.onDestroyView();
    }
}