package com.licht_meilleur.tree_of_yorishiro.recipe;

import net.minecraft.item.ItemStack;

import java.util.List;

public class YorisyokuninRecipeDef {

    private final List<YorisyokuninRequirement> inputs;
    private final ItemStack output;

    public YorisyokuninRecipeDef(List<YorisyokuninRequirement> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    public List<YorisyokuninRequirement> getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public boolean matches(List<ItemStack> stacks) {
        if (stacks.size() != inputs.size()) return false;

        boolean[] used = new boolean[stacks.size()];

        for (YorisyokuninRequirement requirement : inputs) {
            boolean matched = false;
            for (int i = 0; i < stacks.size(); i++) {
                if (used[i]) continue;
                if (requirement.matches(stacks.get(i))) {
                    used[i] = true;
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }

        return true;
    }
}