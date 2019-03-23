(function(A){if(typeof exports=="object"&&typeof module=="object"){A(require("../../lib/codemirror"))}else{if(typeof define=="function"&&define.amd){define(["../../lib/codemirror"],A)}else{A(CodeMirror)}}})(function(C){function A(H,E,I){this.orientation=E;this.scroll=I;this.screen=this.total=this.size=1;this.pos=0;this.node=document.createElement("div");this.node.className=H+"-"+E;this.inner=this.node.appendChild(document.createElement("div"));var F=this;C.on(this.inner,"mousedown",function(K){if(K.which!=1){return}C.e_preventDefault(K);var O=F.orientation=="horizontal"?"pageX":"pageY";var N=K[O],J=F.pos;function L(){C.off(document,"mousemove",M);C.off(document,"mouseup",L)}function M(P){if(P.which!=1){return L()}F.moveTo(J+(P[O]-N)*(F.total/F.size))}C.on(document,"mousemove",M);C.on(document,"mouseup",L)});C.on(this.node,"click",function(K){C.e_preventDefault(K);var J=F.inner.getBoundingClientRect(),L;if(F.orientation=="horizontal"){L=K.clientX<J.left?-1:K.clientX>J.right?1:0}else{L=K.clientY<J.top?-1:K.clientY>J.bottom?1:0}F.moveTo(F.pos+L*F.screen)});function G(J){var L=C.wheelEventPixels(J)[F.orientation=="horizontal"?"x":"y"];var K=F.pos;F.moveTo(F.pos+L);if(F.pos!=K){C.e_preventDefault(J)}}C.on(this.node,"mousewheel",G);C.on(this.node,"DOMMouseScroll",G)}A.prototype.setPos=function(E,F){if(E<0){E=0}if(E>this.total-this.screen){E=this.total-this.screen}if(!F&&E==this.pos){return false}this.pos=E;this.inner.style[this.orientation=="horizontal"?"left":"top"]=(E*(this.size/this.total))+"px";return true};A.prototype.moveTo=function(E){if(this.setPos(E)){this.scroll(E,this.orientation)}};var B=10;A.prototype.update=function(E,H,F){var G=this.screen!=H||this.total!=E||this.size!=F;if(G){this.screen=H;this.total=E;this.size=F}var I=this.screen*(this.size/this.total);if(I<B){this.size-=B-I;I=B}this.inner.style[this.orientation=="horizontal"?"width":"height"]=I+"px";this.setPos(this.pos,G)};function D(F,E,G){this.addClass=F;this.horiz=new A(F,"horizontal",G);E(this.horiz.node);this.vert=new A(F,"vertical",G);E(this.vert.node);this.width=null}D.prototype.update=function(I){if(this.width==null){var G=window.getComputedStyle?window.getComputedStyle(this.horiz.node):this.horiz.node.currentStyle;if(G){this.width=parseInt(G.height)}}var H=this.width||0;var E=I.scrollWidth>I.clientWidth+1;var F=I.scrollHeight>I.clientHeight+1;this.vert.node.style.display=F?"block":"none";this.horiz.node.style.display=E?"block":"none";if(F){this.vert.update(I.scrollHeight,I.clientHeight,I.viewHeight-(E?H:0));this.vert.node.style.bottom=E?H+"px":"0"}if(E){this.horiz.update(I.scrollWidth,I.clientWidth,I.viewWidth-(F?H:0)-I.barLeft);this.horiz.node.style.right=F?H+"px":"0";this.horiz.node.style.left=I.barLeft+"px"}return{right:F?H:0,bottom:E?H:0}};D.prototype.setScrollTop=function(E){this.vert.setPos(E)};D.prototype.setScrollLeft=function(E){this.horiz.setPos(E)};D.prototype.clear=function(){var E=this.horiz.node.parentNode;E.removeChild(this.horiz.node);E.removeChild(this.vert.node)};C.scrollbarModel.simple=function(E,F){return new D("CodeMirror-simplescroll",E,F)};C.scrollbarModel.overlay=function(E,F){return new D("CodeMirror-overlayscroll",E,F)}});