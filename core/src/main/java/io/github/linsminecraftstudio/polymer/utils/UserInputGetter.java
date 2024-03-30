package io.github.linsminecraftstudio.polymer.utils;

import com.google.common.annotations.Beta;
import io.github.linsminecraftstudio.polymer.TempPolymer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Beta
public class UserInputGetter {
    public static @Nullable String getUserInput(Component message, Player p) {
        return getUserInput(message, p, "##QUIT");
    }

    public static @Nullable String getUserInput(Component message, Player p, String quit) {
        String[] store = new String[1];
        ConversationFactory factory = new ConversationFactory(TempPolymer.getInstance());
        factory.withModality(true);
        factory.withFirstPrompt(new Prompt() {
            @Override
            public @NotNull String getPromptText(@NotNull ConversationContext context) {
                return LegacyComponentSerializer.legacySection().serialize(message) + "\n"
                        + LegacyComponentSerializer.legacySection().serialize(
                        TempPolymer.getInstance().getMessageHandler().getColored(p, "Info.InputQuit", quit)
                );
            }

            @Override
            public boolean blocksForInput(@NotNull ConversationContext context) {
                return true;
            }

            @Override
            public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
                if (input != null) {
                    store[0] = input;
                }
                return END_OF_CONVERSATION;
            }
        });
        factory.withEscapeSequence(quit);
        Conversation conversation = factory.buildConversation(p);
        conversation.begin();
        return store[0];
    }
}
