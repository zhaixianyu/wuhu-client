package com.zxy.wuhuclient.features_list;

import com.zxy.wuhuclient.Utils.ZxyUtils;
import com.zxy.wuhuclient.WuHuClientMod;
import net.minecraft.client.player.LocalPlayer;

import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import java.util.List;
//#if MC > 12004
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.FireworkExplosion;
//#else
//$$
//#endif

import static com.zxy.wuhuclient.WuHuClientMod.*;
//快捷烟花 在飞行时按下快捷键可以从背包中拿出烟花并使用，然后还原副手物品
//TODO 从盒子中取出烟花
public class QuickFirework {

    public static void accelerated(){
        LocalPlayer player = client.player;
        if (player == null || !player.isFallFlying()) return;
        AbstractContainerMenu sc = player.containerMenu;
        NonNullList<Slot> slots = sc.slots;
        for (int i = 0; i < slots.size(); i++) {
            ItemStack stack = slots.get(i).getItem();
            //#if MC > 12004
            Fireworks fireworksComponent = stack.get(DataComponents.FIREWORKS);
            if (fireworksComponent != null && fireworksComponent.explosions().isEmpty()) {
                interactItem(sc,i);
                return;
            }
            //#else
            //$$ CompoundTag nbtCompound = stack.getTagElement("Fireworks");
            //$$ if(nbtCompound != null && nbtCompound.getList("Explosions", 10).isEmpty()){
            //$$     interactItem(sc,i);
            //$$     return;
            //$$ }
            //#endif
        }
    }
    public static void interactItem(AbstractContainerMenu sc , int i) {
        client.gameMode.handleInventoryMouseClick(sc.containerId, i, 40, ClickType.SWAP, client.player);
        //#if MC > 11802
        client.gameMode.useItem(client.player, InteractionHand.OFF_HAND);
        //#else
        //$$ client.gameMode.useItem(client.player,client.level, InteractionHand.OFF_HAND);
        //#endif
        client.gameMode.handleInventoryMouseClick(sc.containerId, i, 40, ClickType.SWAP, client.player);
    }
}
