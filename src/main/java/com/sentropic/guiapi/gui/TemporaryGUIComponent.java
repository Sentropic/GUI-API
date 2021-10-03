package com.sentropic.guiapi.gui;

import com.sentropic.guiapi.GUIAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * A GUIComponent set to last only for a certain duration, after which, it will be removed from the {@link GUI}
 */
@SuppressWarnings("unused")
public class TemporaryGUIComponent extends GUIComponent {
    private final int duration;
    private final Map<GUI,RemoveTask> tasks = new HashMap<>();

    /**
     * @param id        the identifier of this GUIComponent
     * @param component the {@link BaseComponent} containing the text and formatting of this GUIComponent
     * @param offset    the lateral offset of this GUIComponent in the player's screen
     * @param alignment the alignment to set this GUIComponent to,
     *                  whether {@link Alignment#LEFT}, {@link Alignment#RIGHT} or {@link Alignment#CENTER}
     * @param scale     whether the text in the provided {@link BaseComponent} should be scaled according to its fonts
     * @param duration  the duration this component will last for in the {@link GUI}, in ticks
     * @throws IllegalArgumentException if the provided {@link BaseComponent} is not supported,
     *                                  according to the criteria of {@link GUIComponent#check(BaseComponent)}
     */
    public TemporaryGUIComponent(String id, BaseComponent component, int offset, Alignment alignment, boolean scale, int duration) {
        super(id, component, offset, alignment, scale);
        this.duration = duration;
    }

    /**
     * @param id        the identifier of this GUIComponent
     * @param component the {@link BaseComponent} containing the text and formatting of this GUIComponent
     * @param width     the total width of the provided {@link BaseComponent} in the player's screen
     * @param offset    the lateral offset of this GUIComponent in the player's screen
     * @param alignment the alignment to set this GUIComponent to,
     *                  whether {@link Alignment#LEFT}, {@link Alignment#RIGHT} or {@link Alignment#CENTER}
     * @param duration  the duration this component will last for in the {@link GUI}, in ticks
     * @throws IllegalArgumentException if the provided {@link BaseComponent} is not supported,
     *                                  according to the criteria of {@link GUIComponent#check(BaseComponent)}
     */
    public TemporaryGUIComponent(String id, BaseComponent component, int width, int offset, Alignment alignment, int duration) {
        super(id, component, width, offset, alignment);
        this.duration = duration;
    }


    @Override
    void onAdd(GUI gui) {
        RemoveTask task = new RemoveTask(gui);
        RemoveTask oldTask = tasks.put(gui, task);
        if (oldTask != null) {
            try { oldTask.cancel(); } catch (IllegalStateException ignore) { }
        }
        task.runTaskLater(GUIAPI.getPlugin(), duration);
    }

    @Override
    void onRemove(GUI gui) {
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
