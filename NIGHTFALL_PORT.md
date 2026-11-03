# Port senjata Crimson Moon (Epic Fight Nightfall 3.4.2) -> Epic Fight Fabric 1.21.1

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

## Belum / disederhanakan
- VFX Crimson Slash, Effekseer, camera shake, afterimage, patah tanah Scarlet End (diganti suara + partikel debu merah)
- Enchant Scythe Enhance
- Efek SIN_STUN_IMMUNITY diganti Stun Immunity bawaan; aturan "kurangi stun saat Damage Reduction" ditebak dari kode asli yang terpotong

Belum dikompilasi di sini (tidak ada toolchain Minecraft); referensi ke API Epic Fight sudah dicocokkan dengan source. Kirim build.log bila ada error.
