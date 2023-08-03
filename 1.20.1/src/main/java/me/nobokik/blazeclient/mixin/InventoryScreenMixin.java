package me.nobokik.blazeclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.image.BufferedImage;

import static me.nobokik.blazeclient.Client.mc;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }


    @Shadow @Final
    private RecipeBookWidget recipeBook;
    @Shadow
    private boolean narrow;
    @Shadow
    private float mouseX;
    @Shadow
    private float mouseY;
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext drawContext, int i, int j, float f, CallbackInfo ci) {
        ci.cancel();

        if(Client.modManager().getMod(GeneralSettings.class).darkenInventory.isEnabled()) this.renderBackground(drawContext);
        if (this.recipeBook.isOpen() && this.narrow) {
            this.drawBackground(drawContext, f, i, j);
            this.recipeBook.render(drawContext, i, j, f);
        } else {
            this.recipeBook.render(drawContext, i, j, f);
            super.render(drawContext, i, j, f);
            this.recipeBook.drawGhostSlots(drawContext, this.x, this.y, false, f);
        }

        this.drawMouseoverTooltip(drawContext, i, j);
        this.recipeBook.drawTooltip(drawContext, this.x, this.y, i, j);
        this.mouseX = (float)i;
        this.mouseY = (float)j;
    }

}
