# Port senjata Crimson Moon (Epic Fight Nightfall 3.4.2) -> Epic Fight Fabric 1.21.1

## Shadow gesit + Shadow Step (v9)
- Shadow punya preset sendiri (`efn:scythe_shadow`): combo lebih cepat (x1.3-1.6) dengan lunge panjang, putaran 540/360 derajat, lompat tinggi, afterimage tiap serangan.
- Shadow Step: dash menghindar ala Genshin (kebal selama animasi ~0.44 dtk, jarak ~1.7x step bawaan, 4 arah). Otomatis terpasang di slot Dodge saat memegang Shadow dan dikembalikan saat dilepas. Setelah dash, serangan berikutnya +30% lebih cepat 2 detik. Tombol: tombol Dodge Epic Fight.

## Fix lag + partikel (v8)
- Partikel tebasan/darah merah dikembalikan; Shadow punya versi ungu (shadow_slash/shadow_harvest/shadow_drop). Yang dibuang hanya partikel bulat vanilla: swirl efek (efek kini tanpa partikel) dan CRIT bawaan Air Slash untuk senjata efn.
- Lag: backface culling di CPU pada mesh (setengah vertex), glow hanya <=20 blok, jumlah tetes darah dikurangi (per hit 10->4, event periodik 6->3 tiap 3 tick, hit particle 5->3).

## Combo RPG baru + partikel asli EFN
- Combo utama baru (scythe_rpg_*): lunge+condong, miring, whirlwind 360, leap (lompat+tebas turun), slam (putar+hantam). Diturunkan dari animasi asli dengan lapisan prosedural (skrip: lunge/spin/lompat pada Root), bukan keyframe tangan baru.
- Semua partikel darah/vanilla kustom dihapus (SoulVfx, CrimsonSlashVfx, blood_drop, crimson_slash, crimson_hit). Hit memakai `hit_blade` bawaan Epic Fight; `blood_harvest` asli EFN (HitParticle ukuran 3, umur 10) hanya di fase 3 Scarlet End.

## Item kedua "Shadow" + animasi Soul
- `efn:shadow`: varian ungu Crimson Moon (mesh sama, tekstur+glow+trail+GUI energi ungu), kemampuan sama. Resep smithing: Crimson Moon + Echo Shard + Nether Star. Kunci per-item di item_skins ("key").
- `SoulVfx`: jiwa target terlepas (spiral + ledakan), mengalir melengkung ke scythe saat kill / Scarlet End, aura jiwa mengorbit saat Blood Power >= 40%. Merah untuk Crimson Moon, ungu untuk Shadow.

## Fix v8
- **Kamera pertama (FPP)**: tombak dulu menghadap kamera/ke samping (poros mesh Tool_R +Z jadi menunjuk ke bawah-ke arah pemain). Tangkai dipegang di bagian tengah, tegak, ujung mata sabit menunjuk ke depan. Sudut dihitung dari simulasi bertekstur yang mereproduksi screenshot bug: `fp_rotation [-2.4, -30.6, -111.1]`, `fp_offset [0.43, 0.467, -0.403]`, `fp_scale 0.55` di `item_skins/crimson_moon.json` (bisa disetel lagi tanpa build, F3+T).
- **Partikel**: semua partikel vanilla pada senjata diganti efek sendiri: `efn:blood_drop` (tetes darah, tekstur sendiri), `efn:crimson_hit` (hit senjata: tebasan merah asli + percikan darah, menggantikan hit_blade kilat kuning/oranye), DAMAGE_INDICATOR dan Dust vanilla dihapus.
- **Armor trim**: UV mesh armor menyentuh tepi tekstur sehingga di atlas `armor_trims` bocor ke sprite tetangga (titik warna acak). Sekarang sprite trim disalin ke tekstur mandiri (`TrimTextureCache`); bila gagal otomatis kembali ke atlas.
- **Optimasi** (tampilan identik): `BakedScythe.draw` tanpa alokasi per vertex (dulu ~55 ribu objek Vector3f/frame), segitiga kembar dibuang, RenderType di-cache, lapisan glow tidak digambar >40 blok, senjata tidak digambar >100 blok.

## Fix v7
- Tekstur mesh: culling aktif (entityCutout) + filter halus (.mcmeta blur) supaya tidak berbintik. Render offline dengan UV asli sudah dicek: model + tekstur cocok (tombak merah-hitam, bilah di bawah).

## Fix v6
- Mesh di-bake sekali (BakedScythe) ke lokal sendi Tool_R, tanpa skinning per frame -> lebih ringan. Kamera pertama & render vanilla: item model `builtin/entity` + BuiltinItemRendererRegistry (3D). Ikon inventori tetap sprite 2D. Penyetelan: `fp_offset/fp_rotation/fp_scale` di item_skins/crimson_moon.json (F3+T).

## Fix v5 (crash holder efek, efek kuning, mesh)
- Crash "Unregistered holder ... mob_effect": semua MobEffectInstance sekarang memakai Holder asli registry (`NightfallMobEffects.real`).
- Efek kuning (BLADE_RUSH_SKILL/EVISCERATE) dihapus; Harvest/Scarlet End memakai partikel darah asli `efn:blood_harvest`.
- Mesh senjata digambar sebagai SkinnedMesh (terikat sendi Tool_R, di punggung memakai pose sendi punggung), bukan ClassicMesh statis.

## Perbaikan crash tahap 1
Game macet di logo Mojang karena kelas `NightfallSounds` baru dimuat saat animasi dibangun, sesudah registry terkunci
("Cannot register new entries to DeferredRegister after registerAll()"). Sekarang `NightfallMod.bootstrap()` memuat semua
kelas holder (sound, item, efek, data key, skill) lebih awal.

## Isi (tahap 1 + 2)
- Item `efn:crimson_moon` (tak bisa rusak), resep smithing, tipe senjata `efn:scythe`, kombo 1-5, dash, air slash, block
- Mode sarung (sheathed/unsheathed) dengan animasi jalan/lari/idle sendiri
- Skill ScytheSkill: Blood Power 0-1000 + GUI, Harvest, Scarlet End (butuh 100% + guard), parry mengisi Blood Power
- Efek: Curse of Blood, Blood Blessing, Damage Reduction, Attack Damage/Speed Increase (stun immunity memakai bawaan Epic Fight)
- Teks EN/ID, ikon efek, suara whoosh, trail senjata

## Model 3D di tangan (v3)
Mesh asli Nightfall (`assets/efn/animmodels/weapon/crimson_moon.json`) digambar di sendi tangan lewat renderer item skin `efn:mesh_item`
(`yesman.epicfight.nightfall.client`), lengkap dengan tekstur dan lapisan menyala (`crimson_moon_light.png`).
Posisi/rotasi/ukuran bisa disetel TANPA build ulang di `assets/efn/item_skins/crimson_moon.json`:
`mesh_offset` [x,y,z], `mesh_rotation` [x,y,z derajat], `mesh_scale`. Ikon inventori tetap sprite 2D.

## Efek visual (v4)
- Tebasan merah: partikel `efn:crimson_slash` (animasi 7 frame crimson_slash1..7) pada Scarlet End, Harvest, dan kombo 5
- Getar kamera ringan (pandangan pemain digeser lalu dikembalikan), afterimage bayangan putih, tanah pecah di akhir Scarlet End (memakai efek bawaan Epic Fight)
- Penyetelan: ukuran/kemiringan tebasan ada di parameter `SPAWN_SLASH` di `NightfallScytheAnimations.java`

## Belum / disederhanakan
- VFX Effekseer asli (kabut/mist); tebasan dibuat ulang sebagai partikel billboard (selalu menghadap kamera), bukan mesh 3D miring seperti aslinya
- (Selesai) Enchant Scythe Enhance: `data/efn/enchantment/scythe_enhance.json` (treasure, hanya Crimson Moon, tebasan merah 30% lebih besar). Dapatkan: `/give @s enchanted_book[stored_enchantments={"efn:scythe_enhance":1}]`
- Efek SIN_STUN_IMMUNITY diganti Stun Immunity bawaan; aturan "kurangi stun saat Damage Reduction" ditebak dari kode asli yang terpotong

Belum dikompilasi di sini (tidak ada toolchain Minecraft); referensi ke API Epic Fight sudah dicocokkan dengan source. Kirim build.log bila ada error.
