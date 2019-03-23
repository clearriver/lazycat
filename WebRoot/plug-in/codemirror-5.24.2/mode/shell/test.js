(function(){var A=CodeMirror.getMode({},"shell");function B(C){test.mode(C,A,Array.prototype.slice.call(arguments,1))}B("var","text [def $var] text");B("varBraces","text[def ${var}]text");B("varVar","text [def $a$b] text");B("varBracesVarBraces","text[def ${a}${b}]text");B("singleQuotedVar","[string 'text $var text']");B("singleQuotedVarBraces","[string 'text ${var} text']");B("doubleQuotedVar",'[string "text ][def $var][string  text"]');B("doubleQuotedVarBraces",'[string "text][def ${var}][string text"]');B("doubleQuotedVarPunct",'[string "text ][def $@][string  text"]');B("doubleQuotedVarVar",'[string "][def $a$b][string "]');B("doubleQuotedVarBracesVarBraces",'[string "][def ${a}${b}][string "]');B("notAString","text\\'text");B("escapes",'outside\\\'\\"\\`\\\\[string "inside\\`\\\'\\"\\\\`\\$notAVar"]outside\\$\\(notASubShell\\)');B("subshell","[builtin echo] [quote $(whoami)] s log, stardate [quote `date`].");B("doubleQuotedSubshell",'[builtin echo] [string "][quote $(whoami)][string \'s log, stardate `date`."]');B("hashbang","[meta #!/bin/bash]");B("comment","text [comment # Blurb]");B("numbers","[number 0] [number 1] [number 2]");B("keywords","[keyword while] [atom true]; [keyword do]","  [builtin sleep] [number 3]","[keyword done]");B("options","[builtin ls] [attribute -l] [attribute --human-readable]");B("operator","[def var][operator =]value");B("doubleParens","foo [quote $((bar))]")})();