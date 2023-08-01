package me.nobokik.blazeclient.mixin;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    @Shadow @Final @Mutable
    private static Identifier PANORAMA_OVERLAY;
    @Shadow @Nullable @Mutable
    private String splashText;
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    protected void renderBackground(CallbackInfo info) {
        PANORAMA_OVERLAY = new Identifier("blaze-client","waves-logo.png");
        splashText = null;
    }
    @Inject(method = "init", at = @At("TAIL"))
    protected void removeButtons(CallbackInfo info) {
        List<ClickableWidget> widgetList = Screens.getButtons(this);
        for (ClickableWidget button : widgetList) {
            button.visible = false;
        }
    }

}
