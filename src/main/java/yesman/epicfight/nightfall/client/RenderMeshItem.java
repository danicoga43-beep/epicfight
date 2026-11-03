package yesman.epicfight.nightfall.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.client.model.ClassicMesh;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/// Renderer item skin "efn:mesh_item": menggambar mesh 3D senjata (model asli Nightfall) di sendi tangan.
/// Kunci JSON item_skins:
///   "mesh_main": "efn:weapon/crimson_moon"   (animmodels/weapon/crimson_moon.json)
///   "texture": tekstur utama, "texture_l": lapisan menyala (opsional)
///   "mesh_offset": [x, y, z], "mesh_rotation": [x, y, z] (derajat), "mesh_scale": angka  -> untuk menyetel posisi tanpa build ulang
public class RenderMeshItem extends RenderItemBase {
	private final MeshAccessor<ClassicMesh> mesh;
	private final ResourceLocation texture;
	private final ResourceLocation glowTexture;
	private final float[] offset;
	private final float[] rotation;
	private final float scale;
	
	public RenderMeshItem(JsonElement jsonElement) {
		super(jsonElement);
		
		JsonObject json = jsonElement.getAsJsonObject();
		String meshName = GsonHelper.getAsString(json, "mesh_main", "efn:weapon/crimson_moon");
		ResourceLocation meshLocation = ResourceLocation.parse(meshName);
		
		this.mesh = NightfallMeshes.CRIMSON_MOON;
		this.texture = ResourceLocation.parse(GsonHelper.getAsString(json, "texture", "efn:textures/item/weapon/crimson_moon.png"));
		this.glowTexture = json.has("texture_l") ? ResourceLocation.parse(GsonHelper.getAsString(json, "texture_l")) : null;
		this.offset = readVector(json, "mesh_offset");
		this.rotation = readVector(json, "mesh_rotation");
		this.scale = GsonHelper.getAsFloat(json, "mesh_scale", 1.0F);
		
		if (!meshLocation.equals(ResourceLocation.parse("efn:weapon/crimson_moon"))) {
			// Hanya model crimson_moon yang didaftarkan di tahap ini; senjata lain menyusul.
			yesman.epicfight.main.EpicFightMod.LOGGER.warn("RenderMeshItem: mesh " + meshLocation + " belum didaftarkan, memakai crimson_moon");
		}
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
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		ClassicMesh classicMesh = this.mesh.get();
		Joint parentJoint = entitypatch.getParentJointOfHand(hand);
		
		if (classicMesh == null || parentJoint == null) {
			super.renderItemInHand(stack, entitypatch, hand, poses, buffer, poseStack, packedLight, partialTicks);
			return;
		}
		
		OpenMatrix4f modelMatrix = new OpenMatrix4f().load(poses[parentJoint.getId()]);
		modelMatrix.translate(this.offset[0], this.offset[1], this.offset[2]);
		
		if (this.rotation[0] != 0.0F) {
			modelMatrix.rotateDeg(this.rotation[0], Vec3f.X_AXIS);
		}
		
		if (this.rotation[1] != 0.0F) {
			modelMatrix.rotateDeg(this.rotation[1], Vec3f.Y_AXIS);
		}
		
		if (this.rotation[2] != 0.0F) {
			modelMatrix.rotateDeg(this.rotation[2], Vec3f.Z_AXIS);
		}
		
		poseStack.pushPose();
		MathUtils.mulStack(poseStack, modelMatrix);
		poseStack.scale(this.scale, this.scale, this.scale);
		
		classicMesh.draw(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(this.texture)), Mesh.DrawingFunction.NEW_ENTITY, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY);
		
		if (this.glowTexture != null) {
			classicMesh.draw(poseStack, buffer.getBuffer(RenderType.eyes(this.glowTexture)), Mesh.DrawingFunction.NEW_ENTITY, 15728880, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY);
		}
		
		poseStack.popPose();
	}
}
