# design.md — mk-format TOC (Structure View)

## File structure

Three new files added to the existing plugin. No existing file is modified.

```
src/main/kotlin/com/moiseskerschner/mkformat/
    MkStructureViewElement.kt      ← tree node: one : line
    MkStructureViewTreeModel.kt    ← wraps the whole file, builds root
    MkStructureViewFactory.kt      ← PsiStructureViewFactory, registered in plugin.xml

src/main/resources/META-INF/
    plugin.xml                     ← one new <lang.psiStructureViewFactory> entry (only change)
```

## Component contracts

### MkStructureViewElement

```kotlin
class MkStructureViewElement(
    val text: String,       // line text with trailing : stripped
    val depth: Int,         // 0–6, leadingSpaces / 4
    val offset: Int,        // char offset of line start in document
    val psiFile: PsiFile,   // needed for NavigationItem contract
    val children: List<MkStructureViewElement>
) : StructureViewTreeElement, ItemPresentation
```

- `getPresentation()` returns `this` (implements `ItemPresentation`)
  - `getPresentableText()` → `text`
  - `getLocationString()` → `null`
  - `getIcon(unused)` → `MkFileType.INSTANCE.icon`
- `getChildren()` → `children.toTypedArray()`
- `navigate(requestFocus)` → moves caret to `offset` in the editor
- `canNavigate()` / `canNavigateToSource()` → `true`

### MkStructureViewTreeModel

```kotlin
class MkStructureViewTreeModel(psiFile: PsiFile) : StructureViewModelBase(psiFile, buildRoot(psiFile))
```

- `buildRoot(psiFile)` — scans document text line by line:
  1. For each line, count leading spaces → `depth = min(spaces / 4, 6)`
  2. If `line.trimEnd().endsWith(":")` → candidate node
  3. Build flat list of `(text, depth, offset)` tuples
  4. Build hierarchy bottom-up using a depth-indexed stack (standard tree-from-indentation algorithm)
  5. Return root `MkStructureViewElement` whose children are all depth-0 nodes
- `getSuitableClasses()` → `arrayOf(PsiFile::class.java)`

### MkStructureViewFactory

```kotlin
class MkStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile) =
        object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?) =
                MkStructureViewTreeModel(psiFile)
        }
}
```

### plugin.xml addition

```xml
<lang.psiStructureViewFactory
    language="mk-format"
    implementationClass="com.moiseskerschner.mkformat.MkStructureViewFactory"/>
```

## Hierarchy algorithm

Uses a stack of size 7 (one slot per depth 0–6).

```
stack = Array(7) { null }   // MkStructureViewElement?
rootChildren = mutableListOf()

for each candidate (text, depth, offset):
    node = MkStructureViewElement(text, depth, offset, psiFile, mutableListOf())
    stack[depth] = node
    if depth == 0:
        rootChildren.add(node)
    else:
        // find nearest parent at depth - 1
        parent = stack[depth - 1]
        parent?.children?.add(node)
        // clear deeper slots to avoid stale parents
        for d in depth+1..6: stack[d] = null
```

This correctly handles skipped depths (e.g. depth 0 → depth 2 directly) by walking up the stack to find the nearest ancestor.

## Data flow

```
PsiFile (document text)
    → line scanner (MkStructureViewTreeModel.buildRoot)
        → flat list of candidate nodes
            → stack-based hierarchy builder
                → MkStructureViewElement tree
                    → IntelliJ Structure View panel
                        → click → navigate(offset) → caret moves
```

## Behavior table

| Action | Result |
|---|---|
| Alt+7 on `.mk` file | Structure panel opens, shows TOC tree |
| Plain line (no `:`) | Not shown in TOC |
| Blank line | Not shown in TOC |
| Click entry | Caret moves to that line |
| Caret moves in editor | Matching TOC entry highlights (auto-scroll from source) |
| Nested `:` lines | Shown as children of their parent `:` line |
| Depth skipped (0→2) | Depth-2 node attached to depth-0, no phantom nodes |

## Architecture decisions

**No PSI tree.** mk-format has no grammar/parser producing a PSI tree. Reading document text directly (as `MkLexer` does) is simpler and sufficient. `PsiFile.text` is used as the source.

**Reuse existing icon.** `MkFileType.INSTANCE.icon` is used for all TOC entries. Per-depth colored icons are out of scope.

**No filtering.** IntelliJ's Structure View has built-in filter toggles; none are registered here. The panel always shows all `:` lines.
