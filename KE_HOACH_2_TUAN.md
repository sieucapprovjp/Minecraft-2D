# 🗓️ KẾ HOẠCH LÀM VIỆC — 2 TUẦN CUỐI
> **Bắt đầu:** 08/05/2026 (Thứ Năm)  
> **Deadline:** ~21/05/2026 (Thứ Tư)  
> **Nguyên tắc:** Merge trước, feature sau, polish cuối.

---

## TUẦN 1: MERGE + FIX (08/05 → 14/05)

### Ngày 1-2 (T5-T6, 08-09/05) — MERGE TẤT CẢ BRANCH

| Ai | Việc | Ưu tiên |
|----|------|---------|
| **Huy** | Merge `kien` → `main` (World + Camera, ít conflict nhất) | 🔴 |
| **Huy** | Merge `Hung` → `main` (Blocks, resolve conflict BlockPalette) | 🔴 |
| **Huy** | Merge `Duoc` → `main` (Player + Cow, resolve assets conflict) | 🔴 |
| **Mọi người** | Pull `main` mới, chạy thử, báo bug | 🔴 |

> ⚠️ Sau 2 ngày này: **TẤT CẢ code phải nằm trên `main`**, không ai code branch riêng nữa.

### Ngày 3-4 (T7-CN, 10-11/05) — FIX BUG SAU MERGE

| Ai | Việc |
|----|------|
| **Lâm Hùng** | Kiểm tra lại PhysicsEngine trên main — collision đã đủ 4 hướng chưa, fix nếu thiếu |
| **Được** | Test Player jump/fall sau merge — đảm bảo physics hoạt động đúng |
| **Kiên** | Test World generation + camera trên main — fix nếu terrain bị lỗi |
| **V. Hưng** | Kiểm tra tất cả block texture load đúng, không bị fallback |
| **Huy** | Chạy tổng thể, liệt kê bug còn lại |

### Ngày 5-7 (T2-T4, 12-14/05) — THÊM CHỨC NĂNG CÒN THIẾU

| Ai | Việc | Chi tiết |
|----|------|----------|
| **Huy** | Tạo `MenuScreen` + `PauseScreen` | Menu: nút Play/Quit. Pause: nhấn ESC pause game, nhấn lại resume |
| **Được** | Thêm Health System cho Player | Thanh máu đơn giản, rơi từ cao mất máu, chết → GameOver screen |
| **Lâm Hùng** | Hoàn thiện physics edge cases | Test rơi từ cao, nhảy sát tường, đi trên cầu 1-block |
| **Kiên** | Cải thiện terrain (tuỳ chọn) | Nếu kịp: thêm biome đơn giản hoặc cải thiện noise |
| **V. Hưng** | Block breaking cơ bản (tuỳ chọn) | Click chuột phá block — nếu không kịp thì bỏ |

---

## TUẦN 2: POLISH + BÁO CÁO (15/05 → 21/05)

### Ngày 8-9 (T5-T6, 15-16/05) — POLISH GAME

| Ai | Việc |
|----|------|
| **Huy** | Thêm HUD đơn giản: hiển thị tọa độ, FPS, block type đang đứng |
| **Được** | Polish animation Player — đảm bảo mượt, không giật |
| **Kiên** | Thêm background gradient (trời) + tối ưu render nếu cần |
| **V. Hưng** | Kiểm tra tất cả texture đẹp, thống nhất style pixel art |
| **Lâm Hùng** | Viết phần Physics trong báo cáo (giải thích AABB, gravity) |

### Ngày 10-11 (T7-CN, 17-18/05) — VIẾT BÁO CÁO

| Ai | Phần viết |
|----|-----------|
| **Huy** | Tổng quan + kiến trúc OOP + class diagram + kết luận |
| **Kiên** | Phần World Generation (thuật toán, frustum culling) |
| **Được** | Phần Entity System (Player, Cow, animation, input) |
| **V. Hưng** | Phần Block System (block types, texture management) |
| **Lâm Hùng** | Phần Physics (gravity, AABB collision, separated axis) |

> Mỗi người viết 1-2 trang, Huy tổng hợp lại.

### Ngày 12-13 (T2-T3, 19-20/05) — THỬ DEMO + CHỈNH SỬA

| Việc | Chi tiết |
|------|----------|
| Chạy demo tổng thể | Quay video game chạy để backup phòng lỗi khi demo |
| Fix bug cuối cùng | Chỉ fix critical bug, KHÔNG thêm feature mới |
| Hoàn thiện báo cáo | Huy review + merge bài viết của mọi người |
| Chuẩn bị thuyết trình | Phân công ai nói phần nào (nếu có thuyết trình) |

### Ngày 14 (T4, 21/05) — NỘP

| Việc | Ai |
|------|----|
| Build jar cuối cùng (`gradlew lwjgl3:jar`) | Huy |
| Kiểm tra file báo cáo lần cuối | Huy |
| Push code lên GitHub + tag release | Huy |
| Nộp | Huy |

---

## CHECKLIST TỔNG HỢP

### Bắt buộc phải xong (MUST HAVE)
- [ ] Merge tất cả branch → main
- [ ] Game chạy không crash: world + player + cow + physics
- [ ] Menu Screen (Play/Quit)
- [ ] Pause Screen (ESC)
- [ ] Bài báo cáo hoàn chỉnh
- [ ] Code build được jar

### Nên có (SHOULD HAVE)
- [ ] Health system đơn giản
- [ ] HUD hiển thị thông tin
- [ ] Background trời
- [ ] GameOver screen khi chết

### Có thì tốt (NICE TO HAVE)
- [ ] Block breaking
- [ ] Biome hệ thống
- [ ] Thêm mob thứ 2
- [ ] Sound effects

---

## QUY TẮC 2 TUẦN CUỐI

1. **Code trên `main` trực tiếp** — không tạo branch mới, commit thường xuyên
2. **Không thêm feature lớn sau ngày 16/05** — chỉ fix bug và polish
3. **Mỗi ngày check-in 1 lần** — báo cáo tiến độ qua group chat
4. **Test trước khi commit** — chạy game ít nhất 1 lần trước khi push
5. **Backup** — quay video demo trước ngày nộp phòng lỗi kỹ thuật
