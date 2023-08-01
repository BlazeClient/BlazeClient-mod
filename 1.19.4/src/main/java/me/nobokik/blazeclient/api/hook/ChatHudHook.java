package me.nobokik.blazeclient.api.hook;

import net.minecraft.text.Text;

import java.util.HashMap;
public class ChatHudHook {
    private final IChatHudExt chatHud;

    public ChatHudHook(IChatHudExt chatHud) {
        this.chatHud = chatHud;
    }

    /**
     * A historical map of all chat messages sent, mapped to their wrapper class
     */
    private final HashMap<Text, ChatMessage> chatMessages = new HashMap<>();

    /**
     * Returns the modified (if applicable) chat message.
     */
    public Text compactChatMessage(Text message) {
        var chatMessage = this.chatMessages.get(message);

        // This chat message has not occurred before
        if (chatMessage == null) {
            this.chatMessages.put(message, new ChatMessage());
            return message;
        }

        // This chat message has occurred before, let's remove the old occurrence(s).
        this.removeMessage(message, chatMessage);
        chatMessage.addOccurrence();

        return chatMessage.modifiedText(message);
    }

    /**
     * Removes a message (and its occurrences modifications) from the Chat HUD.
     */
    public void removeMessage(Text originalMessage, ChatMessage message) {
        var iterator = this.chatHud.getMessages().listIterator();
        while (iterator.hasNext()) {
            var chatHudLine = iterator.next();

            // We remove occurrences because we want to remove existing compacted messages too.
            var contentWithoutOccurrences = message.removeOccurencesText(chatHudLine.content());
            var textWithoutOccurrences = message.removeOccurencesText(originalMessage);

            if (contentWithoutOccurrences.equals(textWithoutOccurrences)) {
                iterator.remove();
                this.chatHud.refreshMessages();

                return;
            }
        }
    }

    /**
     * Called when the Chat HUD is clearing its messages.
     * Since all chat messages aren't visible anymore, we should clear their {@link ChatMessage} too.
     */
    public void onClear() {
        chatMessages.clear();
    }
}