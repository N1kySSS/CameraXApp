package com.ortin.camerax

import android.app.Application
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.ortin.camerax.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application(), CameraXConfig.Provider {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)

            modules(presentationModule)
        }
    }

    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig
            .Builder
            .fromConfig(Camera2Config.defaultConfig())
            .build()
    }
}
