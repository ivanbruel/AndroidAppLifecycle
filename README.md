AndroidAppLifecycle
===================

A small wrapper around activity lifecycles to provide actual Application lifecycle status with callbacks. (API Level >= 14)

#Example Usage

To register for callbacks all you need is a context from an activity or your application and a callback.

```java
ApplicationLifecycle.registerApplicationLifecycleCallback(mContext, new ApplicationLifecycleCallback() {
            @Override
            public void onResume() {
                flush();
            }

            @Override
            public void onPause() {
                stopFlushTimer();
            }
        });

```
