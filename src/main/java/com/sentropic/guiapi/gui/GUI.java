package com.sentropic.guiapi.gui;

import com.sentropic.guiapi.GUIAPI;
import com.sentropic.guiapi.GUIManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

/**
 * Represents the GUI of a specific {@link Player}, and all the visual components that make it up
 */
public class GUI {

    // Storage

    public static final String ID_DEFAULT = "_default_";
    public static final String ID_DEBUG = "debug:";
    private static final GUIComponent defaultComponent =
            new GUIComponent(ID_DEFAULT, new TextComponent(), 0, Alignment.LEFT, false);

    public static final SortedMap<Integer,Character> POS_SPACES = Collections.unmodifiableSortedMap(new TreeMap<Integer,Character>() {{
        put(1024, '\uF82F');
        put(512, '\uF82E');
        put(256, '\uF82D');
        put(128, '\uF82C');
        put(64, '\uF82B');
        put(32, '\uF82A');
        put(16, '\uF829');
        put(8, '\uF828');
        put(7, '\uF827');
        put(6, '\uF826');
        put(5, '\uF825');
        put(4, '\uF824');
        put(3, '\uF823');
        put(2, '\uF822');
        put(1, '\uF821');
    }});
    public static final SortedMap<Integer,Character> NEG_SPACES = Collections.unmodifiableSortedMap(new TreeMap<Integer,Character>() {{
        put(1, '\uF801');
        put(2, '\uF802');
        put(3, '\uF803');
        put(4, '\uF804');
        put(5, '\uF805');
        put(6, '\uF806');
        put(7, '\uF807');
        put(8, '\uF808');
        put(16, '\uF809');
        put(32, '\uF80A');
        put(64, '\uF80B');
        put(128, '\uF80C');
        put(256, '\uF80D');
        put(512, '\uF80E');
        put(1024, '\uF80F');
    }});

    // API code

    private final Player player;
    private final List<GUIComponent> guiComponents = new ArrayList<>();


    /**
     * Important: If you need to get a player's GUI, use {@link GUIManager#getGUI(Player)},
     * since GUIs created through this constructor will not be processed.
     *
     * @param player the player the GUI belongs to.
     */
    @Deprecated
    public GUI(Player player) {
        this.player = player;
        guiComponents.add(defaultComponent);
    }

    /**
     * Gets the owner of this GUI
     *
     * @return the {@link Player} to whom this GUI belongs to
     */
    @SuppressWarnings("unused")
    public Player getPlayer() { return player; }

    // Editing methods

    /**
     * Adds the given {@link GUIComponent} to the GUI after all previously added GUIComponents,
     * and removes any other component with the same ID as the given one
     *
     * @param component the GUIComponent to add to the GUI
     */
    public void putOnTop(@NotNull GUIComponent component) {
        remove(component.getID());
        guiComponents.add(component);
        changed = true;
        component.onAdd(this);
    }

    /**
     * Adds the given {@link GUIComponent} to the GUI before all previously added GUIComponents,
     * and removes any other component with the same ID as the given one
     *
     * @param component the GUIComponent to add to the GUI
     */
    @SuppressWarnings("unused")
    public void putUnderneath(@NotNull GUIComponent component) {
        remove(component.getID());
        guiComponents.add(0, component);
        changed = true;
        component.onAdd(this);
    }

    /**
     * If a {@link GUIComponent} exists with an ID matching the one of the given component,
     * removes it and puts the given one in its place
     *
     * @param component the GUIComponent to add to the GUI
     * @return whether the component could be added
     */
    @SuppressWarnings("unused")
    public boolean update(@NotNull GUIComponent component) {
        boolean success = false;
        String id = component.getID();
        checkID(id);
        for (ListIterator<GUIComponent> iterator = guiComponents.listIterator(); iterator.hasNext(); ) {
            GUIComponent oldComponent = iterator.next();
            if (oldComponent.getID().equals(id)) {
                oldComponent.onRemove(this);
                iterator.set(component);
                component.onAdd(this);
                changed = true;
                success = true;
                break;
            }
        }
        return success;
    }

    /**
     * If a {@link GUIComponent} exists with an ID matching the given id,
     * adds the provided component after it, and removes any other component with the same ID as it
     *
     * @param component the GUIComponent to add to the GUI
     * @return whether the component could be added
     */
    @SuppressWarnings("unused")
    public boolean putAfter(String id, @NotNull GUIComponent component) {
        boolean success = false;
        remove(component.getID());
        int i = 0;
        for (GUIComponent component1 : guiComponents) {
            i++;
            if (component1.getID().equals(id)) {
                guiComponents.add(i, component);
                component.onAdd(this);
                changed = true;
                success = true;
                break;
            }
        }
        return success;
    }

    /**
     * If a {@link GUIComponent} exists with an ID matching the given id,
     * adds the provided component} before it, and removes any other component with the same ID as it
     *
     * @param component the GUIComponent to add to the GUI
     * @return whether the component could be added
     */
    @SuppressWarnings("unused")
    public boolean putBefore(String before, @NotNull GUIComponent component) {
        boolean success = false;
        remove(component.getID());
        int i = -1;
        for (GUIComponent component1 : guiComponents) {
            i++;
            if (component1.getID().equals(before)) {
                guiComponents.add(i, component);
                component.onAdd(this);
                changed = true;
                success = true;
                break;
            }
        }
        return success;
    }

    /**
     * If a {@link GUIComponent} exists with an ID matching the given id, removes it from the GUI
     *
     * @return whether a component with a matching id was removed
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean remove(String id) {
        checkID(id);
        return removeIf(component -> component.getID().equals(id));
    }

    /**
     * Removes any {@link GUIComponent}s that meet a given predicate from the GUI
     *
     * @param predicate the predicate that to-be-removed {@link GUIComponent}s must meet
     * @return whether any {@link GUIComponent}s were removed
     */
    public boolean removeIf(Predicate<GUIComponent> predicate) {
        boolean success = false;
        for (Iterator<GUIComponent> iterator = guiComponents.iterator(); iterator.hasNext(); ) {
            GUIComponent component = iterator.next();
            if (isLegalID(component.getID()) && predicate.test(component)) {
                success = true;
                iterator.remove();
                component.onRemove(this);
            }
        }
        changed = success || changed;
        return success;
    }

    private static boolean isLegalID(String id) { return !ID_DEFAULT.equals(id); }

    private static void checkID(String id) {
        if (!isLegalID(id)) {
            throw new IllegalArgumentException("Cannot remove nor change default component _default_");
        }
    }

    // Debug

    private boolean debug = false;

    /**
     * Gets whether this GUI is in debug mode (displaying the debug {@link GUIComponent}s defined in the plugin's config)
     *
     * @return whether this GUI is in debug mode
     */
    public boolean isDebugging() { return debug; }

    /**
     * Sets this GUI to debug mode if debug is true, or disables it otherwise
     *
     * @param debug whether to put the GUI in debug mode or not
     */
    public void setDebug(boolean debug) {
        if (this.debug == debug) { return; } else {
            this.debug = debug;
            changed = true;
        }
        if (debug) {
            for (GUIComponent component : GUIAPI.getGUIConfig().getDebugComponents()) { putOnTop(component); }
        } else { removeIf(component -> component.getID().startsWith(ID_DEBUG)); }
    }

    // Display

    private BaseComponent gui;
    private boolean changed = true;
    private long lastSend = 0;

    private void build() {
        gui = new TextComponent();
        int offset = 0;
        for (GUIComponent guiComponent : guiComponents) {
            if (debug && !guiComponent.getID().startsWith(ID_DEBUG)) { continue; }
            if (guiComponent.getID().equals(ID_DEFAULT)) {
                if (anonComponent == null) { continue; }
                guiComponent = anonComponent;
            }

            offset += guiComponent.getLeftSpaces();
            if (offset != 0) { gui.addExtra(new TextComponent(spacesOf(offset))); }
            gui.addExtra(guiComponent.getComponent());
            offset = guiComponent.getRightSpaces();
        }
        if (offset != 0) { gui.addExtra(new TextComponent(spacesOf(offset))); }
        changed = false;
    }

    /**
     * Plays the GUI to its {@link Player}. This is already done automatically by {@link GUIAPI}
     * No need to call manually under normal usage conditions
     */
    public void play() {
        long time = System.currentTimeMillis();
        boolean play = changed || time-lastSend >= GUIAPI.getGUIConfig().getSendPeriod();
        if (play) {
            lastSend = time;
            if (changed) { build(); }
            sending = true;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, gui);
            sending = false;
        }
    }

    /**
     * For internal use only
     * Code ran when {@link GUIAPI} is reloaded
     */
    public void onReload() {
        // Update anon period
        anonCycler.reschedule();

        // Remove anonymous components
        for (AnonComponent component : anonComponents) { component.cancelTask(); }
        anonComponents.clear();
        anonComponent = null;

        // Refresh debug components
        if (debug) {
            setDebug(false);
            setDebug(true);
        }
    }

    // Anonymous component handling

    private final LinkedHashSet<AnonComponent> anonComponents = new LinkedHashSet<>();
    private int anonIndex = 0;
    private AnonComponent anonComponent = null;
    private final AnonCycler anonCycler = new AnonCycler();

    /**
     * Adds an {@link AnonComponent} containing the given baseComponent
     * Because the content of other {@link BaseComponent} implementations are unknown to the server,
     * only supports {@link TextComponent} at this moment
     *
     * @param baseComponent the anonymous chat component to add to the GUI
     * @return whether the given baseComponent was of the supported types and could be added
     */
    public boolean addAnonComponent(BaseComponent baseComponent) {
        String text = baseComponent.toPlainText();
        for (AnonComponent otherComponent : anonComponents) {
            if (otherComponent.getComponent().toPlainText().equals(text)) {
                otherComponent.refresh();
                return true;
            }
        }
        AnonComponent component;
        try {
            component = new AnonComponent(baseComponent, this);
        } catch (IllegalArgumentException e) { return false; }
        anonComponents.add(component);
        component.refresh();
        anonComponent = component;
        changed = true;
        anonCycler.stop();
        if (anonComponents.size() > 1) { anonCycler.schedule(); }
        return true;
    }

    /**
     * Removes the given anonymous component from the GUI
     *
     * @param component the anonymous component to remove
     */
    public void removeAnonComponent(AnonComponent component) {
        anonComponents.remove(component);
        if (anonComponent == component) {
            nextAnonComponent();
            anonCycler.reschedule();
        }
        if (anonComponents.size() <= 1) { anonCycler.stop(); }
    }

    private void nextAnonComponent() {
        AnonComponent oldComponent = anonComponent;
        int size = anonComponents.size();
        if (size == 0) {
            anonIndex = 0;
            anonComponent = null;
        } else {
            anonIndex = (anonIndex+1)%size;
            Iterator<AnonComponent> iterator = anonComponents.iterator();
            AnonComponent component = null;
            for (int i = 0; i <= anonIndex; i++) { component = iterator.next(); }
            anonComponent = component;
        }
        if (anonComponent != oldComponent) { changed = true; }
    }

    private class AnonCycler {
        private BukkitRunnable runnable;

        private void schedule() {
            stop();
            runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    nextAnonComponent();
                }
            };
            int period = GUIAPI.getGUIConfig().getAnonPeriod();
            runnable.runTaskTimer(GUIAPI.getPlugin(), period, period);
        }

        private void stop() {
            if (runnable != null && !runnable.isCancelled()) {
                try { runnable.cancel(); } catch (IllegalStateException ignored) { }
            }
        }

        private void reschedule() {
            stop();
            schedule();
        }
    }

    // Static methods

    private static boolean sending = false;

    /**
     * Used to distinguish between action bar text sent by {@link GUIAPI} and those send anonymously
     *
     * @return whether {@link GUIAPI} is sending an action bar packet at the moment of calling
     */
    public static boolean isSending() { return sending; }

    /**
     * Builds a {@link String} containing the specified amount of space,
     * from space characters provided by AmberW's Negative Space resource pack
     *
     * @param amount the amount of space to generate the string for, whether positive, negative or zero
     * @return the built {@link String} containing the specified amount of space
     */
    public static String spacesOf(int amount) {
        Map<Integer,Character> spaces;
        if (amount == 0) { return ""; } else if (amount > 0) { spaces = POS_SPACES; } else {
            amount = -amount;
            spaces = NEG_SPACES;
        }
        StringBuilder builder = new StringBuilder();
        while (amount > 1024) {
            builder.append(spaces.get(1024));
            amount -= 1024;
        }
        int power = 1;
        while (amount/power >= 1) {
            power *= 2;
        }
        while (amount > 0) {
            if (amount > 8) {
                power /= 2;
                if (amount >= power) {
                    builder.append(spaces.get(power));
                    amount -= power;
                }
            } else {
                builder.append(spaces.get(amount));
                break;
            }
        }
        return builder.toString();
    }
}
