package com.mindapps.mind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class Instruction_view_adapter extends PagerAdapter {

    Context context;
    MediaPlayer buttonClickSound;

    int images[] = {
            R.drawable.how_to_play_text,
            R.drawable.instruction_page1,
            R.drawable.instruction_page2,
            R.drawable.instrruction_page3
    };

    public Instruction_view_adapter(Context context){

        this.context = context;
        buttonClickSound = MediaPlayer.create(context, R.raw.btn_click3);
    }

    @Override
    public int getCount() {

        return images.length;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view == (LinearLayout) object;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.instruction_slider, container, false);

        ImageView instruction_image = (ImageView) view.findViewById(R.id.instruction_image);

        instruction_image.setImageResource(images[position]);
        buttonClickSound.start();
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
