package org.godotengine.plugin.appodeal.android

import android.util.Log
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
        signals.add(SignalInfo("rewarded_loaded", String::class.java))
        signals.add(SignalInfo("rewarded_failed", String::class.java))
        signals.add(SignalInfo("rewarded_shown", String::class.java))
        signals.add(SignalInfo("rewarded_show_failed", String::class.java))
        signals.add(SignalInfo("rewarded_clicked", String::class.java))
        signals.add(SignalInfo("rewarded_finished", String::class.java))
        signals.add(SignalInfo("rewarded_closed", String::class.java))
        signals.add(SignalInfo("rewarded_expired", String::class.java))
        signals.add(SignalInfo("interstitial_shown", String::class.java))
        signals.add(SignalInfo("interstitial_failed", String::class.java))
        return signals
    }
    

    @UsedByGodot
    fun check_appodeal() {
        runOnUiThread {
            val version = Appodeal.getVersion()
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
            Appodeal.setRewardedVideoCallbacks(object : com.appodeal.ads.RewardedVideoCallbacks {
                override fun onRewardedVideoLoaded(isPrecache: Boolean) {
                    Log.d(pluginName, "Rewarded video loaded, isPrecache: $isPrecache")
                    emitSignal("rewarded_loaded", isPrecache.toString())
                }
                override fun onRewardedVideoFailedToLoad() {
                    Log.e(pluginName, "Rewarded video failed to load!")
                    emitSignal("rewarded_failed", "false")
                }
                override fun onRewardedVideoShown() {
                    Log.d(pluginName, "Rewarded video shown!")
                    emitSignal("rewarded_shown", "true")
                }
                override fun onRewardedVideoShowFailed() {
                    Log.e(pluginName, "Rewarded video show failed!")
                    emitSignal("rewarded_show_failed", "false")
                }
                override fun onRewardedVideoClicked() {
                    Log.d(pluginName, "Rewarded video clicked!")
                    emitSignal("rewarded_clicked", "true")
                }
                override fun onRewardedVideoFinished(amount: Double, currency: String) {
                    Log.d(pluginName, "Rewarded video finished! Amount: $amount, Currency: $currency")
                    emitSignal("rewarded_finished", "true")
                }
                override fun onRewardedVideoClosed(finished: Boolean) {
                    Log.d(pluginName, "Rewarded video closed! Finished: $finished")
                    emitSignal("rewarded_closed", finished.toString())
                }
                override fun onRewardedVideoExpired() {
                    Log.d(pluginName, "Rewarded video expired!")
                    emitSignal("rewarded_expired", "false")
                }
            })
            Appodeal.setInterstitialCallbacks(object : com.appodeal.ads.InterstitialCallbacks {
                override fun onInterstitialLoaded(isPrecache: Boolean) {
                    Log.d(pluginName, "Interstitial loaded, isPrecache: $isPrecache")
                }
                override fun onInterstitialFailedToLoad() {
                    Log.e(pluginName, "Interstitial failed to load!")
                    emitSignal("interstitial_failed", "false")
                }
                override fun onInterstitialShown() {
                    Log.d(pluginName, "Interstitial shown!")
                    emitSignal("interstitial_shown", "true")
                }
                override fun onInterstitialShowFailed() {
                    Log.e(pluginName, "Interstitial show failed!")
                    emitSignal("interstitial_failed", "false")
                }
                override fun onInterstitialClicked() {
                    Log.d(pluginName, "Interstitial clicked!")
                }
                override fun onInterstitialClosed() {
                    Log.d(pluginName, "Interstitial closed!")
                }
                override fun onInterstitialExpired() {
                    Log.d(pluginName, "Interstitial expired!")
                }
            })
        }
    }

    @UsedByGodot
    fun is_banner_ready(): Boolean {
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
    fun show_rewarded() {
        runOnUiThread {
            activity?.let { safeActivity ->
                Log.d(pluginName, "Attempting to show rewarded ad")
                val result = Appodeal.show(safeActivity, Appodeal.REWARDED_VIDEO)
                Log.d(pluginName, "Rewarded show result: $result")
            }
        }
    }

    @UsedByGodot
    fun is_interstitial_ready(): Boolean {
        return Appodeal.isLoaded(Appodeal.INTERSTITIAL)
    }

    @UsedByGodot
    fun log_event(eventName: String) {
        Log.d(pluginName, "Logging event: $eventName")
        Appodeal.logEvent(eventName, null)
    }

    @UsedByGodot
    fun log_event_with_parameters(eventName: String, parameters: Map<String, Any?>) {
        Log.d(pluginName, "Logging event: $eventName with parameters: $parameters")
        Appodeal.logEvent(eventName, parameters)
    }
}