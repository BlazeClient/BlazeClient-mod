package me.nobokik.blazeclient.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.nobokik.blazeclient.Client.uuid;
import static me.nobokik.blazeclient.api.helpers.CapeHelper.equippedCape;
import static me.nobokik.blazeclient.api.helpers.CapeHelper.getEquippedCosmetic;

@Mixin(PlayerListEntry.class)
public final class PlayerListEntryMixin {

    @Shadow @Final
    private GameProfile profile;
    @Shadow @Final
    private Map<MinecraftProfileTexture.Type, Identifier> textures;
    @Shadow
    private boolean texturesLoaded;

    private static HashMap<UUID, String> playerCapes = new HashMap<>();

    @Inject(method = "getCapeTexture", at = @At("TAIL"), cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<Identifier> cir) {
        if(profile.getId().equals(uuid)) {
            if(!equippedCape.equalsIgnoreCase("")) {
                if (!equippedCape.contains("animated"))
                    cir.setReturnValue(new Identifier("blaze-client", "cape-" + equippedCape + ".png"));
                else
                    cir.setReturnValue(new Identifier("blaze-client", "cape-" + equippedCape + ".gif"));
            }
        } else if(playerCapes.containsKey(profile.getId())) {
            if(!playerCapes.get(profile.getId()).contains("animated"))
                cir.setReturnValue(new Identifier("blaze-client", "cape-"+playerCapes.get(profile.getId())+".png"));
            else
                cir.setReturnValue(new Identifier("blaze-client", "cape-"+playerCapes.get(profile.getId())+".gif"));
        }
    }

    @Inject(
            at = @At("HEAD"),
            method = "loadTextures()V")
    private void loadTextures(CallbackInfo info) {
        if(texturesLoaded) return;
        String equipped = getEquippedCosmetic(profile.getId());
        if(!equipped.equalsIgnoreCase(""))
            playerCapes.put(profile.getId(), equipped);
    }
}