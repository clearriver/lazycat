(function(A){if(typeof exports=="object"&&typeof module=="object"){A(require("../../lib/codemirror"))}else{if(typeof define=="function"&&define.amd){define(["../../lib/codemirror"],A)}else{A(CodeMirror)}}})(function(F){var L={pairs:"()[]{}''\"\"",triples:"",explode:"[]{}"};var C=F.Pos;F.defineOption("autoCloseBrackets",false,function(T,S,R){if(R&&R!=F.Init){T.removeKeyMap(B);T.state.closeBrackets=null}if(S){T.state.closeBrackets=S;T.addKeyMap(B)}});function K(R,S){if(S=="pairs"&&typeof R=="string"){return R}if(typeof R=="object"&&R[S]!=null){return R[S]}return L[S]}var G=L.pairs+"`";var B={Backspace:M,Enter:J};for(var D=0;D<G.length;D++){B["'"+G.charAt(D)+"'"]=I(G.charAt(D))}function I(R){return function(S){return H(S,R)}}function N(T){var S=T.state.closeBrackets;if(!S||S.override){return S}var R=T.getModeAt(T.getCursor());return R.closeBrackets||S}function M(X){var U=N(X);if(!U||X.getOption("disableInput")){return F.Pass}var V=K(U,"pairs");var T=X.listSelections();for(var S=0;S<T.length;S++){if(!T[S].empty()){return F.Pass}var W=E(X,T[S].head);if(!W||V.indexOf(W)%2!=0){return F.Pass}}for(var S=T.length-1;S>=0;S--){var R=T[S].head;X.replaceRange("",C(R.line,R.ch-1),C(R.line,R.ch+1),"+delete")}}function J(W){var T=N(W);var U=T&&K(T,"explode");if(!U||W.getOption("disableInput")){return F.Pass}var S=W.listSelections();for(var R=0;R<S.length;R++){if(!S[R].empty()){return F.Pass}var V=E(W,S[R].head);if(!V||U.indexOf(V)%2!=0){return F.Pass}}W.operation(function(){W.replaceSelection("\n\n",null);W.execCommand("goCharLeft");S=W.listSelections();for(var X=0;X<S.length;X++){var Y=S[X].head.line;W.indentLine(Y,null,true);W.indentLine(Y+1,null,true)}})}function Q(S){var R=F.cmpPos(S.anchor,S.head)>0;return{anchor:new C(S.anchor.line,S.anchor.ch+(R?-1:1)),head:new C(S.head.line,S.head.ch+(R?1:-1))}}function H(c,Y){var d=N(c);if(!d||c.getOption("disableInput")){return F.Pass}var h=K(d,"pairs");var g=h.indexOf(Y);if(g==-1){return F.Pass}var b=K(d,"triples");var S=h.charAt(g+1)==Y;var W=c.listSelections();var Z=g%2==0;var f;for(var T=0;T<W.length;T++){var R=W[T],X=R.head,V;var a=c.getRange(X,C(X.line,X.ch+1));if(Z&&!R.empty()){V="surround"}else{if((S||!Z)&&a==Y){if(S&&A(c,X)){V="both"}else{if(b.indexOf(Y)>=0&&c.getRange(X,C(X.line,X.ch+3))==Y+Y+Y){V="skipThree"}else{V="skip"}}}else{if(S&&X.ch>1&&b.indexOf(Y)>=0&&c.getRange(C(X.line,X.ch-2),X)==Y+Y&&(X.ch<=2||c.getRange(C(X.line,X.ch-3),C(X.line,X.ch-2))!=Y)){V="addFour"}else{if(S){if(!F.isWordChar(a)&&O(c,X,Y)){V="both"}else{return F.Pass}}else{if(Z&&(c.getLine(X.line).length==X.ch||P(a,h)||/\s/.test(a))){V="both"}else{return F.Pass}}}}}if(!f){f=V}else{if(f!=V){return F.Pass}}}var e=g%2?h.charAt(g-1):Y;var U=g%2?Y:h.charAt(g+1);c.operation(function(){if(f=="skip"){c.execCommand("goCharRight")}else{if(f=="skipThree"){for(var j=0;j<3;j++){c.execCommand("goCharRight")}}else{if(f=="surround"){var k=c.getSelections();for(var j=0;j<k.length;j++){k[j]=e+k[j]+U}c.replaceSelections(k,"around");k=c.listSelections().slice();for(var j=0;j<k.length;j++){k[j]=Q(k[j])}c.setSelections(k)}else{if(f=="both"){c.replaceSelection(e+U,null);c.triggerElectric(e+U);c.execCommand("goCharLeft")}else{if(f=="addFour"){c.replaceSelection(e+e+e+e,"before");c.execCommand("goCharRight")}}}}}})}function P(T,S){var R=S.lastIndexOf(T);return R>-1&&R%2==1}function E(T,R){var S=T.getRange(C(R.line,R.ch-1),C(R.line,R.ch+1));return S.length==2?S:null}function O(W,T,U){var V=W.getLine(T.line);var R=W.getTokenAt(T);if(/\bstring2?\b/.test(R.type)||A(W,T)){return false}var X=new F.StringStream(V.slice(0,T.ch)+U+V.slice(T.ch),4);X.pos=X.start=R.start;for(;;){var S=W.getMode().token(X,R.state);if(X.pos>=T.ch+1){return/\bstring2?\b/.test(S)}X.start=X.pos}}function A(T,S){var R=T.getTokenAt(C(S.line,S.ch+1));return/\bstring/.test(R.type)&&R.start==S.ch}});