package com.zxy.wuhuclient.features_list;

import com.zxy.wuhuclient.Utils.ZxyUtils;
import com.zxy.wuhuclient.WuHuClientMod;
import net.minecraft.client.network.ClientPlayerEntity;

import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import java.util.List;
//#if MC > 12004
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.FireworkExplosionComponent;
//#else
//$$
//#endif

import static com.zxy.wuhuclient.WuHuClientMod.*;
//快捷烟花 在飞行时按下快捷键可以从背包中拿出烟花并使用，然后还原副手物品
public class QuickFirework {

    public static void accelerated(){
        ClientPlayerEntity player = client.player;
        if (player == null || !player.isFallFlying()) return;
        ScreenHandler sc = player.currentScreenHandler;
        DefaultedList<Slot> slots = sc.slots;
        for (int i = 0; i < slots.size(); i++) {
            ItemStack stack = slots.get(i).getStack();
            //#if MC > 12004
            FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
            if (fireworksComponent != null && fireworksComponent.explosions().isEmpty()) {
                buiBuiBui(sc,i);
                return;
            }
            //#else
            //$$ NbtCompound nbtCompound = stack.getSubNbt("Fireworks");
            //$$ if(nbtCompound != null && nbtCompound.getList("Explosions", 10).isEmpty()){
            //$$     buiBuiBui(sc,i);
            //$$     return;
            //$$ }
            //#endif

        }
    }
    public static void buiBuiBui(ScreenHandler sc , int i) {
        client.interactionManager.clickSlot(sc.syncId, i, 40, SlotActionType.SWAP, client.player);
        //#if MC > 11802
        client.interactionManager.interactItem(client.player, Hand.OFF_HAND);
        //#else
        //$$ client.interactionManager.interactItem(client.player,client.world, Hand.OFF_HAND);
        //#endif
        client.interactionManager.clickSlot(sc.syncId, i, 40, SlotActionType.SWAP, client.player);
    }
}
