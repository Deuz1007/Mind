package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Instructions_Popup extends AppCompatActivity {

    ViewPager instructions_slider;
    LinearLayout pageDotIndicator;
    Button confirm_next;

    TextView[] dots;
    Instruction_view_adapter instructionViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_popup);

        confirm_next = findViewById(R.id.next_btn);

        confirm_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem(0) < 4)
                    instructions_slider.setCurrentItem(getItem(1), true);
                else {
                    Intent i = new Intent(Instructions_Popup.this, BooleanQuizPage.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        instructions_slider = (ViewPager) findViewById(R.id.viewPagerInstruction);
        pageDotIndicator = (LinearLayout) findViewById(R.id.page_indicator);

        instructionViewAdapter = new Instruction_view_adapter(this);

        instructions_slider.setAdapter(instructionViewAdapter);

        setUpindicator(0);
        instructions_slider.addOnPageChangeListener(viewListener);

    }

    // Page Indicator
    public void setUpindicator(int position){
        dots = new TextView[4];
        pageDotIndicator.removeAllViews();

        for (int i = 0; i < dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(30);
            dots[i].setTextColor(getResources().getColor(R.color.white, getApplicationContext().getTheme())); // for inactive page
            pageDotIndicator.addView(dots[i]);
        }

        // for active page
        dots[position].setTextColor(getResources().getColor(R.color.saturated, getApplicationContext().getTheme()));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            setUpindicator(position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i){

        return instructions_slider.getCurrentItem() + i;

    }
}