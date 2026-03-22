package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChibishiroRenderer extends GeoEntityRenderer<ChibishiroEntity> {

    public ChibishiroRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ChibishiroModel());
        this.shadowRadius = 0.3f;
    }
}