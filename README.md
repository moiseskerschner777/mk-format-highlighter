# mk-format-highlighter

IntelliJ IDEA plugin that provides syntax highlighting for **mk-format** outline files (`.mk`).

## What it does

When you open a `.mk` file in IntelliJ, each line is color-coded based on its indentation depth (4 spaces per level, up to 7 levels). Lines ending with `:` are displayed in **bold** to visually distinguish group/parent items from leaf items.

### Default colors

| Depth | Color |
|-------|-------|
| 0     | Light gray |
| 1     | Orange |
| 2     | Light blue |
| 3     | Green |
| 4     | Purple |
| 5     | Red/pink |
| 6     | Gold/brown |

Colors can be customized under **Settings > Editor > Color Scheme > mk-format**.

## How to build

Requirements:
- JDK 17+

```bash
./gradlew build
```

The plugin distribution (`mk-format-highlighter-1.0.0.zip`) will be produced at `build/distributions/`.

## How to install

1. Build the project (see above)
2. In IntelliJ, go to **Settings > Plugins > Gear icon > Install Plugin from Disk...**
3. Select `build/distributions/mk-format-highlighter-1.0.0.zip`
4. Restart the IDE

## Send comments to a running Claude session

Put a header as the first non-blank line of the `.mk` file:

    session: Notas sobre TCC

The value is matched against the titles of the IDE terminal tabs (case-insensitive, "contains").

When a comment is added (Enter in the comment popup), the formatted comments are still copied to the clipboard and, if the terminal tab is found, also pasted into it and submitted. Without the header, behavior is unchanged.
