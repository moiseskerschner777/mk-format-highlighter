# spec.md — mk-format TOC (Structure View)

## What

Add a Table of Contents extraction feature to the `mk-format-highlighter` IntelliJ plugin.
When the user opens the Structure panel (Alt+7) on a `.mk` file, they see a hierarchical tree of all parent lines (lines ending in `:`), navigable by click.

## Why

mk-format files can grow large. There is no way to jump between sections without scrolling. The Structure View panel is IntelliJ's standard solution for this — used by Java, Markdown, YAML. Adding it to `.mk` files gives first-class navigation with zero learning curve.

## Users

Solo — Moises Kerschner. Plugin is published on JetBrains Marketplace so other mk-format users benefit too.

## Capabilities

- Alt+7 (View > Tool Windows > Structure) on a `.mk` file opens a TOC panel
- Only parent lines (ending in `:`) appear — plain lines are excluded
- Hierarchy mirrors the indentation depth of the file
- Clicking any entry moves the caret to that line in the editor
- Auto-scroll from source: as the caret moves in the editor, the current section highlights in the TOC panel

## Constraints

- No changes to any existing file (`MkLexer`, `MkColors`, `MkSyntaxHighlighter`, etc.)
- No custom tool window — uses IntelliJ's native Structure View infrastructure
- No PSI tree — document text is read directly, same approach as `MkLexer`
- Plugin targets IntelliJ Community 2024.1 (`IC-241`), compatible range `241–252.*`

## Out of scope

- Depth-colored icons per entry (deferred to a future enhancement)
- Filtering or searching within the TOC panel
- TOC for non-`.mk` files
