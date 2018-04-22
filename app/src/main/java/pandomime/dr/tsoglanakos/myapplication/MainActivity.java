package pandomime.dr.tsoglanakos.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.warkiz.widget.IndicatorSeekBar;



public class MainActivity extends AppCompatActivity {
    private TextView button_diafimiseis, button_movies, button_paroimies, button_series, button_softcore, button_mix;
    protected static String ROUNDS = "game_rounds", TIME = "game_timer_per_game";
    private IndicatorSeekBar timer_seek_bar;
    private RadioGroup radioGroup;

    public static enum TYPE {atakes, diafimiseis, tainies, seires, aisthisiakes, paroimies, mix}

    public static TYPE selectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_diafimiseis = (TextView) findViewById(R.id.button_diafimiseis);
        button_movies = (TextView) findViewById(R.id.button_movies);
        button_paroimies = (TextView) findViewById(R.id.button_paroimies);
        button_mix = (TextView) findViewById(R.id.button_mix);
        button_series = (TextView) findViewById(R.id.button_series);
        button_softcore = (TextView) findViewById(R.id.button_softcore);
        Typeface tfpandomima_text = Typeface.createFromAsset(this.getAssets(), "Gecko_PersonalUseOnly.ttf");

        button_softcore.setTypeface(tfpandomima_text);
        button_softcore.setTextColor(getResources().getColor(R.color.Chalk));

        button_series.setTypeface(tfpandomima_text);
        button_series.setTextColor(getResources().getColor(R.color.Chalk));

        button_movies.setTypeface(tfpandomima_text);
        button_movies.setTextColor(getResources().getColor(R.color.Chalk));

        button_diafimiseis.setTypeface(tfpandomima_text);
        button_diafimiseis.setTextColor(getResources().getColor(R.color.Chalk));
        button_paroimies.setTypeface(tfpandomima_text);
        button_paroimies.setTextColor(getResources().getColor(R.color.Chalk));

        button_mix.setTypeface(tfpandomima_text);
        button_mix.setTextColor(getResources().getColor(R.color.Chalk));


        initListeners();
    }


    private void storeValue(String key, int value) {


        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();

    }

    private int getValue(String key, int defaultValue) {


        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        int myIntValue = sp.getInt(key, defaultValue);
        return myIntValue;
    }

    private Rect rectbutton_diafimiseis, rectbutton_movies, rectbutton_series, rectbutton_softcore, rectbutton_paroimies, rectbutton_mix;    // Variable rect to hold the bounds of the view

    private void initListeners() {

        button_paroimies.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    rectbutton_paroimies = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_paroimies.setBackgroundColor(getResources().getColor(R.color.costum_color2));

                        }
                    });
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_paroimies.setBackgroundColor(getResources().getColor(R.color.DarkOliveGreen));

                            if (rectbutton_paroimies.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {

                                selectedType = TYPE.paroimies;
                                Intent intent = new Intent(MainActivity.this, MoviesGameActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
                return true;
            }
        });


        button_diafimiseis.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    rectbutton_diafimiseis = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_diafimiseis.setBackgroundColor(getResources().getColor(R.color.PurpleSageBush));

                        }
                    });
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_diafimiseis.setBackgroundColor(getResources().getColor(R.color.BeeYellow));
                            if (rectbutton_diafimiseis.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                                selectedType = TYPE.diafimiseis;
                                Intent intent = new Intent(MainActivity.this, MoviesGameActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
                return true;
            }
        });


        button_movies.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    rectbutton_movies = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_movies.setBackgroundColor(getResources().getColor(R.color.DarkGreen));

                        }
                    });
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_movies.setBackgroundColor(getResources().getColor(R.color.costumGreen));
                            if (rectbutton_movies.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                                // User moved inside bounds
                                selectedType = TYPE.tainies;
                                Intent intent = new Intent(MainActivity.this, MoviesGameActivity.class);
                                startActivity(intent);
                            }

                        }
                    });
                }
                return true;
            }
        });

        button_series.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    rectbutton_series = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_series.setBackgroundColor(getResources().getColor(R.color.Olive));

                        }
                    });
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_series.setBackgroundColor(getResources().getColor(R.color.costum_orange2));

                            if (rectbutton_series.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {

                                selectedType = TYPE.seires;
                                Intent intent = new Intent(MainActivity.this, MoviesGameActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
                return true;
            }
        });


        button_mix.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    rectbutton_mix = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_mix.setBackgroundColor(getResources().getColor(R.color.costumGreen));

                        }
                    });
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_mix.setBackgroundColor(getResources().getColor(R.color.article_title));

                            if (rectbutton_mix.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {

                                selectedType = TYPE.mix;
                                Intent intent = new Intent(MainActivity.this, MoviesGameActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
                return true;
            }
        });


        button_softcore.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent event) {
                int prevCol = R.color.costum_orange;
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {


                    rectbutton_softcore = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_softcore.setBackgroundColor(getResources().getColor(R.color.purple));

                        }
                    });
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {

                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {

                            button_softcore.setBackgroundColor(getResources().getColor(R.color.red));
                            if (rectbutton_softcore.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                                runOnUiThread(new Thread() {
                                    @Override
                                    public void run() {
                                        enterOnxxx();
                                    }
                                });
                            }
                        }
                    });
                }
                return true;
            }
        });


    }


    private void enterOnxxx() {
//
//        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(this);
//        dialogBuilder.setTitle("Warning");
//        dialogBuilder.setMessage("Caution, this category may contains mature content.!");
//        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                selectedType=TYPE.aisthisiakes;
//                Intent intent = new Intent(MainActivity.this, MoviesGameActivity.class);
//                startActivity(intent);
//            }
//        });
//        dialogBuilder.setNegativeButton(android.R.string.cancel, null);
//        MaterialDialog dialog = dialogBuilder.create();
//        dialog.show();
////
        FancyAlertDialog dd=   new FancyAlertDialog.Builder(this)
                .setTitle("Warning..")
                .setBackgroundColor(Color.parseColor("#303F9F"))  //Don't pass R.color.colorvalue
                .setMessage("Caution, this category may contain mature content.!")
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.SIDE)
                .isCancellable(true)
                .setIcon(R.drawable.ic_error_outline_black_24dp, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        runOnUiThread(new Thread() {
                            @Override
                            public void run() {

                                selectedType = TYPE.aisthisiakes;
                                Intent intent = new Intent(MainActivity.this, MoviesGameActivity.class);
                                startActivity(intent);

                            }
                        });


                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                    }
                })
                .build();

    }


    public void pref_function(View v) {

        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.pref_layout))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                    }
                })
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
        timer_seek_bar = (IndicatorSeekBar) findViewById(R.id.timer_seek_bar);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int storedtime = getValue(TIME, 120);
        timer_seek_bar.setProgress(storedtime);
        int storedRounds = getValue(ROUNDS, 5);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(i);

            if (Integer.parseInt(rb.getText().toString()) == storedRounds) {

                rb.setChecked(true);
            }

        }


        initListener2();
    }


    private void initListener2() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);

                storeValue(ROUNDS, Integer.parseInt(checkedRadioButton.getText().toString()));

            }
        });
        timer_seek_bar.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
            }

            @Override
            public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {
                //only callback on discrete series SeekBar type.
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                storeValue(TIME, seekBar.getProgress());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }
}
