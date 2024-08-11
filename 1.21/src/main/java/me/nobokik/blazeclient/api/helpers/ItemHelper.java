package me.nobokik.blazeclient.api.helpers;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.nobokik.blazeclient.Client.mc;

public class ItemHelper {
    public static List<ItemStorage> storageFromItem(List<ItemStack> items) {
        ArrayList<ItemStorage> storage = new ArrayList<>();
        for (ItemStack item : items) {
            if (item.isEmpty()) {
                continue;
            }
            Optional<ItemStorage> s = getItemFromItem(item, storage);
            if (s.isPresent()) {
                ItemHelper.ItemStorage store = s.get();
                store.incrementTimes(item.getCount());
            } else {
                storage.add(new ItemHelper.ItemStorage(item, item.getCount()));
            }
        }
        return storage;
    }
    public static List<ItemStack> getItems() {
        ArrayList<ItemStack> items = new ArrayList<>();
        if (mc.player == null) {
            return null;
        }
        items.addAll(mc.player.getInventory().armor);
        items.addAll(mc.player.getInventory().offHand);
        items.addAll(mc.player.getInventory().main);
        return items;
    }
    public static int getTotal(ItemStack stack) {
        List<ItemStack> item = ItemHelper.getItems();
        if (item == null || item.isEmpty()) {
            return 0;
        }
        List<ItemHelper.ItemStorage> items = ItemHelper.storageFromItem(item);
        Optional<ItemStorage> stor = ItemHelper.getItemFromItem(stack, items);
        return stor.map(itemStorage -> itemStorage.times).orElse(0);
    }

    public static Optional<ItemHelper.ItemStorage> getItemFromItem(ItemStack item, List<ItemHelper.ItemStorage> list) {
        ItemStack compare = item.copy();
        compare.setCount(1);
        for (ItemHelper.ItemStorage storage : list) {
            if (storage.stack.isOf(compare.getItem())) {
                return Optional.of(storage);
            }
        }
        return Optional.empty();
    }

    public static class ItemStorage {

        public final ItemStack stack;
        public int times;

        public ItemStorage(ItemStack stack) {
            this(stack, 1);
        }

        public ItemStorage(ItemStack stack, int times) {
            ItemStack copy = stack.copy();
            copy.setCount(1);
            this.stack = copy;
            this.times = times;
        }

        public void incrementTimes(int num) {
            times = times + num;
        }

        public ItemStorage copy() {
            return new ItemStorage(stack.copy(), times);
        }
    }

}
