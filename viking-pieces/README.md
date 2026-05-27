# Custom Piece Editor

A template project for creating custom chess pieces in the Fairy Chess engine. This repository provides a pre-configured Maven project structure with examples and guidelines for building and integrating new pieces into the chess game.

## Quick Start

### Prerequisites

- Maven 3.6+
- Java 11+
- A fresh pull from the main Fairy Chess repository

### Step 1: Build the Engine (One-time)

```bash
cd Fairy-Chess-insane-refactor
mvn install
```

This compiles `chessboard-logic` and caches it in your local `~/.m2` repository. The piece project needs this to compile against `Piece`, `GameState`, and related classes.

**Note:** Only re-run this if `chessboard-logic` changes.

### Step 2: Copy the Template

```bash
cp -r custom-piece-editor my-piece
```

The `custom-piece-editor` directory serves as your starting template. It includes:
- Pre-configured `pom.xml`
- Correct folder structure
- Working Viking example to reference

## Implementation Guide

### Step 3: Configure Your Piece

Edit the following 4 files/sections:

#### 1. **pom.xml** — Update Artifact and Pack Name

Change the artifact ID (2 places):

```xml
<artifactId>my-piece</artifactId>          <!-- JAR filename -->
```

```xml
<Pack-Name>mypiece</Pack-Name>             <!-- How PieceRegistry identifies this pack -->
```

The pack name must be lowercase and match exactly when registering pieces in `App.java`.

#### 2. **YourPiece.java** — Write Piece Logic

Create a new file at `src/main/java/.../YourPiece.java` with the following methods:

```java
@Override 
public String identifier() { 
    return "YourPiece"; 
}
```
Returns the unique identifier for your piece (used in board registration).

```java
@Override 
public String icon(BoardPiece thisState) {
    return thisState.color() == Color.BLACK 
        ? "black_yourpiece.png" 
        : "white_yourpiece.png";
}
```
Specifies which image file to display for black and white versions.

```java
@Override 
public String[][] getMovableSquares(BoardPiece thisState, GameState logic) {
    // Use Vector + isInBounds() for bounds checking (like Knight/Viking example)
    // Return "move.png" for empty reachable squares
    // Return "attack.png" for capturable enemy squares
}
```
Defines which squares the piece can move to or attack. Use the Vector utility class and `isInBounds()` for safe boundary checking.

```java
@Override 
public void onMoveCommand(GameState logic, Position from, Position to) {
    // Capture enemy if present
    // Move the piece
    // Call passControl() to end turn
}
```
Handles the actual move execution: capture logic, piece movement, and turn control.

#### 3. **Piece Images** — Add Art Assets

Place PNG images in `src/main/resources/`:

```
black_yourpiece.png
white_yourpiece.png
```

These must match the filenames returned by your `icon()` method exactly.

#### 4. **Service Registration** — Register Your Class

Create or edit `src/main/resources/META-INF/services/com.chess.logic.types.Piece`:

```
com.chess.yourpackage.YourPiece
```

This fully-qualified class name allows the ServiceLoader to discover your piece at runtime.

### Step 4: Build the JAR

```bash
cd my-piece
mvn package
```

Output: `target/my-piece-0.0.1-SNAPSHOT.jar`

### Step 5: Deploy to Game

Copy the JAR to the packs directory:

```bash
cp target/my-piece-0.0.1-SNAPSHOT.jar ../chessboard-gui/packs/
```

The game automatically scans `packs/` for `.jar` files at startup — no additional configuration needed.

### Step 6: Place Piece on Board

In `chessboard-gui/src/main/java/.../App.java`, inside the `setupBoard()` method, add:

```java
placeFromRegistry(logic, new PiecePath("mypiece", "YourPiece"), Color.BLACK, new Position(2, 4));
placeFromRegistry(logic, new PiecePath("mypiece", "YourPiece"), Color.WHITE, new Position(5, 4));
```

**Important:** 
- The pack name (`"mypiece"`) must match your `<Pack-Name>` in `pom.xml` (lowercase)
- The piece name (`"YourPiece"`) must match what your `identifier()` method returns
- Adjust `Position` coordinates as needed for your board layout


## Troubleshooting

**JAR not found by game:**
- Verify the `.jar` is in `chessboard-gui/packs/`
- Check that the filename matches your `<artifactId>` in `pom.xml`

**Piece not appearing on board:**
- Ensure `Pack-Name` in `pom.xml` matches the first argument to `PiecePath`
- Ensure `identifier()` return value matches the second argument to `PiecePath`
- Verify `placeFromRegistry()` call is in `setupBoard()` method

**Compilation errors:**
- Re-run `mvn install` in the main Fairy Chess project
- Ensure `chessboard-logic` is in your local `~/.m2` cache

**Image not displaying:**
- Verify PNG filenames match exactly what `icon()` returns
- Check that images are in `src/main/resources/`
- Ensure no typos in filename references