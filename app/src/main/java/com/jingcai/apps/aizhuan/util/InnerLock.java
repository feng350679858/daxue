package com.jingcai.apps.aizhuan.util;

import java.util.concurrent.atomic.AtomicBoolean;

public class InnerLock {
    public InnerLock() {

    }

    private AtomicBoolean flag = new AtomicBoolean(true);

    public boolean tryLock() {
        return flag.compareAndSet(true, false);
    }

    public void unlock() {
        flag.set(true);
    }
}