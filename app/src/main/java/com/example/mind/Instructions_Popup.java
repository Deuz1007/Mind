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

        String topicId = getIntent().getStringExtra("topicId");
        String quizId = getIntent().getStringExtra("quizId");

        confirm_next.setOnClickListener(view -> {
            int nextItem = instructions_slider.getCurrentItem() + 1;

            if (nextItem < 4) {
                instructions_slider.setCurrentItem(nextItem, true);
                return;
            }

            if (topicId != null && quizId != null) {
                Intent intent = new Intent(Instructions_Popup.this, BooleanQuizPage.class);
                intent.putExtra("topicId", topicId);
                intent.putExtra("quizId", quizId);

                startActivity(intent);
                finish();
            }
        });

        instructions_slider = findViewById(R.id.viewPagerInstruction);
        pageDotIndicator = findViewById(R.id.page_indicator);

        instructionViewAdapter = new Instruction_view_adapter(this);

        instructions_slider.setAdapter(instructionViewAdapter);

        setUpIndicator(0);
        instructions_slider.addOnPageChangeListener(viewListener);

    }

    // Page Indicator
    public void setUpIndicator(int position){
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
            setUpIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}