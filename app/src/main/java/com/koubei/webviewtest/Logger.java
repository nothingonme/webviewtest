package com.koubei.webviewtest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class Logger {

    public static Logger withTag(@Nullable String tag) {
        return new Logger(tag);
    }

    private Logger(@Nullable String tag) {
        this.tag = "KOUBEI_TEST_" + tag;
    }

    private String tag;

    public void d(@NonNull String msg) {
        Log.d(tag, msg);
    }

    public void e(@NonNull String msg) {
        Log.e(tag, msg);
    }
}
