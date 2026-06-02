# Implementation Notes

## Project Structure

The main repository is split into three modules:

- `base-pieces`: A piece pack containing classic chess pieces. The `.jar` file emitted from this module is distributed with the game.
- `chessboard-logic`: The main logic of the game. Classes handling game state is in the `state` folder, while record classes representing common concepts in the game are in the `types` folder. The `boardregion` folder contains classes that represent a region on the board; these help streamline iterating over regions on the board, thereby making piece creation easier.
- `chessboard-gui`: The UI of the game. The `MainLauncher` class has no purpose other than to run the main `App`, in order to avoid limitations of the Maven Shade Plugin. This module emits the game as a standalone executable `.jar` file.

## Modding

To ensure flexibility, custom pieces are written as Java classes (extending from the `Piece` base class) and directly loaded and ran by the game:

- All piece packs (including `base-pieces`) are loaded dynamically from a folder named `packs` that is at the same directory as the game executable.
- Each piece pack has a name (stored as the `Pack-Name` field in the `.jar` manifest file) and one or more piece implementations. The name of each piece implementations is returned via the `identifier()` abstract method.