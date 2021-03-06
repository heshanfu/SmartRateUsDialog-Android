package guy4444.smartrate;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SmartRate {

    private static final long DONT_ASK_AGAIN_VALUE = -1;
    private static final String SP_LIBRARY_NAME = "SP_RATE_LIBRARY";
    private static final String SP_KEY_LAST_ASK_TIME = "SP_KEY_LAST_ASK_TIME";
    private static final String SP_KEY_INIT_TIME = "SP_KEY_INIT_TIME";
    private static int selectedStar = 1;
    private static final long DEFAULT_TIME_BETWEEN_DIALOG_MS = 1000l * 60 * 60 * 24 * 6; // 3 days
    private static final long DEFAULT_DELAY_TO_ACTIVATE_MS = 1000l * 60 * 60 * 24 * 3; // 3 days
    private static String DEFAULT_TEXT_TITLE = "Rate Us";
    private static String DEFAULT_TEXT_CONTENT = "Tell others what you think about this app";
    private static String DEFAULT_TEXT_OK = "Continue";
    private static String DEFAULT_TEXT_LATER = "Ask me later";
    private static String DEFAULT_TEXT_STOP = "Never ask again";
    private static String DEFAULT_TEXT_THANKS = "Thanks for the feedback";

    public static void Rate(
            final Activity activity
            , final String _title
            , final String _content
            , final String _ok_text
            , final String _cancel_text
            , final String _thanksForFeedback
            , final int mainColor
            , final int openStoreFromXStars
    ) {
        Rate(activity
                , _title
                , _content
                , _ok_text
                , _cancel_text
                , ""
                , _thanksForFeedback
                , mainColor
                , openStoreFromXStars
                , -1
                , -1);
    }

    public static void Rate(
            final Activity activity
            , final String _title
            , final String _content
            , final String _ok_text
            , final String _later_text
            , final String _stop_text
            , final String _thanksForFeedback
            , final int mainColor
            , final int openStoreFromXStars
            , final int _hoursBetweenCalls
            , final int _hoursDelayToActivate
    ) {

        final String title = (_title != null && !_title.equals("")) ? _title : DEFAULT_TEXT_TITLE;
        final String content = (_content != null && !_content.equals("")) ? _content : DEFAULT_TEXT_CONTENT;
        final String ok_text = (_ok_text != null && !_ok_text.equals("")) ? _ok_text : DEFAULT_TEXT_OK;
        final String later_text = (_later_text != null && !_later_text.equals("")) ? _later_text : DEFAULT_TEXT_LATER;
        final String stop_text = (_stop_text != null && !_stop_text.equals("")) ? _stop_text : DEFAULT_TEXT_STOP;
        final String thanksForFeedback = (_thanksForFeedback != null && !_thanksForFeedback.equals("")) ? _thanksForFeedback : DEFAULT_TEXT_THANKS;
        final long timeBetweenCalls_Ms = (_hoursBetweenCalls >= 1 && _hoursBetweenCalls < 366 * 24) ? 1000l * 60 * 60 * _hoursBetweenCalls : DEFAULT_TIME_BETWEEN_DIALOG_MS;
        final long timeDelayToActivate_Ms = (_hoursDelayToActivate >= 1 && _hoursDelayToActivate < 366 * 24) ? 1000l * 60 * 60 * _hoursDelayToActivate : DEFAULT_DELAY_TO_ACTIVATE_MS;


        if (_hoursBetweenCalls != -1  &&  _hoursDelayToActivate != -1) {
            // no force asking mode
            long initTime = getInitTime(activity);
            if (initTime == 0) {
                setInitTime(activity, System.currentTimeMillis());
                return;
            }
            if (System.currentTimeMillis() < initTime + timeDelayToActivate_Ms) {
                return;
            }

            if (getLastAskTime(activity) == DONT_ASK_AGAIN_VALUE) {
                // user already rate or click on never ask button
                return;
            }
            if (System.currentTimeMillis() < getLastAskTime(activity) + timeBetweenCalls_Ms) {
                // There was not enough time between the calls.
                return;
            }
        }

        setLastAskTime(activity, System.currentTimeMillis());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rate, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);

        final RelativeLayout alert_LAY_back = (RelativeLayout) dialogView.findViewById(R.id.alert_LAY_back);
        final AppCompatButton alert_BTN_ok = (AppCompatButton) dialogView.findViewById(R.id.alert_BTN_ok);
        final Button alert_BTN_later = (Button) dialogView.findViewById(R.id.alert_BTN_later);
        final Button alert_BTN_stop = (Button) dialogView.findViewById(R.id.alert_BTN_stop);
        final TextView alert_LBL_title = (TextView) dialogView.findViewById(R.id.alert_LBL_title);
        final TextView alert_LBL_content = (TextView) dialogView.findViewById(R.id.alert_LBL_content);
        final ImageButton alert_BTN_star_1 = (ImageButton) dialogView.findViewById(R.id.alert_BTN_star_1);
        final ImageButton alert_BTN_star_2 = (ImageButton) dialogView.findViewById(R.id.alert_BTN_star_2);
        final ImageButton alert_BTN_star_3 = (ImageButton) dialogView.findViewById(R.id.alert_BTN_star_3);
        final ImageButton alert_BTN_star_4 = (ImageButton) dialogView.findViewById(R.id.alert_BTN_star_4);
        final ImageButton alert_BTN_star_5 = (ImageButton) dialogView.findViewById(R.id.alert_BTN_star_5);
        final ImageButton[] stars = new ImageButton[]{alert_BTN_star_1, alert_BTN_star_2, alert_BTN_star_3, alert_BTN_star_4, alert_BTN_star_5};


        alert_LAY_back.setBackgroundColor(mainColor);
        alert_BTN_ok.getBackground().setColorFilter(mainColor, PorterDuff.Mode.MULTIPLY);
        alert_LBL_title.setTextColor(mainColor);
        alert_LBL_content.setTextColor(mainColor);
        alert_BTN_later.setTextColor(Color.parseColor(shadeColor(String.format("#%06X", 0xFFFFFF & mainColor), -33)));
        alert_BTN_stop.setTextColor(Color.parseColor(shadeColor(String.format("#%06X", 0xFFFFFF & mainColor), -33)));


        final int drawable_active = R.drawable.ic_star_active;
        final int drawable_deactive = R.drawable.ic_star_deactive;

        View.OnClickListener starsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedIndex = -1;
                for (int i = 0; i < stars.length; i++) {
                    if (stars[i].getId() == v.getId()) {
                        clickedIndex = i;
                        break;
                    }
                }

                if (clickedIndex != -1) {
                    for (int i = 0; i <= clickedIndex; i++) {
                        stars[i].setImageResource(drawable_active);
                    }
                    for (int i = clickedIndex + 1; i < stars.length; i++) {
                        stars[i].setImageResource(drawable_deactive);
                    }
                }

                alert_BTN_ok.setEnabled(true);
                alert_BTN_ok.setText((clickedIndex + 1) + "/5\n" + ok_text);
                selectedStar = clickedIndex + 1;
            }
        };

        alert_BTN_star_1.setOnClickListener(starsClickListener);
        alert_BTN_star_2.setOnClickListener(starsClickListener);
        alert_BTN_star_3.setOnClickListener(starsClickListener);
        alert_BTN_star_4.setOnClickListener(starsClickListener);
        alert_BTN_star_5.setOnClickListener(starsClickListener);


        alert_LBL_title.setText(title);
        alert_LBL_content.setText(content);


        if (ok_text != null && !ok_text.equals("")) {
            alert_BTN_ok.setText(ok_text);
            alert_BTN_ok.setText("?/5\n" + ok_text);
            alert_BTN_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setLastAskTime(activity, DONT_ASK_AGAIN_VALUE);

                    int _openStoreFrom_Stars = openStoreFromXStars;
                    if (openStoreFromXStars < 1 || openStoreFromXStars > 5) {
                        _openStoreFrom_Stars = 1;
                    }
                    if (selectedStar >= _openStoreFrom_Stars) {
                        launchMarket(activity);
                    } else {
                        Toast.makeText(activity, thanksForFeedback, Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                }
            });
        } else {
            alert_BTN_ok.setVisibility(View.INVISIBLE);
        }
        alert_BTN_ok.setEnabled(false);

        if (later_text != null && !later_text.equals("")) {
            alert_BTN_later.setText(later_text);
            alert_BTN_later.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    alertDialog.dismiss();
                }
            });
        } else {
            alert_BTN_later.setVisibility(View.INVISIBLE);
        }

        if (stop_text != null && !stop_text.equals("")) {
            alert_BTN_stop.setText(stop_text);
            alert_BTN_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setLastAskTime(activity, DONT_ASK_AGAIN_VALUE);
                    alertDialog.dismiss();
                }
            });
        } else {
            alert_BTN_stop.setVisibility(View.INVISIBLE);
        }


        if (_hoursBetweenCalls == -1  &&  _hoursDelayToActivate == -1) {
            // force asking mode
            alert_BTN_stop.setVisibility(View.GONE);
        }

        alertDialog.show();
    }

    private static void launchMarket(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, " unable to find google play app", Toast.LENGTH_LONG).show();
        }
    }

    private static long getLastAskTime(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SP_LIBRARY_NAME, Context.MODE_PRIVATE);
        long val = sharedPreferences.getLong(SP_KEY_LAST_ASK_TIME, 0);
        return val;
    }

    private static void setLastAskTime(Activity activity, long time) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(SP_LIBRARY_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(SP_KEY_LAST_ASK_TIME, time);
        editor.apply();
    }

    private static long getInitTime(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SP_LIBRARY_NAME, Context.MODE_PRIVATE);
        long val = sharedPreferences.getLong(SP_KEY_INIT_TIME, 0);
        return val;
    }

    private static void setInitTime(Activity activity, long time) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(SP_LIBRARY_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(SP_KEY_INIT_TIME, time);
        editor.apply();
    }

    private static String shadeColor(String color, int percent) {

        int R = Integer.parseInt(color.substring(1, 3), 16);
        int G = Integer.parseInt(color.substring(3, 5), 16);
        int B = Integer.parseInt(color.substring(5, 7), 16);

        R = R * (100 + percent) / 100;
        G = G * (100 + percent) / 100;
        B = B * (100 + percent) / 100;

        R = (R < 255) ? R : 255;
        G = (G < 255) ? G : 255;
        B = (B < 255) ? B : 255;

        String RR = (Integer.toString(R, 16).length() == 1) ? "0" + Integer.toString(R, 16) : Integer.toString(R, 16);
        String GG = (Integer.toString(G, 16).length() == 1) ? "0" + Integer.toString(G, 16) : Integer.toString(G, 16);
        String BB = (Integer.toString(B, 16).length() == 1) ? "0" + Integer.toString(B, 16) : Integer.toString(B, 16);

        return "#" + RR + GG + BB;
    }
}
