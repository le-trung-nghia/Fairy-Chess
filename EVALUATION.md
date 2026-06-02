# Evaluation Scenarios

- Classic Chess: Proceed with the game, using the default board layout. The game should play identically to normal chess (in terms of moves, captures, and timers), except for the following points:
  - Checks and checkmates do not exist in Fairy Chess, you can only win by capturing the opponent's king piece.
  - As repetition draws are not implemented, it is impossible for the match to have a draw result.
  - Special moves, such as castling and en passant, are not implemented.
- Fairy Chess: Load the piece pack from the <https://github.com/le-trung-nghia/Fairy-Chess-Modding> GitHub repository (by copying the `.jar` file to the `packs` directory). Use the board customization menus to edit the board, adding the custom pieces and making any other changes to the board. The game should run as normal, with the custom pieces following their own rules. Core rules (timer and king capture/absence) should be enforced.
- Move History: In any match, the `Back` and `Forward` buttons can be clicked to view the board state in the past. This should not affect the current state of the game.
