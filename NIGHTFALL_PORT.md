# Port senjata Crimson Moon (Epic Fight Nightfall 3.4.2) -> Epic Fight Fabric 1.21.1

Tahap 1 (dikerjakan) — senjata bisa dibuat, dipegang, dan dipakai:
- Item `efn:crimson_moon` (tidak bisa rusak, rarity epic, resep smithing: netherite greatsword + poppy + nether star)
- Tipe senjata `efn:scythe` (kombo 1-5, dash, air slash, idle/jalan/lari, block) + data `data/efn/capabilities/weapons/crimson_moon.json`
- Animasi + collider asli Nightfall, suara whoosh_heavy_1..4, trail senjata, tekstur item, lang EN/ID
- Skill bawaan: Crimson Harvest (3 tebasan memutar)
- Kode: package `yesman.epicfight.nightfall` (didaftarkan lewat 3 baris di `EpicFightFabric.java` dan 1 baris di `EpicFightCreativeTabs.java`)

Tahap 2 (belum): Blood Power + efek Curse of Blood / Blood Blessings / Damage Reduction / Attack Speed & Damage Increase,
posisi sarung (sheathed/unsheathed), Scarlet End, GUI energi, VFX Crimson Slash, model 3D di tangan (mesh item), enchant Scythe Enhance.

Belum dikompilasi di sini (tidak ada toolchain Minecraft). Kirim build.log bila ada error.
