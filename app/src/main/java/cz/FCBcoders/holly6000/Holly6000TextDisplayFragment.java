package cz.FCBcoders.holly6000;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Holly6000TextDisplayFragment extends Fragment {
    int mIndex = 0;
    int lastNewLineStart = 0;
    Runnable characterAdder = null;
    Handler mHandler = null;
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

        TextView holly6000TextDisplayTV = (TextView) view.findViewById(R.id.holly6000TextDisplayTV);
        TextView holly6000PromptTV = (TextView) view.findViewById(R.id.holly6000TextDisplayPromptTV);
        ScrollView holly6000TextDisplaySV = (ScrollView) view.findViewById(R.id.holly6000TextDisplaySV);

        holly6000TextDisplayTV.post(new Runnable() {
            @Override
            public void run() {
                //retroComputerTextAnimation(holly6000TextDisplayTV, holly6000PromptTV, "Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... new");
                //retroComputerTextAnimation(holly6000TextDisplaySV, holly6000TextDisplayTV, holly6000PromptTV, "Ahoj, já jsem Holly a mám IQ 6000... abcdefghijklmnopqrstuvwxyz Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... new");
                //retroComputerTextAnimation(holly6000TextDisplayTV, holly6000PromptTV, "Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... Ahoj, já jsem Holly a mám IQ 6000... new");
                retroComputerTextAnimation(holly6000TextDisplaySV, holly6000TextDisplayTV, holly6000PromptTV, "kuk bude sfghs Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Integer vulputate sem a nibh rutrum consequat. Suspendisse sagittis ultrices augue. Integer rutrum, orci vestibulum ullamcorper ultricies, lacus quam ultricies odio, vitae placerat pede sem sit amet enim. Mauris tincidunt sem sed arcu. Nulla turpis magna, cursus sit amet, suscipit a, interdum id, felis. Etiam posuere lacus quis dolor. Suspendisse nisl. Duis viverra diam non justo. Pellentesque arcu. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. In enim a arcu imperdiet malesuada. Aliquam ante.");
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

    public void retroComputerTextAnimation(ScrollView TextDisplaySV, TextView TextDisplayTV, TextView promptTV, String message) {
        long mDelay = 50;
        int firstCursorBlinks = 3;

        String startingText = "Holly 6000> ";
        int startingTextLength = startingText.length();
        promptTV.setText(startingText);

        /*TextPaint textPaint = TextDisplayTV.getPaint();
        int letterWidth = (int) textPaint.measureText("a");
        int textViewWidth = TextDisplayTV.getWidth();*/
        TextPaint textPaint = promptTV.getPaint();
        int letterWidth = (int) textPaint.measureText("a");
        int textViewWidth = promptTV.getWidth();
        int lettersPerLine = textViewWidth/letterWidth;

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
                    if (TextDisplayTV.getVisibility() == View.GONE)
                        TextDisplayTV.setVisibility(View.VISIBLE);
                    TextDisplayTV.setText(wholeMessageWithBreakLines.subSequence(0,  mIndex++));

                    TextDisplaySV.post(new Runnable() {
                        @Override
                        public void run() {
                            TextDisplaySV.fullScroll(View.FOCUS_DOWN);
                        }
                    });

                    if (mIndex < wholeMessageWithBreakLines.length()) {
                        promptTVText = "_";
                        promptTV.setText(promptTVText);
                    } else {
                        promptTV.setText("");
                        return;
                    }
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

    @Override
    public void onDestroyView() {
        //Toast.makeText(getActivity(),"Fragment zničen", Toast.LENGTH_SHORT).show();
        if (characterAdder != null)
            mHandler.removeCallbacks(characterAdder);

        super.onDestroyView();
    }
}