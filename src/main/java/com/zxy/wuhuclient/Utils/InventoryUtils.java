package com.zxy.wuhuclient.Utils;

import com.zxy.wuhuclient.mixin.masa.Litematica_InventoryUtilsMixin;
import fi.dy.masa.litematica.config.Configs;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
//#if MC >= 12006
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
//#else
//$$
//#endif
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;

import static com.zxy.wuhuclient.Utils.SwitchItem.reSwitchItem;
import static com.zxy.wuhuclient.Utils.ZxyUtils.getPlayer;
import static com.zxy.wuhuclient.config.Configs.QUICK_SHULKER;
import static net.minecraft.block.ShulkerBoxBlock.FACING;

public class InventoryUtils {
    public static HashSet<Item> items2 = new HashSet<>();
    @NotNull
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static boolean openIng = false;
    public static boolean switchItem = false;
    public static void switchInv(){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ScreenHandler sc = player.currentScreenHandler;
        if(sc.equals(player.playerScreenHandler)){
            return;
        }
        DefaultedList<Slot> slots = sc.slots;
        for(Item item : items2) {
            for (int y = 0; y < slots.get(0).inventory.size(); y++) {
                if (slots.get(y).getStack().getItem().equals(item)) {

                    String[] str = Configs.Generic.PICK_BLOCKABLE_SLOTS.getStringValue().split(",");
                    if(str.length==0) return;
                    for (String s : str) {
                        if (s == null) break;
                        try {
                            int c = Integer.parseInt(s) - 1;
                            if (Registries.ITEM.getId(player.getInventory().getStack(c).getItem()).toString().contains("shulker_box") &&
                                    QUICK_SHULKER.getBooleanValue()) {
                                MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of("没有可替换的槽位，请将预选位的濳影盒换个位置"),false);
                                continue;
                            }
                            SwitchItem.newItem(slots.get(y).getStack(), null,null,y, shulkerBoxSlot);
                            shulkerBoxSlot = -1;
                            int a = Litematica_InventoryUtilsMixin.getEmptyPickBlockableHotbarSlot(player.getInventory()) == -1 ?
                                    Litematica_InventoryUtilsMixin.getPickBlockTargetSlot(player) :
                                    Litematica_InventoryUtilsMixin.getEmptyPickBlockableHotbarSlot(player.getInventory());
                            c = a == -1 ? c : a;
                            switchPlayerInvToHotbarAir(c);
                            fi.dy.masa.malilib.util.InventoryUtils.swapSlots(sc, y, c);
                            player.getInventory().selectedSlot = c;
                            player.closeHandledScreen();
                            items2 = new HashSet<>();
                            return;
                        } catch (Exception e) {
                            System.out.println("切换物品异常");
                        }
                    }
                }
            }
        }
        shulkerBoxSlot = -1;
        items2 = new HashSet<>();
        player.closeHandledScreen();
    }
    public static boolean switchItem(){
        if(items2.isEmpty()) return false;
        ClientPlayerEntity player = client.player;
        ScreenHandler sc = player.currentScreenHandler;
        if(!switchItem){
            if(!player.currentScreenHandler.equals(player.playerScreenHandler)) player.closeHandledScreen();
            if (sc.slots.stream().skip(9).limit(sc.slots.size()-10).noneMatch(slot -> slot.getStack().isEmpty())) {
                SwitchItem.checkItems();
                return true;
            }
            if(QUICK_SHULKER.getBooleanValue() && openShulker(items2)) return true;
        }
        return false;
    }
    static int shulkerBoxSlot = 0;
    static boolean openShulker(HashSet<Item> items){
        for (Item item : items) {
            ScreenHandler sc = MinecraftClient.getInstance().player.playerScreenHandler;
            for (int i = 9; i < sc.slots.size(); i++) {
                ItemStack stack = sc.slots.get(i).getStack();
                String itemid = Registries.ITEM.getId(stack.getItem()).toString();
                if(itemid.contains("shulker_box")){
                    DefaultedList<ItemStack> items1 = fi.dy.masa.malilib.util.InventoryUtils.getStoredItems(stack, -1);
                    if(items1.stream().anyMatch(s1 -> s1.getItem().equals(item))){
                        try {
                            if(reSwitchItem == null) shulkerBoxSlot = i;
                            Class quickShulker = Class.forName("net.kyrptonaught.quickshulker.client.ClientUtil");
                            Method checkAndSend = quickShulker.getDeclaredMethod("CheckAndSend",ItemStack.class,int.class);
                            checkAndSend.invoke(checkAndSend,stack,i);
                            ScreenManagement.closeScreen++;
                            switchItem = true;
                            return true;
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        return false;
    }
    public static void switchPlayerInvToHotbarAir(int slot){
        if(client.player == null )return;
        ClientPlayerEntity player = client.player;
        ScreenHandler sc = player.currentScreenHandler;
        DefaultedList<Slot> slots = sc.slots;
        int i = sc.equals(player.playerScreenHandler) ? 9 : 0;
        for (; i < slots.size(); i++) {
            if (slots.get(i).getStack().isEmpty() && slots.get(i).inventory instanceof PlayerInventory) {
                fi.dy.masa.malilib.util.InventoryUtils.swapSlots(sc, i, slot);
                return;
            }
        }
    }

    public static boolean equalsItem(ItemStack itemStack1,ItemStack itemStack2){
        //#if MC > 12004
        return ItemStack.areItemsAndComponentsEqual(itemStack1, itemStack2);
        //#else
        //$$ return ItemStack.canCombine(itemStack1, itemStack2);
        //#endif
    }

    public static int getEnchantmentLevel(ItemStack itemStack,
                                         //#if MC > 12006
                                         RegistryKey<Enchantment> enchantment
                                         //#else
                                         //$$ Enchantment enchantment
                                         //#endif
    ){
        //#if MC > 12006
        ItemEnchantmentsComponent enchantments = itemStack.getEnchantments();
        if (enchantments.equals(ItemEnchantmentsComponent.DEFAULT)) return -1;
        Set<RegistryEntry<Enchantment>> enchantmentsEnchantments = enchantments.getEnchantments();
        for (RegistryEntry<Enchantment> entry : enchantmentsEnchantments) {
            if (entry.matchesKey(enchantment)) {
                return enchantments.getLevel(entry);
            }
        }
        return -1;
        //#else
        //$$ return EnchantmentHelper.getLevel(Enchantments.MENDING,itemStack);
        //#endif
    }
    public static void refreshPlayerInventory(){
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (getPlayer().isEmpty()) return;
        ClientPlayerEntity player = getPlayer().get();
        if(networkHandler == null) return;
        ItemStack uniqueItem = new ItemStack(Items.STONE);

        // Tags with NaN are not equal, so the server will find an inventory desync and send an inventory refresh to the client
        //#if MC >= 12006
        var nbt = new NbtCompound();
        nbt.putDouble("force_sync", Double.NaN);
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, uniqueItem, nbt);
        //#else
        //$$ uniqueItem.getOrCreateNbt().putDouble("force_resync", Double.NaN);
        //#endif

        networkHandler.sendPacket(new ClickSlotC2SPacket(
                player.currentScreenHandler.syncId,
                player.currentScreenHandler.getRevision(),
                -999, 2,
                SlotActionType.QUICK_CRAFT,
                uniqueItem,
                new Int2ObjectOpenHashMap<>()

        ));
    }

    public static boolean isInventory(BlockPos pos) {
//        if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
//            BlockPos pos = ((BlockHitResult) client.crosshairTarget).getBlockPos();
        if (client.world == null) return false;
        Inventory inventory = fi.dy.masa.malilib.util.InventoryUtils.getInventory(client.world, pos);

        return inventory != null;
//        BlockState blockState = client.world.getBlockState(pos);
//        BlockEntity blockEntity = client.world.getBlockEntity(pos);
//        try {
//            if (((BlockWithEntityMixin) blockState.getBlock()).createScreenHandlerFactory(blockState, client.world, pos) == null ||
//                    (blockEntity instanceof ShulkerBoxBlockEntity entity &&
//                            //#if MC > 12004
//                            !client.world.isSpaceEmpty(ShulkerEntity.calculateBoundingBox(0.0f,blockState.get(FACING),  0.5f).offset(pos).contract(1.0E-6)) &&
//                            //#else
//                            //$$ !client.world.isSpaceEmpty(ShulkerEntity.calculateBoundingBox(blockState.get(FACING), 0.0f, 0.5f).offset(pos).contract(1.0E-6)) &&
//                            //#endif
//                            entity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED)) {
//                client.inGameHud.setOverlayMessage(Text.of("目标无法打开"), false);
//                return false;
//            }
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
    }

    public static boolean canOpenInv(BlockPos pos){
        if (client.world != null) {
            BlockState blockState = client.world.getBlockState(pos);
            BlockEntity blockEntity = client.world.getBlockEntity(pos);
            boolean isInventory = InventoryUtils.isInventory(pos);
            try {
                if ((isInventory && blockState.createScreenHandlerFactory(client.world,pos) == null) ||
                        (blockEntity instanceof ShulkerBoxBlockEntity entity &&
                                //#if MC > 12101
                                //$$ !client.world.isSpaceEmpty(ShulkerEntity.calculateBoundingBox(1.0F, blockState.get(FACING), 0.0F, 0.5F, pos.toBottomCenterPos()).offset(pos).contract(1.0E-6)) &&
                                //#elseif MC <= 12101 && MC > 12004
                                !client.world.isSpaceEmpty(ShulkerEntity.calculateBoundingBox(1.0F, blockState.get(FACING), 0.0F, 0.5F).offset(pos).contract(1.0E-6)) &&
                                //#elseif MC <= 12004
                                //$$ !client.world.isSpaceEmpty(ShulkerEntity.calculateBoundingBox(blockState.get(FACING), 0.0f, 0.5f).offset(pos).contract(1.0E-6)) &&
                                //#endif
                                entity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED)) {
                    return false;
                }else if(!isInventory){
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }else {
            return false;
        }
    }
}