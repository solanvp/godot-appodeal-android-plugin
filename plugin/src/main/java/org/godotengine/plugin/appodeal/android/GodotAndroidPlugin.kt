package org.godotengine.plugin.appodeal.android

import android.util.Log
import android.widget.Toast
import com.appodeal.ads.Appodeal
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

class GodotAndroidPlugin(godot: Godot): GodotPlugin(godot) {

    override fun getPluginName() = BuildConfig.GODOT_PLUGIN_NAME

    override fun getPluginSignals(): Set<SignalInfo> {
        val signals = mutableSetOf<SignalInfo>()
        signals.add(SignalInfo("signal_test", String::class.java))
        signals.add(SignalInfo("appodeal_initialized", String::class.java))
        signals.add(SignalInfo("banner_shown", String::class.java))
        signals.add(SignalInfo("event_logged", String::class.java))
        return signals
    }


    @UsedByGodot
    fun check_appodeal() {
        runOnUiThread {
            val version = Appodeal.getVersion()
            Toast.makeText(activity, "Appodeal version $version", Toast.LENGTH_LONG).show()
            Log.v(pluginName, "Appodeal version $version")
            emitSignal("signal_test", "Hello from Appodealplugin")
        }
    }

    @UsedByGodot
    fun initialize_appodeal(appKey: String) {
        activity?.let { safeActivity ->
            Log.d(pluginName, "Initializing Appodeal with key: $appKey")
            
            Appodeal.initialize(
                context = safeActivity,
                appKey = appKey,
                adTypes = Appodeal.BANNER or Appodeal.INTERSTITIAL or Appodeal.REWARDED_VIDEO,
                callback = object : ApdInitializationCallback {
                    override fun onInitializationFinished(errors: List<ApdInitializationError>?) {
                        runOnUiThread {
                            if (errors == null) {
                                Log.v(pluginName, "Appodeal initialized successfully")
                                emitSignal("appodeal_initialized", "true")
                            } else {
                                Log.e(pluginName, "Appodeal initialization failed: $errors")
                                emitSignal("appodeal_initialized", "false")
                            }
                        }
                    }
                }
            )
        }
    }

    @UsedByGodot
    fun is_banner_loaded(): Boolean {
        return Appodeal.isLoaded(Appodeal.BANNER)
    }

    @UsedByGodot
    fun show_banner() {
        runOnUiThread {
            activity?.let { safeActivity ->
                Log.d(pluginName, "Attempting to show banner ad")
                val result = Appodeal.show(safeActivity, Appodeal.BANNER_BOTTOM)
                Log.d(pluginName, "Banner show result: $result")
                emitSignal("banner_shown", "true")
            }
        }
    }

    @UsedByGodot
    fun hide_banner() {
        runOnUiThread {
            activity?.let { safeActivity ->
                Log.d(pluginName, "Hiding banner ad")
                Appodeal.hide(safeActivity, Appodeal.BANNER_BOTTOM)
            }
        }
    }

    @UsedByGodot
    fun show_interstitial() {
        runOnUiThread {
            activity?.let { safeActivity ->
                Log.d(pluginName, "Attempting to show interstitial ad")
                val result = Appodeal.show(safeActivity, Appodeal.INTERSTITIAL)
                Log.d(pluginName, "Interstitial show result: $result")
            }
        }
    }

    @UsedByGodot
    fun is_interstitial_loaded(): Boolean {
        return Appodeal.isLoaded(Appodeal.INTERSTITIAL)
    }

    @UsedByGodot
    fun log_event(eventName: String) {
        Log.d(pluginName, "Logging event: $eventName")
        // Appodeal.trackEvent(eventName, null)
        emitSignal("event_logged", eventName)
    }

    @UsedByGodot
    fun log_event_with_parameters(eventName: String, parameters: Map<String, Any?>) {
        Log.d(pluginName, "Logging event: $eventName with parameters: $parameters")
        // Appodeal.trackEvent(eventName, parameters)
        emitSignal("event_logged", eventName)
    }
}