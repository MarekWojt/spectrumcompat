package de.mwojt.spectrumcompat.client;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Layer factories for each distinct mob head shape. Every head is centered on
 * the XZ origin and has its base at Y=0 (so wall skulls flush cleanly against
 * the wall and floor skulls sit on the ground). Texture atlas sizes match the
 * actual PNGs shipped by Aether / Deep Aether.
 */
public final class HeadMeshes {
    private HeadMeshes() {}

    // --- Aether passives -----------------------------------------------------

    public static LayerDefinition phyg() {
        return single(b -> b
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                .texOffs(16, 16).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 3.0F, 1.0F), 64, 32);
    }

    public static LayerDefinition flyingCow() {
        return single(b -> b
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -3.0F, 8.0F, 8.0F, 6.0F)
                .texOffs(22, 0).addBox(-5.0F, -9.0F, -1.0F, 1.0F, 3.0F, 1.0F)
                .texOffs(22, 0).addBox(4.0F, -9.0F, -1.0F, 1.0F, 3.0F, 1.0F), 64, 32);
    }

    public static LayerDefinition sheepuff() {
        return single(b -> b
                .texOffs(0, 0).addBox(-3.0F, -6.0F, -4.0F, 6.0F, 6.0F, 8.0F), 64, 32);
    }

    /**
     * Aerbunny. Atlas 64×32. Head cube + two ears (1×4×2 at
     * {@code texOffs(14,0)}) + two side whiskers/feelers (2×3×2 at
     * {@code texOffs(20,0)}) from Aether's {@code AerbunnyModel}, shifted
     * so the head cube sits on the skull origin.
     */
    public static LayerDefinition aerbunny() {
        return single(b -> b
                .texOffs(0, 0).addBox(-2.0F, -4.0F, -3.0F, 4.0F, 4.0F, 6.0F)
                .texOffs(14, 0).addBox(-2.0F, -8.0F, -1.0F, 1.0F, 4.0F, 2.0F)
                .texOffs(14, 0).addBox(1.0F, -8.0F, -1.0F, 1.0F, 4.0F, 2.0F)
                .texOffs(20, 0).addBox(-4.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F)
                .texOffs(20, 0).addBox(2.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F), 64, 32);
    }

    /**
     * Moa + Cockatrice (BipedBird). Atlas 128×64. Source geometry — cube
     * 4×4×8 at {@code texOffs(0,13)} + jaw 4×1×8 at {@code texOffs(24,13)},
     * both with {@code texScale 0.5}. Rendered 2× uniform via
     * {@link ScaledHeadModel}.
     */
    public static LayerDefinition moaLike() {
        CubeDeformation none = CubeDeformation.NONE;
        return single(b -> b
                .texOffs(0, 13).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 4.0F, 8.0F, none, 0.5F, 0.5F)
                .texOffs(24, 13).addBox(-2.0F, -1.0F, -4.0F, 4.0F, 1.0F, 8.0F, none, 0.5F, 0.5F), 128, 64);
    }

    // --- Aether hostiles -----------------------------------------------------

    /**
     * Swet (Blue + Golden). Vanilla {@link net.minecraft.client.model.SlimeModel}
     * outer shell (8×8×8) at {@code texOffs(0,0)} plus the eye/mouth cubes
     * from the inner layer, shifted 1px forward (Z = -4.5 instead of -3.5)
     * so they protrude visibly through the opaque outer shell — the skull
     * renderer has no translucent pass to replicate the vanilla gel effect.
     */
    public static LayerDefinition swet() {
        return single(b -> b
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                .texOffs(32, 0).addBox(-3.25F, -6.0F, -4.5F, 2.0F, 2.0F, 2.0F)
                .texOffs(32, 4).addBox(1.25F, -6.0F, -4.5F, 2.0F, 2.0F, 2.0F)
                .texOffs(32, 8).addBox(0.0F, -3.0F, -4.5F, 1.0F, 1.0F, 1.0F), 64, 32);
    }

    /**
     * Aechor Plant. Atlas 64×32. Bulb portion of Aether's
     * {@code AechorPlantModel}: 6×2×6 bulb at {@code texOffs(0,12)} with a
     * +0.75 deform, plus three stamen stems (1×6×1 at {@code texOffs(36,13)},
     * -0.25 deform) tipped with a 1×1×1 cube at {@code texOffs(32,15)}.
     * Stamen rotations are baked statically — the vanilla
     * {@link ConfigurableHeadModel#setupAnim} only drives the root head's
     * yaw/pitch, never the children. Stem, thorns, leaves and petals are
     * skipped — stem/thorns/leaves are the plant's below-ground root, and
     * the petals are visually too wide for a block face.
     */
    public static LayerDefinition aechorPlant() {
        CubeDeformation bulbDeform = new CubeDeformation(0.75F);
        CubeDeformation stamenDeform = new CubeDeformation(-0.25F);
        CubeDeformation tipDeform = new CubeDeformation(0.125F);
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create(), PartPose.ZERO);

        head.addOrReplaceChild("bulb",
                CubeListBuilder.create()
                        .texOffs(0, 12).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 2.0F, 6.0F, bulbDeform),
                PartPose.ZERO);

        for (int i = 0; i < 3; i++) {
            float xRot = 0.2F + i / 15.0F;
            float yRot = 0.1F + (float) (Math.PI * 2.0 / 3.0) * i;
            PartDefinition stamen = head.addOrReplaceChild("stamen_stem_" + i,
                    CubeListBuilder.create()
                            .texOffs(36, 13).addBox(0.0F, -9.0F, -1.5F, 1.0F, 6.0F, 1.0F, stamenDeform),
                    PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, xRot, yRot, 0.0F));
            stamen.addOrReplaceChild("stamen_tip_" + i,
                    CubeListBuilder.create()
                            .texOffs(32, 15).addBox(0.0F, -9.0F, -1.5F, 1.0F, 1.0F, 1.0F, tipDeform),
                    PartPose.ZERO);
        }

        return LayerDefinition.create(mesh, 64, 32);
    }

    /**
     * Zephyr. Atlas 128×32. Replicates the head parts of Aether's
     * {@code ZephyrModel}: main cloud body 12×9×14 at {@code texOffs(27,9)},
     * two 4×6×2 face panels at {@code texOffs(67,11)} (left one mirrored),
     * and a 6×3×1 mouth at {@code texOffs(66,19)} (mirrored).
     */
    public static LayerDefinition zephyr() {
        return single(b -> b
                .texOffs(27, 9).addBox(-6.0F, -9.0F, -6.0F, 12.0F, 9.0F, 14.0F)
                .texOffs(67, 11).addBox(-7.0F, -6.0F, -8.0F, 4.0F, 6.0F, 2.0F)
                .mirror().texOffs(67, 11).addBox(3.0F, -6.0F, -8.0F, 4.0F, 6.0F, 2.0F)
                .texOffs(66, 19).addBox(-3.0F, -4.0F, -7.0F, 6.0F, 3.0F, 1.0F)
                .mirror(false), 128, 32);
    }

    // --- Aether dungeon mobs -------------------------------------------------

    /**
     * Mimic. Atlas 128×64. Replicates Aether's {@code MimicModel}: lower body
     * 16×10×16 at {@code texOffs(0,38)} + upper lid 16×6×16 at
     * {@code texOffs(0,10)} (with the vanilla π X-rotation so the lid UV
     * lands right-side-up) + the 2×4×1 knob at {@code texOffs(0,0)} as a
     * child of the lid. Defined at full 16³; rendered at 0.5× via
     * {@link ScaledHeadModel} so it fits vanilla-skull bounds.
     */
    public static LayerDefinition mimic() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 38).addBox(0.0F, 0.0F, 0.0F, 16.0F, 10.0F, 16.0F),
                PartPose.offset(-8.0F, -10.0F, -8.0F));
        PartDefinition lid = head.addOrReplaceChild("lid",
                CubeListBuilder.create()
                        .texOffs(0, 10).addBox(0.0F, 0.0F, 0.0F, 16.0F, 6.0F, 16.0F),
                PartPose.offsetAndRotation(-8.0F, -10.0F, 8.0F, (float) Math.PI, 0.0F, 0.0F));
        lid.addOrReplaceChild("knob",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(7.0F, -2.0F, 16.0F, 2.0F, 4.0F, 1.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }

    public static LayerDefinition sentry() {
        return single(b -> b
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), 64, 32);
    }

    public static LayerDefinition humanoidSmall() {
        return single(b -> b
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), 64, 32);
    }

    /**
     * Sun Spirit / Fire Minion head. Atlas 64×64. Head crown at texOffs(0,0)
     * 8×5×7 + chin plate at texOffs(0,12) 8×3×8 — combined Y=-8..0 centered.
     */
    public static LayerDefinition sunSpirit() {
        return single(b -> b
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -3.0F, 8.0F, 5.0F, 7.0F)
                .texOffs(0, 12).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 3.0F, 8.0F), 64, 64);
    }

    /**
     * Aerwhale. Atlas 256×128. Two of Aether's {@code AerwhaleModel} head
     * cubes: the main 24×18×28 body at {@code texOffs(0,0)} (where the face
     * lives) plus the 26×6×30 "underside2" chin slab at {@code texOffs(104,0)}
     * which hangs 1 unit below, 1 unit wider on each X/Z edge. Rendered at
     * 4/7× via {@link ScaledHeadModel} so the 28-deep body fits a block depth
     * centered on Z=0.
     */
    public static LayerDefinition aerwhale() {
        return single(b -> b
                .texOffs(0, 0).addBox(-12.0F, -19.0F, -14.0F, 24.0F, 18.0F, 28.0F)
                .texOffs(104, 0).addBox(-13.0F, -6.0F, -15.0F, 26.0F, 6.0F, 30.0F), 256, 128);
    }

    // --- Deep Aether ---------------------------------------------------------

    /**
     * Quail. Atlas 32×32. Head cube 4×4×5 at {@code texOffs(0,14)} + beak
     * 4×2×1 at {@code texOffs(13,14)} (both shifted +3 in Y and Z from the
     * entity model so the head cube sits at skull Y=-4..0 / Z=-1..4). Plus
     * the two side "ear" tufts from Deep Aether's {@code QuailModel} —
     * zero-thickness flat planes 0×4×3 at {@code texOffs(6,20)}, tilted
     * ±22.5° outward around Y so they splay from the sides of the head.
     */
    public static LayerDefinition quail() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 14).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 5.0F)
                        .texOffs(13, 14).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 2.0F, 1.0F),
                PartPose.ZERO);
        head.addOrReplaceChild("ear_left",
                CubeListBuilder.create()
                        .texOffs(6, 20).mirror().addBox(0.0F, -2.0F, -1.0F, 0.0F, 4.0F, 3.0F).mirror(false),
                PartPose.offsetAndRotation(-2.0F, -3.0F, 3.0F, 0.0F, -0.3927F, 0.0F));
        head.addOrReplaceChild("ear_right",
                CubeListBuilder.create()
                        .texOffs(6, 20).addBox(0.0F, -2.0F, -1.0F, 0.0F, 4.0F, 3.0F),
                PartPose.offsetAndRotation(2.0F, -3.0F, 3.0F, 0.0F, 0.3927F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    /**
     * Aerglow Fish. Atlas 64×64. Replicates the three head/body cubes of
     * Deep Aether's {@code AerglowFishModel}: head 2×4×3 at
     * {@code texOffs(13,0)}, the tiny nose child 2×3×1 at {@code texOffs(0,0)}
     * (1 px in front of the head, top-aligned), and body 2×4×9 at
     * {@code texOffs(0,0)} behind the head. Back flush at Z=+4; the nose
     * protrudes 1 px in front of the block.
     */
    public static LayerDefinition aerglowFish() {
        return single(b -> b
                .texOffs(13, 0).addBox(-1.0F, -4.0F, -6.0F, 2.0F, 4.0F, 3.0F)
                .texOffs(0, 0).addBox(-1.0F, -4.0F, -7.0F, 2.0F, 3.0F, 1.0F)
                .texOffs(0, 0).addBox(-1.0F, -4.0F, -3.0F, 2.0F, 4.0F, 9.0F), 64, 64);
    }

    /**
     * Venomite. Atlas 128×128. The pure (non-purple) anthracite region sits
     * in an L-shape: a 24×8 band at atlas {@code (42..66, 38..46)} plus a
     * 40×6 band at {@code (32..72, 46..52)}. Anthracite eyes (RGB
     * 218,224,234) sit at {@code (42,48)} and {@code (53,48)}. A 12×6×8
     * head cube at {@code texOffs(34,38)} covers both eyes on its front
     * face. Two 8×8×0 feeler flat planes on the head top sample the
     * two 8×8 anthracite patches whose diagonal {@code #}-streaks read as
     * swept/"geschwungen" outlines — left uses {@code texOffs(42,38)}
     * (streak sweeping down-left), right uses {@code texOffs(58,38)}
     * (mirror streak).
     */
    public static LayerDefinition venomite() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(34, 38).addBox(-6.0F, -6.0F, -4.0F, 12.0F, 6.0F, 8.0F),
                PartPose.ZERO);
        head.addOrReplaceChild("feeler_right",
                CubeListBuilder.create()
                        .texOffs(84, 40).addBox(-8.0F, -14.0F, 0.0F, 8.0F, 8.0F, 0.0F),
                PartPose.offsetAndRotation(2.0F, 2.0F, -1.0F, 0.0F, (float) (-Math.PI / 2.0 - 0.3054), 0.0F));
        head.addOrReplaceChild("feeler_left",
                CubeListBuilder.create()
                        .texOffs(84, 40).addBox(-8.0F, -14.0F, 0.0F, 8.0F, 8.0F, 0.0F),
                PartPose.offsetAndRotation(-2.0F, 2.0F, -1.0F, 0.0F, (float) (-Math.PI / 2.0 + 0.3054), 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    /**
     * Baby Zephyr. Atlas 128×128. Full body cube 12×9×16 at texOffs(0,0) plus the
     * three bb_main decorations (two side puffs and one front face detail) from
     * BabyZephyrModel. Cubes live on a "body" sub-part with a π Y-rotation so the
     * face lands on the skull's -Z side — wrapping is needed because the base
     * head part's yRot is overwritten by setupAnim with the block's rotation.
     */
    public static LayerDefinition babyZephyr() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-6.0F, -9.0F, -8.0F, 12.0F, 9.0F, 16.0F)
                        .texOffs(30, 42).addBox(-8.0F, -8.0F, -5.0F, 2.0F, 7.0F, 10.0F)
                        .texOffs(2, 40).addBox(6.0F, -8.0F, -5.0F, 2.0F, 7.0F, 10.0F)
                        .texOffs(16, 38).addBox(-3.0F, -7.0F, -10.0F, 6.0F, 5.0F, 2.0F),
                PartPose.rotation(0.0F, (float) Math.PI, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    /**
     * EOTS Controller. Atlas 128×128. Replicates the "bb_main" hierarchy from
     * EOTSSegmentModel (shown when the segment is the controlling one) — main
     * head cube plus six rotated bone-spikes (Head_r1..r6), a tilted flat plane
     * (cube_r1) and upper/lower mouth. bb_main is offset to Y=1 so the main
     * head cube's top lands at Y=0 (skull top).
     */
    public static LayerDefinition eotsController() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition bbMain = head.addOrReplaceChild("bb_main",
                CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -3.0F));

        bbMain.addOrReplaceChild("cube_r1",
                CubeListBuilder.create()
                        .texOffs(44, 13).addBox(-4.0F, -5.0F, 0.0F, 8.0F, 5.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, -10.0F, 1.0F, 0.4363F, 0.0F, 0.0F));

        PartDefinition head11 = bbMain.addOrReplaceChild("Head11",
                CubeListBuilder.create(), PartPose.offset(2.0F, -6.0F, 8.0F));
        head11.addOrReplaceChild("Head_r1",
                CubeListBuilder.create()
                        .texOffs(16, 28).addBox(-3.5F, -1.0F, -4.0F, 2.0F, 2.0F, 12.0F),
                PartPose.offsetAndRotation(4.0F, -6.0F, 1.0F, 1.0462F, 0.5295F, 0.2342F));

        PartDefinition head10 = bbMain.addOrReplaceChild("Head10",
                CubeListBuilder.create(), PartPose.offset(-2.0F, -6.0F, 8.0F));
        head10.addOrReplaceChild("Head_r2",
                CubeListBuilder.create()
                        .texOffs(0, 21).addBox(1.5F, -6.0F, -4.0F, 2.0F, 7.0F, 12.0F),
                PartPose.offsetAndRotation(-4.0F, 0.0F, -2.0F, 0.2618F, -0.3054F, 0.0F));

        PartDefinition head2 = bbMain.addOrReplaceChild("Head2",
                CubeListBuilder.create(), PartPose.offset(2.0F, -6.0F, 8.0F));
        head2.addOrReplaceChild("Head_r3",
                CubeListBuilder.create()
                        .texOffs(0, 40).addBox(-3.5F, -1.0F, -4.0F, 2.0F, 2.0F, 12.0F),
                PartPose.offsetAndRotation(4.0F, -3.0F, 3.0F, 0.5662F, 0.5295F, 0.2342F));
        head2.addOrReplaceChild("Head_r4",
                CubeListBuilder.create()
                        .texOffs(28, 9).addBox(-3.5F, -6.0F, -4.0F, 2.0F, 7.0F, 12.0F),
                PartPose.offsetAndRotation(4.0F, 0.0F, -2.0F, 0.2618F, 0.3054F, 0.0F));

        PartDefinition headSub = bbMain.addOrReplaceChild("Head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.0F, -4.0F, -10.0F, 8.0F, 9.0F, 12.0F),
                PartPose.offset(-2.0F, -6.0F, 8.0F));
        headSub.addOrReplaceChild("Head_r5",
                CubeListBuilder.create()
                        .texOffs(32, 30).addBox(1.5F, -1.0F, -4.0F, 2.0F, 2.0F, 12.0F),
                PartPose.offsetAndRotation(-4.0F, -3.0F, 3.0F, 0.5662F, -0.5295F, -0.2342F));
        headSub.addOrReplaceChild("Head_r6",
                CubeListBuilder.create()
                        .texOffs(16, 42).addBox(1.5F, -1.0F, -4.0F, 2.0F, 2.0F, 12.0F),
                PartPose.offsetAndRotation(-4.0F, -6.0F, 1.0F, 1.0462F, -0.5295F, -0.2342F));
        headSub.addOrReplaceChild("upperMouth",
                CubeListBuilder.create()
                        .texOffs(44, 0).addBox(-2.5F, -3.0F, -8.0F, 5.0F, 3.0F, 8.0F),
                PartPose.offset(2.0F, 3.0F, -10.0F));
        headSub.addOrReplaceChild("lowerMouth",
                CubeListBuilder.create()
                        .texOffs(70, 3).addBox(-2.5F, 0.0F, -8.0F, 5.0F, 2.0F, 8.0F),
                PartPose.offset(2.0F, 3.0F, -10.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    // --- VanillaBackport -----------------------------------------------------

    /**
     * Happy Ghast. Atlas 64×64 (matches VanillaBackport's own
     * {@code HappyGhastModel.createBodyLayer} — the PNG itself is 128×128
     * hi-res, but the model uses the 64×64 "logical" layout). The outer
     * {@code body} cube 16×16×16 at {@code texOffs(0,0)}, shifted so the cube
     * bottom sits at Y=0. Tentacles, inner body and harness are omitted — the
     * skull is head-only. Rendered at 0.5× via {@link ScaledHeadModel} so the
     * 16³ cube fits a vanilla skull's 8³ bounds.
     */
    public static LayerDefinition happyGhast() {
        return single(b -> b
                .texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F), 64, 64);
    }

    /**
     * Warm Chicken head. Atlas 64×32 (vanilla layout). 4×6×3 head cube +
     * 4×2×2 beak at {@code texOffs(14,0)} + 2×2×2 wattle at
     * {@code texOffs(14,4)}. Geometry matches the vanilla {@code ChickenModel}
     * — VB's WarmChicken doesn't override the head, only adds a body cape.
     */
    public static LayerDefinition warmChicken() {
        return single(b -> b
                .texOffs(0, 0).addBox(-2.0F, -6.0F, -1.5F, 4.0F, 6.0F, 3.0F)
                .texOffs(14, 0).addBox(-2.0F, -4.0F, -3.5F, 4.0F, 2.0F, 2.0F)
                .texOffs(14, 4).addBox(-1.0F, -2.0F, -2.5F, 2.0F, 2.0F, 2.0F), 64, 32);
    }

    /**
     * Cold Chicken head. Atlas 64×32. Same vanilla head + beak + wattle as
     * the warm chicken, plus a 6×3×4 feather-crest cube at {@code texOffs(44,0)}
     * sitting on top of the head (Y=-7..-4, X=-3..3, Z=-1.515..2.485). The
     * 0.015 Z bias mirrors VB's {@code ColdChickenModel} — it offsets the
     * crest a hair forward to avoid Z-fighting against the main head's front
     * face. Box positions translated from VB's body-relative head
     * (PartPose offset {@code 0, 15, -4}) into skull-local coords by
     * {@code z += 0.5} so the head cube stays centered at Z=-1.5..1.5.
     */
    public static LayerDefinition coldChicken() {
        return single(b -> b
                .texOffs(0, 0).addBox(-2.0F, -6.0F, -1.5F, 4.0F, 6.0F, 3.0F)
                .texOffs(44, 0).addBox(-3.0F, -7.0F, -1.515F, 6.0F, 3.0F, 4.0F)
                .texOffs(14, 0).addBox(-2.0F, -4.0F, -3.5F, 4.0F, 2.0F, 2.0F)
                .texOffs(14, 4).addBox(-1.0F, -2.0F, -2.5F, 2.0F, 2.0F, 2.0F), 64, 32);
    }

    /**
     * Warm Cow head. Atlas 64×64 (VB's WarmCowModel — the 1.21.2 atlas is taller
     * than the vanilla 64×32 cow). 8×8×6 head cube at {@code texOffs(0,0)} +
     * 6×3×1 muzzle at {@code texOffs(1,33)} + symmetric horn pair: each horn
     * is a 4×2×2 base at {@code texOffs(27,0)} stacked with a 2×2×2 tip at
     * {@code texOffs(39,0)} (right side mirrored). Box positions translated
     * from VB's body-relative head (PartPose offset {@code 0, 4, -8}) into
     * skull-local coords by {@code y -= 4, z += 3} so the head cube sits at
     * Y=-8..0 / Z=-3..3.
     */
    public static LayerDefinition warmCow() {
        return single(b -> b
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -3.0F, 8.0F, 8.0F, 6.0F)
                .texOffs(1, 33).addBox(-3.0F, -3.0F, -4.0F, 6.0F, 3.0F, 1.0F)
                .texOffs(27, 0).addBox(-8.0F, -7.0F, -2.0F, 4.0F, 2.0F, 2.0F)
                .texOffs(39, 0).addBox(-8.0F, -9.0F, -2.0F, 2.0F, 2.0F, 2.0F)
                .mirror().texOffs(27, 0).addBox(4.0F, -7.0F, -2.0F, 4.0F, 2.0F, 2.0F)
                .texOffs(39, 0).addBox(6.0F, -9.0F, -2.0F, 2.0F, 2.0F, 2.0F)
                .mirror(false), 64, 64);
    }

    /**
     * Cold Cow head. Atlas 64×64 (VB's ColdCowModel — yak/highland-cattle look).
     * 8×8×6 head cube at {@code texOffs(0,0)} + 6×3×1 muzzle at
     * {@code texOffs(9,33)} (different region than warm cow). Hooves are two
     * long 2×6×2 forward-tilted horns living in their own child parts:
     * {@code right_horn} samples {@code texOffs(0,40)}, {@code left_horn}
     * samples {@code texOffs(0,32)}. The horn parts inherit their original
     * VB rotations (π/2 around X, so the bone hangs forward) and offsets;
     * only the head's own boxes are translated by {@code y -= 4, z += 3}
     * to drop into skull-local coords. Horn child PartPoses get the same
     * shift since they were originally relative to the head's body offset.
     */
    public static LayerDefinition coldCow() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -3.0F, 8.0F, 8.0F, 6.0F)
                        .texOffs(9, 33).addBox(-3.0F, -3.0F, -4.0F, 6.0F, 3.0F, 1.0F),
                PartPose.ZERO);
        head.addOrReplaceChild("right_horn",
                CubeListBuilder.create()
                        .texOffs(0, 40).addBox(-1.5F, -4.5F, -0.5F, 2.0F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(-4.5F, -6.5F, -0.5F, 1.5708F, 0.0F, 0.0F));
        head.addOrReplaceChild("left_horn",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-1.5F, -3.0F, -0.5F, 2.0F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(5.5F, -6.5F, -2.0F, 1.5708F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /**
     * Warm Pig head. Atlas 64×32 (matches vanilla pig). 8×8×8 head cube +
     * 4×3×1 snout at {@code texOffs(16,16)}. VB's warm-pig texture keeps
     * the vanilla atlas size unchanged.
     */
    public static LayerDefinition warmPig() {
        return single(b -> b
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                .texOffs(16, 16).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 3.0F, 1.0F), 64, 32);
    }

    /**
     * Cold Pig head. Same head/snout geometry as the warm pig, but the
     * atlas is 64×64 (VB's ColdPigModel — the lower half of the PNG holds
     * an extra mantle/fluff region used by the body, never sampled by the
     * skull). The atlas size must match the texture so the head's V
     * coordinates normalise correctly; reusing the 64×32 warm layer would
     * compress the head sample into the upper quarter of the atlas.
     */
    public static LayerDefinition coldPig() {
        return single(b -> b
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                .texOffs(16, 16).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 3.0F, 1.0F), 64, 64);
    }

    // --- CopperAgeBackport ---------------------------------------------------

    /**
     * Copper Golem. Atlas 64×64. Replicates the "head" child of CopperGolemModel's
     * {@code createBodyLayer}: main head cube 8×5×10 at {@code texOffs(0,0)}, the
     * nose/spout 2×3×2 at {@code texOffs(56,0)}, antenna stem 2×4×2 at
     * {@code texOffs(37,8)}, and antenna bulb 4×4×4 at {@code texOffs(37,0)}.
     * All cubes shifted +1 in Y from their upstream local positions so the nose —
     * which originally hangs 1 unit below the main head cube's base — clears the
     * skull block's base plane (y=0) instead of clipping into the block below.
     * Rendered at 0.5× via {@link ScaledHeadModel} so the ~14-unit antenna stack
     * fits the skull's 8-unit block height.
     */
    public static LayerDefinition copperGolem() {
        return single(b -> b
                .texOffs(0, 0).addBox(-4.0F, -6.0F, -5.0F, 8.0F, 5.0F, 10.0F)
                .texOffs(56, 0).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 3.0F, 2.0F)
                .texOffs(37, 8).addBox(-1.0F, -10.0F, -1.0F, 2.0F, 4.0F, 2.0F)
                .texOffs(37, 0).addBox(-2.0F, -14.0F, -2.0F, 4.0F, 4.0F, 4.0F), 64, 64);
    }

    // --- plumbing ------------------------------------------------------------

    @FunctionalInterface
    private interface CubeBuild {
        CubeListBuilder build(CubeListBuilder in);
    }

    private static LayerDefinition single(CubeBuild cubes, int texW, int texH) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", cubes.build(CubeListBuilder.create()), PartPose.ZERO);
        return LayerDefinition.create(mesh, texW, texH);
    }
}
