# BÁO CÁO TIẾN ĐỘ DỰ ÁN: PAPER MINECRAFT
**Môn:** Lập trình hướng đối tượng | **Nhóm:** Huy (Lead), Kiên, Được, Việt Hưng, Lâm Hùng  
**Công nghệ:** Java · libGDX · Gradle | **Cập nhật:** 07/05/2026

---

## 1. Tổng quan

Game 2D lấy cảm hứng từ Minecraft, xây dựng bằng Java/libGDX. Áp dụng 4 nguyên lý OOP: Abstraction, Inheritance, Polymorphism, Encapsulation. Gồm 18 class Java, ~1500 dòng code.

---

## 2. Nguyên lý OOP áp dụng

**Abstraction** — 3 abstract class (`Entity`, `AbstractBlock`, `BaseScreen`) định nghĩa hành vi chung, ẩn chi tiết triển khai.

**Inheritance** — `Player`/`Mob` kế thừa `Entity`; `SimpleBlock` kế thừa `AbstractBlock`; `GameScreen`/`StateScreen` kế thừa `BaseScreen`.

**Polymorphism** — Cùng gọi `entity.update()` nhưng Player xử lý input, Mob xử lý AI. Cùng `block.getTexture()` nhưng mỗi loại trả texture khác nhau.

**Encapsulation** — Fields dùng `protected`/`private`, truy cập qua getter/setter. `Constants`, `BlockPalette` dùng private constructor chỉ cho phép truy cập static.

---

## 3. Kiến trúc hệ thống

```
com.main.game
├── MainGame              ← Entry point, SpriteBatch + AssetManager + ScreenRouter
├── navigation/           ← ScreenRouter điều hướng, ScreenId enum
├── screens/
│   ├── BaseScreen        ← Abstract: update()/draw()/onEnter()/onExit()
│   ├── GameScreen        ← Tích hợp World + Entity + Physics + Camera
│   └── StateScreen       ← Menu / Pause / Game Over
├── entities/
│   ├── Entity            ← Abstract: position, velocity, bounds, state
│   ├── EntityState       ← Enum: IDLE/RUN/JUMP/FALL/HURT/DEAD
│   ├── Player            ← Input, animation, health, state machine
│   ├── Mob               ← AI: PATROL/CHASE/ATTACK, 2 type (Zombie/Skeleton)
│   └── EntityManager     ← Quản lý vòng đời entity, update/render tập trung
├── blocks/
│   ├── AbstractBlock     ← Abstract: solid, breakable, hardness, blockId
│   └── SimpleBlock       ← Block cụ thể với texture
├── world/
│   ├── World             ← Sinh terrain procedural 400×128, frustum culling
│   └── BlockPalette      ← Load & quản lý 8 loại texture block
├── physics/
│   └── PhysicsEngine     ← Gravity, AABB collision, ground detection
└── utils/
    └── Constants         ← Cấu hình tập trung (screen, physics, player)
```

---

## 4. Chức năng đã hoàn thành

| Module | Chức năng chính |
|--------|----------------|
| **Core** | `MainGame` quản lý tài nguyên chung; `ScreenRouter` điều hướng 4 screen (Game, Menu, Pause, Game Over) |
| **World** | Sinh terrain procedural 400×128 tiles, 5 lớp địa chất, cây tự động; frustum culling; camera follow player |
| **Player** | Di chuyển + nhảy; state machine 6 trạng thái (IDLE/RUN/JUMP/FALL/HURT/DEAD); hệ thống máu 20 HP |
| **Mob** | 2 loại (Zombie, Skeleton); AI: PATROL → CHASE → ATTACK; phát hiện/mất mục tiêu theo bán kính |
| **Physics** | Trọng lực, terminal velocity; ground detection qua `World.isSolid()`; `EntityManager` quản lý vòng đời entity |
| **Block** | 8 loại block (grass/stone/bedrock/sand/wood/leaves/planks/dirt); thuộc tính solid/breakable/hardness |

---

## 5. Phân công

| Thành viên | Module | Công việc |
|------------|--------|-----------|
| Huy | Core | MainGame, BaseScreen, ScreenRouter, Constants, BlockPalette |
| Kiên | World | World.generate(), frustum culling, camera follow |
| Được | Entity | Player, Mob, EntityManager, EntityState, animation, AI |
| Việt Hưng | Block | AbstractBlock, SimpleBlock, texture management |
| Lâm Hùng | Physics | PhysicsEngine, gravity, collision detection |

---

## 6. Hạn chế & Hướng phát triển

- Collision mới chỉ check ground (Y), chưa đầy đủ 4 hướng
- Chưa có đào/đặt block, inventory
- World load toàn bộ 1 lần, chưa chia chunk
- Terrain dùng sin wave, chưa nâng lên Perlin noise
- Chưa có save/load game

---

## 7. Kết luận

Dự án hoàn thành vertical slice: thế giới procedural, nhân vật điều khiển được với physics, mob AI đa trạng thái, hệ thống screen điều hướng. Các nguyên lý OOP được áp dụng xuyên suốt với 3 cây kế thừa, đa hình qua override, và đóng gói dữ liệu chặt chẽ.
