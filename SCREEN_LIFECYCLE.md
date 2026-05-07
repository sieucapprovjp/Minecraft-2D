# Screen Lifecycle Rules (HUY-LEAD)

## Muc tieu
- Shared resources (`SpriteBatch`, `AssetManager`) chi duoc tao/dispose trong `MainGame`.
- Chuyen screen chi qua `ScreenRouter.request(ScreenId)`.
- Moi lan roi screen: `onExit()` -> `dispose()` ngay (khong cache).

## Luong chay
1. `MainGame.create()` tao `batch`, `assetManager`, `screenRouter`.
2. `MainGame.render()` goi `screenRouter.flush()` truoc `super.render()`.
3. `ScreenRouter.flush()`:
   - goi `onExit()` cua screen hien tai
   - dispose screen hien tai
   - tao screen moi qua `MainGame.createScreen()`
   - `setScreen(next)`
   - goi `onEnter()` cua screen moi

## Rule cho team
- Khong goi `setScreen()` truc tiep trong gameplay/module.
- Khong dispose `batch` hoac `assetManager` trong cac Screen.
- Neu can doi man, chi goi `game.getScreenRouter().request(...)`.

## Phim test transition hien tai
- Trong `GameScreen`:
  - `P` -> `PAUSE`
  - `M` -> `MENU`
  - `K` -> `GAME_OVER`
- Trong `StateScreen`:
  - `ESC` hoac `G` -> quay lai `GAME`
