package com.your.package.utils.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * A small warapper around activity lifecycles to provide actual Application lifecycle status with
 * callbacks on background/foreground changes.
 */
public class ApplicationLifecycle {

    /**
     * The Activity statuses that actually matter.
     */
    private enum ActivityStatus {
        PAUSED, RESUMED, STOPPED
    }

    private enum ApplicationStatus {
        FOREGROUND, BACKGROUND
    }

    /**
     * Static instance to handle static callback registration
     */
    private static ApplicationLifecycle mInstance;

    private ApplicationStatus mApplicationStatus;
    private List<ApplicationLifecycleCallback> mCallbacks;

    /**
     * The only public function. Allows the registration of application lifecycle callbacks, by
     * passing a context and a callback. You can register multiple callbacks, they shall be executed
     * in order of registration.
     *
     * @param context   A context.
     * @param callback  A callback.
     */
    public static void registerApplicationLifecycleCallback(Context context, ApplicationLifecycleCallback callback) {
        getInstance(context).registerApplicationLifecycleCallback(callback);
    }
    
    /**
     * Private initializer, starting on FOREGROUND since it is what actually makes sense.
     *
     * @param context A context.
     */
    private ApplicationLifecycle(Context context) {
        mCallbacks = new ArrayList<ApplicationLifecycleCallback>();
        mApplicationStatus = ApplicationStatus.FOREGROUND;
        registerActivityLifecycle(context);
    }

    private static ApplicationLifecycle getInstance(Context context) {
        if (mInstance == null)
            mInstance = new ApplicationLifecycle(context);
        return mInstance;
    }

    private void registerApplicationLifecycleCallback(ApplicationLifecycleCallback callback) {
        mCallbacks.add(callback);
    }

    private void goingToBackground() {
        if (mApplicationStatus == ApplicationStatus.FOREGROUND) {
            mApplicationStatus = ApplicationStatus.BACKGROUND;
            for (ApplicationLifecycleCallback callback : mCallbacks) {
                callback.onPause();
            }
        }
    }

    private void goingToForeground() {
        if (mApplicationStatus == ApplicationStatus.BACKGROUND) {
            mApplicationStatus = ApplicationStatus.FOREGROUND;
            for (ApplicationLifecycleCallback callback : mCallbacks) {
                callback.onResume();
            }
        }
    }

    /**
     * Background happens when an activity is paused and stopped without any resumes in the middle.
     * Foreground might happen on every resume, it is only triggered when the application's state is
     * BACKGROUND.
     *
     * @param context A context.
     */
    private void registerActivityLifecycle(Context context) {
        if (context != null) {
            Application application = (Application) context.getApplicationContext();
            application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {


                private ArrayList<ActivityStatus> statusHistory = new ArrayList<ActivityStatus>();

                @Override
                public void onActivityCreated(Activity activity, Bundle bundle) {
                }

                @Override
                public void onActivityStarted(Activity activity) {
                }

                @Override
                public void onActivityResumed(Activity activity) {
                    if (statusHistory.size() != 0)
                        statusHistory.add(ActivityStatus.RESUMED);
                    goingToForeground();
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    statusHistory.add(ActivityStatus.PAUSED);
                }


                @Override
                public void onActivityStopped(Activity activity) {
                    statusHistory.add(ActivityStatus.STOPPED);
                    handlePossibleBackground();

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                }

                private void handlePossibleBackground() {
                    if (statusHistory.get(0) == ActivityStatus.PAUSED && statusHistory.get(1) == ActivityStatus.STOPPED) {
                        goingToBackground();
                    }
                    statusHistory = new ArrayList<ActivityStatus>();
                }

            });
        } else {
            throw new NullPointerException();
        }
    }

    

}
