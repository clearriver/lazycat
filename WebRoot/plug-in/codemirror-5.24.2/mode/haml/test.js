(function(){var A=CodeMirror.getMode({tabSize:4,indentUnit:2},"haml");function B(C){test.mode(C,A,Array.prototype.slice.call(arguments,1))}B("elementName","[tag %h1] Hey There");B("oneElementPerLine","[tag %h1] Hey There %h2");B("idSelector","[tag %h1][attribute #test] Hey There");B("classSelector","[tag %h1][attribute .hello] Hey There");B("docType","[tag !!! XML]");B("comment","[comment / Hello WORLD]");B("notComment","[tag %h1] This is not a / comment ");B("attributes",'[tag %a]([variable title][operator =][string "test"]){[atom :title] [operator =>] [string "test"]}');B("htmlCode","[tag&bracket <][tag h1][tag&bracket >]Title[tag&bracket </][tag h1][tag&bracket >]");B("rubyBlock","[operator =][variable-2 @item]");B("selectorRubyBlock","[tag %a.selector=] [variable-2 @item]");B("nestedRubyBlock","[tag %a]",'   [operator =][variable puts] [string "test"]');B("multilinePlaintext","[tag %p]","  Hello,","  World");B("multilineRuby","[tag %p]","  [comment -# this is a comment]","     [comment and this is a comment too]","  Date/Time","  [operator -] [variable now] [operator =] [tag DateTime][operator .][property now]","  [tag %strong=] [variable now]",'  [operator -] [keyword if] [variable now] [operator >] [tag DateTime][operator .][property parse]([string "December 31, 2006"])','     [operator =][string "Happy"]','     [operator =][string "Belated"]','     [operator =][string "Birthday"]');B("multilineComment","[comment /]","  [comment Multiline]","  [comment Comment]");B("hamlComment","[comment -# this is a comment]");B("multilineHamlComment","[comment -# this is a comment]","   [comment and this is a comment too]");B("multilineHTMLComment","[comment <!--]","  [comment what a comment]","  [comment -->]");B("hamlAfterRubyTag","[attribute .block]","  [tag %strong=] [variable now]","  [attribute .test]","     [operator =][variable now]","  [attribute .right]");B("stretchedRuby",'[operator =] [variable puts] [string "Hello"],','   [string "World"]');B("interpolationInHashAttribute",'[tag %div]{[atom :id] [operator =>] [string "#{][variable test][string }_#{][variable ting][string }"]} test');B("interpolationInHTMLAttribute",'[tag %div]([variable title][operator =][string "#{][variable test][string }_#{][variable ting]()[string }"]) Test')})();