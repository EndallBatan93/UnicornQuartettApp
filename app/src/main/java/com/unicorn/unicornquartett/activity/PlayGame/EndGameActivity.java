package com.unicorn.unicornquartett.activity.PlayGame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.activity.Menu.MenuActivity;

import static com.unicorn.unicornquartett.Utility.Constants.WINNER;

public class EndGameActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        final Context context = this;
        TextView endText = findViewById(R.id.endText);
        TextView endText2 = findViewById(R.id.endText2);
        String winner = getIntent().getStringExtra(WINNER);
        LinearLayout linearLayout = findViewById(R.id.mainEndGameLayout);
        if (winner.equals("user")) {
            endText.setText("You have won");
            endText.setTextColor(Color.WHITE);
            endText.setTextSize(32);

            Drawable drawable = getDrawable(R.drawable.luke);
            linearLayout.setBackground(drawable);
        } else {
            endText.setText("I find your lack of skill.");
            endText2.setText("DISGUSTING");
            endText.setTextColor(Color.WHITE);
            endText.setTextSize(20);
            endText2.setTextColor(Color.WHITE);
            Drawable drawable = getDrawable(R.drawable.vader);
            linearLayout.setBackground(drawable);
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
