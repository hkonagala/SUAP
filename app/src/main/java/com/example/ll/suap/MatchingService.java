package com.example.ll.suap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MatchingService extends Service {
    myBinder mybinder_= new myBinder();

    public MatchingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mybinder_;
    }
    class myBinder extends Binder {
        MatchingService getService(){
            return MatchingService.this;
        }
    }
}
