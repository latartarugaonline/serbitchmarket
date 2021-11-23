package it.ltm.scp.module.android.managers;

import android.content.Context;

public class ConnectionManagerIGP2030SImpl implements ConnectionManager {

    @Override
    public void init(Context context) {

    }

    @Override
    public void resume(Context context) {

    }

    @Override
    public void pause(Context context) {

    }

    @Override
    public void clearListeners() {

    }

    @Override
    public void register(ConnectionCallback callback) {
        callback.onConnected();
    }

    @Override
    public void unregister(ConnectionCallback callback) {

    }

    @Override
    public State getState() {
        return State.CONNECTED;
    }

    @Override
    public void forceReconnectWifi() {

    }
}
