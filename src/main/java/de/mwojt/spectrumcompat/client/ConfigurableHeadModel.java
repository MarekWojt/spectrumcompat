package de.mwojt.spectrumcompat.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;

/**
 * Generic {@link SkullModelBase} that renders whatever geometry is under the
 * baked layer's {@code "head"} part. Layer geometry is defined in
 * {@link HeadMeshes} — this class is the shared render loop for all of them.
 */
public class ConfigurableHeadModel extends SkullModelBase {
    private final ModelPart head;

    public ConfigurableHeadModel(ModelPart root) {
        this.head = root.getChild("head");
    }

    @Override
    public void setupAnim(float animTicks, float yaw, float pitch) {
        this.head.yRot = yaw * ((float) Math.PI / 180.0F);
        this.head.xRot = pitch * ((float) Math.PI / 180.0F);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer,
                               int packedLight, int packedOverlay, int color) {
        this.head.render(pose, buffer, packedLight, packedOverlay, color);
    }
}
