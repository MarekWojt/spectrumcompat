package de.mwojt.spectrumcompat.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;

/**
 * {@link SkullModelBase} that applies a per-axis scale to the baked
 * {@code "head"} part. Scale is written to {@link ModelPart#xScale}/
 * {@link ModelPart#yScale}/{@link ModelPart#zScale} so
 * {@code translateAndRotate} applies it in the cube's local (pre-rotation)
 * frame. Using {@code poseStack.scale} instead would apply after the head's
 * yaw rotation and distort the cube whenever the skull is placed in a
 * different facing.
 */
public class ScaledHeadModel extends SkullModelBase {
    private final ModelPart head;
    private final float scaleX;
    private final float scaleY;
    private final float scaleZ;

    public ScaledHeadModel(ModelPart root, float scaleX, float scaleY, float scaleZ) {
        this.head = root.getChild("head");
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    @Override
    public void setupAnim(float animTicks, float yaw, float pitch) {
        this.head.xScale = this.scaleX;
        this.head.yScale = this.scaleY;
        this.head.zScale = this.scaleZ;
        this.head.yRot = yaw * ((float) Math.PI / 180.0F);
        this.head.xRot = pitch * ((float) Math.PI / 180.0F);
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer,
                               int packedLight, int packedOverlay, int color) {
        this.head.render(pose, buffer, packedLight, packedOverlay, color);
    }
}
