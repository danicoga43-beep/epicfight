package yesman.epicfight.nightfall;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.level.ServerPlayer;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/// Memasang Shadow Step di slot dodge selama pemain memegang Shadow, lalu mengembalikan skill dodge sebelumnya.
public final class ShadowDodge {
	private ShadowDodge() {}
	
	private static final Map<UUID, Optional<Skill>> PREVIOUS = new ConcurrentHashMap<>();
	
	public static void tick(ServerPlayerPatch patch, boolean holdingShadow) {
		if (holdingShadow) {
			equip(patch);
		} else if (PREVIOUS.containsKey(patch.getOriginal().getUUID())) {
			restore(patch);
		}
	}
	
	public static void equip(ServerPlayerPatch patch) {
		SkillContainer dodge = patch.getSkill(SkillSlots.DODGE);
		Skill step = NightfallSkills.SHADOW_STEP.get();
		
		if (dodge == null || dodge.getSkill() == step) {
			return;
		}
		
		PREVIOUS.put(patch.getOriginal().getUUID(), Optional.ofNullable(dodge.getSkill()));
		
		if (dodge.setSkill(step)) {
			sync(patch, dodge);
		}
	}
	
	public static void restore(ServerPlayerPatch patch) {
		Optional<Skill> previous = PREVIOUS.remove(patch.getOriginal().getUUID());
		
		if (previous == null) {
			return;
		}
		
		SkillContainer dodge = patch.getSkill(SkillSlots.DODGE);
		
		if (dodge != null && dodge.getSkill() == NightfallSkills.SHADOW_STEP.get()) {
			dodge.setSkill(previous.orElse(null));
			sync(patch, dodge);
		}
	}
	
	private static void sync(ServerPlayerPatch patch, SkillContainer container) {
		ServerPlayer player = patch.getOriginal();
		EpicFightNetworkManager.sendToPlayer(container.createSyncPacketToLocalPlayer(), player);
	}
}
