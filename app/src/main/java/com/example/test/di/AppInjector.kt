package com.example.test.di


import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.test.TestApp
import dagger.android.AndroidInjection
import dagger.android.HasAndroidInjector

object AppInjector {

    fun init(app: TestApp) {

        DaggerAppComponent.builder()
            .application(app)
            .build()
            .inject(app)

        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                if (activity is HasAndroidInjector) {
                    AndroidInjection.inject(activity)
                }
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }
}
