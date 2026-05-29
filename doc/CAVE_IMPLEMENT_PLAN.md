# Cave System V1 Plan

## Muc tieu

V1 chi implement hang dong huu han, ore veins co ban va deepslate layer cho finite world. Khong implement dripstone, lush cave, dungeon, mineshaft, geode hoac underground structure trong phase nay.

World van gioi han theo `WORLD_WIDTH=400` va `WORLD_HEIGHT=128`. Chunk chi la storage/render optimization noi bo, khong duoc gia dinh infinite world.

## Pham vi V1

- `CaveCarver`: dao hang/tunnel bang seeded random, chi carve block `stone` hoac `deepslate`.
- `OreVeinPlacer`: dat ore blob co ban, chi replace `stone` hoac `deepslate`.
- `World.generate()`: sinh toan bo finite world mot lan truoc khi spawn player de cave/ore khong bi seam o bien chunk.
- `World.getSurfaceY(x)`: luu surface theo cot X de cave tranh gan mat dat.
- `WorldBlockFactory`: support cac ore id hien co: `coal_ore`, `iron_ore`, `gold_ore`, `diamond_ore`, `copper_ore`, `lapis_ore`, `redstone_ore`, `emerald_ore`.
- Deepslate layer: `y <= 3` la bedrock, `4 <= y <= 40` la deepslate, phan da cao hon la stone.

## Nguyen tac generation

Thu tu generation:

1. Terrain tao bedrock, deepslate, stone, dirt, grass, tree trong finite bounds.
2. Cave carver dao tunnel sau khi terrain cua toan world da co.
3. Ore placer dat ore sau cave, khong dat vao air; ore trong deepslate dung texture deepslate ore.

Carver khong duoc:

- Carve ngoai `0 <= x < world.width`, `0 <= y < world.height`.
- Carve gan mat dat, mac dinh cach surface khoang 6 block.
- Carve vao block khac `stone`/`deepslate`, gom bedrock, dirt, grass, wood, leaves va structure block.
- Carve vung spawn trung tam gan surface.

## Test

- `.\gradlew.bat classes`
- `.\gradlew.bat test`
- Manual game test:
  - Player spawn an toan tren surface.
  - Camera khong ra khoi bien world.
  - Y <= 3 la bedrock; Y <= 40 hien deepslate thay vi bedrock day.
  - Underground co tunnel ket noi, khong chi la noise hole nho.
  - Hang khong pha mat dat qua nhieu.
  - Bedrock/deep boundary con duoc bao ve.
  - Ore nam trong stone va khong lo lung trong air.
  - Dao block/ore trong hang khong crash.

## Ngoai pham vi

- Cave decoration: dripstone, lush cave, vines, moss.
- Underground structure: dungeon, mineshaft, geode.
- Infinite world, chunk unload/save, biome-specific cave config.
