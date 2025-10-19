package net.mat0u5.lifeseries.events;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ClientKeybinds {

    // === Powers ===
    public static KeyBinding timeControl;
    public static KeyBinding creaking;
    public static KeyBinding windCharge;
    public static KeyBinding astralProjection;
    public static KeyBinding superPunch;
    public static KeyBinding mimicry;
    public static KeyBinding teleportation;
    public static KeyBinding listening;
    public static KeyBinding shadowPlay;
    public static KeyBinding flight;
    public static KeyBinding playerDisguise;
    public static KeyBinding animalDisguise;
    public static KeyBinding tripleJump;
    public static KeyBinding invisibility;
    public static KeyBinding superspeed;
    public static KeyBinding necromancy;

    // === Misc ===
    public static KeyBinding openConfig;
    public static KeyBinding runCommand;

    // === Keybind Category ===
    //? if <= 1.21.6 {
    public static final String KEYBIND_ID = "key.category.lifeseries.general";
    //?} else {
    /* public static final KeyBinding.Category KEYBIND_ID = new KeyBinding.Category(Identifier.of("lifeseries", "general")); */
    //?}

    public static void tick() {
        checkPress(timeControl, "TimeControl");
        checkPress(creaking, "Creaking");
        checkPress(windCharge, "WindCharge");
        checkPress(astralProjection, "AstralProjection");
        checkPress(superPunch, "SuperPunch");
        checkPress(mimicry, "Mimicry");
        checkPress(teleportation, "Teleportation");
        checkPress(listening, "Listening");
        checkPress(shadowPlay, "ShadowPlay");
        checkPress(flight, "Flight");
        checkPress(playerDisguise, "PlayerDisguise");
        checkPress(animalDisguise, "AnimalDisguise");
        checkPress(tripleJump, "TripleJump");
        checkPress(invisibility, "Invisibility");
        checkPress(superspeed, "Superspeed");
        checkPress(necromancy, "Necromancy");

        // Dev & UI bindings
        while (openConfig != null && openConfig.wasPressed()) {
            NetworkHandlerClient.pressOpenConfigKey();
        }
        while (runCommand != null && runCommand.wasPressed() && VersionControl.isDevVersion()) {
            NetworkHandlerClient.pressRunCommandKey();
        }
    }

    private static void checkPress(KeyBinding binding, String powerName) {
        while (binding != null && binding.wasPressed()) {
            NetworkHandlerClient.pressPowerKey(powerName);
        }
    }

    public static void registerKeybinds() {
        // Default key (same as old superpower)
        int defaultKey = GLFW.GLFW_KEY_G;

        // === Core Powers ===
        timeControl = register("timecontrol", defaultKey);
        windCharge = register("windcharge", defaultKey);
        astralProjection = register("astralprojection", defaultKey);
        superPunch = register("superpunch", defaultKey);
        mimicry = register("mimicry", defaultKey);
        teleportation = register("teleportation", defaultKey);
        listening = register("listening", defaultKey);
        shadowPlay = register("shadowplay", defaultKey);
        playerDisguise = register("playerdisguise", defaultKey);
        animalDisguise = register("animaldisguise", defaultKey);
        tripleJump = register("triplejump", defaultKey);
        invisibility = register("invisibility", defaultKey);
        superspeed = register("superspeed", defaultKey);
        necromancy = register("necromancy", defaultKey);

        // === Version-Locked Powers ===
        if (VersionControl.isAtLeast("1.21.2")) {
            flight = register("flight", defaultKey);
            creaking = register("creaking", defaultKey);
        }

        // === Misc ===
        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lifeseries.openconfig",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEYBIND_ID
        ));

        if (VersionControl.isDevVersion()) {
            runCommand = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.lifeseries.runcommand",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_ALT,
                    KEYBIND_ID
            ));
        }
    }

    private static KeyBinding register(String name, int defaultKey) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lifeseries." + name,
                InputUtil.Type.KEYSYM,
                defaultKey,
                KEYBIND_ID
        ));
    }
}
