# codex.md

## Muc tieu du an
- Du an game 2D phong cach Minecraft, xay dung bang Java + LibGDX.
- Vertical slice uu tien: `world + player + collision + 1 mob`.

## Stack va cau truc
- Engine: LibGDX.
- Build: Gradle (`gradlew.bat`).
- Modules:
  - `core`: logic game, screens, world, entities, physics, navigation.
  - `lwjgl3`: launcher desktop.
  - `assets`: texture/sprite/font.

## Core file map (chi tiet)
- `core/src/main/java/com/main/game/MainGame.java`
  - Entry point cua game (`Game`).
  - Tao resource dung chung: `SpriteBatch`, `AssetManager`, `ScreenRouter`, `GameState`.
  - `render()` goi `screenRouter.flush()` truoc `super.render()`.
  - Chiu trach nhiem dispose resource shared khi thoat game.
- `core/src/main/java/com/main/game/GameState.java`
  - Luu state toan cuc (vd: `brightness`) dung cho nhieu screen.

- `core/src/main/java/com/main/game/navigation/ScreenId.java`
  - Enum dinh danh cac screen (`LOADING`, `MENU`, `GAME`, `PAUSE`, `GAME_OVER`, ...).
- `core/src/main/java/com/main/game/navigation/ScreenRouter.java`
  - Co che chuyen screen an toan qua `request()` + `flush()`.
  - Dam bao thu tu lifecycle: `onExit -> dispose -> create screen moi -> onEnter`.
  - Tranh switch trung screen hien tai.

- `core/src/main/java/com/main/game/screens/BaseScreen.java`
  - Base class cho cac screen, giu reference `MainGame`, `batch`, `camera`, `viewport`.
  - Dinh nghia hook `onEnter()` / `onExit()` va `update()` / `draw()`.
- `core/src/main/java/com/main/game/screens/GameScreen.java`
  - Noi integration gameplay chinh: `World`, `PhysicsEngine`, `Player`, `EntityManager`.
  - Xu ly input test transition (`ESC/P`, `M`, `K`, ...), camera follow, HUD, overlay.
  - Render order: world -> entities -> HUD -> overlay.
- `core/src/main/java/com/main/game/screens/StateScreen.java`
  - Screen trang thai (`PAUSE`, `GAME_OVER`) va dieu huong quay lai game/menu.
- `core/src/main/java/com/main/game/screens/MenuScreen.java`
  - Menu chinh.
- `core/src/main/java/com/main/game/screens/LoadingScreen.java`
  - Man hinh loading va chuyen tiep.
- `core/src/main/java/com/main/game/screens/ModeSelectScreen.java`
  - Chon mode game.
- `core/src/main/java/com/main/game/screens/SettingsScreen.java`
  - Cai dat (anh huong `GameState`).

- `core/src/main/java/com/main/game/world/World.java`
  - Quan ly grid world, generation terrain, spawn point, render theo camera.
  - Noi can uu tien toi uu khi doi sang chunk streaming.
- `core/src/main/java/com/main/game/world/BlockPalette.java`
  - Load/giu texture block dung chung.
  - Co fallback texture va dispose tap trung.
- `core/src/main/java/com/main/game/world/DemoBlockViewer.java`
  - Tool debug de spawn/xem nhanh block trong world.

- `core/src/main/java/com/main/game/blocks/AbstractBlock.java`
  - Contract block co thuoc tinh vat ly: `solid`, `breakable`, `hardness`, `bounds`.
- `core/src/main/java/com/main/game/blocks/SimpleBlock.java`
  - Implement co ban cho block.
- `core/src/main/java/com/main/game/blocks/types/*.java`
  - Nhom block theo domain (`Nature`, `Ore`, `Stone`, `Wood`, `Utility`).

- `core/src/main/java/com/main/game/entities/Entity.java`
  - Base entity: position, velocity, bounds, trang thai song/chet.
- `core/src/main/java/com/main/game/entities/Player.java`
  - Input + state machine player + health/damage/respawn.
- `core/src/main/java/com/main/game/entities/Mob.java`
  - AI mob (patrol/chase/attack tuy branch).
- `core/src/main/java/com/main/game/entities/EntityManager.java`
  - Update/render/dispose tap trung cho player + danh sach mob.
- `core/src/main/java/com/main/game/entities/EntityState.java`
  - Enum state cho entity (`IDLE`, `RUN`, `JUMP`, `FALL`, ...).

- `core/src/main/java/com/main/game/physics/PhysicsEngine.java`
  - Gravity + collision/ground detection.
  - Diem nong can nang cap: AABB day du 4 phia, resolve theo truc X/Y.

- `core/src/main/java/com/main/game/utils/Constants.java`
  - Hang so game (viewport, tile size, gravity, toc do, ...).
- `core/src/main/java/com/main/game/utils/TextureManager.java`
  - Quan ly texture dung chung theo key/cache.
- `core/src/main/java/com/main/game/ui/UISkin.java`
  - Dinh nghia style UI (font, mau, skin binding neu dung Scene2D UI).

## Cach chay nhanh
- Chay game desktop: `./gradlew.bat lwjgl3:run`
- Build toan bo: `./gradlew.bat build`
- Chay test (neu co): `./gradlew.bat test`

## Co che da lam (hien tai)
- Screen lifecycle:
  - Tao/dispose `SpriteBatch`, `AssetManager` chi trong `MainGame`.
  - Chuyen screen chi qua `ScreenRouter.request(ScreenId)`.
  - Khi doi screen: `onExit() -> dispose() -> create screen moi -> onEnter()`.
- World:
  - Terrain ngau nhien (khoang 400x128), nhieu lop dia chat.
  - Co culling de giam block render ngoai khung nhin.
- Player:
  - Input co ban: di trai/phai, jump.
  - State co ban: `IDLE`, `RUN`, `JUMP`, `FALL` (mo rong them `HURT`, `DEAD` tuy nhanh).
- Physics:
  - Da co gravity + ground detection co ban.
  - Chua hoan thien collision day du 4 phia.
- Blocks/Assets:
  - Co `BlockPalette`, `TextureManager`, bo block type classes.

## Co che dang thieu/uu tien tiep
- AABB collision day du (tren/duoi/trai/phai), resolve theo truc X/Y.
- On dinh jump/ground detection cho Player.
- Hoan thien mob AI (patrol/chase/attack) cho mob dau tien.
- Chuan hoa asset atlas va naming convention.

## Quy tac lam viec voi Codex
- Khong doi kien truc lon neu chua duoc yeu cau.
- Moi thay doi phai bao gom:
  - File da sua.
  - Co che nao bi anh huong.
  - Cach verify trong game (input/scene expected).
- Uu tien fix theo blocker gameplay truoc (physics, input, state).

## Quy chuan comment trong code
- Chi comment `why` hoac trade-off; khong mo ta lai dong code hien ro nghia.
- O logic game loop/physics, comment ngan de ghi ro assumption.
- TODO phai co ngu canh:
  - Pham vi viec can lam.
  - Dieu kien hoan thanh.
  - Nguoi xu ly (neu co).
- Mau:
  - `// Why: resolve Y truoc de tranh ket goc khi roi xuong block`
  - `// TODO(lhung): bo sung collision canh trai/phai cho AABB truoc merge physics`

## Quy chuan comment PR/Review
- Dung muc do: `blocker`, `major`, `minor`, `nit`.
- Moi comment nen co:
  - Van de quan sat duoc.
  - Rui ro gameplay/bug.
  - De xuat sua cu the.
- Uu tien review vao: crash, collision sai, state sai, leak tai nguyen, pha lifecycle screen.

## Rule bat buoc cho screen va tai nguyen
- Khong goi `setScreen()` truc tiep trong module gameplay.
- Khong dispose `batch`/`assetManager` trong Screen.
- Neu can doi man, goi `game.getScreenRouter().request(...)`.

## Checklist khi nhan task
1. Nhac lai task + module bi anh huong (`world`, `player`, `physics`, `screen`, `assets`).
2. Doc file lien quan trong `core/src/main/java/com/main/game/...`.
3. Sua nho, dung scope.
4. Chay lai `lwjgl3:run` hoac test lien quan.
5. Bao cao ket qua va huong test thu cong trong game.

## Ghi chu team
- Merge vao `main` qua PR, tranh merge thang khi chua review.
- Uu tien xu ly blocker >24h (dac biet Physics dang block cac nhanh khac).
