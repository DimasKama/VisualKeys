package io.github.dimaskama.visualkeys.client;

import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import io.github.dimaskama.visualkeys.client.screen.KeyboardScreen;
import io.github.dimaskama.visualkeys.config.ModConfig;
import io.github.dimaskama.visualkeys.mixin.KeyBindingAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.stream.Stream;

import static io.github.dimaskama.visualkeys.client.KeyEntry.Type.*;
import static org.lwjgl.sdl.SDLScancode.*;

import com.mojang.blaze3d.platform.InputConstants;

public class VisualKeys implements ClientModInitializer {
    public static final String MOD_ID = "visualkeys";
    public static final Logger LOGGER = LogManager.getLogger("VisualKeys");
    public static final ModConfig CONFIG = new ModConfig("config/visualkeys.json");
    public static final QwertyKeyboard QWERTY = new QwertyKeyboard();

    @Override
    public void onInitializeClient() {
        CONFIG.loadOrCreate();
        CONFIG.getData().enabled &= CONFIG.getData().saveEnabledState;
        QWERTY.init();
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "keyboard"), (graphics, _) -> onRender(graphics));
    }

    public static void onRender(GuiGraphicsExtractor context) {
        if (CONFIG.getData().enabled) {
            KeyboardRenderer.render(context, QWERTY.map.values(), CONFIG.getData().hudRenderOptions);
        }
    }

    public static void onUpdateKeyBindings() {
        Stream<KeyEntry.Bind> binds = KeyBindingAccessor.visualkeys_getKeysById().values().stream()
                .filter(b -> ((KeyBindingAccessor) b).visualkeys_getBoundKey().getType() == InputConstants.Type.KEYBOARD)
                .map(b -> {
                    Component text = Component.translatable(b.getName());
                    return new KeyEntry.Bind(((KeyBindingAccessor) b).visualkeys_getBoundKey().getValue(), text, Component.empty().append(b.getCategory().label()).append(": ").append(text));
                });
        if (FabricLoader.getInstance().isModLoaded("malilib")) {
            binds = Stream.concat(binds, InputEventHandler.getKeybindManager().getKeybindCategories().stream().flatMap(c -> {
                ArrayList<KeyEntry.Bind> list = new ArrayList<>();
                for (IHotkey h : c.getHotkeys()) {
                    if (!h.getKeybind().getKeys().isEmpty()) {
                        Component text = Component.literal(h.getPrettyName());
                        list.add(new KeyEntry.Bind(h.getKeybind().getKeys().getLast(), text, Component.literal(c.getModName()).append(": ").append(text)));
                    }
                }
                return list.stream();
            }));
        }
        for (KeyEntry entry : QWERTY.map.values()) {
            entry.binds.clear();
            entry.tooltipTexts = null;
            entry.firstBindText = null;
        }
        binds.forEach(bind -> {
            KeyEntry entry = QWERTY.map.get(bind.code());
            if (entry != null) {
                entry.binds.addFirst(bind);
                entry.firstBindText = bind.text();
            }
        });
    }

    public static Button createOpenKeyboardButton(Minecraft client, Screen screen) {
        return Button.builder(Component.translatable("visualkeys.open_keyboard"), button -> {
            client.gui.setScreen(new KeyboardScreen(screen));
        }).size(100, 20).build();
    }

    public static String keyCategoryToString(KeyMapping.Category category) {
        Identifier id = category.id();
        return "minecraft".equals(id.getNamespace())
                ? id.getPath()
                : id.toString();
    }

    public static class QwertyKeyboard {
        public final Int2ObjectMap<KeyEntry> map = new Int2ObjectArrayMap<>();

        public void init() {
            // Function Line
            putKey(SDL_SCANCODE_ESCAPE, FUNCTION, "Esc", 0, 0);
            putKey(SDL_SCANCODE_F1, FUNCTION, "F1", 200, 0);
            putKey(SDL_SCANCODE_F2, FUNCTION, "F2", 300, 0);
            putKey(SDL_SCANCODE_F3, FUNCTION, "F3", 400, 0);
            putKey(SDL_SCANCODE_F4, FUNCTION, "F4", 500, 0);
            putKey(SDL_SCANCODE_F5, FUNCTION, "F5", 650, 0);
            putKey(SDL_SCANCODE_F6, FUNCTION, "F6", 750, 0);
            putKey(SDL_SCANCODE_F7, FUNCTION, "F7", 850, 0);
            putKey(SDL_SCANCODE_F8, FUNCTION, "F8", 950, 0);
            putKey(SDL_SCANCODE_F9, FUNCTION, "F9", 1100, 0);
            putKey(SDL_SCANCODE_F10, FUNCTION, "F10", 1200, 0);
            putKey(SDL_SCANCODE_F11, FUNCTION, "F11", 1300, 0);
            putKey(SDL_SCANCODE_F12, FUNCTION, "F12", 1400, 0);

            // Main Line 1
            putKey(SDL_SCANCODE_GRAVE, "~", 0, 150);
            putKey(SDL_SCANCODE_1, "1", 100, 150);
            putKey(SDL_SCANCODE_2, "2", 200, 150);
            putKey(SDL_SCANCODE_3, "3", 300, 150);
            putKey(SDL_SCANCODE_4, "4", 400, 150);
            putKey(SDL_SCANCODE_5, "5", 500, 150);
            putKey(SDL_SCANCODE_6, "6", 600, 150);
            putKey(SDL_SCANCODE_7, "7", 700, 150);
            putKey(SDL_SCANCODE_8, "8", 800, 150);
            putKey(SDL_SCANCODE_9, "9", 900, 150);
            putKey(SDL_SCANCODE_0, "0", 1000, 150);
            putKey(SDL_SCANCODE_MINUS, "-", 1100, 150);
            putKey(SDL_SCANCODE_EQUALS, "=", 1200, 150);
            putKey(SDL_SCANCODE_BACKSPACE, "Backspace", 1300, 150, 200, 100);

            // Main Line 2
            putKey(SDL_SCANCODE_TAB, "Tab", 0, 250, 150, 100);
            putKey(SDL_SCANCODE_Q, "Q", 150, 250);
            putKey(SDL_SCANCODE_W, "W", 250, 250);
            putKey(SDL_SCANCODE_E, "E", 350, 250);
            putKey(SDL_SCANCODE_R, "R", 450, 250);
            putKey(SDL_SCANCODE_T, "T", 550, 250);
            putKey(SDL_SCANCODE_Y, "Y", 650, 250);
            putKey(SDL_SCANCODE_U, "U", 750, 250);
            putKey(SDL_SCANCODE_I, "I", 850, 250);
            putKey(SDL_SCANCODE_O, "O", 950, 250);
            putKey(SDL_SCANCODE_P, "P", 1050, 250);
            putKey(SDL_SCANCODE_LEFTBRACKET, "[", 1150, 250);
            putKey(SDL_SCANCODE_RIGHTBRACKET, "]", 1250, 250);
            putKey(SDL_SCANCODE_BACKSLASH, "\\", 1350, 250, 150, 100);

            // Main Line 3
            putKey(SDL_SCANCODE_CAPSLOCK, "CapsLock", 0, 350, 175, 100);
            putKey(SDL_SCANCODE_A, "A", 175, 350);
            putKey(SDL_SCANCODE_S, "S", 275, 350);
            putKey(SDL_SCANCODE_D, "D", 375, 350);
            putKey(SDL_SCANCODE_F, "F", 475, 350);
            putKey(SDL_SCANCODE_G, "G", 575, 350);
            putKey(SDL_SCANCODE_H, "H", 675, 350);
            putKey(SDL_SCANCODE_J, "J", 775, 350);
            putKey(SDL_SCANCODE_K, "K", 875, 350);
            putKey(SDL_SCANCODE_L, "L", 975, 350);
            putKey(SDL_SCANCODE_SEMICOLON, ";", 1075, 350);
            putKey(SDL_SCANCODE_APOSTROPHE, "'", 1175, 350);
            putKey(SDL_SCANCODE_RETURN, "Enter", 1275, 350, 225, 100);

            // Main Line 4
            putKey(SDL_SCANCODE_LSHIFT, "Shift", 0, 450, 225, 100);
            putKey(SDL_SCANCODE_Z, "Z", 225, 450);
            putKey(SDL_SCANCODE_X, "X", 325, 450);
            putKey(SDL_SCANCODE_C, "C", 425, 450);
            putKey(SDL_SCANCODE_V, "V", 525, 450);
            putKey(SDL_SCANCODE_B, "B", 625, 450);
            putKey(SDL_SCANCODE_N, "N", 725, 450);
            putKey(SDL_SCANCODE_M, "M", 825, 450);
            putKey(SDL_SCANCODE_COMMA, ",", 925, 450);
            putKey(SDL_SCANCODE_PERIOD, ".", 1025, 450);
            putKey(SDL_SCANCODE_SLASH, "/", 1125, 450);
            putKey(SDL_SCANCODE_RSHIFT, "Shift", 1225, 450, 275, 100);

            // Main Line 5
            putKey(SDL_SCANCODE_LCTRL, "Ctrl", 0, 550, 125, 100);
            putKey(-4, "Win", 125, 550, 125, 100);
            putKey(SDL_SCANCODE_LALT, "Alt", 250, 550, 125, 100);
            putKey(SDL_SCANCODE_SPACE, "Space", 375, 550, 625, 100);
            putKey(SDL_SCANCODE_RALT, "Alt", 1000, 550, 125, 100);
            putKey(-3, "Win", 1125, 550, 125, 100);
            putKey(-2, "Fn", 1250, 550, 125, 100);
            putKey(SDL_SCANCODE_RCTRL, "Ctrl", 1375, 550, 125, 100);

            // Mid
            putKey(SDL_SCANCODE_PRINTSCREEN, MID, "PrtScn", 1525, 0);
            putKey(SDL_SCANCODE_SCROLLLOCK, MID, "ScrLck", 1625, 0);
            putKey(SDL_SCANCODE_PAUSE, MID, "Pause", 1725, 0);

            putKey(SDL_SCANCODE_INSERT, MID, "Insert", 1525, 150);
            putKey(SDL_SCANCODE_HOME, MID, "Home", 1625, 150);
            putKey(SDL_SCANCODE_PAGEUP, MID, "PgUp", 1725, 150);

            putKey(SDL_SCANCODE_DELETE, MID, "Delete", 1525, 250);
            putKey(SDL_SCANCODE_END, MID, "End", 1625, 250);
            putKey(SDL_SCANCODE_PAGEDOWN, MID, "PgDown", 1725, 250);

            putKey(SDL_SCANCODE_UP, MID, "Up", 1625, 450);

            putKey(SDL_SCANCODE_LEFT, MID, "Left", 1525, 550);
            putKey(SDL_SCANCODE_DOWN, MID, "Down", 1625, 550);
            putKey(SDL_SCANCODE_RIGHT, MID, "Right", 1725, 550);

            // Num Pad
            putKey(SDL_SCANCODE_NUMLOCKCLEAR, NUMPAD, "NumLk", 1850, 150);
            putKey(SDL_SCANCODE_KP_DIVIDE, NUMPAD, "/", 1950, 150);
            putKey(SDL_SCANCODE_KP_MULTIPLY, NUMPAD, "*", 2050, 150);
            putKey(SDL_SCANCODE_KP_MINUS, NUMPAD, "-", 2150, 150);

            putKey(SDL_SCANCODE_KP_7, NUMPAD, "7", 1850, 250);
            putKey(SDL_SCANCODE_KP_8, NUMPAD, "8", 1950, 250);
            putKey(SDL_SCANCODE_KP_9, NUMPAD, "9", 2050, 250);
            putKey(SDL_SCANCODE_KP_PLUS, NUMPAD, "+", 2150, 250, 100, 200);

            putKey(SDL_SCANCODE_KP_4, NUMPAD, "4", 1850, 350);
            putKey(SDL_SCANCODE_KP_5, NUMPAD, "5", 1950, 350);
            putKey(SDL_SCANCODE_KP_6, NUMPAD, "6", 2050, 350);

            putKey(SDL_SCANCODE_KP_1, NUMPAD, "1", 1850, 450);
            putKey(SDL_SCANCODE_KP_2, NUMPAD, "2", 1950, 450);
            putKey(SDL_SCANCODE_KP_3, NUMPAD, "3", 2050, 450);
            putKey(SDL_SCANCODE_KP_ENTER, NUMPAD, "Enter", 2150, 450, 100, 200);

            putKey(SDL_SCANCODE_KP_0, NUMPAD, "0", 1850, 550, 200, 100);
            putKey(SDL_SCANCODE_KP_PERIOD, NUMPAD, ".", 2050, 550);
        }

        private void putKey(int code, String name, int x, int y) {
            putKey(code, MAIN, name, x, y);
        }

        private void putKey(int code, String name, int x, int y, int width, int height) {
            putKey(code, MAIN, name, x, y, width, height);
        }

        private void putKey(int code, KeyEntry.Type type, String name, int x, int y) {
            putKey(code, type, name, x, y, 100, 100);
        }

        private void putKey(int code, KeyEntry.Type type, String name, int x, int y, int width, int height) {
            map.put(code, new KeyEntry(code, type, name, x, y, width, height));
        }
    }
}
