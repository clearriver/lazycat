(function(A){if(typeof exports=="object"&&typeof module=="object"){A(require("../../lib/codemirror"))}else{if(typeof define=="function"&&define.amd){define(["../../lib/codemirror"],A)}else{A(CodeMirror)}}})(function(C){function A(I){var F={},D=I.split(",");for(var E=0;E<D.length;++E){var H=D[E].toUpperCase();var G=D[E].charAt(0).toUpperCase()+D[E].slice(1);F[D[E]]=true;F[H]=true;F[G]=true}return F}function B(D){D.eatWhile(/[\w\$_]/);return"meta"}C.defineMode("vhdl",function(D,L){var E=D.indentUnit,S=L.atoms||A("null"),K=L.hooks||{"`":B,"$":B},R=L.multiLineStrings;var G=A("abs,access,after,alias,all,and,architecture,array,assert,attribute,begin,block,body,buffer,bus,case,component,configuration,constant,disconnect,downto,else,elsif,end,end block,end case,end component,end for,end generate,end if,end loop,end process,end record,end units,entity,exit,file,for,function,generate,generic,generic map,group,guarded,if,impure,in,inertial,inout,is,label,library,linkage,literal,loop,map,mod,nand,new,next,nor,null,of,on,open,or,others,out,package,package body,port,port map,postponed,procedure,process,pure,range,record,register,reject,rem,report,return,rol,ror,select,severity,signal,sla,sll,sra,srl,subtype,then,to,transport,type,unaffected,units,until,use,variable,wait,when,while,with,xnor,xor");var Q=A("architecture,entity,begin,case,port,else,elsif,end,for,function,if");var O=/[&|~><!\)\(*#%@+\/=?\:;}{,\.\^\-\[\]]/;var J;function H(X,W){var V=X.next();if(K[V]){var U=K[V](X,W);if(U!==false){return U}}if(V=='"'){W.tokenize=N(V);return W.tokenize(X,W)}if(V=="'"){W.tokenize=P(V);return W.tokenize(X,W)}if(/[\[\]{}\(\),;\:\.]/.test(V)){J=V;return null}if(/[\d']/.test(V)){X.eatWhile(/[\w\.']/);return"number"}if(V=="-"){if(X.eat("-")){X.skipToEnd();return"comment"}}if(O.test(V)){X.eatWhile(O);return"operator"}X.eatWhile(/[\w\$_]/);var T=X.current();if(G.propertyIsEnumerable(T.toLowerCase())){if(Q.propertyIsEnumerable(T)){J="newstatement"}return"keyword"}if(S.propertyIsEnumerable(T)){return"atom"}return"variable"}function P(T){return function(Y,W){var V=false,X,U=false;while((X=Y.next())!=null){if(X==T&&!V){U=true;break}V=!V&&X=="--"}if(U||!(V||R)){W.tokenize=H}return"string"}}function N(T){return function(Y,W){var V=false,X,U=false;while((X=Y.next())!=null){if(X==T&&!V){U=true;break}V=!V&&X=="--"}if(U||!(V||R)){W.tokenize=H}return"string-2"}}function F(X,W,T,V,U){this.indented=X;this.column=W;this.type=T;this.align=V;this.prev=U}function M(V,U,T){return V.context=new F(V.indented,U,T,null,V.context)}function I(U){var T=U.context.type;if(T==")"||T=="]"||T=="}"){U.indented=U.context.indented}return U.context=U.context.prev}return{startState:function(T){return{tokenize:null,context:new F((T||0)-E,0,"top",false),indented:0,startOfLine:true}},token:function(W,U){var V=U.context;if(W.sol()){if(V.align==null){V.align=false}U.indented=W.indentation();U.startOfLine=true}if(W.eatSpace()){return null}J=null;var T=(U.tokenize||H)(W,U);if(T=="comment"||T=="meta"){return T}if(V.align==null){V.align=true}if((J==";"||J==":")&&V.type=="statement"){I(U)}else{if(J=="{"){M(U,W.column(),"}")}else{if(J=="["){M(U,W.column(),"]")}else{if(J=="("){M(U,W.column(),")")}else{if(J=="}"){while(V.type=="statement"){V=I(U)}if(V.type=="}"){V=I(U)}while(V.type=="statement"){V=I(U)}}else{if(J==V.type){I(U)}else{if(V.type=="}"||V.type=="top"||(V.type=="statement"&&J=="newstatement")){M(U,W.column(),"statement")}}}}}}}U.startOfLine=false;return T},indent:function(X,U){if(X.tokenize!=H&&X.tokenize!=null){return 0}var W=U&&U.charAt(0),T=X.context,V=W==T.type;if(T.type=="statement"){return T.indented+(W=="{"?0:E)}else{if(T.align){return T.column+(V?0:1)}else{return T.indented+(V?0:E)}}},electricChars:"{}"}});C.defineMIME("text/x-vhdl","vhdl")});