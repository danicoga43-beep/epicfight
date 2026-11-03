package yesman.epicfight.nightfall.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/// Renderer item skin "efn:mesh_item": mesh 3D asli Crimson Moon (sudah di-bake, lihat BakedScythe).
/// Kunci JSON item_skins (semua opsional, bisa disetel tanpa build ulang lalu F3+T):
///   "texture", "texture_l"            : tekstur utama dan lapisan menyala
///   "mesh_offset"/"mesh_rotation"/"mesh_scale" : tambahan di sendi (tangan atau punggung)
///   "fp_offset"/"fp_rotation"/"fp_scale"       : tambahan khusus kamera pertama / render vanilla
public class RenderMeshItem extends RenderItemBase {
	public static volatile ResourceLocation TEXTURE = ResourceLocation.parse("efn:textures/item/weapon/crimson_moon.png");
	public static volatile ResourceLocation GLOW = ResourceLocation.parse("efn:textures/item/weapon/crimson_moon_light.png");
	public static volatile float[] FP_OFFSET = new float[3];
	public static volatile float[] FP_ROTATION = new float[3];
	public static volatile float FP_SCALE = 1.0F;
	
	private final float[] offset;
	private final float[] rotation;
	private final float scale;
	
	public RenderMeshItem(JsonElement jsonElement) {
		super(jsonElement);
		
		JsonObject json = jsonElement.getAsJsonObject();
		this.offset = readVector(json, "mesh_offset");
		this.rotation = readVector(json, "mesh_rotation");
		this.scale = GsonHelper.getAsFloat(json, "mesh_scale", 1.0F);
		
		TEXTURE = ResourceLocation.parse(GsonHelper.getAsString(json, "texture", "efn:textures/item/weapon/crimson_moon.png"));
		GLOW = json.has("texture_l") ? ResourceLocation.parse(GsonHelper.getAsString(json, "texture_l")) : null;
		FP_OFFSET = readVector(json, "fp_offset");
		FP_ROTATION = readVector(json, "fp_rotation");
		FP_SCALE = GsonHelper.getAsFloat(json, "fp_scale", 1.0F);
	}
	
	private static float[] readVector(JsonObject json, String key) {
		float[] result = new float[3];
		
		if (json.has(key)) {
			var array = json.getAsJsonArray(key);
			
			for (int i = 0; i < 3 && i < array.size(); i++) {
				result[i] = array.get(i).getAsFloat();
			}
		}
		
		return result;
	}
	
	private static OpenMatrix4f handCorrection() {
		return GLOBAL_MAINHAND_ITEM_TRANSFORMS.get("Tool_R");
	}
	
	/// item-space (kerangka item vanilla) <- lokal Tool_R. Dipakai render vanilla / kamera pertama.
	public static OpenMatrix4f handInverse() {
		return new OpenMatrix4f().load(handCorrection()).invert();
	}
	
	/// Kerangka sendi punggung <- lokal Tool_R: pakai koreksi punggung bawaan Epic Fight untuk item di punggung.
	private static OpenMatrix4f backMatrix() {
		return new OpenMatrix4f().load(GLOBAL_MAINHAND_ITEM_TRANSFORMS.get("Chest")).mulBack(handInverse());
	}
	
	public static void applyTuning(OpenMatrix4f m, float[] offset, float[] rotation, float scale) {
		m.translate(offset[0], offset[1], offset[2]);
		
		if (rotation[0] != 0.0F) {
			m.rotateDeg(rotation[0], Vec3f.X_AXIS);
		}
		
		if (rotation[1] != 0.0F) {
			m.rotateDeg(rotation[1], Vec3f.Y_AXIS);
		}
		
		if (rotation[2] != 0.0F) {
			m.rotateDeg(rotation[2], Vec3f.Z_AXIS);
		}
		
		if (scale != 1.0F) {
			m.scale(scale, scale, scale);
		}
	}
	
	public static void drawMesh(PoseStack poseStack, MultiBufferSource buffer, int light) {
		BakedScythe.draw(poseStack, buffer.getBuffer(EpicFightRenderTypes.getTriangulated(RenderType.entityCutout(TEXTURE))), light, 1.0F, 1.0F, 1.0F, 1.0F);
		
		if (GLOW != null) {
			BakedScythe.draw(poseStack, buffer.getBuffer(EpicFightRenderTypes.getTriangulated(RenderType.eyes(GLOW))), 15728880, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		Joint parentJoint = entitypatch.getParentJointOfHand(hand);
		
		if (parentJoint == null || !BakedScythe.ensure()) {
			super.renderItemInHand(stack, entitypatch, hand, poses, buffer, poseStack, packedLight, partialTicks);
			return;
		}
		
		OpenMatrix4f matrix = new OpenMatrix4f().load(poses[parentJoint.getId()]);
		
		if (!parentJoint.getName().startsWith("Tool_")) {
			matrix.mulBack(backMatrix());
		}
		
		applyTuning(matrix, this.offset, this.rotation, this.scale);
		
		poseStack.pushPose();
		MathUtils.mulStack(poseStack, matrix);
		drawMesh(poseStack, buffer, packedLight);
		poseStack.popPose();
	}
}
