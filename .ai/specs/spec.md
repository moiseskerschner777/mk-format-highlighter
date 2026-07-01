# spec.md — Comment stacking for mk-format-highlighter

## What

A feature inside `mk-format-highlighter` that lets the user select text in an
open `.mk` file, type a short change request inline, and stack it. Stacked
requests accumulate in a persistent bottom bar. When ready, the user copies
the full stack — formatted with file, line, snippet, and request per entry —
to the clipboard, to paste into an external LLM agent chat.

This is the plugin-native version of the review-comment workflow already
used against this project (file path, line range, snippet, request text),
minus the network round-trip: no live API call is made from inside the IDE.

## Why

Moises currently edits `.mk`-adjacent code (the plugin itself) by describing
changes in prose to an LLM agent. There's no way to anchor a change request to
an exact spot in a file without re-typing context every time. This feature
lets multiple change requests be collected against precise text ranges, then
handed to an agent in one batch, in a consistent format.

## Users

Single user: Moises Kerschner, using this inside IntelliJ IDEA Community
2024.1 while editing `.mk` files (including this project's own planning
docs and sample files).

## Capabilities

- Select a text range in the editor, get an inline input at the selection.
- Press Enter to stack the comment (range + snippet + request text).
- Gutter icon appears on every line with a stacked comment.
- A bottom bar shows the running count of stacked comments and stays visible
  whenever there is at least one.
- "Copy for agent" formats the full stack as paste-ready text and puts it on
  the clipboard.
- Comments persist across file close/reopen (sidecar JSON file).
- Individual comments can be cleared; "Clear all" empties the stack.

## Constraints

- No live LLM/API integration in this phase — output is clipboard text only.
- RangeMarker-backed anchoring, so comments stay attached to their text as
  the file is edited (consistent with how `MkLexer` already treats the file
  as plain text, not a PSI tree).
- Must not interfere with existing syntax highlighting, folding, or
  structure view functionality.
- Targets IntelliJ Community 2024.1 (`IC-241`), same compatibility range as
  the rest of the plugin (`241–252.*`).

## Out of scope

- Sending the stack to a real LLM agent over the network (explicitly
  deferred — may become a future phase/project).
- Multi-file stacks (each file's stack is independent for now).
- Collaborative/shared comments.
- Resolving comments automatically based on agent responses.
