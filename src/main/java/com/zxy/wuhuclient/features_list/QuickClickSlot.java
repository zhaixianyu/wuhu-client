package com.zxy.wuhuclient.features_list;


import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;


import static com.zxy.wuhuclient.Utils.ZxyUtils.getPlayer;
import static com.zxy.wuhuclient.WuHuClientMod.*;

public class QuickClickSlot {

    public static void clickLastSlot() {
        getPlayer().ifPresent(clientPlayer -> {
            AbstractContainerMenu sc = clientPlayer.containerMenu;
            if (clientPlayer.containerMenu.equals(clientPlayer.inventoryMenu)) return;
            for (int i = 0; i < sc.slots.size(); i++) {
                if (sc.slots.get(i).container instanceof Inventory && i > 0) {
                    if (sc instanceof CraftingMenu) i = 1;
                    client.gameMode.handleContainerInput(sc.containerId, i-1, 0, ContainerInput.QUICK_MOVE, client.player);
                    return;
                }
            }

            //下面的代码在砂轮下不行
//            int size = sc.slots.get(0).inventory.size();
//            client.interactionManager.clickSlot(sc.syncId, size-1, 0, SlotActionType.QUICK_MOVE, client.player);
        });
    }
}
