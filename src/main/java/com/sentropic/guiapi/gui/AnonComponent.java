package com.sentropic.guiapi.gui;

import com.sentropic.guiapi.GUIAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class AnonComponent extends GUIComponent {
    private RemoveTask task;
    private final GUI gui;

    AnonComponent(@NotNull BaseComponent component, @NotNull GUI gui) {
        super(GUI.ID_DEFAULT, component, 0, Alignment.CENTER, true);
        this.gui = gui;
    }

    void refresh() {
        if (task != null) {
            try {
                task.cancel();
            } catch (IllegalArgumentException ignored) { }
        }
        task = new RemoveTask();
        task.runTaskLater(GUIAPI.getPlugin(), GUIAPI.getGUIConfig().getAnonDuration());
    }

    void cancelTask() {
        if (task != null) {
            try {
                task.cancel();
            } catch (IllegalArgumentException ignored) { }
        }
    }

    private class RemoveTask extends BukkitRunnable {
        @Override
        public void run() { gui.removeAnonComponent(AnonComponent.this); }
    }
}
