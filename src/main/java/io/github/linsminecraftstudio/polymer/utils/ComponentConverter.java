package io.github.linsminecraftstudio.polymer.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ComponentConverter {
    private static final MiniMessage simpleTextMiniMessage = MiniMessage.builder().tags(TagResolver.builder().resolvers(
            StandardTags.color(), StandardTags.reset(), StandardTags.newline(), StandardTags.gradient(),
            StandardTags.decorations(), StandardTags.rainbow(), StandardTags.font()).build()).build();;
    public static String replaceLegacyColorsToMiniMessageFormat(String text){
        MiniMessage miniMessage = MiniMessage.miniMessage();
        LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacySection();
        return miniMessage.serialize(legacyComponentSerializer.deserialize(text));
    }

    public static Component toComponent(String text){
        return MiniMessage.miniMessage().deserialize(replaceLegacyColorsToMiniMessageFormat(text));
    }

    public static String replaceLegacyColorsToMiniMessageFormatSimpleText(String text){
        LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacySection();
        return simpleTextMiniMessage.serialize(legacyComponentSerializer.deserialize(text));
    }

    public static Component toSimpleTextComponent(String text){
        return simpleTextMiniMessage.deserialize(replaceLegacyColorsToMiniMessageFormatSimpleText(text));
    }

    public static String toSimpleText(Component simpleText){
        return simpleTextMiniMessage.serialize(simpleText);
    }
}
