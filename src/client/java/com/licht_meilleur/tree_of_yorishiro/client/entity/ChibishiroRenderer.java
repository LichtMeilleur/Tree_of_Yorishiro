package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.licht_meilleur.tree_of_yorishiro.client.entity.ChibishiroModel;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChibishiroRenderer extends GeoEntityRenderer<ChibishiroEntity> {

    public ChibishiroRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ChibishiroModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public void render(ChibishiroEntity entity,
                       float entityYaw,
                       float partialTick,
                       MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers,
                       int packedLight) {

        matrices.push();

        float s = 0.65f; // 0.5f～0.8fで調整
        matrices.scale(s, s, s);

        super.render(entity, entityYaw, partialTick, matrices, vertexConsumers, packedLight);

        matrices.pop();
    }
}