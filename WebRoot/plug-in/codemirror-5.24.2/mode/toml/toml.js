(function(A){if(typeof exports=="object"&&typeof module=="object"){A(require("../../lib/codemirror"))}else{if(typeof define=="function"&&define.amd){define(["../../lib/codemirror"],A)}else{A(CodeMirror)}}})(function(A){A.defineMode("toml",function(){return{startState:function(){return{inString:false,stringType:"",lhs:true,inArray:0}},token:function(C,B){if(!B.inString&&((C.peek()=='"')||(C.peek()=="'"))){B.stringType=C.peek();C.next();B.inString=true}if(C.sol()&&B.inArray===0){B.lhs=true}if(B.inString){while(B.inString&&!C.eol()){if(C.peek()===B.stringType){C.next();B.inString=false}else{if(C.peek()==="\\"){C.next();C.next()}else{C.match(/^.[^\\\"\']*/)}}}return B.lhs?"property string":"string"}else{if(B.inArray&&C.peek()==="]"){C.next();B.inArray--;return"bracket"}else{if(B.lhs&&C.peek()==="["&&C.skipTo("]")){C.next();if(C.peek()==="]"){C.next()}return"atom"}else{if(C.peek()==="#"){C.skipToEnd();return"comment"}else{if(C.eatSpace()){return null}else{if(B.lhs&&C.eatWhile(function(D){return D!="="&&D!=" "})){return"property"}else{if(B.lhs&&C.peek()==="="){C.next();B.lhs=false;return null}else{if(!B.lhs&&C.match(/^\d\d\d\d[\d\-\:\.T]*Z/)){return"atom"}else{if(!B.lhs&&(C.match("true")||C.match("false"))){return"atom"}else{if(!B.lhs&&C.peek()==="["){B.inArray++;C.next();return"bracket"}else{if(!B.lhs&&C.match(/^\-?\d+(?:\.\d+)?/)){return"number"}else{if(!C.eatSpace()){C.next()}}}}}}}}}}}}return null}}});A.defineMIME("text/x-toml","toml")});