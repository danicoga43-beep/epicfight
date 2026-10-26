# Epic Fight → Fabric 1.21.1 — porting status

**Not compiled or run yet.** This was written without a Minecraft/Fabric toolchain available, so the first
GitHub Actions run is expected to report compile errors. Send `build.log` (artifact "build-log") back and they get fixed in rounds.

## How to build on GitHub (works from a phone browser)
1. Create a GitHub repository and upload the contents of this folder (keep `.github/workflows/build.yml`).
2. Open the **Actions** tab → "Build Epic Fight (Fabric 1.21.1)" → **Run workflow** (it also runs on every push).
3. When it finishes, download the artifact `epicfight-fabric-jar` (success) or `build-log` (failure).
4. Install with Fabric Loader 0.16+ and Fabric API on Minecraft 1.21.1 (Forge Config API Port is bundled inside the jar).

## Ported (written, unverified)
- Gradle/Loom build, GitHub Actions workflow, `fabric.mod.json`, access widener (converted from the access transformer; field descriptors written by hand).
- Registries: `DeferredRegister`/`DeferredHolder`/`RegistryBuilder`/callbacks shim on top of Fabric registries; vanilla-order registration.
- Entity patch storage (replaces NeoForge attachments) and item capability provider (replaces NeoForge capabilities).
- Combat core via mixins (`platform/fabric/mixin`): incoming damage, damage pre/post, knockback, death, drops (+ skill-book mob drops), shield block, jump,
  mob effects, mount/dismount, entity tick pre/post, join level, enderman spawn/teleport, projectile impact, item-use start, arrow loose, player data load, Ender Dragon size.
- Fabric API events: equipment change, tracking, player clone/dimension change, right-click item, item attribute modifiers, loot tables, datapack sync, commands, reload listeners, skill-book creative tabs.
- Networking (payload registrar / distributor shim on Fabric networking v1), configs (via Forge Config API Port), custom attributes, spawn placements, datapack registry (Emote).
- Client: typed event bus + shim events for all events the render/control engines use, particles, renderers, HUD layers, screen events, shaders, key mappings, built-in legacy pack.

## Build log fixes (round 1)
- Added `jsr305` and `checker-qual` (javax.annotation / checkerframework annotations were missing: ~750 of the 798 errors).
- `ServerEntityEvents` import moved to `net.fabricmc.fabric.api.event.lifecycle.v1`.
- `ModifyItemAttributeModifiersCallback` does not exist in Fabric API 1.21.1: replaced by `MixinItemStackModifiers`.
- `MixinWitherBossAccessor`: overloaded `performRangedAttack` now selected by descriptor.
- Core no longer references Iris (`IrisComputeShaderSetup` moved to `src/compat/iris`, loaded by reflection) or Simply Tooltips.

## Build log fixes (round 2)
- `MixinWitherBossAccessor`: `@Invoker` kembali memakai nama saja (`performRangedAttack`); overload dibedakan oleh signature method accessor.
  Memakai descriptor di dalam `value` membuat annotation processor Mixin gagal ("Could not locate @Invoker target").
- `MixinEntity`: inject ke `Entity#onAddedToLevel` dihapus (method itu tambahan NeoForge, tidak ada di vanilla -> game akan crash saat start).
  Gantinya `VanillaEntityEventHooks.onJoinLevel` memanggil `entitypatch.onAddedToLevel()`.
- `MixinRespawnEvent`: `@Shadow minecraft` dihapus (field itu ada di superclass `ClientCommonPacketListenerImpl`); dipakai `Minecraft.getInstance()`.
- Forge Config API Port sekarang dibundel di dalam jar (`include` / jar-in-jar).
- Workflow: jar `-sources` tidak ikut artifact, error pertama tampil di tab Summary.

## Build log fixes (round 3)
- Semua `getEnchantmentLevel(Holder)` pada ItemStack (extension NeoForge) -> `FabricHolders.getEnchantmentLevel(stack, holder)` = `EnchantmentHelper.getItemEnchantmentLevel`.
- Typo `this.Minecraft.getInstance()` -> `Minecraft.getInstance()` (PopupBox, Grid, Static, SideNavigationBarOpener).
- `EmoteEditScreen.getEntryAtPosition`: harus `public` (access widener membuatnya public).
- Access widener ditambah, setara AT NeoForge: `IdMapper` (nextId/tToId/idToT) dan `GameRules$BooleanValue/IntegerValue#create`.
- `LightningBolt#damage` hanya ada di NeoForge: ditiru lewat `MixinLightningBolt` + `MixinEntityThunderHit` (vanilla selalu 5.0F).
- `CreativeModeTab.Builder#withTabsBefore` hanya NeoForge: dihapus (tab ditaruh di akhir daftar).
- Catatan: javac berhenti di tahap resolusi simbol; setelah ini bisa muncul error tahap berikutnya (round 4).

## Build log fixes (round 4)
- Tinggal 1 error kompilasi: `IdMapper.tToId` bertipe `Reference2IntMap` (bukan `Object2IntMap`) di 1.21.1, jadi baris access widener-nya diperbaiki (diverifikasi dari mapping intermediary Fabric 1.21.1). Catatan: access widener dengan deskriptor salah diabaikan diam-diam oleh Loom, tidak error.

## Build log fixes (round 5)
- Kompilasi Java sudah lolos. `validateAccessWidener` gagal di entri `FolderRepositorySource#createDiscoveredFilePackInfo` (tidak ada di 1.21.1). Entri itu dan `LightningBolt#damage` dihapus, task validasi dimatikan di build.gradle.

## Known gaps / TODO
- **Compat modules.** Loader: `compat/CompatLoader` (reflection, only loads a module when its mod is installed).
  - Always in the core build: Better Third Person, Iris (compute-shader init; block renderer used if the Sodium module is built).
  - Optional (`./gradlew build -Pcompat=iris,playeranimator,firstperson`, sources in `src/compat/<module>/java`): Iris+Sodium fake block renderer, PlayerAnimator, First Person Model.
    The GitHub workflow builds each of them as a separate job; a failed one does not affect the core jar. Library versions are in `gradle.properties` (only the Sodium coordinate pattern was checked; the rest are guesses).
  - Still in `_pending/compat` (not ported): Geckolib, AzureLib, SkinLayers3D, Wildfire Female Gender, Simply Tooltips, Controlify, Shoulder Surfing, JEI, KubeJS, Curios (needs Trinkets/Accessories), PlayerRevive, MCreator, Vampirism, Werewolves.
- Fixed: `build.gradle` used to exclude `yesman/epicfight/compat/**`, which also removed `ICompatModule`/`MinecraftMod` that `RenderEngine` imports (compile error).
- Data generators (`_pending/datagen`) removed from the build.
- `Rarity` "unique" (green) falls back to UNCOMMON. Nothing in the code or datapacks references `UNIQUE`, so there is no visible effect today.
- Skill-book icons per category: done with plain item-model overrides (`skillbook.json` + property `epicfight:skill` returning 1..5). Categories added by addons use the default icon.
- Camera roll: applied in `MixinGameRendererEvents` (unverified). `AFTER_TRIPWIRE_BLOCKS` render stage is still approximated by `AFTER_TRANSLUCENT`.
- In-game config: Mod Menu entrypoint `EpicFightModMenu` opens `EpicFightSettingScreen`. The Forge-style config files (`ClientConfig`, `CommonConfig`) can also be edited with Mod Menu + the Configured mod. `modmenu_version=11.0.3` is unverified.
- Unverified guesses to check first if the build/launch complains: Loom version, Forge Config API Port coordinates/package (`fuzs.forgeconfigapiport.fabric.api.neoforge.v4`),
  mixin injection targets (names/descriptors for 1.21.1), access widener descriptors.
- `_neoforge_reference/` keeps the original NeoForge platform code for comparison; it is not compiled.

## Licensing
Code is GPL (see `LICENSE`). Assets (textures, models, animations) are "all rights reserved" (`LICENSE-ASSETS`): fine for personal use, do not redistribute without the Epic Fight team's permission.
