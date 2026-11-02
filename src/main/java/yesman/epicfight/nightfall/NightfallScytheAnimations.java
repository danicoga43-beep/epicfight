package yesman.epicfight.nightfall;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.AirSlashAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;
import yesman.epicfight.api.animation.types.DashAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.registry.entries.EpicFightSounds;

/// Animasi sabit (Scythe) Epic Fight Nightfall: idle/jalan/lari, combo 1-5, dash, air slash, block, dan skill Harvest.
/// Berkas animasi (.json) ada di assets/efn/animmodels/animations/biped/scythe/...
/// Event VFX (slash merah, Effekseer, camera shake) belum dibawa pada tahap 1.
public final class NightfallScytheAnimations {
	private NightfallScytheAnimations() {}
	
	public static AnimationAccessor<StaticAnimation> SCYTHE_IDLE_COMBAT;
	public static AnimationAccessor<MovementAnimation> SCYTHE_WALK_COMBAT;
	public static AnimationAccessor<MovementAnimation> SCYTHE_RUN_COMBAT;
	public static AnimationAccessor<StaticAnimation> SCYTHE_IDLE_NORMAL;
	public static AnimationAccessor<MovementAnimation> SCYTHE_WALK_NORMAL;
	public static AnimationAccessor<MovementAnimation> SCYTHE_RUN_NORMAL;
	public static AnimationAccessor<ComboAttackAnimation> SCYTHE_AUTO1;
	public static AnimationAccessor<ComboAttackAnimation> SCYTHE_AUTO2;
	public static AnimationAccessor<ComboAttackAnimation> SCYTHE_AUTO3;
	public static AnimationAccessor<ComboAttackAnimation> SCYTHE_AUTO4;
	public static AnimationAccessor<ComboAttackAnimation> SCYTHE_AUTO5;
	public static AnimationAccessor<AirSlashAnimation> SCYTHE_AIR_SLASH;
	public static AnimationAccessor<DashAttackAnimation> SCYTHE_DASH;
	public static AnimationAccessor<AttackAnimation> SCYTHE_HARVEST;
	public static AnimationAccessor<StaticAnimation> SCYTHE_BLOCK;
	
	private static final MultiOBBCollider SCYTHE_COLLIDER = new MultiOBBCollider(
		new OBBCollider(0.25, 0.25, 1.3, 0.0, 0.0, 1.0),
		new OBBCollider(0.25, 0.25, 1.3, 0.0, 0.0, 1.0),
		new OBBCollider(0.7, 0.9, 0.7, 0.0, -0.5, -0.45),
		new OBBCollider(0.7, 0.9, 0.7, 0.0, -0.5, -0.45)
	);
	
	public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
		event.newBuilder(NightfallRegistries.MODID, NightfallScytheAnimations::build);
	}
	
	public static void build(AnimationBuilder builder) {
		Joint mainHand = Armatures.BIPED.get().toolR;
		Collider scytheCollider = SCYTHE_COLLIDER;
		
		// Kecepatan Harvest naik bersama efek Haste (efek Attack Speed Increase bawaan Nightfall belum dibawa).
		AnimationProperty.PlaybackSpeedModifier harvestSpeed = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
			LivingEntity entity = entitypatch.getOriginal();
			MobEffectInstance haste = entity.getEffect(MobEffects.DIG_SPEED);
			float bonus = haste == null ? 0.0F : (haste.getAmplifier() + 1) * 0.1F;
			
			return Math.min(1.0F + bonus, 1.5F);
		};
		
		SCYTHE_IDLE_COMBAT = builder.nextAccessor("biped/scythe/living/scythe_idle_combat", (accessor) -> new StaticAnimation(0.2F, true, accessor, Armatures.BIPED));
		SCYTHE_WALK_COMBAT = builder.nextAccessor("biped/scythe/living/scythe_walk_combat", (accessor) -> new MovementAnimation(0.2F, true, accessor, Armatures.BIPED));
		SCYTHE_RUN_COMBAT = builder.nextAccessor("biped/scythe/living/scythe_run_combat", (accessor) -> new MovementAnimation(0.2F, true, accessor, Armatures.BIPED));
		SCYTHE_IDLE_NORMAL = builder.nextAccessor("biped/scythe/living/scythe_idle_normal", (accessor) -> new StaticAnimation(0.2F, true, accessor, Armatures.BIPED));
		SCYTHE_WALK_NORMAL = builder.nextAccessor("biped/scythe/living/scythe_walk_normal", (accessor) -> new MovementAnimation(0.2F, true, accessor, Armatures.BIPED));
		SCYTHE_RUN_NORMAL = builder.nextAccessor("biped/scythe/living/scythe_run_normal", (accessor) -> new MovementAnimation(0.2F, true, accessor, Armatures.BIPED));
		
		SCYTHE_AUTO1 = builder.nextAccessor("biped/scythe/combat/scythe_auto1", (accessor) ->
			new ComboAttackAnimation(0.25F, 0.11666667F, 0.575F, 0.71F, 1.0F, scytheCollider, mainHand, accessor, Armatures.BIPED)
				.addProperty(AttackPhaseProperty.SWING_SOUND, NightfallSounds.WHOOSH_HEAVY_1.get())
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
				.newTimePair(0.0F, 0.735F)
					.addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false));
		
		SCYTHE_AUTO2 = builder.nextAccessor("biped/scythe/combat/scythe_auto2", (accessor) ->
			new ComboAttackAnimation(0.1F, 0.11666667F, 0.58F, 0.7F, 1.3F, scytheCollider, mainHand, accessor, Armatures.BIPED)
				.addProperty(AttackPhaseProperty.SWING_SOUND, NightfallSounds.WHOOSH_HEAVY_2.get())
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
				.newTimePair(0.0F, 0.725F)
					.addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false));
		
		SCYTHE_AUTO3 = builder.nextAccessor("biped/scythe/combat/scythe_auto3", (accessor) ->
			new ComboAttackAnimation(0.1F, 0.11666667F, 0.58F, 0.75F, 1.33F, scytheCollider, mainHand, accessor, Armatures.BIPED)
				.addProperty(AttackPhaseProperty.SWING_SOUND, NightfallSounds.WHOOSH_HEAVY_4.get())
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
				.newTimePair(0.0F, 0.75F)
					.addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false));
		
		SCYTHE_AUTO4 = builder.nextAccessor("biped/scythe/combat/scythe_auto4", (accessor) ->
			new ComboAttackAnimation(0.01F, 0.33333334F, 0.75F, 0.86F, 1.42F, scytheCollider, mainHand, accessor, Armatures.BIPED)
				.addProperty(AttackPhaseProperty.SWING_SOUND, NightfallSounds.WHOOSH_HEAVY_2.get())
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
				.newTimePair(0.0F, 0.885F)
					.addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false));
		
		SCYTHE_AUTO5 = builder.nextAccessor("biped/scythe/combat/scythe_auto5", (accessor) ->
			new ComboAttackAnimation(0.1F, accessor, Armatures.BIPED,
				new AttackAnimation.Phase(0.0F, 0.11666667F, 0.52F, 0.71F, 1.0F, 1.0F, mainHand, scytheCollider)
					.addProperty(AttackPhaseProperty.SWING_SOUND, NightfallSounds.WHOOSH_HEAVY_2.get()),
				new AttackAnimation.Phase(1.0F, 1.14F, 1.31F, 2.3F, 2.3F, mainHand, scytheCollider)
					.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
					.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
					.addProperty(AttackPhaseProperty.SWING_SOUND, NightfallSounds.WHOOSH_HEAVY_3.get()))
				.addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
				.newTimePair(0.0F, 1.8F)
					.addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false));
		
		SCYTHE_AIR_SLASH = builder.nextAccessor("biped/scythe/combat/scythe_airslash", (accessor) ->
			new AirSlashAnimation(0.1F, 0.56F, 0.65F, 1.28F, scytheCollider, mainHand, accessor, Armatures.BIPED)
				.addProperty(AttackPhaseProperty.SWING_SOUND, NightfallSounds.WHOOSH_HEAVY_3.get())
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
				.addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 0.65F))
				.newTimePair(0.0F, 0.75F)
					.addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false));
		
		SCYTHE_DASH = builder.nextAccessor("biped/scythe/combat/scythe_dash", (accessor) ->
			new DashAttackAnimation(0.1F, 0.61F, 0.61F, 0.71F, 1.35F, scytheCollider, mainHand, accessor, Armatures.BIPED)
				.addProperty(AttackPhaseProperty.SWING_SOUND, NightfallSounds.WHOOSH_HEAVY_2.get())
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
				.newTimePair(0.0F, 0.81F)
					.addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false));
		
		// Skill Harvest: 3 tebasan memutar
		SCYTHE_HARVEST = builder.nextAccessor("biped/scythe/skill/scythe_harvest", (accessor) ->
			new AttackAnimation(0.1F, accessor, Armatures.BIPED,
				new AttackAnimation.Phase(0.0F, 0.66F, 0.81F, 0.81F, 0.81F, mainHand, scytheCollider)
					.addProperty(AttackPhaseProperty.SWING_SOUND, NightfallSounds.WHOOSH_HEAVY_4.get()),
				new AttackAnimation.Phase(0.81F, 0.81F, 1.0F, 1.0F, 1.0F, mainHand, scytheCollider)
					.addProperty(AttackPhaseProperty.SWING_SOUND, NightfallSounds.WHOOSH_HEAVY_4.get()),
				new AttackAnimation.Phase(1.0F, 1.05F, 1.21F, 2.13F, 2.13F, mainHand, scytheCollider)
					.addProperty(AttackPhaseProperty.SWING_SOUND, NightfallSounds.WHOOSH_HEAVY_2.get()))
				.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, harvestSpeed)
				.newTimePair(0.0F, 1.63F)
					.addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
				.newTimePair(0.0F, 1.83F)
					.addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false));
		
		SCYTHE_BLOCK = builder.nextAccessor("biped/scythe/skill/scythe_block", (accessor) -> new StaticAnimation(0.2F, true, accessor, Armatures.BIPED));
	}
}
