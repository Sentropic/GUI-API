package com.sentropic.guiapi.gui;

import com.sentropic.guiapi.GUIAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class TemporaryGUIComponent extends GUIComponent {
    private final int duration;
    private final Map<GUI,RemoveTask> tasks = new HashMap<>();

    public TemporaryGUIComponent(String id, BaseComponent component, int offset, Alignment alignment, boolean scale, int duration) {
        super(id, component, offset, alignment, scale);
        this.duration = duration;
    }


    @Override
    public void onAdd(GUI gui) {
        RemoveTask task = new RemoveTask(gui);
        RemoveTask oldTask = tasks.put(gui, task);
        if (oldTask != null) {
            try { oldTask.cancel(); } catch (IllegalStateException ignore) { }
        }
        task.runTaskLater(GUIAPI.getPlugin(), duration);
    }

    @Override
    public void onRemove(GUI gui) {
        RemoveTask task = tasks.remove(gui);
        if (task != null) {
            try { task.cancel(); } catch (IllegalStateException ignore) { }
        }
    }

    private class RemoveTask extends BukkitRunnable {
        private final GUI gui;

        private RemoveTask(GUI gui) { this.gui = gui; }

        @Override
        public void run() { gui.remove(getID()); }
    }
}
