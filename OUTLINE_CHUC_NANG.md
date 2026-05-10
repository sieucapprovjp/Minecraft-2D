# OUTLINE CHỨC NĂNG GAME — PAPER MINECRAFT

## 1. Hệ thống màn hình
- Menu chính (MENU) — vào game / thoát
- Màn chơi (GAME) — gameplay chính
- Tạm dừng (PAUSE) — nhấn P/ESC để pause/resume
- Kết thúc (GAME OVER) — khi player chết
- Điều hướng giữa các screen qua `ScreenRouter`

## 2. Thế giới (World)
- Sinh terrain ngẫu nhiên 400×128 tiles từ seed
- 5 lớp địa chất: bedrock → stone → dirt → grass/sand
- Cây tự động: thân gỗ 3-4 block + tán lá
- 8 loại block: grass, stone, bedrock, sand, wood, leaves, planks, dirt
- Mỗi block có thuộc tính: solid, breakable, hardness
- Frustum culling — chỉ render block trong tầm nhìn camera

## 3. Nhân vật (Player)
- Di chuyển trái/phải (A/D, ←/→)
- Nhảy (SPACE/W/↑) khi đứng trên mặt đất
- 6 trạng thái: IDLE, RUN, JUMP, FALL, HURT, DEAD
- Animation riêng cho mỗi trạng thái
- Flip sprite theo hướng di chuyển
- Hệ thống máu 20 HP, nhận damage, hurt blink

## 4. Mob (AI)
- 2 loại: Zombie (cận chiến), Skeleton (tầm xa)
- AI 3 trạng thái:
  - PATROL — đi tuần trong phạm vi
  - CHASE — phát hiện player → đuổi theo
  - ATTACK — trong tầm đánh → gây damage
- Aggro/de-aggro theo khoảng cách
- Animation walk, idle, hurt

## 5. Vật lý (Physics)
- Trọng lực kéo entity xuống
- Ground detection — chạm block solid → dừng rơi
- Entity Manager quản lý update/render tất cả entity

## 6. Camera
- Theo dõi player với lerp smoothing
- Giới hạn trong biên world
- Zoom có thể điều chỉnh
- Fallback WASD khi player chết

## 7. Quản lý tài nguyên
- SpriteBatch + AssetManager dùng chung
- BlockPalette load texture tập trung, fallback khi lỗi
- Dispose pattern giải phóng bộ nhớ đúng cách
