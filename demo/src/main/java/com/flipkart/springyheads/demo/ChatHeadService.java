package com.flipkart.springyheads.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flipkart.chatheads.ui.ChatHead;
import com.flipkart.chatheads.ui.ChatHeadViewAdapter;
import com.flipkart.chatheads.ui.MaximizedArrangement;
import com.flipkart.chatheads.ui.MinimizedArrangement;
import com.flipkart.chatheads.ui.container.DefaultChatHeadManager;
import com.flipkart.chatheads.ui.container.WindowManagerContainer;
import com.flipkart.circularImageView.CircularDrawable;
import com.flipkart.circularImageView.TextDrawer;
import com.flipkart.circularImageView.notification.CircularNotificationDrawer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChatHeadService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private DefaultChatHeadManager<String> chatHeadManager;
    private int chatHeadIdentifier = 0;
    private WindowManagerContainer windowManagerContainer;
    private Map<String, View> viewCache = new HashMap<>();


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManagerContainer = new WindowManagerContainer(this);
        chatHeadManager = new DefaultChatHeadManager<String>(this, windowManagerContainer);
        chatHeadManager.setViewAdapter(new ChatHeadViewAdapter<String>() {

            @Override
            public View attachView(String key, ChatHead chatHead, ViewGroup parent) {
                View cachedView = viewCache.get(key);
                if (cachedView == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.fragment_test, parent, false);
                    TextView identifier = (TextView) view.findViewById(R.id.identifier);
                    identifier.setText(key);
                    cachedView = view;
                    viewCache.put(key, view);
                }
                parent.addView(cachedView);
                return cachedView;
            }

            @Override
            public void detachView(String key, ChatHead<? extends Serializable> chatHead, ViewGroup parent) {
                View cachedView = viewCache.get(key);
                if(cachedView!=null) {
                    parent.removeView(cachedView);
                }
            }

            @Override
            public void removeView(String key, ChatHead<? extends Serializable> chatHead, ViewGroup parent) {
                View cachedView = viewCache.get(key);
                if(cachedView!=null) {
                    viewCache.remove(key);
                    parent.removeView(cachedView);
                }
            }

            @Override
            public Drawable getChatHeadDrawable(String key) {
                return ChatHeadService.this.getChatHeadDrawable(key);
            }
        });

        addChatHead();
        addChatHead();
        addChatHead();
        addChatHead();
        chatHeadManager.setArrangement(MinimizedArrangement.class, null);
        moveToForeground();

    }

    private Drawable getChatHeadDrawable(String key) {
        Random rnd = new Random();
        int randomColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        CircularDrawable circularDrawable = new CircularDrawable();
        circularDrawable.setBitmapOrTextOrIcon(new TextDrawer().setText("C" + key).setBackgroundColor(randomColor));
        int badgeCount = (int) (Math.random() * 10f);
        circularDrawable.setNotificationDrawer(new CircularNotificationDrawer().setNotificationText(String.valueOf(badgeCount)).setNotificationAngle(135).setNotificationColor(Color.WHITE, Color.RED));
        circularDrawable.setBorder(Color.WHITE, 3);
        return circularDrawable;

    }

    private void moveToForeground() {
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            channelId = createNotificationChannel("Springy_heads_service", "Springy heads Service");
        }
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle("Springy heads")
                .setContentText("Click to configure.")
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, FloatingActivity.class), 0))
                .build();

        startForeground(1, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName){
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    public void addChatHead() {
        chatHeadIdentifier++;
        chatHeadManager.addChatHead(String.valueOf(chatHeadIdentifier), false, true);
        chatHeadManager.bringToFront(chatHeadManager.findChatHeadByKey(String.valueOf(chatHeadIdentifier)));
    }

    public void removeChatHead() {
        chatHeadManager.removeChatHead(String.valueOf(chatHeadIdentifier), true);
        chatHeadIdentifier--;
    }

    public void removeAllChatHeads() {
        chatHeadIdentifier = 0;
        chatHeadManager.removeAllChatHeads(true);
    }

    public void toggleArrangement() {
        if (chatHeadManager.getActiveArrangement() instanceof MinimizedArrangement) {
            chatHeadManager.setArrangement(MaximizedArrangement.class, null);
        } else {
            chatHeadManager.setArrangement(MinimizedArrangement.class, null);
        }
    }

    public void updateBadgeCount() {
        chatHeadManager.reloadDrawable(String.valueOf(chatHeadIdentifier));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManagerContainer.destroy();
    }

    public void minimize() {
        chatHeadManager.setArrangement(MinimizedArrangement.class,null);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        ChatHeadService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ChatHeadService.this;
        }
    }
}