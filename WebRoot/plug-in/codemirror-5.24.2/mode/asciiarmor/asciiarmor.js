(function(A){if(typeof exports=="object"&&typeof module=="object"){A(require("../../lib/codemirror"))}else{if(typeof define=="function"&&define.amd){define(["../../lib/codemirror"],A)}else{A(CodeMirror)}}})(function(B){function A(D){var C=D.match(/^\s*\S/);D.skipToEnd();return C?"error":null}B.defineMode("asciiarmor",function(){return{token:function(F,E){var D;if(E.state=="top"){if(F.sol()&&(D=F.match(/^-----BEGIN (.*)?-----\s*$/))){E.state="headers";E.type=D[1];return"tag"}return A(F)}else{if(E.state=="headers"){if(F.sol()&&F.match(/^\w+:/)){E.state="header";return"atom"}else{var C=A(F);if(C){E.state="body"}return C}}else{if(E.state=="header"){F.skipToEnd();E.state="headers";return"string"}else{if(E.state=="body"){if(F.sol()&&(D=F.match(/^-----END (.*)?-----\s*$/))){if(D[1]!=E.type){return"error"}E.state="end";return"tag"}else{if(F.eatWhile(/[A-Za-z0-9+\/=]/)){return null}else{F.next();return"error"}}}else{if(E.state=="end"){return A(F)}}}}}},blankLine:function(C){if(C.state=="headers"){C.state="body"}},startState:function(){return{state:"top",type:null}}}});B.defineMIME("application/pgp","asciiarmor");B.defineMIME("application/pgp-keys","asciiarmor");B.defineMIME("application/pgp-signature","asciiarmor")});