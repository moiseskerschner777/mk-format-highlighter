# design.md — Comment stacking for mk-format-highlighter

## File structure

New package alongside the existing `com.moiseskerschner.mkformat` sources:

```
src/main/kotlin/com/moiseskerschner/mkformat/comments/
├── MkComment.kt              # data class
├── MkCommentManager.kt       # RangeMarker-backed store, one per open document
├── MkCommentStorage.kt       # JSON sidecar persistence
├── AddCommentInlineAction.kt # editor selection -> inline input -> stack on Enter
├── MkCommentGutterProvider.kt# gutter icon per commented line
├── MkCommentBarPanel.kt      # bottom bar: count + Copy + Clear all
└── MkCommentFormatter.kt     # stack -> paste-ready text block
```

Sidecar files live next to the source file:
```
.mk-comments/<filename>.json
```
e.g. `sample.mk` → `.mk-comments/sample.mk.json`.

## Component contracts

### `MkComment`
```kotlin
data class MkComment(
    val id: String,           // UUID
    val startOffset: Int,
    val endOffset: Int,
    val snippet: String,      // text at anchor time
    val request: String,      // user's typed note
    val lineNumber: Int       // 1-indexed, for display/copy formatting
)
```

### `MkCommentManager`
- Owns a `MutableList<MkComment>` plus a parallel `RangeMarker` per comment
  (RangeMarkers keep offsets correct as the document is edited).
- `addComment(editor: Editor, request: String): MkComment` — reads current
  selection, creates a RangeMarker + MkComment, appends to the list.
- `removeComment(id: String)`
- `clearAll()`
- `getComments(): List<MkComment>` — resolves each RangeMarker's *current*
  offsets/snippet before returning, so stale text is never shown.
- One manager instance per open `Document`, keyed via a project service +
  `Document` → manager map (or a `Key<MkCommentManager>` stored via
  `Document.putUserData`, following the same "don't touch PSI" philosophy as
  `MkLexer`).

### `MkCommentStorage`
- `save(file: VirtualFile, comments: List<MkComment>)` — writes JSON to
  `.mk-comments/<filename>.json`.
- `load(file: VirtualFile): List<MkComment>` — reads JSON, returns comments
  with their *original* offsets; `MkCommentManager` re-creates RangeMarkers
  from those offsets on file open (best-effort re-anchoring — if the file
  changed structurally between sessions, offsets may drift, which is an
  accepted limitation of this phase).

### `MkCommentGutterProvider`
- Implements `LineMarkerProvider`. For each line with ≥1 comment, renders a
  small icon (message/comment glyph) in the gutter. Click opens a popup
  listing the comment(s) on that line with a per-comment "x" to clear.

### `MkCommentBarPanel`
- A `JPanel` docked at the bottom of the editor (via `EditorNotifications` or
  a custom `FileEditor` panel — decided during Phase 3 implementation).
- Shows "N comments stacked" (or "No comments stacked" state = hidden
  entirely per spec: bar only shows when count ≥ 1).
- "Copy for agent" button → `MkCommentFormatter.format(comments)` →
  `CopyPasteManager.getInstance().setContents(...)`.
- "Clear all" button → `MkCommentManager.clearAll()`.

### `MkCommentFormatter`
```kotlin
fun format(fileName: String, comments: List<MkComment>): String
```
Output shape (plain text, paste-ready):
```
file: sample.mk

line 2: "    mk-format-highlighter:"
> rename this node to "mk plugin"

line 31: "    color palette:"
> change this
```

## Architecture decisions

- **RangeMarker over PSI**: consistent with the rest of the plugin, which
  deliberately avoids PSI tree usage (see `MkLexer`). Comments are anchored
  to raw document offsets, not structural nodes.
- **Per-document manager, not a global service**: keeps comment state scoped
  to the file it was created in — no cross-file leakage, no need for a
  project-wide index in this phase.
- **Inline input, not modal dialog**: appears directly below the selected
  line (matches the reviewed mockup), dismissed on Enter (stack) or Escape
  (cancel). Implemented as a lightweight `JBPopup` or inline `JPanel`
  inserted via `EditorComponentImpl`, decided during Phase 2.
- **Bottom bar visibility**: bar is added/removed from the editor's panel
  hierarchy based on `comments.isEmpty()`, not just hidden — keeps the
  editor UI clean when there's nothing stacked.
- **No auto-clear on copy**: "Copy for agent" is non-destructive; "Clear
  all" is a separate, explicit action. Avoids accidentally losing context if
  the user wants to copy twice.

## Data flow

```
select text
    -> AddCommentInlineAction triggers inline input
    -> Enter pressed
    -> MkCommentManager.addComment() creates RangeMarker + MkComment
    -> MkCommentGutterProvider repaints gutter for that line
    -> MkCommentBarPanel updates count, becomes visible if it wasn't
...
(repeat for more selections)
...
click "Copy for agent"
    -> MkCommentManager.getComments() resolves current RangeMarker offsets
    -> MkCommentFormatter.format() builds text block
    -> CopyPasteManager puts it on the system clipboard
```

Persistence is a side channel, triggered on file save/close and file open —
it does not sit in the interactive stacking loop above.

## Behavior table

| Action | Precondition | Result |
|---|---|---|
| Select text, type note, Enter | Editor has a `.mk` file open | Comment stacked, gutter icon shown, bar count +1 |
| Select text, type note, Escape | Inline input open | Input dismissed, nothing stacked |
| Click gutter icon | Line has ≥1 comment | Popup lists comment(s), each with clear "x" |
| Click "Copy for agent" | Stack has ≥1 comment | Clipboard set, stack unchanged |
| Click "Clear all" | Stack has ≥1 comment | Stack emptied, bar hides, gutter icons removed |
| Close and reopen file | Sidecar JSON exists | Comments reloaded, RangeMarkers re-anchored by offset |
