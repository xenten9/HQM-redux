package com.xenten9.hardcorequesting.reward;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class ItemStackRewardList extends QuestRewardList<ItemStack> {

    public void addAll(ItemStack[] array) {
        for (ItemStack stack : array) add(new ItemStackReward(stack));
    }

    public void set(ItemStack[] array) {
        clear();
        if (array != null) addAll(array);
    }

    public void set(int id, ItemStack stack) {
        set(id, new ItemStackReward(stack));
    }

    public void add(ItemStack stack) {
        add(new ItemStackReward(stack));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ItemStack[] toArray() {
        List<ItemStack> result = new ArrayList<>();
        for (QuestReward<ItemStack> reward : list) result.add(reward.getReward());
        return result.isEmpty() ? null : result.toArray(new ItemStack[result.size()]);
    }
}
