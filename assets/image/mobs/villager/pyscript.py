import os
from PIL import Image


def resize_entire_folder():
    print("=== CÔNG CỤ RESIZE TOÀN BỘ FOLDER (40x140) ===")

    # Cho phép bạn nhập đường dẫn folder nguồn khi chạy script
    input_folder = input("Nhập đường dẫn folder chứa ảnh gốc: ").strip().strip('"')
    output_folder = input("Nhập đường dẫn folder muốn lưu ảnh mới: ").strip().strip('"')

    # Kiểm tra xem folder nguồn có tồn tại thật không
    if not os.path.exists(input_folder):
        print(f"❌ Lỗi: Không tìm thấy folder nguồn tại '{input_folder}'!")
        return

    # Tự động tạo folder đích nếu chưa có
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    # Định dạng ảnh mà game hay dùng
    valid_extensions = ('.png', '.jpg', '.jpeg', '.bmp', '.webp')

    # Lấy danh sách toàn bộ file trong folder
    all_files = os.listdir(input_folder)
    image_files = [f for f in all_files if f.lower().endswith(valid_extensions)]

    if not image_files:
        print("ℹ️ Không tìm thấy file ảnh hợp lệ nào trong folder này!")
        return

    print(f"\n🚀 Tìm thấy {len(image_files)} ảnh. Bắt đầu xử lý hàng loạt...")

    count = 0
    for file_name in image_files:
        input_path = os.path.join(input_folder, file_name)
        output_path = os.path.join(output_folder, file_name)

        try:
            with Image.open(input_path) as img:
                # Giữ độ sắc nét tuyệt đối cho Pixel Art với NEAREST
                resized_img = img.resize((56, 152), Image.Resampling.NEAREST)
                resized_img.save(output_path)
                print(f"  ✓ Đã xử lý: {file_name}")
                count += 1
        except Exception as e:
            print(f"  ❌ Lỗi file {file_name}: {e}")

    print(f"\n🎉 Hoàn thành! Đã resize thành công {count}/{len(image_files)} file về cỡ 40x140.")


if __name__ == "__main__":
    resize_entire_folder()
