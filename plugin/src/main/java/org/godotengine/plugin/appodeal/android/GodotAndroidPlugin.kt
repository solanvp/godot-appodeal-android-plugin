// TODO: Update to match your plugin's package name.
package org.godotengine.plugin.appodeal.android

import android.util.Log
import android.widget.Toast
import com.appodeal.ads.Appodeal
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot

class GodotAndroidPlugin(godot: Godot): GodotPlugin(godot) {

    override fun getPluginName() = BuildConfig.GODOT_PLUGIN_NAME

    /**
     * Example showing how to declare a method that's used by Godot.
     *
     * Shows a 'Hello World' toast.
     */
    @UsedByGodot
    fun helloWorld() {
        runOnUiThread {
            Toast.makeText(activity, "Hello from Appodeal", Toast.LENGTH_LONG).show()
            Log.v(pluginName, "Hello from Appodeal")
        }
    }

    @UsedByGodot
    fun check_appodeal() {
        runOnUiThread {
            val version = Appodeal.getVersion()
            Toast.makeText(activity, "Appodeal version $version", Toast.LENGTH_LONG).show()
            Log.v(pluginName, "Appodeal version $version")
        }
    }
}
