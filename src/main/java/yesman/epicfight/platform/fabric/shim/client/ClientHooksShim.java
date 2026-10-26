package yesman.epicfight.platform.fabric.shim.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

/// The few NeoForge ClientHooks helpers Epic Fight relies on.
public final class ClientHooksShim {
    private ClientHooksShim() {}

    public static BakedModel handleCameraTransforms(PoseStack poseStack, BakedModel model, ItemDisplayContext context, boolean applyLeftHandTransform) {
        model.getTransforms().getTransform(context).apply(applyLeftHandTransform, poseStack);
        return model;
    }

    public static float getGuiFarPlane() {
        return 21000.0F;
    }

    /// NeoForge's ClientHooksShim.InteractionKeyMappingTriggered replacement. Nothing cancels it on Fabric.
    public static final class InteractionKeyMappingTriggered {
        private boolean canceled;
        private boolean swingHand = true;

        public boolean isCanceled() { return this.canceled; }
        public void setCanceled(boolean canceled) { this.canceled = canceled; }
        public boolean shouldSwingHand() { return this.swingHand; }
        public void setSwingHand(boolean swingHand) { this.swingHand = swingHand; }
    }

    public static InteractionKeyMappingTriggered onClickInput(int button, net.minecraft.client.KeyMapping keyBinding, net.minecraft.world.InteractionHand hand) {
        return new InteractionKeyMappingTriggered();
    }
}
