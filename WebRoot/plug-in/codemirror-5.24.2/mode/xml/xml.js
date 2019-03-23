(function(A){if(typeof exports=="object"&&typeof module=="object"){A(require("../../lib/codemirror"))}else{if(typeof define=="function"&&define.amd){define(["../../lib/codemirror"],A)}else{A(CodeMirror)}}})(function(C){var B={autoSelfClosers:{"area":true,"base":true,"br":true,"col":true,"command":true,"embed":true,"frame":true,"hr":true,"img":true,"input":true,"keygen":true,"link":true,"meta":true,"param":true,"source":true,"track":true,"wbr":true,"menuitem":true},implicitlyClosed:{"dd":true,"li":true,"optgroup":true,"option":true,"p":true,"rp":true,"rt":true,"tbody":true,"td":true,"tfoot":true,"th":true,"tr":true},contextGrabbers:{"dd":{"dd":true,"dt":true},"dt":{"dd":true,"dt":true},"li":{"li":true},"option":{"option":true,"optgroup":true},"optgroup":{"optgroup":true},"p":{"address":true,"article":true,"aside":true,"blockquote":true,"dir":true,"div":true,"dl":true,"fieldset":true,"footer":true,"form":true,"h1":true,"h2":true,"h3":true,"h4":true,"h5":true,"h6":true,"header":true,"hgroup":true,"hr":true,"menu":true,"nav":true,"ol":true,"p":true,"pre":true,"section":true,"table":true,"ul":true},"rp":{"rp":true,"rt":true},"rt":{"rp":true,"rt":true},"tbody":{"tbody":true,"tfoot":true},"td":{"td":true,"th":true},"tfoot":{"tbody":true},"th":{"td":true,"th":true},"thead":{"tbody":true,"tfoot":true},"tr":{"tr":true}},doNotIndent:{"pre":true},allowUnquoted:true,allowMissing:true,caseFold:true};var A={autoSelfClosers:{},implicitlyClosed:{},contextGrabbers:{},doNotIndent:{},allowUnquoted:false,allowMissing:false,caseFold:false};C.defineMode("xml",function(W,S){var T=W.indentUnit;var D={};var Q=S.htmlMode?B:A;for(var Y in Q){D[Y]=Q[Y]}for(var Y in S){D[Y]=S[Y]}var a,O;function F(g,f){function d(h){f.tokenize=h;return h(g,f)}var e=g.next();if(e=="<"){if(g.eat("!")){if(g.eat("[")){if(g.match("CDATA[")){return d(I("atom","]]>"))}else{return null}}else{if(g.match("--")){return d(I("comment","-->"))}else{if(g.match("DOCTYPE",true,true)){g.eatWhile(/[\w\._\-]/);return d(X(1))}else{return null}}}}else{if(g.eat("?")){g.eatWhile(/[\w\._\-]/);f.tokenize=I("meta","?>");return"meta"}else{a=g.eat("/")?"closeTag":"openTag";f.tokenize=M;return"tag bracket"}}}else{if(e=="&"){var c;if(g.eat("#")){if(g.eat("x")){c=g.eatWhile(/[a-fA-F\d]/)&&g.eat(";")}else{c=g.eatWhile(/[\d]/)&&g.eat(";")}}else{c=g.eatWhile(/[\w\.\-:]/)&&g.eat(";")}return c?"atom":"error"}else{g.eatWhile(/[^&<]/);return null}}}F.isInText=true;function M(f,e){var d=f.next();if(d==">"||(d=="/"&&f.eat(">"))){e.tokenize=F;a=d==">"?"endTag":"selfcloseTag";return"tag bracket"}else{if(d=="="){a="equals";return null}else{if(d=="<"){e.tokenize=F;e.state=P;e.tagName=e.tagStart=null;var c=e.tokenize(f,e);return c?c+" tag error":"tag error"}else{if(/[\'\"]/.test(d)){e.tokenize=R(d);e.stringStartCol=f.column();return e.tokenize(f,e)}else{f.match(/^[^\s\u00a0=<>\"\']*[^\s\u00a0=<>\"\'\/]/);return"word"}}}}}function R(c){var d=function(f,e){while(!f.eol()){if(f.next()==c){e.tokenize=M;break}}return"string"};d.isInAttribute=true;return d}function I(d,c){return function(f,e){while(!f.eol()){if(f.match(c)){e.tokenize=F;break}f.next()}return d}}function X(c){return function(f,e){var d;while((d=f.next())!=null){if(d=="<"){e.tokenize=X(c+1);return e.tokenize(f,e)}else{if(d==">"){if(c==1){e.tokenize=F;break}else{e.tokenize=X(c-1);return e.tokenize(f,e)}}}}return"meta"}}function J(e,c,d){this.prev=e.context;this.tagName=c;this.indent=e.indented;this.startOfLine=d;if(D.doNotIndent.hasOwnProperty(c)||(e.context&&e.context.noIndent)){this.noIndent=true}}function U(c){if(c.context){c.context=c.context.prev}}function H(e,d){var c;while(true){if(!e.context){return}c=e.context.tagName;if(!D.contextGrabbers.hasOwnProperty(c)||!D.contextGrabbers[c].hasOwnProperty(d)){return}U(e)}}function P(c,e,d){if(c=="openTag"){d.tagStart=e.column();return N}else{if(c=="closeTag"){return E}else{return P}}}function N(c,e,d){if(c=="word"){d.tagName=e.current();O="tag";return L}else{O="error";return N}}function E(c,f,e){if(c=="word"){var d=f.current();if(e.context&&e.context.tagName!=d&&D.implicitlyClosed.hasOwnProperty(e.context.tagName)){U(e)}if((e.context&&e.context.tagName==d)||D.matchClosing===false){O="tag";return Z}else{O="tag error";return G}}else{O="error";return G}}function Z(c,d,e){if(c!="endTag"){O="error";return Z}U(e);return P}function G(c,e,d){O="error";return Z(c,e,d)}function L(c,f,g){if(c=="word"){O="attribute";return b}else{if(c=="endTag"||c=="selfcloseTag"){var d=g.tagName,e=g.tagStart;g.tagName=g.tagStart=null;if(c=="selfcloseTag"||D.autoSelfClosers.hasOwnProperty(d)){H(g,d)}else{H(g,d);g.context=new J(g,d,e==g.indented)}return P}}O="error";return L}function b(c,e,d){if(c=="equals"){return K}if(!D.allowMissing){O="error"}return L(c,e,d)}function K(c,e,d){if(c=="string"){return V}if(c=="word"&&D.allowUnquoted){O="string";return L}O="error";return L(c,e,d)}function V(c,e,d){if(c=="string"){return V}return L(c,e,d)}return{startState:function(c){var d={tokenize:F,state:P,indented:c||0,tagName:null,tagStart:null,context:null};if(c!=null){d.baseIndent=c}return d},token:function(e,d){if(!d.tagName&&e.sol()){d.indented=e.indentation()}if(e.eatSpace()){return null}a=null;var c=d.tokenize(e,d);if((c||a)&&c!="comment"){O=null;d.state=d.state(a||c,e,d);if(O){c=O=="error"?c+" error":O}}return c},indent:function(h,e,f){var d=h.context;if(h.tokenize.isInAttribute){if(h.tagStart==h.indented){return h.stringStartCol+1}else{return h.indented+T}}if(d&&d.noIndent){return C.Pass}if(h.tokenize!=M&&h.tokenize!=F){return f?f.match(/^(\s*)/)[0].length:0}if(h.tagName){if(D.multilineTagIndentPastTag!==false){return h.tagStart+h.tagName.length+2}else{return h.tagStart+T*(D.multilineTagIndentFactor||1)}}if(D.alignCDATA&&/<!\[CDATA\[/.test(e)){return 0}var c=e&&/^<(\/)?([\w_:\.-]*)/.exec(e);if(c&&c[1]){while(d){if(d.tagName==c[2]){d=d.prev;break}else{if(D.implicitlyClosed.hasOwnProperty(d.tagName)){d=d.prev}else{break}}}}else{if(c){while(d){var g=D.contextGrabbers[d.tagName];if(g&&g.hasOwnProperty(c[2])){d=d.prev}else{break}}}}while(d&&d.prev&&!d.startOfLine){d=d.prev}if(d){return d.indent+T}else{return h.baseIndent||0}},electricInput:/<\/[\s\w:]+>$/,blockCommentStart:"<!--",blockCommentEnd:"-->",configuration:D.htmlMode?"html":"xml",helperType:D.htmlMode?"html":"xml",skipAttribute:function(c){if(c.state==K){c.state=L}}}});C.defineMIME("text/xml","xml");C.defineMIME("application/xml","xml");if(!C.mimeModes.hasOwnProperty("text/html")){C.defineMIME("text/html",{name:"xml",htmlMode:true})}});