(function(A){if(typeof exports=="object"&&typeof module=="object"){A(require("../../lib/codemirror"))}else{if(typeof define=="function"&&define.amd){define(["../../lib/codemirror"],A)}else{A(CodeMirror)}}})(function(A){A.runMode=function(I,B,C,K){var M=A.getMode(A.defaults,B);var J=/MSIE \d/.test(navigator.userAgent);var P=J&&(document.documentMode==null||document.documentMode<9);if(C.appendChild){var N=(K&&K.tabSize)||A.defaults.tabSize;var L=C,Q=0;L.innerHTML="";C=function(W,V){if(W=="\n"){L.appendChild(document.createTextNode(P?"\r":W));Q=0;return}var Y="";for(var X=0;;){var S=W.indexOf("\t",X);if(S==-1){Y+=W.slice(X);Q+=W.length-X;break}else{Q+=S-X;Y+=W.slice(X,S);var T=N-Q%N;Q+=T;for(var U=0;U<T;++U){Y+=" "}X=S+1}}if(V){var R=L.appendChild(document.createElement("span"));R.className="cm-"+V.replace(/ +/g," cm-");R.appendChild(document.createTextNode(Y))}else{L.appendChild(document.createTextNode(Y))}}}var F=A.splitLines(I),G=(K&&K.state)||A.startState(M);for(var O=0,D=F.length;O<D;++O){if(O){C("\n")}var H=new A.StringStream(F[O]);if(!H.string&&M.blankLine){M.blankLine(G)}while(!H.eol()){var E=M.token(H,G);C(H.current(),E,O,H.start,G);H.start=H.pos}}}});