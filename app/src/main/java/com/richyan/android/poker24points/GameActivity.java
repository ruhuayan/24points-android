package com.richyan.android.poker24points;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.TimerTask;
import java.util.Timer;

public class GameActivity extends AppCompatActivity {
    private Deck deck;
    private ArrayList<Card> cards;
    private ImageView ivCards[];
    private EditText solutionEtxt;
    private TextView cardTxt;
    private TextView timerTxt;
    private TextView scoreTxt;
    private Timer timer;
    private TimerTask task;
    private int sec = 60;
    private int scores = 0;
    private boolean isSolProvided = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Card> cardList = getCards();
        outState.putParcelableArrayList("card_list", cardList);
        outState.putInt("sec", sec);
        outState.putInt("scores",scores);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rule:
                PopupFragment ruleFragment = new PopupFragment();
                ruleFragment.setmPopupID(1);

                ruleFragment.show(getSupportFragmentManager(),"Rules");
                return true;
            case R.id.action_recommend:
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                send.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sendMsg));
                startActivity(Intent.createChooser(send, getResources().getString(R.string.share)));
                return true;
            case R.id.action_about:
                PopupFragment aboutFragment = new PopupFragment();
                aboutFragment.setmPopupID(0);
                aboutFragment.show(getSupportFragmentManager(),"Rules");
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ivCards = new ImageView[4];
        ivCards[0] = (ImageView) findViewById(R.id.card1);
        ivCards[1] = (ImageView) findViewById(R.id.card2);
        ivCards[2] = (ImageView) findViewById(R.id.card3);
        ivCards[3] = (ImageView) findViewById(R.id.card4);


        if (savedInstanceState != null) {
            cards = savedInstanceState.getParcelableArrayList("card_list");
            setImageViews();
            sec = savedInstanceState.getInt("sec");
            scores = savedInstanceState.getInt("scores");

        } else {
            deck = new Deck();
            cards = (ArrayList<Card>) deck.getCards();//have to obtain cards;
            Collections.shuffle(cards);
            setImageViews();
        }
        setTimerTask();
        cardTxt = (TextView) findViewById(R.id.cardTxt);
        timerTxt = (TextView) findViewById(R.id.timerTxt);
        scoreTxt = (TextView) findViewById(R.id.scoreTxt);

        cardTxt.setText((cards.size() - 4) + "");
        scoreTxt.setText(scores+ "");

        //keyboard
        final Keyboard mKeyboard = new Keyboard(this, R.xml.keyboard);
        final KeyboardView mKeyboardView = (KeyboardView) findViewById(R.id.keyboardview);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setEnabled(true);

        solutionEtxt = (EditText) findViewById(R.id.solutionEtxt);
        solutionEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSolutionText();
                mKeyboardView.setVisibility(View.VISIBLE);
                mKeyboardView.setEnabled(true);
                if( v!=null)((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        for (int i = 0; i < 4; i++) {
            final int cardNum = i;
            ivCards[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSolutionText(cards.get(cardNum).getValue()+"");
                }
            });
        }
        mKeyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                if(primaryCode == 1) setSolutionText("(");
                if(primaryCode == 2) setSolutionText("+");
                if(primaryCode == 3) setSolutionText("-");
                if(primaryCode == 4) setSolutionText("*");
                if(primaryCode == 5) setSolutionText("/");
                if(primaryCode == 6) setSolutionText(")");
                if(primaryCode == 7) {
                    calSolutionText();
                    if(isLandLayout())
                        mKeyboardView.setVisibility(View.GONE);
                }
                if(primaryCode == -1) {
                    String temp = solutionEtxt.getText().toString();
                    if(temp.length()==0) return;
                    else if(temp.equals(getResources().getString(R.string.eTxt_solution))){
                        solutionEtxt.setText("");
                        return;
                    }else {
                        temp = temp.substring(0, temp.length() - 1);
                        solutionEtxt.setText(temp);
                        solutionEtxt.setSelection(temp.length());
                    }
                }
            }
            @Override public void onPress(int arg0) {
            }
            @Override public void onRelease(int primaryCode) {
            }
            @Override public void onText(CharSequence text) {
            }
            @Override public void swipeDown() {
            }
            @Override public void swipeLeft() {
            }
            @Override public void swipeRight() {
            }
            @Override public void swipeUp() {
            }
        });
        Button validateBtn = (Button) findViewById(R.id.validateBtn);
        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calSolutionText();
            }

        });
        final Button solutionBtn = (Button) findViewById(R.id.findSolutionBtn);
        solutionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSolProvided) return;
                isSolProvided = true;
                int[] cardNums = new int[4];
                for (int i = 0; i < 4; i++) cardNums[i] = cards.get(i).getValue();
                String exp = findSolution(cardNums);
                solutionEtxt.setText(exp);
                if(exp.equals(getResources().getString(R.string.no_solution))) setScoreTxt(2);
                else setScoreTxt(-4);
                timer.cancel();
                timer.purge();
            }
        });

        final Handler handler = new Handler();
        Button refreshBtn = (Button) findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCard();
                if(!isSolProvided) setScoreTxt(-4);
                isSolProvided = false;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setImageViews();
                                solutionEtxt.setText(R.string.eTxt_solution);
                            }
                        });
                    }
                };
                new Thread(runnable).start();
                if (cards.size() <= 4) {
                    deck = new Deck();
                    cards.addAll(deck.getCards());
                    Collections.shuffle(cards);
                }
                timer.cancel();
                timer.purge();
                sec=60;
                setTimerTask();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }//end protected void onCreate(Bundle savedInstanceState){}
    private void setTimerTask(){
        timer  = new Timer();
        final Handler timerHandler = new Handler();
        task = new TimerTask() {
            @Override
            public void run()
            {  sec= sec -1;
                timerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(sec < 0 ) {
                            Toast.makeText(getApplicationContext(),R.string.timer_up,Toast.LENGTH_LONG).show();
                            setScoreTxt(-4);
                            isSolProvided = true;
                            timer.cancel();
                            timer.purge();
                            return;
                        }else
                            timerTxt.setText(sec+"s");
                    }
                });
            }
        };
        timer.schedule(task, 1000l, 1000L);
    }
    private boolean isLandLayout(){
        return (findViewById(R.id.game_activity).getTag().equals("landscape"));
    }
    private void clearSolutionText(){
        if(solutionEtxt.getText().toString().equals(getResources().getString(R.string.eTxt_solution))) solutionEtxt.setText("");
    }
    private void setSolutionText(String input){
        clearSolutionText();
        String temp = solutionEtxt.getText().toString();

        solutionEtxt.setText(temp + input);
        solutionEtxt.setSelection(solutionEtxt.getText().toString().length());
    }
    private void removeCard() {
        for (int i = 0; i < 4; i++) cards.remove(i);
        cardTxt.setText((cards.size() - 4) + "");
    }

    private ArrayList<Card> getCards() {
        return cards;
    }

    private void setImageViews() {
        for (int i = 0; i < 4; i++) {
            int imgId = getResources().getIdentifier(cards.get(i).getImgSource(), "drawable", GameActivity.this.getPackageName());
            ivCards[i].setImageResource(imgId);
        }
    }
    private void calSolutionText(){
        if(isSolProvided) return;

        String expression = solutionEtxt.getText().toString();
        String temp = expression.replaceAll("÷", "/");
        temp = temp.replaceAll("×", "*");
        int expId = validate(temp);
        if (expId == -1) {
            Toast.makeText(getApplicationContext(), R.string.form_invalid, Toast.LENGTH_LONG).show();
            return;
        }
        int[] cardNums = valNums(temp);
        if (cardNums == null || cardNums.length != 4) {
            Toast.makeText(getApplicationContext(), R.string.card_invalid, Toast.LENGTH_LONG).show();
            return;
        }
        String[] opArray = valOperations(temp);
        if (opArray.length != 3) {
            Toast.makeText(getApplicationContext(), R.string.op_invalid, Toast.LENGTH_LONG).show();
            return;
        }
        double result = calculateExp(cardNums[0], cardNums[1], cardNums[2], cardNums[3], opArray[0], opArray[1], opArray[2], expId);
        solutionEtxt.setText(expression + "=" + result);
        if (testResult(result)) {
            setScoreTxt(4);
            Toast.makeText(getApplicationContext(), R.string.you_won, Toast.LENGTH_LONG).show();
        }
        else{
            setScoreTxt(-4);
            Toast.makeText(getApplicationContext(), R.string.you_lost, Toast.LENGTH_LONG).show();
        }
        isSolProvided = true;
    }
    private void setScoreTxt(int num){
        scores = scores + num;
        scoreTxt.setText(scores + "");
    }
    private int validate(String equation) {
        equation = equation.replaceAll("\\s+", "");
        Pattern[] pattern = new Pattern[11];
        String op = "[\\+\\-/\\*]"; //operation
        String d = "\\d{1,2}";      //digit(1or2)
        pattern[0] = Pattern.compile("(" + d + op + "){3}" + d + "$");              //am1bm2cm3d
        pattern[1] = Pattern.compile("^[(]" + d + op + d + "[)]" + op + d + op + d + "$");           //"(am1b)m2cm3d"
        pattern[2] = Pattern.compile("^\\(" + d + op + d + op + d + "\\)" + op + d + "$");            //"(am1bm2c) m3 d"
        pattern[3] = Pattern.compile("^\\(\\(" + d + op + d + "\\)" + op + d + "\\)" + op + d + "$");   //"((am1b)m2c)m3d"
        pattern[4] = Pattern.compile("^\\(" + d + op + "\\(" + d + op + d + "\\)\\)" + op + d + "$");    //"(am1(bm2c))m3d"
        pattern[5] = Pattern.compile("^" + d + op + "\\(" + d + op + d + "\\)" + op + d + "$");          //"am1(bm2c)m3d"
        pattern[6] = Pattern.compile("^" + d + op + "\\(" + d + op + d + op + d + "\\)$");             //"am1(bm2cm3d)"
        pattern[7] = Pattern.compile("^" + d + op + "\\(\\(" + d + op + d + "\\)" + op + d + "\\)$");     //"am1((bm2c)m3d)"
        pattern[8] = Pattern.compile("^" + d + op + "\\(" + d + op + "\\(" + d + op + d + "\\)\\)$");     //"am1(bm2(cm3d))"
        pattern[9] = Pattern.compile("^" + d + op + d + op + "\\(" + d + op + d + "\\)$");             //"am1bm2(cm3d)"
        pattern[10] = Pattern.compile("^\\(" + d + op + d + "\\)" + op + "\\(" + d + op + d + "\\)$");     //"(am1b)m2(cm3d)"

        for (int i = 0; i < 11; i++) {
            if (pattern[i].matcher(equation).find())
                return i;
        }
        return -1;
    }

    private int[] valNums(String equation) {
        String temp = equation.replaceAll("\\s+", "");
        temp = temp.replaceAll("[\\(\\)]", "");
        temp = temp.replaceAll("[\\+\\-/\\*]", " ");
        String[] strNums = temp.split(" ");
        int[] cardNums = new int[strNums.length];
        for (int i = 0; i < strNums.length; i++) cardNums[i] = Integer.parseInt(strNums[i]);
        for (int i = 0; i < 4; i++) {
            temp = temp.replaceFirst(cards.get(i).getValue() + "", "");
        }
        if (Pattern.compile("\\d+").matcher(temp).find())
            return null;
        else
            return cardNums;
    }

    @NonNull
    private String[] valOperations(String equation) {
        String temp = equation.replaceAll("\\s+", "");
        temp = temp.replaceAll("[\\(\\)]", "");
        temp = temp.replaceAll("\\d{1,2}", " ");
        temp = temp.substring(1); //to get rid of the first space
        return temp.split(" ");
    }


    private final String[] operation = {"+", "-", "*", "/"};
    String equation = new String();

    private String findSolution(int[] nums) {
        int a, b, c, d;
        String m1, m2, m3;
        for (int i = 0; i < 4; i++) {
            a = nums[i];
            for (int j = 0; j < 4; j++) {
                if (i == j) continue;
                b = nums[j];
                for (int x = 0; x < 4; x++) {
                    if (x == j || x == i) continue;
                    c = nums[x];
                    for (int y = 0; y < 4; y++) {
                        if (y == x || y == j || y == i) continue;
                        d = nums[y];

                        for (int ta = 0; ta < 4; ta++) {
                            m1 = operation[ta];
                            for (int tb = 0; tb < 4; tb++) {
                                m2 = operation[tb];
                                for (int tc = 0; tc < 4; tc++) {
                                    m3 = operation[tc];
                                    for (int n = 0; n < 11; n++) {
                                        double result = calculateExp(a, b, c, d, m1, m2, m3, n);
                                        if (testResult(result)) return equation + "=24";
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return getResources().getString(R.string.no_solution);
    }

    private double calculateExp(int a, int b, int c, int d, String m1, String m2, String m3, int patternId) {
        double result = 0;
        switch (patternId) {
            //express 0----------------------am1bm2cm3d
            case 0:
                equation = a + m1 + b + m2 + c + m3 + d;
                return calculate(a, b, c, d, m1, m2, m3);
            //express 1-----------------------"(am1b)m2cm3d"
            case 1:
                equation = "(" + a + m1 + b + ")" + m2 + c + m3 + d;
                result = cal(a, b, m1);
                return cal(result, c, d, m2, m3);
            //express 2--------------------------"(am1bm2c) m3 d"
            case 2:
                equation = "(" + a + m1 + b + m2 + c + ")" + m3 + d;
                result = cal(a, b, c, m1, m2);
                return cal(result, d, m3);
            //express 3 -----------------------"((am1b)m2c)m3d"
            case 3:
                equation = "((" + a + m1 + b + ")" + m2 + c + ")" + m3 + d;
                return cal(a, b, c, d, m1, m2, m3);
            //express 4---------------------------"(am1(bm2c))m3d"
            case 4:
                equation = "(" + a + m1 + "(" + b + m2 + c + "))" + m3 + d;
                result = cal(b, c, m2);
                result = cal(a, result, m1);
                return cal(result, d, m3);
            //express 5******************************"am1(bm2c)m3d"
            case 5:
                equation = a + m1 + "(" + b + m2 + c + ")" + m3 + d;
                result = cal(b, c, m2);
                return cal(a, result, d, m1, m3);
            //express 6*****************************"am1(bm2cm3d)"
            case 6:
                equation = a + m1 + "(" + b + m2 + c + m3 + d + ")";
                result = cal(b, c, d, m2, m3);
                return cal(a, result, m1);
            //express 7------------------------------"am1((bm2c)m3d)"
            case 7:
                equation = a + m1 + "((" + b + m2 + c + ")" + m3 + d + ")";
                result = cal(b, c, m2);
                result = cal(result, d, m3);
                return cal(a, result, m1);
            //express 8-------------------------------"am1(bm2(cm3d))"
            case 8:
                equation = a + m1 + "(" + b + m2 + "(" + c + m3 + d + "))";
                result = cal(c, d, m3);
                result = cal(b, result, m2);
                return cal(a, result, m1);
            //express 9-------------------------------"am1bm2(cm3d)"
            case 9:
                equation = a + m1 + b + m2 + "(" + c + m3 + d + ")";
                result = cal(c, d, m3);
                return cal(a, b, result, m1, m2);
            //express 10------------------------------"(am1b)m2(cm3d)"
            default:
                equation = "(" + a + m1 + b + ")" + m2 + "(" + c + m3 + d + ")";
                double temp = cal(a, b, m1);
                result = cal(c, d, m3);
                return cal(temp, result, m2);
        }
    }

    public boolean testResult(double result) {
        if (result < 24.1 && result > 23.9) return true;
        else return false;
    }

    public double cal(double a, double b, String operation) {
        if (operation.equals("*")) return a * b;
        if (operation.equals("/")) return a / b;
        if (operation.equals("+")) return a + b;
        if (operation.equals("-")) ;
        return a - b;
    }

    public double cal(double a, double b, double c, String m1, String m2) {//System.out.println("inside"+a+m1+b+m2+c);
        double result;
        ArrayList<Double> cardNums = new ArrayList<>();
        ArrayList<String> listOp = new ArrayList<>();
        cardNums.add(a);
        cardNums.add(b);
        cardNums.add(c);
        listOp.add(m1);
        listOp.add(m2);

        result = calculate(cardNums, listOp);
        return result;
    }

    private double cal(int a, int b, int c, int d, String m1, String m2, String m3) {
        double result = cal(a, b, m1);
        result = cal(result, c, m2);
        result = cal(result, d, m3);
        return result;
    }

    private double calculate(int a, int b, int c, int d, String m1, String m2, String m3) {
        ArrayList<Double> cardNums = new ArrayList<>();
        ArrayList<String> listOp = new ArrayList<>();
        cardNums.add((double) a);
        cardNums.add((double) b);
        cardNums.add((double) c);
        cardNums.add((double) d);
        listOp.add(m1);
        listOp.add(m2);
        listOp.add(m3);
        return calculate(cardNums, listOp);
    }

    public final double calculate(List<Double> nums, List<String> op) {
        double result = 0;
        if (op.size() == 1) return cal(nums.get(0), nums.get(1), op.get(0));
        else if (op.size() == 0) return nums.get(0);
        else {
            while (op.contains("/") || op.contains("*")) {
                int i = -1, j = -1;
                String charOp;

                if (!op.contains("/")) {
                    i = op.indexOf("*");
                    charOp = "*";
                } else if (!op.contains("*")) {
                    i = op.indexOf("/");
                    charOp = "/";
                } else {
                    i = op.indexOf("*");
                    j = op.indexOf("/");
                    i = (i < j) ? i : j;
                    charOp = (i < j) ? "*" : "/"; //which comes first, calcultes first
                }
                result = cal(nums.get(i), nums.get(i + 1), charOp);
                nums.set(i, result);
                nums.remove(i + 1);
                op.remove(i);
                return calculate(nums, op);
            }

            while (op.contains("+") || op.contains("-")) {
                int i = -1, j = -1;
                String charOp;
                if (!op.contains("-")) {
                    i = op.indexOf("+");
                    charOp = "+";
                } else if (!op.contains("+")) {
                    i = op.indexOf("-");
                    charOp = "-";
                } else {
                    i = op.indexOf("+");
                    j = op.indexOf("-");
                    i = i < j ? i : j;
                    charOp = i < j ? "+" : "-"; //operation comes first get calculation first
                }
                result = cal(nums.get(i), nums.get(i + 1), charOp);
                nums.set(i, result);
                nums.remove(i + 1);
                op.remove(i);
                //System.out.println("new list: " + nums+" " +op+"index: "+i);
                return calculate(nums, op);
            }
            return result;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Game Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.richyan.android.poker24points/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Game Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.richyan.android.poker24points/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
