(function(A){if(typeof exports=="object"&&typeof module=="object"){A(require("../../lib/codemirror"))}else{if(typeof define=="function"&&define.amd){define(["../../lib/codemirror"],A)}else{A(CodeMirror)}}})(function(C){C.defineOption("scrollPastEnd",false,function(F,E,D){if(D&&D!=C.Init){F.off("change",B);F.off("refresh",A);F.display.lineSpace.parentNode.style.paddingBottom="";F.state.scrollPastEndPadding=null}if(E){F.on("change",B);F.on("refresh",A);A(F)}});function B(E,D){if(C.changeEnd(D).line==E.lastLine()){A(E)}}function A(G){var E="";if(G.lineCount()>1){var F=G.display.scroller.clientHeight-30,D=G.getLineHandle(G.lastLine()).height;E=(F-D)+"px"}if(G.state.scrollPastEndPadding!=E){G.state.scrollPastEndPadding=E;G.display.lineSpace.parentNode.style.paddingBottom=E;G.off("refresh",A);G.setSize();G.on("refresh",A)}}});