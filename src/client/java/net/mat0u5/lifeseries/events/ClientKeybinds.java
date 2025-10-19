package net.mat0u5.lifeseries.events;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.utils.versions.VersionControl;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category;
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

    // === Config and dev keys ===
    public static KeyBinding openConfig;
    public static KeyBinding runCommand;

    // Keybind category (compatible with all versions)
    public static final Category KEYBIND_CATEGORY = new Category("lifeseries.general");

    // === Called every tick ===
    public static void tick() {
        checkPower(timeControl, "time_control");
        checkPower(creaking, "creaking");
        checkPower(windCharge, "wind_charge");
        checkPower(astralProjection, "astral_projection");
        checkPower(superPunch, "super_punch");
        checkPower(mimicry, "mimicry");
        checkPower(teleportation, "teleportation");
        checkPower(listening, "listening");
        checkPower(shadowPlay, "shadow_play");
        checkPower(flight, "flight");
        checkPower(playerDisguise, "player_disguise");
        checkPower(animalDisguise, "animal_disguise");
        checkPower(tripleJump, "triple_jump");
        checkPower(invisibility, "invisibility");
        checkPower(superspeed, "superspeed");
        checkPower(necromancy, "necromancy");

        if (runCommand != null && runCommand.wasPressed() && VersionControl.isDevVersion()) {
            NetworkHandlerClient.pressRunCommandKey();
        }

        if (openConfig != null && openConfig.wasPressed()) {
            NetworkHandlerClient.pressOpenConfigKey();
        }
    }

    // Helper to check power key and send packet
    private static void checkPower(KeyBinding key, String powerName) {
        if (key != null && key.wasPressed()) {
            NetworkHandlerClient.sendStringPacket(
                    net.mat0u5.lifeseries.utils.enums.PacketNames.SUPERPOWER_KEY,
                    powerName
            );
        }
    }

    // === Register all keybinds ===
    public static void registerKeybinds() {
        timeControl = registerKey("timeControl", GLFW.GLFW_KEY_T);
        creaking = registerKey("creaking", GLFW.GLFW_KEY_C);
        windCharge = registerKey("windCharge", GLFW.GLFW_KEY_W);
        astralProjection = registerKey("astralProjection", GLFW.GLFW_KEY_A);
        superPunch = registerKey("superPunch", GLFW.GLFW_KEY_P);
        mimicry = registerKey("mimicry", GLFW.GLFW_KEY_M);
        teleportation = registerKey("teleportation", GLFW.GLFW_KEY_Y);
        listening = registerKey("listening", GLFW.GLFW_KEY_L);
        shadowPlay = registerKey("shadowPlay", GLFW.GLFW_KEY_H);
        flight = registerKey("flight", GLFW.GLFW_KEY_F);
        playerDisguise = registerKey("playerDisguise", GLFW.GLFW_KEY_U);
        animalDisguise = registerKey("animalDisguise", GLFW.GLFW_KEY_I);
        tripleJump = registerKey("tripleJump", GLFW.GLFW_KEY_J);
        invisibility = registerKey("invisibility", GLFW.GLFW_KEY_V);
        superspeed = registerKey("superspeed", GLFW.GLFW_KEY_S);
        necromancy = registerKey("necromancy", GLFW.GLFW_KEY_N);

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lifeseries.openconfig",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEYBIND_CATEGORY
        ));

        if (VersionControl.isDevVersion()) {
            runCommand = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.lifeseries.runcommand",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_ALT,
                    KEYBIND_CATEGORY
            ));
        }
    }

    // Helper method to reduce duplication
    private static KeyBinding registerKey(String name, int defaultKey) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lifeseries." + name.toLowerCase(),
                InputUtil.Type.KEYSYM,
                defaultKey,
                KEYBIND_CATEGORY
        ));
    }
}