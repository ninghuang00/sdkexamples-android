package com.easemob.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.easemob.easeui.R;

public class EaseSwitchButton extends FrameLayout{

    private ImageView openImage;
    private ImageView closeImage;

    public EaseSwitchButton(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseSwitchButton(Context context) {
        this(context, null);
    }
    
    public EaseSwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EMSwitchButton);
        Drawable openDrawable = ta.getDrawable(R.styleable.EMSwitchButton_switchOpenImage);
        Drawable closeDrawable = ta.getDrawable(R.styleable.EMSwitchButton_switchCloseImage);
        ta.recycle();
        
        LayoutInflater.from(context).inflate(R.layout.ease_widget_switch_button, this);
        openImage = (ImageView) findViewById(R.id.iv_switch_open);
        closeImage = (ImageView) findViewById(R.id.iv_switch_close);
        if(openDrawable != null)
            openImage.setImageDrawable(openDrawable);
        if(closeDrawable != null)
            closeImage.setImageDrawable(closeDrawable);
        
        
    }
    
    /**
     * 开关是否为打开状态
     */
    public boolean isSwitchOpen(){
        return openImage.getVisibility() == View.VISIBLE;
    }
    
    
    
}
