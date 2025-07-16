# Godot 4.4 android Appodeal plugin

### Work in progress

This is only the first step towards integrating the Appodeal SDK to Godot 4.4, latest versions of both as of June 2025

### Setup

1. Run `./gradlew assemble`

2. Copy the folder Appodealplugin (/plugin/demo/addons/) to your godot project (res://addons)

3. Enable the plugin (Project -> Project Settings -> Plugins tab)

4. Add the following gdscript snippet on your game:
```
	if Engine.has_singleton("Appodealplugin"):
		var appodealplugin = Engine.get_singleton("Appodealplugin")
		appodealplugin.connect("signal_test", func(result): 
			print(result)
		)
		appodealplugin.check_appodeal()
```

5. Export game to android. A message should display at the bottom of the screen with the current Appodeal SDK
