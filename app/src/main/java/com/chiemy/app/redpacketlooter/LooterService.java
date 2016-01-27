package com.chiemy.app.redpacketlooter;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class LooterService extends AccessibilityService {

    public LooterService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("-", ">>> event = " + event.getClassName());
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                handleRedPacketNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                findRedPacketContentAndClick(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                openRedPacket(event);
                break;
        }
    }

    /**
     * 处理通知
     * @param event
     */
    private void handleRedPacketNotification(AccessibilityEvent event){
        List<CharSequence> texts = event.getText();
        int size = texts.size();
        for (int i = 0 ; i < size ; i++){
            String text = texts.get(i).toString();
            if (text.contains("[微信红包]")){
                if (event.getParcelableData() != null
                        && event.getParcelableData() instanceof Notification){
                    Notification notification = (Notification) event
                            .getParcelableData();
                    PendingIntent pendingIntent = notification.contentIntent;
                    try {
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }


    /**
     * 查找红包相关内容并点击
     * @param event
     */
    private void findRedPacketContentAndClick(AccessibilityEvent event){
        AccessibilityNodeInfo lastNodeInfo = findAccessibilityNodeInfosByText(event, "微信红包");
        if (lastNodeInfo != null){
            Parcelable parcelable = event.getParcelableData();
            Log.d("-", ">>" + parcelable.toString());
            AccessibilityNodeInfo parent = lastNodeInfo.getParent();
            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void openRedPacket(AccessibilityEvent event){
        AccessibilityNodeInfo lastNodeInfo = findAccessibilityNodeInfosByText(event, "拆红包");
        if (lastNodeInfo != null){
            lastNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private AccessibilityNodeInfo findAccessibilityNodeInfosByText(AccessibilityEvent event, String text){
        AccessibilityNodeInfo rootNodeInfo = event.getSource();
        if (rootNodeInfo == null){
            return null;
        }
        List<AccessibilityNodeInfo> childNodeInfos = rootNodeInfo.findAccessibilityNodeInfosByText(text);
        int size = childNodeInfos.size();
        rootNodeInfo.recycle();
        if (size > 0){
            return childNodeInfos.get(size - 1);
        }

        return null;
    }

    @Override
    public void onInterrupt() {
    }

}
