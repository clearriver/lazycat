(function(){var A=CodeMirror.getMode({indentUnit:4},{name:"python",version:3,singleLineStringErrors:false});function B(C){test.mode(C,A,Array.prototype.slice.call(arguments,1))}B("decoratorStartOfLine","[meta @dec]","[keyword def] [def function]():","    [keyword pass]");B("decoratorIndented","[keyword class] [def Foo]:","    [meta @dec]","    [keyword def] [def function]():","        [keyword pass]");B("matmulWithSpace:","[variable a] [operator @] [variable b]");B("matmulWithoutSpace:","[variable a][operator @][variable b]");B("matmulSpaceBefore:","[variable a] [operator @][variable b]");B("fValidStringPrefix","[string f'this is a {formatted} string']");B("uValidStringPrefix","[string u'this is an unicode string']")})();