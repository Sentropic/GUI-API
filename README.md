# GUI API
Minecraft API plugin for display of visual elements in the player's screen, such as text, images or interface components.

Requires the usage of the Negative Space Resource Pack by AmberW:

https://www.spigotmc.org/threads/negative-space-font-resource-pack.440952/

Reminder that you can include AmberW's resource pack in your own resource pack (see the link for details).
You can also set a server resource pack in server.properties, or by using a plugin such as ForceResourcePack:

https://www.spigotmc.org/resources/forceresourcepack.6097/

## For server owners:
If you are a server owner, you might want to add this plugin to your server for mainly two purposes:

### Resolve conflicting action bars coming from different plugins
GUI API will store the conflicting action bar texts and cycle through them, displaying them one by one at a configurable rate.

### Make depending plugins work!
Through the use of GUI API, other plugins can achieve amazing visual effects, displaying information relevant to their functionality or gameplay.

## For developers:
You can use GUI API as a dependency to display information and other visual elements, further customizing the looks of your plugin.
Possible GUI Components include text in different places of the screen, and images added through a resource pack.
You get to choose the position and order in which each individual GUI component is rendered.
