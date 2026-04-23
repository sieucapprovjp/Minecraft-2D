# Team TODO List (Foundation)

## TODO Tags (de filter nhanh trong IDE/Notion)
- HUY-LEAD
- KIEN-WORLD
- DUOC-ENTITY
- VHUNG-BLOCKS
- LHUNG-PHYSICS

## Huy (Leader) - Core & DevOps
- [ ] Chuan hoa cau truc package, naming convention, va coding style cho ca team.
- [ ] Setup template cho PR/Issue va quy trinh review (main chi merge qua PR).
- [ ] Kiem tra va cap nhat `README.md` de clone -> build -> run bang mot command.
- [ ] Quan ly lifecycle tai nguyen dung chung (SpriteBatch, sau nay AssetManager).

## Kien - World & Camera
- [ ] Nang cap `World.generate(seed)` tu terrain tam thoi len noise terrain (surface on dinh).
- [ ] Them cave generation co threshold de tao hang dong.
- [ ] Tach world theo chunk (`CHUNK_SIZE`) de toi uu update/render.
- [ ] Camera follow player + clamp world thay cho camera test WASD.

## Duoc - Entity & Input
- [ ] Tao `Player` class ke thua `Entity` voi state co ban: idle/run/jump/fall.
- [ ] Xu ly input trai/phai/nhay, ket noi voi `PhysicsEngine`.
- [ ] Tao mob co AI don gian (patrol/chase co ban).
- [ ] Quan ly update/render entity trong `GameScreen`.

## Viet Hung - Blocks & Assets
- [ ] Tao block classes cu the (Air, Dirt, Grass, Stone, Bedrock, ...).
- [ ] Setup TextureAtlas that su (thay cho `BlockPalette` tam thoi).
- [ ] Mapping blockId -> texture region + metadata (solid, breakable, hardness).
- [ ] Dam bao block API dung duoc cho world generation + physics collision.

## Lam Hung - Physics
- [ ] Hoan thien collision AABB voi block (tach truc X/Y de tranh ket).
- [ ] Ground detection chinh xac, on dinh jump/landing.
- [ ] Resolve collision theo huong va reset velocity phu hop.
- [ ] Viet test scenario thu cong (di ngang, roi tu cao, cham canh block).

## Leader (Huy)
- [ ] Chot Definition of Done cho tung module truoc khi code tiep.
- [ ] Theo doi blocker hang ngay, xu ly dependency bi tre > 24h.
- [ ] Duyet PR theo tieu chi: dung scope, build pass, khong pha module khac.
- [ ] Chot milestone "vertical slice": world + player + collision + 1 mob.
