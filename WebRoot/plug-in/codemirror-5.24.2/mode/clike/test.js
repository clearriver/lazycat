(function(){var B=CodeMirror.getMode({indentUnit:2},"text/x-c");function C(E){test.mode(E,B,Array.prototype.slice.call(arguments,1))}C("indent","[variable-3 void] [def foo]([variable-3 void*] [variable a], [variable-3 int] [variable b]) {","  [variable-3 int] [variable c] [operator =] [variable b] [operator +]","    [number 1];","  [keyword return] [operator *][variable a];","}");C("indent_switch","[keyword switch] ([variable x]) {","  [keyword case] [number 10]:","    [keyword return] [number 20];","  [keyword default]:",'    [variable printf]([string "foo %c"], [variable x]);',"}");C("def","[variable-3 void] [def foo]() {}","[keyword struct] [def bar]{}","[variable-3 int] [variable-3 *][def baz]() {}");C("def_new_line","::[variable std]::[variable SomeTerribleType][operator <][variable T][operator >]","[def SomeLongMethodNameThatDoesntFitIntoOneLine]([keyword const] [variable MyType][operator &] [variable param]) {}");C("double_block","[keyword for] (;;)","  [keyword for] (;;)","    [variable x][operator ++];","[keyword return];");C("preprocessor","[meta #define FOO 3]","[variable-3 int] [variable foo];","[meta #define BAR\\]","[meta 4]","[variable-3 unsigned] [variable-3 int] [variable bar] [operator =] [number 8];","[meta #include <baz> ][comment // comment]");var A=CodeMirror.getMode({indentUnit:2},"text/x-c++src");function D(E){test.mode(E,A,Array.prototype.slice.call(arguments,1))}D("cpp14_literal","[number 10'000];","[number 0b10'000];","[number 0x10'000];","[string '100000'];")})();