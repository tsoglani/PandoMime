package pandomime.dr.tsoglanakos.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class MoviesGameActivity extends AppCompatActivity implements RewardedVideoAdListener{

    private boolean isPlayerOne = true;
    private int gameRounds = 5;
    private int totalTime;
    private int curentTimer = 0;
    private int curentRound = 0;
    private int playerAScore = 0;
    private int playerBScore = 0;
    private int curentScore = 0;

    private ArrayList<String> movies = new ArrayList();
    private int totalSkip = 3;

    private int playerACountSkips = 0, playerBCountSkips = 0;


    private ImageView imageView, game_image_view;
    private TextView pandomima_text, count_down, player_a_scores, player_b_scores, show_round_text, skips_text_view;
    private Button start_game_button, skip_button;
    private RelativeLayout game_center_layout;
    private Button correct_button, wrong_button;
    private Thread timerThread;
    private boolean isCountingDown = false;
    private Steps curentsStep;
    private LinearLayout scoreView;
    private MediaPlayer mp;
    private String skips_textString;
 PowerManager.WakeLock wakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_game);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wakeLock.acquire();
        imageView = (ImageView) findViewById(R.id.player_icons);
        start_game_button = (Button) findViewById(R.id.start_game_button);
        correct_button = (Button) findViewById(R.id.correct_button);
        wrong_button = (Button) findViewById(R.id.wrong_button);

        game_center_layout = (RelativeLayout) findViewById(R.id.game_center_layout);
        totalTime = getValue(MainActivity.TIME, 120);
        gameRounds = getValue(MainActivity.ROUNDS, 5);

        totalSkip = (gameRounds / 5) * 3;


        skip_button = (Button) findViewById(R.id.skip_button);
        game_image_view = (ImageView) findViewById(R.id.game_image_view);
        pandomima_text = (TextView) findViewById(R.id.pandomima_text);
        show_round_text = (TextView) findViewById(R.id.show_round_text);
        skips_text_view = (TextView) findViewById(R.id.skips_text_view);
        count_down = (TextView) findViewById(R.id.count_down);
        curentsStep = Steps.player1_ready_to_play;

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup.LayoutParams default_layout_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scoreView = (LinearLayout) inflater.inflate(R.layout.score_layout, null);
        addContentView(scoreView, default_layout_params);
        init();


        initListeners();
        initComponents();
        scoreView.setVisibility(View.INVISIBLE);
        player_a_scores = (TextView) scoreView.getChildAt(0);
        player_b_scores = (TextView) scoreView.getChildAt(1);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "madame_cosmetic.ttf");
        player_a_scores.setTypeface(tf);
        player_b_scores.setTypeface(tf);


        Typeface tfpandomima_text = Typeface.createFromAsset(this.getAssets(), "Gecko_PersonalUseOnly.ttf");
        player_a_scores.setTypeface(tf);

        pandomima_text.setTypeface(tfpandomima_text);

        if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
            start_game_button.setBackgroundResource(R.drawable.aisthisiakes_start_button);
            pandomima_text.setBackgroundResource(R.drawable.aisthisiakes_text_bc);
            game_image_view.setBackgroundResource(R.drawable.aisthisiakes_center);
            skip_button.setBackgroundResource(R.drawable.aisthisiakes_hint);
            imageView.setBackgroundResource(R.drawable.aisthisiakes_player_one);
        }


        skip_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
                                skip_button.setBackgroundResource(R.drawable.aisthisiakes_hint_pressed);
                            } else
                                skip_button.setBackgroundResource(R.drawable.hint2_preview);
                        }
                    });
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            runOnUiThread(new Thread() {
                                @Override
                                public void run() {
                                    if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
                                        skip_button.setBackgroundResource(R.drawable.aisthisiakes_hint);
                                    } else
                                        skip_button.setBackgroundResource(R.drawable.hint1_preview);
                                }
                            });

                            if (rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {

                                // User moved inside bounds

                                if (isPlayerOne) {
                                    if (playerACountSkips < totalSkip) {

                                                createBanner();

                                        playerACountSkips++;
                                        skips_textString = (totalSkip - playerACountSkips) + " Remain";

                                    } else {
                                        runOnUiThread(new Thread() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MoviesGameActivity.this, "You have no more Hints", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        return;
                                    }
                                } else {
                                    if (playerBCountSkips < totalSkip) {
                                        playerBCountSkips++;
                                        skips_textString = (totalSkip - playerBCountSkips) + " Remain";
                                                createBanner();


                                    } else {
                                        runOnUiThread(new Thread() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MoviesGameActivity.this, "You have no more Hints", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        return;
                                    }

                                }
                                runOnUiThread(new Thread() {
                                    @Override
                                    public void run() {
                                        pandomima_text.setText(getAPandomimaText());
                                        skips_text_view.setText(skips_textString);
                                    }
                                });
                            }
                        }
                    });
                }

                return true;
            }
        });

try {
    MobileAds.initialize(this, appID);

//        AdView  mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
    mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
    mRewardedVideoAd.setRewardedVideoAdListener(this);

//    mInterstitialAd = new InterstitialAd(this);
//    mInterstitialAd.setAdUnitId(AD_UNIT_ID);
//    mInterstitialAd.loadAd(new AdRequest.Builder().build());
//    mInterstitialAd.setAdListener(new AdListener() {
//
//        @Override
//        public void onAdLoaded() {
//            //Remove show() code from here
////            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            Toast.makeText(MoviesGameActivity.this, "eee", Toast.LENGTH_SHORT).show();
//        }
//    });
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                // Load the next interstitial.
//                super.onAdClosed();
//                try {
//                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                }catch (Exception e){
//                    e.printStackTrace();
//                }//                Toast.makeText(MoviesGameActivity.this, "onAdClosed", Toast.LENGTH_SHORT).show();
//            }
//
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                try {
//                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            public void onAdLoaded() {
//                // Load the next interstitial.
//                super.onAdLoaded();
//                try {
//                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                //                Toast.makeText(MoviesGameActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
//            }
////
//        });


    loadRewardedVideoAd();

}catch (Exception e){
    e.printStackTrace();
}
    }
    private RewardedVideoAd mRewardedVideoAd;

//    private InterstitialAd mInterstitialAd;
    private static String appID ="ca-app-pub-6197752096190071~2241123642"; //"ca-app-pub-6197752096190071~2087164686";
    private static final String AD_UNIT_ID = "ca-app-pub-6197752096190071/7877180434";//"ca-app-pub-6197752096190071/6708349242"; // admob id

// ******************************** For Admob
private void loadRewardedVideoAd() {
    mRewardedVideoAd.loadAd(AD_UNIT_ID,
            new AdRequest.Builder().build());
}
    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
        loadRewardedVideoAd();

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    public void onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }

    private void createBanner() {
try {
//    if (mInterstitialAd.isLoaded()) {
//        mInterstitialAd.show();
////        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//
//
//    } else {
//    }

    if (mRewardedVideoAd.isLoaded()) {
        mRewardedVideoAd.show();
    }else{
        loadRewardedVideoAd();

    }
}catch (Exception e){
    e.printStackTrace();
}
    }


    private void showScore() {
        isCountingDown = false;
        runOnUiThread(new Thread() {
            @Override
            public void run() {
                correct_button.setVisibility(View.INVISIBLE);
                wrong_button.setVisibility(View.INVISIBLE);
                scoreView.setVisibility(View.VISIBLE);
                pandomima_text.setVisibility(View.INVISIBLE);
                count_down.setVisibility(View.INVISIBLE);

                if (isPlayerOne) {
                    playerAScore += curentScore;

                } else {
                    playerBScore += curentScore;
                }
                player_a_scores.setText(Integer.toString(playerAScore));
                player_b_scores.setText(Integer.toString(playerBScore));

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



    private void startCountdown() {
        isCountingDown = true;
        if (timerThread == null || !timerThread.isAlive()) {
            curentTimer = totalTime;

            if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
//Indigo
                runOnUiThread(new Thread(){
                    @Override
                    public void run() {
                        count_down.setTextColor(getResources().getColor(R.color.black));


                    }
                });
            }


                timerThread = new Thread() {
                @Override
                public void run() {
                    while (curentTimer >= 0 && isCountingDown) {
                        try {

                            int[] times = splitToComponentTimes(curentTimer);
                            final int min = times[0];
                            final int sec = times[1];
                            runOnUiThread(new Thread() {
                                @Override
                                public void run() {
                                    String minText, secText;

                                    if (min < 10) {
                                        minText = "0" + min;

                                    } else {
                                        minText = Integer.toString(min);
                                    }
                                    if (sec < 10) {
                                        secText = "0" + sec;

                                    } else {
                                        secText = Integer.toString(sec);
                                    }


                                    count_down.setText(minText + "m  " + secText + "s");
                                }
                            });

                            sleep(1000);
                            curentTimer--;
                            if (curentTimer < 0) {

                                try {

                                    runOnUiThread(new Thread() {
                                        @Override
                                        public void run() {
                                            try {
                                                playSound("finishtime");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                } catch (Exception e) {

                                }

                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    timerThread = null;
                }
            };
            timerThread.start();
        }
    }

    private void playSound2(String str) {
        AssetFileDescriptor afd;
        try {
            afd = getAssets().openFd(str);

            MediaPlayer player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                    afd.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void playFinishSound() {
        try {
            mp.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    protected void playSound(String fname) {
        int resID = getResources().getIdentifier(fname, "raw", getPackageName());

        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), resID);
        mediaPlayer.start();
    }


    private static int[] splitToComponentTimes(int longVal) {


        int mins = longVal / 60;
        int remainder = 0;

        remainder = longVal - mins * 60;
        int secs = remainder;

        int[] ints = {mins, secs};
        return ints;
    }

    private Rect rect;    // Variable rect to hold the bounds of the view

    private void initListeners() {
        start_game_button.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
                                start_game_button.setBackgroundResource(R.drawable.aisthisiakes_start_button_pressed);
                            } else
                                start_game_button.setBackgroundResource(R.drawable.newgamebutton1_preview);
                        }
                    });
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
                                start_game_button.setBackgroundResource(R.drawable.aisthisiakes_start_button);
                            } else
                                start_game_button.setBackgroundResource(R.drawable.start_game_button);
                        }
                    });

                    if (rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                        // User moved outside bounds
                        next();
                    }

                }
                return true;
            }
        });
        scoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });


        wrong_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        wrong_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            wrong_button.setBackgroundResource(R.drawable.error_pressed);
                        }
                    });
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            runOnUiThread(new Thread() {
                                @Override
                                public void run() {
                                    wrong_button.setBackgroundResource(R.drawable.error);
                                }
                            });

                            if (rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {

                                // User moved inside bounds
                                next();

                            }
                        }
                    });
                }

                return true;
            }
        });


        correct_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            correct_button.setBackgroundResource(R.drawable.correct_pressed);
                        }
                    });
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            runOnUiThread(new Thread() {
                                @Override
                                public void run() {
                                    correct_button.setBackgroundResource(R.drawable.right);
                                }
                            });

                            if (rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {

                                // User moved inside bounds


                                curentScore += 20 + (int) (((float) curentTimer / totalTime * 20.0));
                                next();

                            }
                        }
                    });
                }

                return true;
            }
        });

    }



    private enum Steps {
        player1_ready_to_play, player1_text, player1_game, scoreAfterPlayer1, player2_ready_to_play,
        player2_text, player2_game, scoreAfterPlayer2
    }

    private void enumPlusPlus() {

        if (curentsStep == Steps.player1_ready_to_play) {
            curentsStep = Steps.player1_text;
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    start_game_button.setVisibility(View.VISIBLE);
                    skip_button.setVisibility(View.VISIBLE);
                    skips_text_view.setVisibility(View.VISIBLE);
                    skips_text_view.setText((totalSkip - playerACountSkips) + " Remains");

                }
            });
        } else if (curentsStep == Steps.player1_text) {

            curentsStep = Steps.player1_game;

            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    start_game_button.setVisibility(View.INVISIBLE);
                    skip_button.setVisibility(View.INVISIBLE);
                    skips_text_view.setVisibility(View.INVISIBLE);
                    correct_button.setVisibility(View.VISIBLE);
                    wrong_button.setVisibility(View.VISIBLE);
                    startGame();
                }
            });
        } else if (curentsStep == Steps.player1_game) {
            curentsStep = Steps.scoreAfterPlayer1;
            showScore();
        } else if (curentsStep == Steps.scoreAfterPlayer1) {
            curentsStep = Steps.player2_ready_to_play;
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    scoreView.setVisibility(View.INVISIBLE);
                    pandomima_text.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
                        imageView.setBackgroundResource(R.drawable.aisthisiakes_player_two);

                    } else
                        imageView.setBackgroundResource(R.drawable.player_two);
                }
            });

            isPlayerOne = false;

        } else if (curentsStep == Steps.player2_ready_to_play) {
            curentsStep = Steps.player2_text;
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    start_game_button.setVisibility(View.VISIBLE);
                    skip_button.setVisibility(View.VISIBLE);
                    skips_text_view.setText((totalSkip - playerBCountSkips) + " Remains");
                    skips_text_view.setVisibility(View.VISIBLE);

                }
            });

        } else if (curentsStep == Steps.player2_text) {
            curentsStep = Steps.player2_game;
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    start_game_button.setVisibility(View.INVISIBLE);
                    skip_button.setVisibility(View.INVISIBLE);
                    skips_text_view.setVisibility(View.INVISIBLE);
                    correct_button.setVisibility(View.VISIBLE);
                    wrong_button.setVisibility(View.VISIBLE);
                    startGame();
                }
            });
        } else if (curentsStep == Steps.player2_game) {
            curentsStep = Steps.scoreAfterPlayer2;
            showScore();
        } else if (curentsStep == Steps.scoreAfterPlayer2) {
            curentsStep = Steps.player1_ready_to_play;
            curentRound++;
            if (curentRound >= gameRounds) {
///// if game ends
                gameOver();
            }
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    scoreView.setVisibility(View.INVISIBLE);
                    pandomima_text.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);

                    if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
                        imageView.setBackgroundResource(R.drawable.aisthisiakes_player_one);

                    } else
                        imageView.setBackgroundResource(R.drawable.player_one);
                    show_round_text.setText("Round " + (curentRound + 1));

                }
            });
            isPlayerOne = true;


        }


    }

    private void gameOver() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup.LayoutParams default_layout_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final RelativeLayout finalView = (RelativeLayout) inflater.inflate(R.layout.final_view, null);
        finalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MoviesGameActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                runOnUiThread(new Thread() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
            }
        });
        if (playerAScore > playerBScore) {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    addContentView(finalView, default_layout_params);
                    if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
                        finalView.setBackgroundResource(R.drawable.aisthisiakes_player_one_won);

                    } else
                        finalView.setBackgroundResource(R.drawable.player_one_won);
                }
            });
        } else if (playerAScore < playerBScore) {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    addContentView(finalView, default_layout_params);
                    if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
                        finalView.setBackgroundResource(R.drawable.aisthisiakes_player_two_won);

                    } else
                        finalView.setBackgroundResource(R.drawable.player_two_won);

                }
            });
        } else {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    addContentView(finalView, default_layout_params);
                    if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
                        finalView.setBackgroundResource(R.drawable.aisthisiakes_draw);

                    } else
                        finalView.setBackgroundResource(R.drawable.draw);

                }
            });
        }

    }

    private void startGame() {
        curentScore = 0;
        startCountdown();

        runOnUiThread(new Thread() {
            @Override
            public void run() {

                count_down.setVisibility(View.VISIBLE);
            }
        });

    }

    private void next() {

        enumPlusPlus();// allazei state
        if (curentsStep == (Steps.player1_text) || curentsStep == (Steps.player2_text)) {


            pandomima_text.setText(getAPandomimaText());
        }


    }




    private String getAPandomimaText() {
        String str = null;
        int randomTextId = (int) (Math.random() * movies.size());
        str = movies.remove(randomTextId);

        return str;
    }

    private void initComponents() {
        mp = MediaPlayer.create(getApplicationContext(), R.raw.finishtime);// the song is a filename which i have pasted inside a folder **raw** created under the **res** folder.//

    }

    private void init() {
        String[] myResArray = null;
        if (MainActivity.selectedType == MainActivity.TYPE.tainies) {
            myResArray = getResources().getStringArray(R.array.movies);
        } else if (MainActivity.selectedType == MainActivity.TYPE.seires) {
            myResArray = getResources().getStringArray(R.array.seires);
        } else if (MainActivity.selectedType == MainActivity.TYPE.diafimiseis) {
            myResArray = getResources().getStringArray(R.array.diafimiseis);
        } else if (MainActivity.selectedType == MainActivity.TYPE.aisthisiakes) {
            myResArray = getResources().getStringArray(R.array.aisthisiakes);
        } else if (MainActivity.selectedType == MainActivity.TYPE.paroimies) {
            myResArray = getResources().getStringArray(R.array.paroimies);
        }


        if (MainActivity.selectedType == MainActivity.TYPE.mix) {


            List<String> myResArrayList = new ArrayList<>();
            myResArrayList.addAll(Arrays.asList(getResources().getStringArray(R.array.paroimies)));
            myResArrayList.addAll(Arrays.asList(getResources().getStringArray(R.array.movies)));
            myResArrayList.addAll(Arrays.asList(getResources().getStringArray(R.array.seires)));
            myResArrayList.addAll(Arrays.asList(getResources().getStringArray(R.array.diafimiseis)));

            movies = new ArrayList<String>(myResArrayList);
            movies = removeDuplicates(movies);


        } else {

            List<String> myResArrayList = (Arrays.asList(myResArray));
            movies = new ArrayList<String>(myResArrayList);
            movies = removeDuplicates(movies);

        }
    }


    static ArrayList<String> removeDuplicates(ArrayList<String> list) {

        // Store unique items in result.
        ArrayList<String> result = new ArrayList<>();

        // Record encountered Strings in HashSet.
        HashSet<String> set = new HashSet<>();

        // Loop over argument list.
        for (String item : list) {

            // If String is not in set, add it to the list and the set.
            if (!set.contains(item)) {
                result.add(item);
                set.add(item);
            }
        }
        return result;
    }

    public void addMovie(String movie) {
        if (!movies.contains(movie)) {
            movies.add(movie);
        }
    }

    public void player_icons(final View v) {

        runOnUiThread(new Thread() {
            @Override
            public void run() {
                ImageView b = (ImageView) v;
                b.setVisibility(View.INVISIBLE);
            }
        });
        next();

    }

    private int getValue(String key, int defaultValue) {


        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        int myIntValue = sp.getInt(key, defaultValue);
        return myIntValue;
    }




    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
        this.wakeLock.release();
        isCountingDown=false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }



        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            isCountingDown=false;

            finish();
          System.gc();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click BACK again to go to menu", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }




}
