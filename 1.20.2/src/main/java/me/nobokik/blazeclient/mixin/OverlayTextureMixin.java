package me.nobokik.blazeclient.mixin;

import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.event.events.OverlayReloadListener;
import me.nobokik.blazeclient.mod.mods.HitColorMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.awt.*;

@Mixin(OverlayTexture.class)
public abstract class OverlayTextureMixin implements OverlayReloadListener {
    @Shadow
    @Final
    private NativeImageBackedTexture texture;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void modifyHitColor(CallbackInfo ci) {
        this.reloadOverlay();
        OverlayReloadListener.register(this);
    }

    public void onOverlayReload() {
        this.reloadOverlay();
    }
    private static int getColorInt(int red, int green, int blue, int alpha) {
        alpha = 255 - alpha;
        return (alpha << 24) + (blue << 16) + (green << 8) + red;
    }

    public void reloadOverlay() {
        if(Client.modManager() == null) return;
        NativeImage nativeImage = this.texture.getImage();

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (i < 8) {
                    Color color = Client.modManager().getMod(HitColorMod.class).hitColor.getColor();
                    assert nativeImage != null;
                    if(Client.modManager().getMod(HitColorMod.class).isEnabled())
                        nativeImage.setColor(j, i, getColorInt(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
                    else
                        nativeImage.setColor(j, i, -1308622593);
                }
            }
        }

        RenderSystem.activeTexture(33985);
        this.texture.bindTexture();
        nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
        RenderSystem.activeTexture(33984);
    }
}