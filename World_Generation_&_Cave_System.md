# World Generation & Cave System — Paper Minecraft

## Tổng quan pipeline sinh thế giới

```
Spawn From Seed(new_chunk?)
    ├── init(false)                    ← sinh địa hình chính
    │     ├── Init Prep               ← chuẩn bị biome, kích thước
    │     ├── Init Seams              ← đặt lớp đá, ore, lớp nền
    │     ├── Init Ground Curves      ← tạo đường cong bề mặt (altitude[])
    │     ├── Init Caves              ← đào hang (Make Seams)
    │     ├── Populate Trees & shrubs ← cây, hoa, cỏ
    │     ├── Add Postgen             ← vines, deep ore, coral...
    │     ├── Add Water or Lava       ← đổ nước/dung nham
    │     └── Freeze Surface          ← đóng băng mặt nước (snow biome)
    │
    ├── Spawn village()
    ├── Add Dungeons()                 ← geode, bastion chest, mineshaft...
    ├── Spawn Structures()             ← nhà làng theo biome
    └── Spawn Underground Structures() ← Trial Chamber / Stronghold / Ancient City
```

---

## Cấu trúc dữ liệu thế giới

### `_LEVEL[]` — World array (1D)

```
index = x + y * _lsx

_lsx = chiều rộng chunk (số block theo chiều ngang)
_lsy = chiều cao chunk (số block theo chiều dọc)

Tọa độ:
  x = (index - 1) mod _lsx
  y = floor((index - 1) / _lsx)

Truy cập các ô lân cận:
  trên    = index - _lsx
  dưới    = index + _lsx
  trái    = index - 1
  phải    = index + 1
```

### `altitude[]` — Độ cao bề mặt

```
altitude[x] = y của block bề mặt tại cột x
Dùng để đặt cây, structure, spawn point...
```

### `_WaterLevel` — Mực nước biển

```
Các block dưới _WaterLevel bị fill nước nếu là air.
```

---

## 1. `Init Ground Curves` — Sinh địa hình bề mặt

Dùng **pseudorandom sine waves** để tạo đường cong địa hình:

```python
# Mỗi chunk có CHUNK_SEED riêng → địa hình khác nhau
_RandomSeed = _CHUNK_SEED

# altitude[] được tính từ hàm noise
# Biome ảnh hưởng đến:
#   - WaterLevel (mực nước)
#   - Độ cao trung bình
#   - Biên độ dao động

# Block bề mặt theo biome:
biome_surface = {
    0:  grass (2), dirt (3), stone (4)    # Plains
    4:  sand (7), sandstone               # Desert  
    5:  sand (7), gravel                  # Beach
    6:  snow (55), ice (11)               # Snowy plains
    8:  sand (7), packed ice              # Ice spikes
    9:  mycelium (57)                     # Mushroom island
    14: sand (7) dưới nước               # Ocean
    15: clay (419), sand                  # Swamp
    17: podzol (998), dirt               # Old growth taiga
    18: red sand (1426), terracotta       # Badlands
    19: pale_moss (1757), dirt            # Pale garden
}
```

---

## 2. `Init Seams` — Đặt các lớp khoáng sản

Sinh các vỉa khoáng theo độ sâu:

```python
# Format: (blockID, chance%, maxDepth, veinSize)
ore_table = [
    (13, coal,      chance=high, depth=_WaterLevel+4,  vein=large),  # Coal
    (15, iron,      chance=mid,  depth=_WaterLevel+3,  vein=medium),
    (16, gold,      chance=low,  depth=30,              vein=small),
    (17, diamond,   chance=rare, depth=16,              vein=tiny),
    (18, lapis,     chance=rare, depth=25,              vein=small),
    (784,copper,    chance=mid,  depth=48,              vein=large),
    # ... emerald, redstone, v.v.
]

# Deepslate ores (depth > 69):
deepslate_ore_table = [...]  # giống trên nhưng dùng deepslate variant
```

### `Make Seams(blockID, chance, veinSize, maxDepth, replace, slip, deep)`

```python
def Make_Seams(typ, chance, vein_size, max_depth, replace?, slip, deep?):
    total = (_lsx * max_depth) * (chance / 100)  # số block cần đặt
    
    while total > 0:
        # Chọn vị trí ngẫu nhiên
        x = random(0, _lsx)
        y = random(0, max_depth)
        
        # Sinh vein (blob hình tròn ngẫu nhiên)
        call Fill_Circle(typ, x, y, radius=vein_size, ...)
        total -= vein_size²
```

---

## 3. `Init Caves` — Sinh hang động

```python
def Init_Caves():
    if biome < 1000:  # Overworld
        # Tạo các hang ngẫu nhiên
        _RandIdx = 4000
        n_caves = random(6, 12)
        
        # Hang đá thường (air = block 1)
        if biome có cave flag:
            Make_Seams(
                typ=1,           # air
                chance=Rand[1],  # ~6-12% của chunk
                vein=4,          # bán kính
                maxDepth=_WaterLevel+4,
                replace=True,
                slip=0,
                deep=True
            )
        
        # Hang dripstone (block 53 = dripstone)
        if biome != Desert:
            Make_Seams(
                typ=53,
                chance=0.12,
                vein=3,
                maxDepth=_WaterLevel+1,
                replace=False,
                slip=7,         # chance đặt dripstone thay vì air
                deep=True
            )
    
    elif biome < 2000:  # Nether
        n_caves = random(12, 20)
        Make_Seams(
            typ=1,
            chance=Rand[1],
            vein=6,
            maxDepth=_lsy,      # toàn bộ chiều cao
            replace=True,
            ...
        )
```

**Kết quả:** Hang là các **vùng air hình tròn/blob** ngẫu nhiên, overlap với nhau tạo thành tunnel.

---

## 4. `Stalagmites & Stalactites` — Măng đá / nhũ đá

Chạy sau khi hang đã được tạo:

```python
for each block in _LEVEL:
    if block == 1177 (dripstone block):
        if block bên dưới == air:
            # Tạo stalactite (nhũ đá rủ xuống)
            track = current
            if random == 1:
                repeat random(1, 3):
                    if block dưới track là air:
                        track += _lsx
                        _LEVEL[track] = 1178  # pointed dripstone (tip)
            
            # Tạo stalagmite (măng đá từ dưới lên)
            if block bên dưới là dripstone/stone:
                if random == 1:
                    repeat random(1, 2):
                        if block trên == air:
                            _LEVEL[track] = 1179  # stalagmite tip
                            track -= _lsx
```

---

## 5. `Add Postgen` — Xử lý sau khi hang tạo xong

Duyệt toàn bộ `_LEVEL` nhiều lần, xử lý đặc biệt:

```python
# Vines trong hang (block 38 = air trong hang?)
if block == 38 AND block trên == stone:
    if random(1,8) == 3:
        tạo vine pattern (L-shape hoặc I-shape)
        _LEVEL[tile] = 414 hoặc 1350  # vine variants

# Deep ores (depth > 73)
if block in [stone, grass, dirt] AND y > 73:
    if random(1,30) == 1:
        _LEVEL[tile] = 271  # deepslate
    else:
        _LEVEL[tile] = 4    # stone

# Deepslate ore veins (depth > 69)
if block == stone AND y > 69:
    if random(1,51)==1: Fill_Circle(13, x, y, 0.8)  # coal
    if random(1,45)==1: Fill_Circle(16, x, y, 1.0)  # gold
    if random(1,42)==1: Fill_Circle(784, x, y, 1.6) # copper

# Geode (block 4/628 = amethyst marker, depth < 45)
if block in [4, 628] AND y < 45:
    if random(1, 6021) == 1:
        call Spawn_Geode()

# Dripstone circles (block 305 = dripstone marker)
if block == 305 AND random==1:
    Fill_Circle(53, x, y, radius=1, ...)

# Ocean floor (biome Ocean, depth < 13)
if block == 4 AND y < 13:
    _LEVEL[tile] = 628  # deep ocean stone

# Kelp / seagrass (biome Ocean/Beach)
if block == 2 AND y == _WaterLevel:
    if random(1,10) in [1,2] AND biome in [Plains, Forest, Jungle]:
        _LEVEL[tile+_lsx] = 1793  # tall seagrass
    if random(1,10) in [3,4] AND biome in [Forest, Taiga]:
        _LEVEL[tile+_lsx] = 1792  # seagrass

# Badlands terracotta layers
if biome == Badlands:
    Fill_Circle(1426, x, y, 4) on dirt
    Fill_Circle(999, x, y, 3)  on grass

# Pale garden moss
if biome == PaleGarden:
    Fill_Circle(1757, x, y, 2) on dirt
```

---

## 6. `Populate Trees and shrubs` — Cây và thực vật

```python
# Đếm vị trí có thể đặt cây (surface open)
tree_positions = [x for x in range(_lsx) if _LEVEL[x] == grass AND _LEVEL[x+_lsx] == air]

# Số cây theo biome
n_trees = random(4, 12)   # thường
n_trees = random(3, 6)    # Mesa/Ice

# Chọn loại thực vật theo biome (xác suất):
vegetation_table = {
    Plains:      {flower: 71%, grass: 24%, tall_grass: 5%},
    Forest:      {oak: 71%, birch: 24%, tall_tree: 5%},
    Desert:      {dead_bush: 57%, cactus: 43%},
    Taiga:       {spruce: 80%, fern: 20%},
    Jungle:      {jungle_tree: 57%, bamboo: 43%},
    Swamp:       {waterlily: 40%, oak: 35%, mushroom: 25%},
    Mushroom:    {brown_mushroom: 57%, red_mushroom: 43%},
    PaleGarden:  {pale_oak: 40%, hanging_moss: 30%, oak: 30%},
    Badlands:    {terracotta_plant: 40%, dead_bush: 60%},
    ...
}

# Đặt cây
for pos in random.sample(tree_positions, n_trees):
    _LEVEL[pos + _lsx] = chosen_vegetation
```

---

## 7. `Spawn Underground Structures` — Cấu trúc ngầm

```python
def Spawn_Underground_Structures():
    if biome > 999: return  # chỉ Overworld

    r = random(1, 3)
    if r == 1:
        Spawn_Trial_Chamber()
    elif r == 2:
        r2 = random(1, 3)
        if r2 == 2: Spawn_Stronghold()
        else:
            r3 = random(1, 3)
            if r3 == 1: Spawn_Ancient_City()
    
    if biome == Ocean:
        if random(1, 3) > 1: Spawn_Shipwreck()
```

---

## 8. `Add Dungeons` — Phòng mob, Mineshaft, Geode

```python
# Duyệt toàn bộ world, xử lý các marker blocks
for each tile in _LEVEL:
    
    # Geode marker
    if tile in [4, 628] AND y < 45:
        if random(1, 6021) == 1: Spawn_Geode()
    
    # Bastion chest marker (block 1176)
    if tile == 1176:
        replace với chest
        add loot table "bastion" (27 items)
    
    # Mineshaft (block 1175)
    if tile == 1175:
        Spawn_Mineshaft()

    # Dripstone cave fill
    if tile == 352: Fill_Circle(351, x, y, 2)
    if tile == 305: Fill_Circle(53,  x, y, 1)

    # Sculk veins (deep dark marker, block 785)
    if tile == 785:
        place sculk catalyst (742) ở ô liền kề

# Mineshaft corridors
Spawn_Mineshaft(biome)

# Stalagmites/stalactites
Stalagmites_and_Stalactites()
```

---

## 9. Biome IDs

| ID | Biome |
|----|-------|
| 0  | Meadow / Forest |
| 1  | Forest |
| 2  | Birch Forest |
| 3  | Jungle |
| 4  | Desert |
| 5  | Beach |
| 6  | Snowy Plains |
| 7  | Flower Meadow |
| 8  | Ice Spikes |
| 9  | Mushroom Island |
| 10 | Sunflower Plains |
| 11 | Spruce Taiga |
| 12 | Dark Forest |
| 13 | Mangrove Swamp |
| 14 | Ocean |
| 15 | Swamp |
| 16 | Cherry Blossom |
| 17 | Old Growth Taiga |
| 18 | Badlands |
| 19 | Pale Garden |
| 1000 | Nether |
| 2000 | End |

---

## 10. Tóm tắt nhanh

```
Mỗi chunk gen theo thứ tự:
1. Init Prep     → đọc biome, seed, kích thước
2. Init Seams    → đặt stone layers + ore veins
3. Init Ground   → tính altitude[], đặt surface blocks (grass/sand/snow)
4. Init Caves    → đào hang bằng Make Seams(air, ...)
5. Fill water    → đổ nước vào _WaterLevel
6. Trees/shrubs  → đặt thực vật bề mặt
7. Add Postgen   → vines, deepslate, coral, kelp...
8. Structures    → làng, nhà, portal hỏng, giếng sa mạc...
9. Dungeons      → mineshaft, geode, bastion, trial chamber...
10. Underground  → stronghold, ancient city, shipwreck...
```