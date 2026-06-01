# COMP1020 Final Project Team 8: Fairy Chess

A modular JavaFX chess game that supports custom piece packs via a plugin system.

## Building

```bash
mvn clean package
```

Produces:

- `target/fairy-chess.jar` — the game (fat JAR, run directly)
- `target/packs/base-pieces.jar` — the built-in piece pack

## Running

```bash
java -jar target/fairy-chess.jar
```

## Modding

Create your own piece pack or download piece packs from others and drop the JAR into the `packs/` folder next to the game.

**Modding example & template:**
<https://github.com/le-trung-nghia/Fairy-Chess-Modding>
