package it.ltm.scp.module.android.utils;

/**
 * Created by HW64 on 19/08/2016.
 */
public class Events {

    public static class SimpleMessageEvent{
        private String message;

        public SimpleMessageEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
