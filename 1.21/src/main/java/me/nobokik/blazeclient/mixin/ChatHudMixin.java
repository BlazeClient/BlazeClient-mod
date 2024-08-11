package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.hook.ChatHudHook;
import me.nobokik.blazeclient.api.hook.IChatHudExt;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


/**
 * The priority is set to 9999 to ensure that this mixin is applied after all other mixins.
 * See GitHub issue #23 for more information.
 */
@Mixin(value = ChatHud.class, priority = 9999)
public abstract class ChatHudMixin implements IChatHudExt {
    @Unique
    private final ChatHudHook compactchat$hook = new ChatHudHook(this);

    @Shadow
    @Final
    private List<ChatHudLine> messages;

    @Mutable
    @Shadow
    @Final
    private static int MAX_MESSAGES;

    @Shadow
    public abstract void reset();

    @Shadow
    public abstract void clear(boolean clearHistory);

    @ModifyVariable(
            method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Text compactchat$compactChatMessage(Text message, Text parameterMessage, MessageSignatureData data, MessageIndicator indicator) {
        if (!Client.modManager().getMod(GeneralSettings.class).stackChatMessages.isEnabled()) {
            return message;
        }

        return this.compactchat$hook.compactChatMessage(message);
    }

    @Inject(method = "clear", at = @At("RETURN"))
    private void compactchat$onClear(boolean clearHistory, CallbackInfo ci) {
        this.compactchat$hook.reset();
    }
    @Override
    public List<ChatHudLine> compactchat$getMessages() {
        return this.messages;
    }

    @Override
    public void compactchat$refreshMessages() {
        this.reset();
    }

    @Override
    public void compactchat$clear() {
        this.clear(false);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(DrawContext drawContext, int i, int j, int k, boolean bl, CallbackInfo ci) {
        if(Client.modManager().getMod(GeneralSettings.class).unlimitedChatHistory.isEnabled()) MAX_MESSAGES = 16384;
        else MAX_MESSAGES = 100;
    }
}