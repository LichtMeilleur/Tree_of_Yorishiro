package com.licht_meilleur.tree_of_yorishiro.block.entity;

import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroAnimState;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import net.minecraft.nbt.NbtCompound;

public class TreeChibishiroData {

    private ChibishiroColor color;
    private int genki;
    private int kashikosa;
    private int chikara;
    private int stress;
    private boolean training;
    private boolean adventuring;
    private ChibishiroAnimState animState;

    public TreeChibishiroData(ChibishiroColor color) {
        this.color = color;
        this.genki = 100;
        this.kashikosa = 0;
        this.chikara = 0;
        this.stress = 0;
        this.training = false;
        this.adventuring = false;
        this.animState = ChibishiroAnimState.IDLE;
    }

    public ChibishiroColor getColor() {
        return color;
    }

    public int getGenki() {
        return genki;
    }

    public int getKashikosa() {
        return kashikosa;
    }

    public int getChikara() {
        return chikara;
    }

    public int getStress() {
        return stress;
    }

    public boolean isTraining() {
        return training;
    }

    public boolean isAdventuring() {
        return adventuring;
    }

    public ChibishiroAnimState getAnimState() {
        return animState;
    }

    public void setGenki(int genki) {
        this.genki = genki;
    }

    public void setKashikosa(int kashikosa) {
        this.kashikosa = kashikosa;
    }

    public void setChikara(int chikara) {
        this.chikara = chikara;
    }

    public void setStress(int stress) {
        this.stress = stress;
    }

    public void setTraining(boolean training) {
        this.training = training;
    }

    public void setAdventuring(boolean adventuring) {
        this.adventuring = adventuring;
    }

    public void setAnimState(ChibishiroAnimState animState) {
        this.animState = animState;
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putString("Color", color.getId());
        nbt.putInt("Genki", genki);
        nbt.putInt("Kashikosa", kashikosa);
        nbt.putInt("Chikara", chikara);
        nbt.putInt("Stress", stress);
        nbt.putBoolean("Training", training);
        nbt.putBoolean("Adventuring", adventuring);
        nbt.putString("AnimState", animState.name());
    }

    public static TreeChibishiroData fromNbt(NbtCompound nbt) {
        ChibishiroColor color = switch (nbt.getString("Color")) {
            case "blue" -> ChibishiroColor.BLUE;
            case "yellow" -> ChibishiroColor.YELLOW;
            case "purple" -> ChibishiroColor.PURPLE;
            case "red" -> ChibishiroColor.RED;
            default -> ChibishiroColor.WHITE;
        };

        TreeChibishiroData data = new TreeChibishiroData(color);
        data.genki = nbt.getInt("Genki");
        data.kashikosa = nbt.getInt("Kashikosa");
        data.chikara = nbt.getInt("Chikara");
        data.stress = nbt.getInt("Stress");
        data.training = nbt.getBoolean("Training");
        data.adventuring = nbt.getBoolean("Adventuring");

        String animName = nbt.getString("AnimState");
        try {
            data.animState = animName.isEmpty() ? ChibishiroAnimState.IDLE : ChibishiroAnimState.valueOf(animName);
        } catch (IllegalArgumentException ignored) {
            data.animState = ChibishiroAnimState.IDLE;
        }

        return data;
    }
}