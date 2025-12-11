package io.github.dimaskama.visualkeys.client;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public class KeyEntry {
    public final int code;
    public final Type type;
    public final String name;
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public final ArrayList<Bind> binds = new ArrayList<>();
    @Nullable
    public List<Component> tooltipTexts;
    @Nullable
    public Component firstBindText = null;

    public KeyEntry(int code, Type type, String name, int x, int y, int width, int height) {
        this.code = code;

        this.type = type;
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX(KeyboardRenderOptions options) {
        return x + type.getOffsetX(options);
    }

    public int getY(KeyboardRenderOptions options) {
        return y + type.getOffsetY(options);
    }

    public record Bind(int code, Component text, Component textWithCategory) { }

    public enum Type implements StringRepresentable {
        MAIN("main"),
        FUNCTION("function"),
        MID("mid"),
        NUMPAD("numpad");

        public static final com.mojang.serialization.Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private final String key;

        Type(String key) {
            this.key = key;
        }

        @Override
        public String getSerializedName() {
            return key;
        }

        public boolean isVisible(KeyboardRenderOptions options) {
            return switch (this) {
                case FUNCTION: yield options.functionVisible;
                case MID: yield options.midVisible;
                case NUMPAD: yield options.numpadVisible;
                default: yield true;
            };
        }

        public int getOffsetX(KeyboardRenderOptions options) {
            if (this == NUMPAD && !options.midVisible) {
                return -325;
            }
            return 0;
        }

        public int getOffsetY(KeyboardRenderOptions options) {
            if ((this == MAIN || this == NUMPAD) && !(options.functionVisible || options.midVisible)) {
                return -150;
            }
            return 0;
        }
    }
}
