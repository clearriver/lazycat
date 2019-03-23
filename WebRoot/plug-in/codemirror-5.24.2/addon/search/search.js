(function(A){if(typeof exports=="object"&&typeof module=="object"){A(require("../../lib/codemirror"),require("./searchcursor"),require("../dialog/dialog"))}else{if(typeof define=="function"&&define.amd){define(["../../lib/codemirror","./searchcursor","../dialog/dialog"],A)}else{A(CodeMirror)}}})(function(H){function E(W,V){if(typeof W=="string"){W=new RegExp(W.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g,"\\$&"),V?"gi":"g")}else{if(!W.global){W=new RegExp(W.source,W.ignoreCase?"gi":"g")}}return{token:function(Y){W.lastIndex=Y.pos;var X=W.exec(Y.string);if(X&&X.index==Y.pos){Y.pos+=X[0].length||1;return"searching"}else{if(X){Y.pos=X.index}else{Y.skipToEnd()}}}}}function D(){this.posFrom=this.posTo=this.lastQuery=this.query=null;this.overlay=null}function Q(V){return V.state.search||(V.state.search=new D())}function O(V){return typeof V=="string"&&V==V.toLowerCase()}function C(X,W,V){return X.getSearchCursor(W,V,O(W))}function T(Z,X,W,Y,V){Z.openDialog(X,Y,{value:W,selectValueOnOpen:true,closeOnEnter:false,onClose:function(){K(Z)},onKeyDown:V})}function F(Y,X,W,V,Z){if(Y.openDialog){Y.openDialog(X,Z,{value:V,selectValueOnOpen:true})}else{Z(prompt(W,V))}}function S(Y,X,W,V){if(Y.openConfirm){Y.openConfirm(X,V)}else{if(confirm(W)){V[0]()}}}function U(V){return V.replace(/\\(.)/g,function(X,W){if(W=="n"){return"\n"}if(W=="r"){return"\r"}return W})}function M(W){var X=W.match(/^\/(.*)\/([a-z]*)$/);if(X){try{W=new RegExp(X[1],X[2].indexOf("i")==-1?"":"i")}catch(V){}}else{W=U(W)}if(typeof W=="string"?W=="":W.test("")){W=/x^/}return W}var P='Search: <input type="text" style="width: 10em" class="CodeMirror-search-field"/> <span style="color: #888" class="CodeMirror-search-hint">(Use /re/ syntax for regexp search)</span>';function J(W,X,V){X.queryText=V;X.query=M(V);W.removeOverlay(X.overlay,O(X.query));X.overlay=E(X.query,O(X.query));W.addOverlay(X.overlay);if(W.showMatchesOnScrollbar){if(X.annotate){X.annotate.clear();X.annotate=null}X.annotate=W.showMatchesOnScrollbar(X.query,O(X.query))}}function L(W,c,b,X){var Y=Q(W);if(Y.query){return N(W,c)}var V=W.getSelection()||Y.lastQuery;if(b&&W.openDialog){var a=null;var Z=function(d,e){H.e_stop(e);if(!d){return}if(d!=Y.queryText){J(W,Y,d);Y.posFrom=Y.posTo=W.getCursor()}if(a){a.style.opacity=1}N(W,e.shiftKey,function(h,f){var g;if(f.line<3&&document.querySelector&&(g=W.display.wrapper.querySelector(".CodeMirror-dialog"))&&g.getBoundingClientRect().bottom-4>W.cursorCoords(f,"window").top){(a=g).style.opacity=0.4}})};T(W,P,V,Z,function(g,d){var e=H.keyName(g);var f=H.keyMap[W.getOption("keyMap")][e];if(!f){f=W.getOption("extraKeys")[e]}if(f=="findNext"||f=="findPrev"||f=="findPersistentNext"||f=="findPersistentPrev"){H.e_stop(g);J(W,Q(W),d);W.execCommand(f)}else{if(f=="find"||f=="findPersistent"){H.e_stop(g);Z(d,g)}}});if(X&&V){J(W,Y,V);N(W,c)}}else{F(W,P,"Search for:",V,function(d){if(d&&!Y.query){W.operation(function(){J(W,Y,d);Y.posFrom=Y.posTo=W.getCursor();N(W,c)})}})}}function N(X,W,V){X.operation(function(){var Z=Q(X);var Y=C(X,Z.query,W?Z.posFrom:Z.posTo);if(!Y.find(W)){Y=C(X,Z.query,W?H.Pos(X.lastLine()):H.Pos(X.firstLine(),0));if(!Y.find(W)){return}}X.setSelection(Y.from(),Y.to());X.scrollIntoView({from:Y.from(),to:Y.to()},20);Z.posFrom=Y.from();Z.posTo=Y.to();if(V){V(Y.from(),Y.to())}})}function K(V){V.operation(function(){var W=Q(V);W.lastQuery=W.query;if(!W.query){return}W.query=W.queryText=null;V.removeOverlay(W.overlay);if(W.annotate){W.annotate.clear();W.annotate=null}})}var A=' <input type="text" style="width: 10em" class="CodeMirror-search-field"/> <span style="color: #888" class="CodeMirror-search-hint">(Use /re/ syntax for regexp search)</span>';var I='With: <input type="text" style="width: 10em" class="CodeMirror-search-field"/>';var R="Replace? <button>Yes</button> <button>No</button> <button>All</button> <button>Stop</button>";function G(X,W,V){X.operation(function(){for(var Y=C(X,W);Y.findNext();){if(typeof W!="string"){var Z=X.getRange(Y.from(),Y.to()).match(W);Y.replace(V.replace(/\$(\d)/g,function(b,a){return Z[a]}))}else{Y.replace(V)}}})}function B(Y,V){if(Y.getOption("readOnly")){return}var X=Y.getSelection()||Q(Y).lastQuery;var W=V?"Replace all:":"Replace:";F(Y,W+A,W,X,function(Z){if(!Z){return}Z=M(Z);F(Y,I,"Replace with:","",function(c){c=U(c);if(V){G(Y,Z,c)}else{K(Y);var a=C(Y,Z,Y.getCursor("from"));var b=function(){var f=a.from(),e;if(!(e=a.findNext())){a=C(Y,Z);if(!(e=a.findNext())||(f&&a.from().line==f.line&&a.from().ch==f.ch)){return}}Y.setSelection(a.from(),a.to());Y.scrollIntoView({from:a.from(),to:a.to()});S(Y,R,"Replace?",[function(){d(e)},b,function(){G(Y,Z,c)}])};var d=function(e){a.replace(typeof Z=="string"?c:c.replace(/\$(\d)/g,function(g,f){return e[f]}));b()};b()}})})}H.commands.find=function(V){K(V);L(V)};H.commands.findPersistent=function(V){K(V);L(V,false,true)};H.commands.findPersistentNext=function(V){L(V,false,true,true)};H.commands.findPersistentPrev=function(V){L(V,true,true,true)};H.commands.findNext=L;H.commands.findPrev=function(V){L(V,true)};H.commands.clearSearch=K;H.commands.replace=B;H.commands.replaceAll=function(V){B(V,true)}});