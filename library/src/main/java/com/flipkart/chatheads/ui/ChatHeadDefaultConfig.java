package com.flipkart.chatheads.ui;

import android.content.Context;
import android.graphics.Point;

import com.flipkart.chatheads.ChatHeadUtils;

/**
 * Created by kiran.kumar on 06/05/15.
 */
public class ChatHeadDefaultConfig extends ChatHeadConfig {
    public ChatHeadDefaultConfig(Context context) {
        int diameter = 60;
        int closeBtnSize = diameter + 4;
        setHeadHeight(ChatHeadUtils.dpToPx(context,diameter));
        setHeadWidth(ChatHeadUtils.dpToPx(context, diameter));
        setHeadHorizontalSpacing(ChatHeadUtils.dpToPx(context, 10));
        setHeadVerticalSpacing(ChatHeadUtils.dpToPx(context, 5));
        setInitialPosition(new Point(0,ChatHeadUtils.dpToPx(context,0)));
        setCloseButtonHidden(false);
        setCloseButtonWidth(ChatHeadUtils.dpToPx(context, closeBtnSize));
        setCloseButtonHeight(ChatHeadUtils.dpToPx(context, closeBtnSize));
        setCloseButtonBottomMargin(ChatHeadUtils.dpToPx(context, 50));
        setCircularRingWidth(ChatHeadUtils.dpToPx(context,closeBtnSize+5));
        setCircularRingHeight(ChatHeadUtils.dpToPx(context,closeBtnSize+5));
        setMaxChatHeads(5);
    }

    @Override
    public int getCircularFanOutRadius(int maxWidth, int maxHeight) {
        return (int) (maxWidth/2.5f);
    }
}
