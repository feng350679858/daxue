package com.jingcai.apps.aizhuan.util;

import java.util.concurrent.Executor;

public class AzExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        new Thread(command).start();
    }
}