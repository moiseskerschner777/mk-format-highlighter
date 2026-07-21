# mk-format-highlighter — Features

IntelliJ IDEA plugin for **mk-format** outline files (`.mk`). It provides
depth-aware syntax highlighting, structural navigation, and an inline
comment-stacking workflow for handing precise change requests to an external
LLM agent.

- **Plugin ID:** `com.moiseskerschner.mkformat`
- **Version:** 1.0.2
- **Compatibility:** IntelliJ Community 2024.1+ (`since-build 241`, `until-build 261.*`)
- **Language target:** JVM 17 / Kotlin 1.9.22

---

## 1. File type recognition

- Registers the `.mk` extension as a dedicated **mk-format** language/file type.
- File type description: *"mk-format outline file"*.

## 2. Depth-based syntax highlighting

- Each line is colored by its **indentation depth** (4 spaces = 1 level),
  clamped to 7 levels (depth 0–6).
- Parent lines (trimmed content ending with `:`) are rendered **bold** on top
  of their depth color, distinguishing group nodes from leaf items.
- Lexer treats the file as plain text line-by-line (no PSI tree), so
  highlighting never interferes with editing.

## 4. Code folding

- Any parent line (`...:`) with at least one indented child becomes a
  **foldable region**.
- Fold spans from the parent down to its last deeper-indented descendant;
  blank lines are skipped.
- Collapsed placeholder shows `...`. Regions are **expanded by default**.

## 5. Structure view (outline navigation)

- Builds a hierarchical **Structure tool window** tree from parent (`:`) lines.
- Nesting is derived from indentation depth (up to 7 levels).
- Each node is **navigable** — clicking jumps the editor to that line.
- Tracks the caret: the current element is highlighted as you move through the
  file.

## 6. Inline comment stacking (LLM-agent workflow)

A plugin-native review-comment workflow: anchor short change requests to exact
text ranges, accumulate them, and copy the batch to an external LLM agent.

### Adding a comment
- Select text in a `.mk` file and trigger **Add Comment** via the editor
  right-click menu or the shortcut **Ctrl+Alt+Shift+A**.
- An inline popup input appears at the selection:
  - **Enter** — stacks the comment.
  - **Shift+Enter** — inserts a newline in the note.
  - **Tab / Shift+Tab** — indent / outdent by 4 spaces inside the note.
  - **Escape** — cancels without stacking.
- The action is only enabled when a `.mk` file is open **and** text is selected.

### Anchoring
- Each comment stores id (UUID), start/end offsets, snippet, request text, and
  line number.
- Comments are backed by **RangeMarkers**, so anchors follow the text as the
  document is edited; snippets and line numbers are re-resolved live before
  display or copy.

### Gutter indicators
- A balloon icon appears in the **gutter** on every line that has a stacked
  comment.

### Bottom bar
- An editor notification bar shows the running count
  (*"N comments stacked"*), visible only while at least one comment exists.
- **Click the counter** → read-only preview popover of the formatted stack,
  with mk-format depth colors and monospaced font, plus a per-comment remove
  ("x") button.
- **Copy** button → puts the formatted stack on the clipboard.
- **Close** button → clears the entire stack.

### Auto-copy on stack
- Stacking a comment with Enter immediately copies the **full formatted stack**
  to the clipboard — no manual click required. Copying is non-destructive; the
  stack is only emptied by an explicit clear.

### Clipboard / paste format
```
path/to/file.mk: file.mk

line 2: "    parent-node:"
> rename this node

line 31: "    color palette:"
> change this
```
- The file header uses the path **relative to the content root** (falls back to
  the bare filename if no content root is found).

### Persistence
- Comments survive file close/reopen.
- Stored as JSON in the IDE system directory:
  `{PathManager.getSystemPath()}/mk-comments/<sha256-of-full-path>.json`
  (SHA-256 of the file's full path as filename — no sidecar files next to
  sources, no collisions).
- One manager instance per open document (`Document` user-data key), keeping
  comment state scoped per file with no cross-file leakage.

### Debug action
- **Tools > Log mk Comments** logs the current stack (and stacks a test comment
  if text is selected) — a development/diagnostic helper.

*Source: `comments/AddCommentInlineAction.kt`, `MkCommentManager.kt`,
`MkCommentStorage.kt`, `MkCommentGutterProvider.kt`, `MkCommentBarProvider.kt`,
`MkCommentBarPanel.kt`, `MkCommentFormatter.kt`, `MkComment.kt`,
`LogCommentsAction.kt`*

---

## Feature summary

| Feature | Entry point | Key file(s) |
|---|---|---|
| `.mk` file type + icon | Project view / tabs | `MkFileType.kt` |
| Depth syntax highlighting | Editor | `MkLexer.kt`, `MkSyntaxHighlighter.kt` |
| Bold parent lines | Editor | `MkColors.kt` |
| Color customization | Settings > Color Scheme | `MkColorSettingsPage.kt` |
| Code folding | Editor gutter | `MkFoldingBuilder.kt` |
| Structure view | Structure tool window | `MkStructureView*.kt` |
| Add inline comment | Ctrl+Alt+Shift+A / context menu | `AddCommentInlineAction.kt` |
| Gutter comment icons | Editor gutter | `MkCommentGutterProvider.kt` |
| Comment count bar + preview | Editor notification bar | `MkCommentBarProvider.kt`, `MkCommentBarPanel.kt` |
| Copy stack for agent | Auto on Enter / Copy button | `MkCommentFormatter.kt` |
| Comment persistence | Automatic | `MkCommentStorage.kt`, `MkCommentManager.kt` |
| Log comments (debug) | Tools menu | `LogCommentsAction.kt` |
