---
name: release-plugin
description: Builds the mk-format Highlighter plugin and publishes it to both the JetBrains Marketplace (IntelliJ plugin store) and a GitHub release with the zip attached, using Chrome. Use ALWAYS when the user asks to release, publish, upload, or ship the plugin, "upload to the intellij store", "upload to marketplace", "create a github release", or "upload the build". Optional argument - an explicit version (e.g. "1.1.0"); otherwise the patch version is bumped when needed.
---

# Release mk-format Highlighter

Every release goes to **two places**: the JetBrains Marketplace and a GitHub release. Do both unless the user says otherwise.

| Thing | Value |
|---|---|
| Marketplace plugin | `https://plugins.jetbrains.com/plugin/32356-mk-format-highlighter` |
| Marketplace versions page | `https://plugins.jetbrains.com/plugin/32356-mk-format-highlighter/edit/versions` |
| GitHub repo | `moiseskerschner777/mk-format-highlighter` (remote `origin`, branch `main`) |
| Version source | `version = "x.y.z"` in `build.gradle.kts` |
| Build output | `build/distributions/mk-format-highlighter-<version>.zip` |

Uploads use **Chrome** (`claude-in-chrome` skill). The GitHub MCP has no release/asset tools and `gh` is not installed, so don't try them.

## 1. Pick the version

1. Read the current version: `grep '^version' build.gradle.kts`.
2. Open the Marketplace versions page in Chrome and read the version list on the left (`get_page_text` returns nothing there; take a screenshot).
3. If the user gave a version, use it. Otherwise, if the current version is already on the Marketplace, bump the patch number (`1.0.4` → `1.0.5`). The Marketplace rejects a version it already has.
4. Edit `build.gradle.kts` with the new version.

## 2. Commit and push the bump

The GitHub release tag must point to a commit that contains the bumped version.

1. `git status` — if there are other uncommitted changes, ask the user whether they belong in this release before committing anything.
2. Commit directly on `main` (never create a branch) and push to `origin main`. Check `git status -sb` shows `main...origin/main` with nothing ahead.

## 3. Build

```bash
./build-plugin.sh </dev/null
```

`</dev/null` stops the script from waiting at its interactive JDK prompt. Then verify the artifact:

```bash
unzip -l build/distributions/mk-format-highlighter-<version>.zip
unzip -p build/distributions/mk-format-highlighter-<version>.zip 'mk-format-highlighter/lib/mk-format-highlighter-<version>.jar' > <scratchpad>/plugin.jar
unzip -p <scratchpad>/plugin.jar META-INF/plugin.xml | grep -E '<version>|since-build'
```

`<version>` in `plugin.xml` must match. If the build fails, stop and report it.

## 4. Upload to the JetBrains Marketplace

1. `tabs_context_mcp` (createIfEmpty), then navigate to the versions page.
2. Click **Upload Update** (top right).
3. `find` the dialog's file input ("No file selected"), the Channel box, and the **Upload update** button.
4. `file_upload` the zip into the file input. Leave Channel = **Stable** and leave **Make Hidden** unchecked.
5. Zoom in on the dialog to confirm the file name shows the right version, then click **Upload update**.
6. Wait a few seconds and take a screenshot. Expect an **Upload Successful** dialog (JetBrains review takes up to 2 business days). Click **Got it**. If there's an error (duplicate version, verifier failure), stop and report it.

## 5. Create the GitHub release

Match the existing release style: tag, title and notes are all `v<version>`, with the zip attached.

1. Navigate to:
   `https://github.com/moiseskerschner777/mk-format-highlighter/releases/new?tag=v<version>&target=main&title=v<version>&body=v<version>`
2. Take a screenshot and check: tag `v<version>`, target `main`, title and notes `v<version>`, release label **Latest**.
3. `find` the release-assets file input ("Attach binaries…") and `file_upload` the zip into it. Wait about 5 seconds and confirm in a screenshot that the zip appears in the assets list.
4. `find` **Publish release** and click it.
5. Wait, then `get_page_text`. The URL should become `/releases/tag/v<version>`, with the commit hash and `mk-format-highlighter-<version>.zip` listed under Assets.

## 6. Finish

Close the tabs you opened. Report:
- The version released and why (user-specified or bumped from what).
- Marketplace: uploaded to Stable and waiting for JetBrains review.
- GitHub: the release URL, the tagged commit, and the asset sha256 from the release page.
