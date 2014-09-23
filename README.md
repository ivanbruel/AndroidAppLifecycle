AndroidAppLifecycle
===================

A small wrapper around activity lifecycles to provide actual Application lifecycle status with callbacks avoiding the implementation of the onResume() and onPause() calls on the Application class.

#Example Usage

To register for callbacks all you need is a context from an activity or your application and a callback.

```java
ApplicationLifecycle.registerApplicationLifecycleCallback(mContext, new ApplicationLifecycleCallback() {
            @Override
            public void applicationWillEnterForeground() {
                flush();
            }

            @Override
            public void applicationWillEnterBackground() {
                stopFlushTimer();
            }
        });

```
