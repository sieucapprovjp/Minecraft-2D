# Thiết Kế Hệ Thống Hướng Dẫn (Game Help & Tutorial System)

## 1. Mục tiêu
Xây dựng hệ thống hướng dẫn người chơi mới làm quen với các cơ chế cơ bản của game, từ việc thu thập tài nguyên thô đến mục tiêu cuối cùng là chiến thắng game thông qua việc bảo vệ làng.

## 2. Phương pháp tiếp cận (Hybrid Approach)
Kết hợp hai hình thức hướng dẫn để tối ưu hóa trải nghiệm:
- **Help Screen (Tĩnh):** Màn hình tra cứu chi tiết, cho phép người chơi xem lại công thức chế tạo, phím điều khiển và mục tiêu game bất cứ lúc nào.
- **Interactive Tutorial (Động):** Hệ thống thông báo (popups/toast) xuất hiện theo thời gian thực dựa trên hành động của người chơi.

---

## 3. Luồng Hướng Dẫn Tương Tác (Tutorial Flow)

Hệ thống chia thành các cột mốc (Milestones), bước sau chỉ xuất hiện khi bước trước được hoàn thành.

### Giai đoạn 1: Sinh tồn cơ bản
| Bước | Mục tiêu | Nội dung hướng dẫn | Điều kiện hoàn thành |
| :--- | :--- | :--- | :--- |
| **1** | Chặt gỗ | "Hãy tìm một cái cây và nhấn **Chuột Trái** để chặt gỗ." | Phá hủy ít nhất 1 block gỗ |
| **2** | Tạo Que (Stick) | "Nhấn phím **E** mở kho đồ -> Tạo Ván gỗ (Planks) -> Tạo Que (Stick)." | Có ít nhất 4 Que trong kho đồ |
| **3** | Bàn chế tạo | "Dùng 4 Ván gỗ tạo Bàn chế tạo (Crafting Table) và nhấn **Chuột Phải** để đặt xuống." | Đặt thành công Bàn chế tạo vào world |

### Giai đoạn 2: Phát triển công cụ
| Bước | Mục tiêu | Nội dung hướng dẫn | Điều kiện hoàn thành |
| :--- | :--- | :--- | :--- |
| **4** | Chế tạo Tool | "Nhấn **E** khi nhìn vào Bàn chế tạo -> Chế tạo Cúp gỗ (Wooden Pickaxe)." | Chế tạo thành công Cúp gỗ |
| **5** | Khai thác Mine | "Dùng Cúp gỗ đào đá và tìm kiếm các quặng quý (Than, Sắt, Kim cương) dưới lòng đất." | Đào được ít nhất 1 block quặng/đá |

### Giai đoạn 3: Thắng Game
| Bước | Mục tiêu | Nội dung hướng dẫn | Điều kiện hoàn thành |
| :--- | :--- | :--- | :--- |
| **6** | Bảo vệ làng | "Tìm ngôi làng và tiêu diệt toàn bộ Raiders tấn công để giành chiến thắng cuối cùng!" | Tiêu diệt toàn bộ mob trong một đợt Raid |

---

## 4. Thiết kế Giao diện & Trải nghiệm (UI/UX)

### 4.1. Thông báo Tutorial (Popups)
- **Vị trí:** Góc trên bên phải hoặc chính giữa phía dưới màn hình.
- **Đặc điểm:** Hiệu ứng fade-in/out, nền tối chữ sáng, có nút "Đã hiểu".

### 4.2. Màn hình Help Screen
- **Truy cập:** Nút "Help" tại `MenuScreen` hoặc phím tắt `F1` trong game.
- **Cấu trúc Tab:**
    - **Điều khiển:** Danh sách phím tắt.
    - **Chế tạo:** Minh họa công thức (Gỗ -> Que -> Bàn chế tạo).
    - **Vật liệu:** Bảng tra cứu quặng và cấp độ công cụ.
    - **Mục tiêu:** Giải thích về Raid Village và điều kiện thắng.

---

## 5. Logic Vận Hành (Conceptual Logic)

### 5.1. Quản lý trạng thái
Sử dụng một `TutorialStateTracker` để lưu trữ tiến độ hiện tại của người chơi (ví dụ: `step = 1`).

### 5.2. Cơ chế kích hoạt (Triggers)
Kết nối với các sự kiện hiện có trong game:
- **BlockBreaker:** Kích hoạt khi phá gỗ/quặng.
- **CraftingController:** Kích hoạt khi chế tạo Stick/Table/Pickaxe.
- **MobDeathListener:** Kích hoạt khi tiêu diệt Raider cuối cùng.

---

## 6. Lộ trình triển khai dự kiến (Roadmap)
1. **UI Design:** Thiết kế khung Popup và bố cục Help Screen.
2. **State Machine:** Xây dựng logic quản lý các bước hướng dẫn.
3. **Event Hooking:** Gắn logic kiểm tra điều kiện hoàn thành vào các module gameplay.
4. **Content Writing:** Hoàn thiện nội dung văn bản cho tất cả các bước.
