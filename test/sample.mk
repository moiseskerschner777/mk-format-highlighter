project overview:

    mk-format-highlighter:
        native IntelliJ plugin for .mk files
        depth-based syntax highlighting
        isolated color settings page
        status:
            base plugin scaffolded and functional
            structure view in progress
            code folding in progress


    architecture:
        MkLanguage
            anchor language object
        MkFileType
            maps .mk extension to MkLanguage
            loads file icon from svg resource
        MkTokenTypes
            14 token types
            7 depth levels
                plain variant
                parent variant
        MkLexer:
            hand-written line-based lexer
            counts leading spaces
            depth = min(leadingSpaces / 4, 6)
            flags colon-ending lines as parents
        MkColors
            8 TextAttributesKeys
            depth colors baked in as defaults
            PARENT_BOLD key
                Font.BOLD only, no foreground override
color palette:
    depth 0:
        hex F5F7FA
        near white
    depth 1:
        hex FF9E64
        orange
    depth 2:
        hex 7DCFFF
        sky blue
    depth 3:
        hex 9ECE6A
        green
    depth 4:
        hex BB9AF7
        violet
    depth 5:
        hex F7768E
        rose
    depth 6:
        hex E0AF68
        amber
current work:
    structure view:
        spec.md and design.md generated
        phases 1 through 3 reported complete
        bug:
            panel shows No structure
            debug focus:
                plugin.xml registration
                language ID mismatch
                getChildren returning empty
    code folding:
        MkFoldingBuilder.kt
        multiple bug fix iterations completed
        latest issue:
            end-of-region calculation
            consuming blank separator lines on collapse
        latest fix:
            stop fold regions at last non-blank content line
notes:
    no bullet characters
    no markdown syntax
    indentation and colons only
    4 spaces per depth level
    lines ending in colon are parents and render bold



