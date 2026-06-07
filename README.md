# Minecraft 2D

Minecraft 2D là bài tập lớn môn Lập trình hướng đối tượng, được xây dựng bằng Java và libGDX. Dự án mô phỏng một game sandbox 2D lấy cảm hứng từ Minecraft, có thế giới dạng block, người chơi, mob, inventory, crafting, utility block, combat, raid, trading và âm thanh.

## Công nghệ sử dụng

- Java
- libGDX
- LWJGL3
- Gradle
- JUnit

## Tính năng chính

- Màn hình game: loading, menu, chọn chế độ, game, pause, game over, settings và help.
- Thế giới 2D hữu hạn kích thước 500 x 128 tile, có chunk, camera follow và camera clamp.
- Sinh địa hình theo seed với biome forest, plains, desert, snow và cherry.
- Cave, ore, deepslate, bedrock, cây, thực vật và village.
- Player movement, gravity, jump, collision, death và respawn.
- Đào block, đặt block, dropped item và pickup item.
- Inventory, hotbar, item stack, tool, armor, food và music disc.
- Crafting table, furnace, chest, door và jukebox.
- Mob passive/hostile, villager, combat, projectile, evoker spell và raid.
- Trading với villager theo profession.
- Chu kỳ ngày đêm và spawn mob theo biome/thời điểm.
- Audio cho menu, gameplay, raid, UI, block, combat và utility block.
- Unit test cho nhiều module logic quan trọng.

## Cấu trúc dự án

```text
.
├── assets/                  # Hình ảnh, âm thanh và tài nguyên runtime
├── core/                    # Logic chính của game
│   └── src/main/java/com/main/game/
│       ├── audio/           # Quản lý âm thanh
│       ├── blocks/          # Block và metadata block
│       ├── combat/          # Chiến đấu
│       ├── crafting/        # Crafting và recipe
│       ├── entities/        # Entity, player, mob
│       ├── interaction/     # Đào/đặt block
│       ├── inventory/       # Inventory, item, tool, armor, food
│       ├── navigation/      # Điều hướng màn hình
│       ├── physics/         # Vật lý và va chạm
│       ├── raid/            # Raid
│       ├── screens/         # Các màn hình game
│       ├── trading/         # Trading với villager
│       ├── ui/              # HUD, overlay, camera
│       ├── utilityblock/    # Furnace, chest, door, jukebox
│       ├── world/           # Lưu trữ world/chunk
│       └── worldgen/        # Sinh thế giới, biome, cave, village
├── doc/                     # Tài liệu và báo cáo
├── lwjgl3/                  # Desktop launcher
├── build.gradle
└── settings.gradle
```

## Cách chạy dự án

Yêu cầu:

- JDK phù hợp với cấu hình Gradle của dự án.
- Windows có thể chạy trực tiếp bằng `gradlew.bat`.

Chạy game desktop:

```powershell
.\gradlew.bat --no-daemon lwjgl3:run
```

Build source:

```powershell
.\gradlew.bat --no-daemon classes
```

Chạy toàn bộ test:

```powershell
.\gradlew.bat --no-daemon test
```

Chạy test module `core`:

```powershell
.\gradlew.bat --no-daemon core:test
```

Build file `.jar` desktop:

```powershell
.\gradlew.bat --no-daemon lwjgl3:jar
```

File `.jar` sau khi build nằm trong:

```text
lwjgl3/build/libs/
```

## Điều khiển cơ bản

| Phím/chuột | Chức năng |
| --- | --- |
| `A` / `D` | Di chuyển trái/phải |
| `Space` | Nhảy |
| Chuột trái | Đào block hoặc tấn công |
| Chuột phải | Đặt block, dùng item hoặc tương tác |
| `E` | Mở inventory hoặc tương tác với utility block/villager |
| Hotbar | Chọn item đang cầm |

Một số phím có thể thay đổi tùy theo phần cài đặt trong code hiện tại.

## Một số kịch bản kiểm thử thủ công

1. Chạy game bằng `.\gradlew.bat --no-daemon lwjgl3:run`.
2. Vào game mới và kiểm tra player spawn an toàn trên mặt đất.
3. Di chuyển, nhảy và kiểm tra va chạm với địa hình.
4. Đào block bằng chuột trái, kiểm tra crack overlay và item rơi ra.
5. Đặt block bằng chuột phải, kiểm tra block không đặt được ngoài tầm hoặc vào vị trí bị chiếm.
6. Mở inventory bằng `E`, kiểm tra kéo thả, gộp stack và tách stack.
7. Tương tác với crafting table, furnace, chest, door và jukebox.
8. Tấn công mob, kiểm tra sát thương, knockback và item drop.
9. Đặt raid banner trong khu vực village để kiểm tra raid.
10. Kiểm tra âm thanh khi mở UI, phá/đặt block, combat và dùng utility block.

## Tài liệu

- Báo cáo bài tập lớn: `doc/BAO_CAO_BTL_OOP.md`
- Tổng quan tiến độ và tính năng đã hoàn thành: `doc/codex.md`
- Tài liệu hệ thống cave/world generation: `doc/CAVE_IMPLEMENT_PLAN.md`, `doc/World_Generation_&_Cave_System.md`
- Tài liệu crafting và các ghi chú thiết kế khác nằm trong thư mục `doc/`

## Hạn chế hiện tại

- Chưa có save/load world đầy đủ.
- Chưa có multiplayer và hệ thống XP.
- Một số trạng thái gameplay mới tồn tại trong runtime của `GameScreen`.
- Một số asset, animation và audio vẫn còn đơn giản so với Minecraft gốc.

## Hướng phát triển

- Thêm save/load world và player state.
- Mở rộng gameplay với XP, enchantment, farming, potion/debuff, boss hoặc quest.
- Cải thiện UI/UX, animation, audio và hiệu năng.
- Đóng gói bản release để dễ chạy trên máy người dùng.
