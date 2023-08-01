package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.hook.ChatHudHook;
import me.nobokik.blazeclient.api.hook.IChatHudExt;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public class ChatHudMixin implements IChatHudExt {
    @Unique
    private final ChatHudHook compactchat$hook = new ChatHudHook(this);

    @Shadow
    @Final
    private List<ChatHudLine> messages;

    @Shadow
    private void refresh() {

    }

    @ModifyVariable(
            method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Text compactchat$compactChatMessage(Text message, Text parameterMessage, MessageSignatureData data, int ticks, MessageIndicator indicator, boolean refreshing) {
        // If we're refreshing, we have probably already modified the message, therefore we don't want to do anything.
        if(!Client.modManager().getMod(GeneralSettings.class).stackChatMessages.isEnabled()) return message;
        if (refreshing) {
            return message;
        }

        return this.compactchat$hook.compactChatMessage(message);
    }

    @Inject(
            method = "clear",
            at = @At("RETURN")
    )
    private void compactchat$onClear(boolean clearHistory, CallbackInfo ci) {
        this.compactchat$hook.onClear();
    }

    @Override
    public List<ChatHudLine> getMessages() {
        return this.messages;
    }

    @Override
    public void refreshMessages() {
        this.refresh();
    }

    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", constant = @Constant(intValue = 100), expect = 2)
    public int changeMaxHistory(int original) {
        if(Client.modManager().getMod(GeneralSettings.class).unlimitedChatHistory.isEnabled()) return 16384;
        return original;
    }

}
