(function(A){if(typeof exports=="object"&&typeof module=="object"){A(require("../../lib/codemirror"),require("../htmlmixed/htmlmixed"),require("../ruby/ruby"))}else{if(typeof define=="function"&&define.amd){define(["../../lib/codemirror","../htmlmixed/htmlmixed","../ruby/ruby"],A)}else{A(CodeMirror)}}})(function(A){A.defineMode("slim",function(L){var g=A.getMode(L,{name:"htmlmixed"});var m=A.getMode(L,"ruby");var X={html:g,ruby:m};var l={ruby:"ruby",javascript:"javascript",css:"text/css",sass:"text/x-sass",scss:"text/x-scss",less:"text/x-less",styl:"text/x-styl",coffee:"coffeescript",asciidoc:"text/x-asciidoc",markdown:"text/x-markdown",textile:"text/x-textile",creole:"text/x-creole",wiki:"text/x-wiki",mediawiki:"text/x-mediawiki",rdoc:"text/x-rdoc",builder:"text/x-builder",nokogiri:"text/x-nokogiri",erb:"application/x-erb"};var v=function(Ac){var Ad=[];for(var Ab in Ac){Ad.push(Ab)}return new RegExp("^("+Ad.join("|")+"):")}(l);var n={"commentLine":"comment","slimSwitch":"operator special","slimTag":"tag","slimId":"attribute def","slimClass":"attribute qualifier","slimAttribute":"attribute","slimSubmode":"keyword special","closeAttributeTag":null,"slimDoctype":null,"lineContinuation":null};var e={"{":"}","[":"]","(":")"};var W="_a-zA-Z\xC0-\xD6\xD8-\xF6\xF8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD";var d=W+"\\-0-9\xB7\u0300-\u036F\u203F-\u2040";var K=new RegExp("^[:"+W+"](?::["+d+"]|["+d+"]*)");var S=new RegExp("^[:"+W+"][:\\."+d+"]*(?=\\s*=)");var P=new RegExp("^[:"+W+"][:\\."+d+"]*");var x=/^\.-?[_a-zA-Z]+[\w\-]*/;var Z=/^#[_a-zA-Z]+[\w\-]*/;function I(Ab,Ae,Ac){var Ad=function(Ag,Af){Af.tokenize=Ae;if(Ag.pos<Ab){Ag.pos=Ab;return Ac}return Af.tokenize(Ag,Af)};return function(Ag,Af){Af.tokenize=Ad;return Ae(Ag,Af)}}function w(Ah,Ag,Ad,Ac,Ae){var Ab=Ah.current();var Af=Ab.search(Ad);if(Af>-1){Ag.tokenize=I(Ah.pos,Ag.tokenize,Ae);Ah.backUp(Ab.length-Af-Ac)}return Ae}function T(Ac,Ab){Ac.stack={parent:Ac.stack,style:"continuation",indented:Ab,tokenize:Ac.line};Ac.line=Ac.tokenize}function Y(Ab){if(Ab.line==Ab.tokenize){Ab.line=Ab.stack.tokenize;Ab.stack=Ab.stack.parent}}function o(Ac,Ab){return function(Af,Ae){Y(Ae);if(Af.match(/^\\$/)){T(Ae,Ac);return"lineContinuation"}var Ad=Ab(Af,Ae);if(Af.eol()&&Af.current().match(/(?:^|[^\\])(?:\\\\)*\\$/)){Af.backUp(1)}return Ad}}function a(Ac,Ab){return function(Af,Ae){Y(Ae);var Ad=Ab(Af,Ae);if(Af.eol()&&Af.current().match(/,$/)){T(Ae,Ac)}return Ad}}function u(Ac,Ab){return function(Af,Ae){var Ad=Af.peek();if(Ad==Ac&&Ae.rubyState.tokenize.length==1){Af.next();Ae.tokenize=Ab;return"closeAttributeTag"}else{return q(Af,Ae)}}}function O(Ad){var Ac;var Ab=function(Af,Ae){if(Ae.rubyState.tokenize.length==1&&!Ae.rubyState.context.prev){Af.backUp(1);if(Af.eatSpace()){Ae.rubyState=Ac;Ae.tokenize=Ad;return Ad(Af,Ae)}Af.next()}return q(Af,Ae)};return function(Af,Ae){Ac=Ae.rubyState;Ae.rubyState=A.startState(m);Ae.tokenize=Ab;return q(Af,Ae)}}function q(Ac,Ab){return m.token(Ac,Ab.rubyState)}function B(Ac,Ab){if(Ac.match(/^\\$/)){return"lineContinuation"}return Q(Ac,Ab)}function Q(Ac,Ab){if(Ac.match(/^#\{/)){Ab.tokenize=u("}",Ab.tokenize);return null}return w(Ac,Ab,/[^\\]#\{/,1,g.token(Ac,Ab.htmlState))}function G(Ab){return function(Ae,Ad){var Ac=B(Ae,Ad);if(Ae.eol()){Ad.tokenize=Ab}return Ac}}function D(Ad,Ac,Ab){Ac.stack={parent:Ac.stack,style:"html",indented:Ad.column()+Ab,tokenize:Ac.line};Ac.line=Ac.tokenize=Q;return null}function z(Ac,Ab){Ac.skipToEnd();return Ab.stack.style}function p(Ac,Ab){Ab.stack={parent:Ab.stack,style:"comment",indented:Ab.indented+1,tokenize:Ab.line};Ab.line=z;return z(Ac,Ab)}function t(Ac,Ab){if(Ac.eat(Ab.stack.endQuote)){Ab.line=Ab.stack.line;Ab.tokenize=Ab.stack.tokenize;Ab.stack=Ab.stack.parent;return null}if(Ac.match(P)){Ab.tokenize=N;return"slimAttribute"}Ac.next();return null}function N(Ac,Ab){if(Ac.match(/^==?/)){Ab.tokenize=j;return null}return t(Ac,Ab)}function j(Ad,Ac){var Ab=Ad.peek();if(Ab=='"'||Ab=="'"){Ac.tokenize=y(Ab,"string",true,false,t);Ad.next();return Ac.tokenize(Ad,Ac)}if(Ab=="["){return O(t)(Ad,Ac)}if(Ad.match(/^(true|false|nil)\b/)){Ac.tokenize=t;return"keyword"}return O(t)(Ad,Ac)}function k(Ad,Ac,Ab){Ad.stack={parent:Ad.stack,style:"wrapper",indented:Ad.indented+1,tokenize:Ab,line:Ad.line,endQuote:Ac};Ad.line=Ad.tokenize=t;return null}function E(Ae,Ad){if(Ae.match(/^#\{/)){Ad.tokenize=u("}",Ad.tokenize);return null}var Ab=new A.StringStream(Ae.string.slice(Ad.stack.indented),Ae.tabSize);Ab.pos=Ae.pos-Ad.stack.indented;Ab.start=Ae.start-Ad.stack.indented;Ab.lastColumnPos=Ae.lastColumnPos-Ad.stack.indented;Ab.lastColumnValue=Ae.lastColumnValue-Ad.stack.indented;var Ac=Ad.subMode.token(Ab,Ad.subState);Ae.pos=Ab.pos+Ad.stack.indented;return Ac}function R(Ac,Ab){Ab.stack.indented=Ac.column();Ab.line=Ab.tokenize=E;return Ab.tokenize(Ac,Ab)}function F(Ab){var Ac=l[Ab];var Ad=A.mimeModes[Ac];if(Ad){return A.getMode(L,Ad)}var Ae=A.modes[Ac];if(Ae){return Ae(L,{name:Ac})}return A.getMode(L,"null")}function b(Ab){if(!X.hasOwnProperty(Ab)){return X[Ab]=F(Ab)}return X[Ab]}function f(Ab,Ae){var Ad=b(Ab);var Ac=A.startState(Ad);Ae.subMode=Ad;Ae.subState=Ac;Ae.stack={parent:Ae.stack,style:"sub",indented:Ae.indented+1,tokenize:Ae.line};Ae.line=Ae.tokenize=R;return"slimSubmode"}function Aa(Ac,Ab){Ac.skipToEnd();return"slimDoctype"}function U(Ae,Ad){var Ac=Ae.peek();if(Ac=="<"){return(Ad.tokenize=G(Ad.tokenize))(Ae,Ad)}if(Ae.match(/^[|']/)){return D(Ae,Ad,1)}if(Ae.match(/^\/(!|\[\w+])?/)){return p(Ae,Ad)}if(Ae.match(/^(-|==?[<>]?)/)){Ad.tokenize=o(Ae.column(),a(Ae.column(),q));return"slimSwitch"}if(Ae.match(/^doctype\b/)){Ad.tokenize=Aa;return"keyword"}var Ab=Ae.match(v);if(Ab){return f(Ab[1],Ad)}return V(Ae,Ad)}function H(Ac,Ab){if(Ab.startOfLine){return U(Ac,Ab)}return V(Ac,Ab)}function V(Ac,Ab){if(Ac.eat("*")){Ab.tokenize=O(s);return null}if(Ac.match(K)){Ab.tokenize=s;return"slimTag"}return C(Ac,Ab)}function s(Ac,Ab){if(Ac.match(/^(<>?|><?)/)){Ab.tokenize=C;return null}return C(Ac,Ab)}function C(Ac,Ab){if(Ac.match(Z)){Ab.tokenize=C;return"slimId"}if(Ac.match(x)){Ab.tokenize=C;return"slimClass"}return c(Ac,Ab)}function c(Ac,Ab){if(Ac.match(/^([\[\{\(])/)){return k(Ab,e[RegExp.$1],c)}if(Ac.match(S)){Ab.tokenize=h;return"slimAttribute"}if(Ac.peek()=="*"){Ac.next();Ab.tokenize=O(r);return null}return r(Ac,Ab)}function h(Ac,Ab){if(Ac.match(/^==?/)){Ab.tokenize=i;return null}return c(Ac,Ab)}function i(Ad,Ac){var Ab=Ad.peek();if(Ab=='"'||Ab=="'"){Ac.tokenize=y(Ab,"string",true,false,c);Ad.next();return Ac.tokenize(Ad,Ac)}if(Ab=="["){return O(c)(Ad,Ac)}if(Ab==":"){return O(M)(Ad,Ac)}if(Ad.match(/^(true|false|nil)\b/)){Ac.tokenize=c;return"keyword"}return O(c)(Ad,Ac)}function M(Ac,Ab){Ac.backUp(1);if(Ac.match(/^[^\s],(?=:)/)){Ab.tokenize=O(M);return null}Ac.next();return c(Ac,Ab)}function y(Ac,Ad,Af,Ae,Ab){return function(Ak,Aj){Y(Aj);var Ah=Ak.current().length==0;if(Ak.match(/^\\$/,Ah)){if(!Ah){return Ad}T(Aj,Aj.indented);return"lineContinuation"}if(Ak.match(/^#\{/,Ah)){if(!Ah){return Ad}Aj.tokenize=u("}",Aj.tokenize);return null}var Ag=false,Ai;while((Ai=Ak.next())!=null){if(Ai==Ac&&(Ae||!Ag)){Aj.tokenize=Ab;break}if(Af&&Ai=="#"&&!Ag){if(Ak.eat("{")){Ak.backUp(2);break}}Ag=!Ag&&Ai=="\\"}if(Ak.eol()&&Ag){Ak.backUp(1)}return Ad}}function r(Ac,Ab){if(Ac.match(/^==?/)){Ab.tokenize=q;return"slimSwitch"}if(Ac.match(/^\/$/)){Ab.tokenize=H;return null}if(Ac.match(/^:/)){Ab.tokenize=V;return"slimSwitch"}D(Ac,Ab,0);return Ab.tokenize(Ac,Ab)}var J={startState:function(){var Ac=A.startState(g);var Ab=A.startState(m);return{htmlState:Ac,rubyState:Ab,stack:null,last:null,tokenize:H,line:H,indented:0}},copyState:function(Ab){return{htmlState:A.copyState(g,Ab.htmlState),rubyState:A.copyState(m,Ab.rubyState),subMode:Ab.subMode,subState:Ab.subMode&&A.copyState(Ab.subMode,Ab.subState),stack:Ab.stack,last:Ab.last,tokenize:Ab.tokenize,line:Ab.line}},token:function(Ad,Ac){if(Ad.sol()){Ac.indented=Ad.indentation();Ac.startOfLine=true;Ac.tokenize=Ac.line;while(Ac.stack&&Ac.stack.indented>Ac.indented&&Ac.last!="slimSubmode"){Ac.line=Ac.tokenize=Ac.stack.tokenize;Ac.stack=Ac.stack.parent;Ac.subMode=null;Ac.subState=null}}if(Ad.eatSpace()){return null}var Ab=Ac.tokenize(Ad,Ac);Ac.startOfLine=false;if(Ab){Ac.last=Ab}return n.hasOwnProperty(Ab)?n[Ab]:Ab},blankLine:function(Ab){if(Ab.subMode&&Ab.subMode.blankLine){return Ab.subMode.blankLine(Ab.subState)}},innerMode:function(Ab){if(Ab.subMode){return{state:Ab.subState,mode:Ab.subMode}}return{state:Ab,mode:J}}};return J},"htmlmixed","ruby");A.defineMIME("text/x-slim","slim");A.defineMIME("application/x-slim","slim")});