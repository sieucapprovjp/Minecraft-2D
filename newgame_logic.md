# New Game — World Options & Settings Logic
> File này mô tả logic cho 2 màn hình: ModeSelectScreen (World Options) và SettingsScreen.
> Menu chính đã hoàn thiện — không đề cập ở đây.

---

## 1. ModeSelectScreen (World Options)

### Màn hình hiện tại (đã có)
- Nền biome panorama ngẫu nhiên (`empty2.png` → `empty28.png`)
- Logo nhỏ (`splash-worldoptions.png`)
- Panel chọn (`world-options2.png`) + nhãn (`world-options-text.png`)
- Nút **Done** → `ScreenId.GAME`
- Nút **Back** → `ScreenId.MENU`
- Hiệu ứng hover: scale `1.0 → 1.05`, smooth `* 0.2f`

### Phần còn thiếu — Logic lựa chọn option

Panel có **4 hàng**, mỗi hàng có **3–4 lựa chọn** dạng toggle.

#### Tọa độ gốc (hệ Scratch, tâm = 0,0 — màn hình 480×360)

```
Hàng 1 (Game Mode):   scratchY = +15
Hàng 2 (Bonus Chest): scratchY = -19
Hàng 3 (Skin):        scratchY = -53
Hàng 4 (Loot):        scratchY = -87

Cột 1: scratchX = -58
Cột 2: scratchX = +14
Cột 3: scratchX = +86
Cột 4: scratchX = +158
```

#### Quy đổi sang LibGDX screen pixel
```java
float lx = (scratchX + 240f) / 480f * screenWidth;
float ly = (scratchY + 180f) / 360f * screenHeight;
```

#### Công thức detect click ô (từ Scratch gốc)
```java
// mouseX, mouseY là tọa độ Scratch (đã quy đổi ngược từ screen)
int optID    = (int) Math.round((15 - mouseY) / 34.0) + 1;  // hàng 1–4
int choiceID = (int) Math.round((mouseX - (-58)) / 72.0);    // cột 0–3

if (optID < 1 || optID > 4) return; // bỏ qua click ngoài vùng
```

#### Nội dung từng ô

| optID | Tên | choiceID 0 | choiceID 1 | choiceID 2 | choiceID 3 |
|---|---|---|---|---|---|
| 1 | Game Mode | Survival | Creative | Adventure | Hardcore |
| 2 | Bonus Chest | Off | On | Large | — |
| 3 | Skin | Steve (0) | Skin 1 | Skin 2 | ... |
| 4 | Loot | Off | On | — | — |

#### Lưu vào GameState
```java
// Mảng lưu lựa chọn hiện tại, index 0–3
int[] menuChoices = {0, 0, 0, 0}; // mặc định tất cả = 0

void onOptionClicked(int optID, int choiceID) {
    menuChoices[optID - 1] = choiceID;
}
```

### Logic Do_Done() — khi bấm Done

```java
void applyMenuChoices() {
    int gameMode = menuChoices[0];

    switch (gameMode) {
        case 0: // Survival
            gameState.creative = false;
            gameState.survival = true;
            gameState.hardcore = false;
            break;
        case 1: // Creative
            gameState.creative = true;
            gameState.survival = false;
            gameState.hardcore = false;
            break;
        case 2: // Adventure
            gameState.creative = false;
            gameState.survival = true;
            gameState.hardcore = false;
            break;
        case 3: // Hardcore
            gameState.creative = false;
            gameState.survival = true;
            gameState.hardcore = true;
            break;
    }

    gameState.bonusChest = menuChoices[1]; // 0=Off, 1=On, 2=Large
    gameState.skin       = menuChoices[2]; // index skin
    gameState.loot       = menuChoices[3]; // 0=Off, 1=On
}
```

### Nút Settings (thay Mods)

Nút **Settings** đặt cạnh Done (vị trí cũ của Mods):
```
scratchY ≈ -135
scratchX: 0 đến +160  (bên phải Done)
```
Click Settings → `screenRouter.request(ScreenId.SETTINGS)`

---

## 2. SettingsScreen

### Màn hình Settings gồm

| Setting | Loại | Giá trị | Mặc định |
|---|---|---|---|
| Sound | Toggle (On/Off) | boolean | true |
| Music | Toggle (On/Off) | boolean | true |
| Brightness | Slider (0–100) | int | 50 |

Ngôn ngữ: **tiếng Anh cố định**, không có tùy chọn.

### Layout đề xuất

```
┌─────────────────────────────┐
│       [Logo / Title]        │
│                             │
│  Sound      [  ON  ] [OFF]  │
│                             │
│  Music      [  ON  ] [OFF]  │
│                             │
│  Brightness [ ◄ ─────── ► ]│
│             0%         100% │
│                             │
│         [  DONE  ]          │
└─────────────────────────────┘
```

### Lưu vào GameState

```java
public class GameSettings {
    public boolean soundEnabled   = true;
    public boolean musicEnabled   = true;
    public int     brightness     = 50;   // 0–100
}
```

### Logic Brightness

Brightness áp dụng bằng cách overlay màu đen/trắng lên toàn màn hình:

```java
// Trong GameScreen.draw() — vẽ sau cùng
float alpha;
if (brightness < 50) {
    // Tối dần: brightness 0 → alpha 0.8 (đen), brightness 50 → alpha 0
    alpha = (50 - brightness) / 50f * 0.8f;
    batch.setColor(0, 0, 0, alpha);
} else {
    // Sáng dần: brightness 50 → alpha 0, brightness 100 → alpha 0.4 (trắng)
    alpha = (brightness - 50) / 50f * 0.4f;
    batch.setColor(1, 1, 1, alpha);
}
batch.draw(overlayTexture, 0, 0, screenWidth, screenHeight);
batch.setColor(Color.WHITE);
```

### Logic Sound / Music

```java
// Áp dụng ngay khi toggle
void onSoundToggled(boolean enabled) {
    gameSettings.soundEnabled = enabled;
    // Tắt/bật tất cả SoundEffect đang play
    if (!enabled) SoundManager.stopAllSounds();
}

void onMusicToggled(boolean enabled) {
    gameSettings.musicEnabled = enabled;
    if (enabled) MusicManager.resume();
    else         MusicManager.pause();
}
```

### Slider Brightness

```java
// Kéo slider ngang
// sliderX: tọa độ X bắt đầu slider, sliderWidth: chiều rộng
void onSliderDragged(float mouseX) {
    float percent = (mouseX - sliderX) / sliderWidth;
    brightness = (int) MathUtils.clamp(percent * 100f, 0f, 100f);
}
```

### Điều hướng

- Nút **Done** → `screenRouter.request(ScreenId.MODE_SELECT)` (quay về World Options)
- Hoặc nếu mở từ Pause → `screenRouter.request(ScreenId.PAUSE)`

---

## 3. GameState — Biến cần lưu sau cả 2 màn hình

```java
public class GameState {
    // --- Từ ModeSelectScreen ---
    public boolean creative   = false;
    public boolean survival   = true;
    public boolean hardcore   = false;
    public int     bonusChest = 0;   // 0=Off, 1=On, 2=Large
    public int     skin       = 0;   // index skin
    public int     loot       = 0;   // 0=Off, 1=On

    // --- Từ SettingsScreen ---
    public boolean soundEnabled = true;
    public boolean musicEnabled = true;
    public int     brightness   = 50; // 0–100
}
```

---

## 4. Luồng điều hướng

```
MenuScreen
    └── [New Game] → ModeSelectScreen
                        ├── [Settings] → SettingsScreen
                        │                   └── [Done] → ModeSelectScreen
                        ├── [Back]     → MenuScreen
                        └── [Done]     → GameScreen
```
