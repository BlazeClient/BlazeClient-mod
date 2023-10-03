package me.nobokik.blazeclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.minecraft.client.gui.DrawableHelper;
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

import static me.nobokik.blazeclient.Client.mc;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Shadow
    protected abstract void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY);


    @Shadow @Final
    private RecipeBookWidget recipeBook;
    @Shadow
    private boolean narrow;
    @Shadow
    private float mouseX;
    @Shadow
    private float mouseY;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();

        if(Client.modManager().getMod(GeneralSettings.class).darkenInventory.isEnabled()) this.renderBackground(matrices);
        if (this.recipeBook.isOpen() && this.narrow) {
            this.drawBackground(matrices, delta, mouseX, mouseY);
            this.recipeBook.render(matrices, mouseX, mouseY, delta);
        } else {
            this.recipeBook.render(matrices, mouseX, mouseY, delta);
            super.render(matrices, mouseX, mouseY, delta);
            this.recipeBook.drawGhostSlots(matrices, this.x, this.y, false, delta);
        }

        //(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        RenderSystem.setShaderTexture(0, new Identifier("blaze-client", "blazetext.png"));
        DrawableHelper.drawTexture(matrices, 0, mc.getWindow().getScaledHeight() - 32, 0, 0, 167, 28, 167, 28);

        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        this.recipeBook.drawTooltip(matrices, this.x, this.y, mouseX, mouseY);
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
    }

}
