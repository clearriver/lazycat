(function(A){if(typeof exports=="object"&&typeof module=="object"){A(require("../../lib/codemirror"),require("../../addon/mode/simple"))}else{if(typeof define=="function"&&define.amd){define(["../../lib/codemirror","../../addon/mode/simple"],A)}else{A(CodeMirror)}}})(function(E){var B=["from","maintainer","run","cmd","expose","env","add","copy","entrypoint","volume","user","workdir","onbuild"],A="("+B.join("|")+")",D=new RegExp(A+"\\s*$","i"),C=new RegExp(A+"(\\s+)","i");E.defineSimpleMode("dockerfile",{start:[{regex:/#.*$/,token:"comment"},{regex:D,token:"variable-2"},{regex:C,token:["variable-2",null],next:"arguments"},{regex:/./,token:null}],arguments:[{regex:/#.*$/,token:"error",next:"start"},{regex:/[^#]+\\$/,token:null},{regex:/[^#]+/,token:null,next:"start"},{regex:/$/,token:null,next:"start"},{token:null,next:"start"}],meta:{lineComment:"#"}});E.defineMIME("text/x-dockerfile","dockerfile")});