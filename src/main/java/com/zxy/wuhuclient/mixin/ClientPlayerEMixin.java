package com.zxy.wuhuclient.mixin;

import com.zxy.wuhuclient.featuresList.Synthesis;
import com.zxy.wuhuclient.featuresList.Test;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.HashSet;

import static com.zxy.wuhuclient.featuresList.Synthesis.*;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEMixin {
    @Mutable
    @Final
    @Shadow
    protected final MinecraftClient client;
    public ClientPlayerEMixin(MinecraftClient client) {
        this.client = client;
    }

    @Inject(at = @At("TAIL"),method = "tick")
    public void tick(CallbackInfo ci){
        Synthesis.tick();
        Test.tick();
//        System.out.println(Arrays.toString(updatedSlot.toArray()));
//        System.out.println(updatedSlot.size());
//        System.out.println("tick");
//        updatedSlot = new HashSet<>();
    }
    @Inject(at = @At("TAIL"),method = "closeHandledScreen")
    public void closeHandledScreen(CallbackInfo ci){
//        if(/*Synthesis.recipe != null && Synthesis.recipe.getRecipeItems().length == 9&&*/ step == 2 &&Synthesis.pos != null){


//
////            client.interactionManager.attackBlock(pos,Direction.UP);
////            client.interactionManager.attackBlock(pos,Direction.UP);
//            client.interactionManager.interactBlock(client.player,client.world, Hand.MAIN_HAND,
//                    new BlockHitResult(new Vec3d(pos.getX()+0.5, pos.getY()+0.5,pos.getZ()+0.5),Direction.UP,pos,false));
//        }

    }
}
