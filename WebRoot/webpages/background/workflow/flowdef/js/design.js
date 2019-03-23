/**
 * 加载设计的代码
 * @param {} code
 */
function loadDesignCode(code) {
	myDiagram.model = go.Model.fromJson(code);
}


function initload() {
	//获取回填的JSON
	var defJson = $("#FLOWDEF_JSON").val();
	var jsonValue = "";
	if (defJson) {
		jsonValue = defJson;
	} else {
		jsonValue = "{\"class\":\"go.GraphLinksModel\",\"linkFromPortIdProperty\": \"fromPort\",\"linkToPortIdProperty\": \"toPort\",";
		jsonValue += "\"nodeDataArray\": [],\"linkDataArray\": []}";
	}
	myDiagram.model = go.Model.fromJson(jsonValue);
}

/**
 * 显示设计的代码
 * operType(1:预览,2:加载)
 */
function showDesignCode(operType) {
	var url = "workflow/FlowDefController.do?showDesignCode";
	if (operType == "1") {
		url += "&operType=1";
		title = "预览设计代码";
		var jsonCode = myDiagram.model.toJson();
		$("#FLOWDEF_JSON").val(jsonCode);
	} else {
		title = "导入设计代码";
		url += "&operType=2";
	}
	PlatUtil.openWindow({
		title:title,
		area: ["80%","70%"],
		content: url,
		end:function(){
		}
	});
}

/**
 * 显示节点的端点
 * @param {} node
 * @param {} show
 */
function showPorts(node, show) {
	var diagram = node.diagram;
	if (!diagram || diagram.isReadOnly || !diagram.allowLink)
		return;
	node.ports.each(function(port) {
		port.stroke = (show ? "white" : null);
	});
}

/**
 * 初始化流程图
 * @param {} isReadOnly:是否只读
 * @param {} isPalette:是否展现工具栏
 * @param {} doubleClickFunction:图元双击事件
 * @param {} singleClickFunction:图元单击事件
 */
function initFlowDesign(isReadOnly, isPalette, doubleClickFunction,singleClickFunction) {
	var $ = go.GraphObject.make; // for conciseness in defining templates
	//显示线上的标签值
	function showLinkLabel(e) {
		var label = e.subject.findObject("LABEL");
		if (label !== null) {
			label.visible = true;
			//label.visible = (e.subject.fromNode.data.figure === "Diamond");
		}
		//label.visible = (e.subject.fromNode.data.figure === "Diamond");
	}
	//定义绘图对象
	myDiagram = $(go.Diagram, "myDiagram", // must name or refer to the DIV
	// HTML element
	{
		initialContentAlignment : go.Spot.TopCenter,
		allowDrop : true,
		isReadOnly : isReadOnly,
		"grid.visible" : true,
		"LinkDrawn" : showLinkLabel,
		"LinkRelinked" : showLinkLabel,
		"animationManager.duration" : 800,
		//允许ctrl+z,ctrl+y功能
		"undoManager.isEnabled" : true
	});
	
	if(singleClickFunction){
		myDiagram.addDiagramListener("ObjectSingleClicked", function(e) {
			var part = e.subject.part;
			if (!(part instanceof go.Link)) {
				singleClickFunction.call(this, part.data);
			}
		});
	}

	myDiagram.addDiagramListener("ObjectDoubleClicked", function(e) {
		var part = e.subject.part;
		if (!(part instanceof go.Link)) {
			if (doubleClickFunction) {
				doubleClickFunction.call(this, part.data);
			}
		}
		//alert(part);
		//if (!(part instanceof go.Link)) showMessage("Clicked on " + part.data.key);
	});
	
	//初始化连线标签工具
	myDiagram.toolManager.mouseMoveTools.insertAt(0,
			new LinkLabelDraggingTool());
	//监控画图修改动作
	myDiagram.addDiagramListener("Modified", function(e) {
		var button = document.getElementById("SaveButton");
		if (button)
			button.disabled = !myDiagram.isModified;
		var idx = document.title.indexOf("*");
		if (myDiagram.isModified) {
			if (idx < 0)
				document.title += "*";
		} else {
			if (idx >= 0)
				document.title = document.title.substr(0, idx);
		}
	});
	//定义节点的样式
	function nodeStyle() {
		return [
				new go.Binding("location", "loc", go.Point.parse)
						.makeTwoWay(go.Point.stringify), {
					// the Node.location is at the center of each node
					locationSpot : go.Spot.Center,
					resizable : true,
					// isShadowed: true,
					// shadowColor: "#888",
					// handle mouse enter/leave events to show/hide the ports
					mouseEnter : function(e, obj) {
						showPorts(obj.part, true);
					},
					mouseLeave : function(e, obj) {
						showPorts(obj.part, false);
					}
				} ];
	}
	//创建一个透明的端点
	function makePort(name, spot, output, input) {
		// the port is basically just a small circle that has a white stroke
		// when it is made visible
		return $(go.Shape, "Circle", {
			fill : "transparent",
			stroke : null, // this is changed to "white" in the
			desiredSize : new go.Size(8, 8),
			alignment : spot,
			alignmentFocus : spot, // align the port on the main Shape
			portId : name, // declare this object to be a "port"
			fromSpot : spot,
			toSpot : spot, // declare where links may connect at this
			fromLinkable : output,
			toLinkable : input, // declare whether the user may draw
			cursor : "pointer" // show a different cursor to indicate
		});
	}
	//定义文本的样式
	var lightText = 'whitesmoke';
	//var lightText = 'black';
	//开始定义缺省的节点样式,定义的形状为矩形
	myDiagram.nodeTemplateMap.add("", // the default category
	$(go.Node, "Spot", nodeStyle(),
	// the main object is a Panel that surrounds a TextBlock
	// with a rectangular Shape
	$(go.Panel, "Auto", $(go.Shape, "RoundedRectangle", {
		stroke : null
	}, new go.Binding("fill", "color"),new go.Binding("figure", "figure"), new go.Binding("stroke",
			"highlight", function(v) {
				return v ? "red" : null;
			}), new go.Binding("strokeWidth", "highlight", function(v) {
		return v ? 3 : 1;
	})), $(go.TextBlock, {
		font : "bold 11pt Helvetica, Arial, sans-serif",
		stroke : lightText,
		margin : 8,
		maxSize : new go.Size(160, NaN),
		wrap : go.TextBlock.WrapFit,
		editable : true
	}, new go.Binding("text").makeTwoWay())),
	// four named ports, one on each side:
	makePort("T", go.Spot.Top, true, true), makePort("L", go.Spot.Left, true,
			true), makePort("R", go.Spot.Right, true, true), makePort("B",
			go.Spot.Bottom, true, true)));
	//开始定义开始节点的形状
	myDiagram.nodeTemplateMap.add("start", $(go.Node, "Spot", nodeStyle(), $(
			go.Panel, "Auto", $(go.Shape, "StopSign", {
				minSize : new go.Size(50, 50),
				stroke : null
			}, new go.Binding("fill", "color"),new go.Binding("stroke", "highlight", function(v) {
				return v ? "red" : null;
			}), new go.Binding("strokeWidth", "highlight", function(v) {
				return v ? 3 : 1;
			})), $(go.TextBlock, {
				font : "bold 11pt Helvetica, Arial, sans-serif",
				stroke : lightText,
				textAlign : "center",
				alignment : go.Spot.Center,
				wrap : go.TextBlock.WrapFit,
				editable : true
			}, new go.Binding("text").makeTwoWay())), makePort("T",
			go.Spot.Top, true, true), makePort("L", go.Spot.Left, true, true),
			makePort("R", go.Spot.Right, true, true), makePort("B",
					go.Spot.Bottom, true, true)));
	//开始定义结束节点的形状
	myDiagram.nodeTemplateMap.add("end", $(go.Node, "Spot", nodeStyle(), $(
			go.Panel, "Auto", $(go.Shape, "StopSign", {
				minSize : new go.Size(50, 50),
				stroke : null
			},new go.Binding("fill", "color"), new go.Binding("stroke", "highlight", function(v) {
				return v ? "red" : null;
			}), new go.Binding("strokeWidth", "highlight", function(v) {
				return v ? 3 : 1;
			})), $(go.TextBlock, "End", {
				font : "bold 11pt Helvetica, Arial, sans-serif",
				stroke : lightText,
				textAlign : "center",
				alignment : go.Spot.Center,
				wrap : go.TextBlock.WrapFit,
				editable : true
			}, new go.Binding("text").makeTwoWay())), makePort("T",
			go.Spot.Top, true, true), makePort("L", go.Spot.Left, true, true),
			makePort("R", go.Spot.Right, true, true), makePort("B",
					go.Spot.Bottom, true, true)));

	//开始定义并行节点的形状
	myDiagram.nodeTemplateMap.add("parallel", $(go.Node, "Spot", nodeStyle(),
			$(go.Panel, "Auto", $(go.Shape, "RoundedRectangle", {
				minSize : new go.Size(50, 40),
				stroke : null
			}, new go.Binding("fill", "color"),new go.Binding("stroke", "highlight", function(v) {
				return v ? "red" : null;
			}), new go.Binding("strokeWidth", "highlight", function(v) {
				return v ? 3 : 1;
			})), $(go.TextBlock, {
				font : "bold 11pt Helvetica, Arial, sans-serif",
				stroke : lightText,
				textAlign : "center",
				alignment : go.Spot.Center,
				wrap : go.TextBlock.WrapFit,
				editable : true
			}, new go.Binding("text").makeTwoWay())), makePort("T",
					go.Spot.Top, true, true), makePort("L", go.Spot.Left, true,
					true), makePort("R", go.Spot.Right, true, true), makePort(
					"B", go.Spot.Bottom, true, true)));
	//开始定义合并节点的形状
	myDiagram.nodeTemplateMap.add("join", $(go.Node, "Spot", nodeStyle(), $(
			go.Panel, "Auto", $(go.Shape, "RoundedRectangle", {
				minSize : new go.Size(50, 40),
				stroke : null
			}, new go.Binding("fill", "color"),new go.Binding("stroke", "highlight", function(v) {
				return v ? "red" : null;
			}), new go.Binding("strokeWidth", "highlight", function(v) {
				return v ? 3 : 1;
			})), $(go.TextBlock, {
				font : "bold 11pt Helvetica, Arial, sans-serif",
				stroke : lightText,
				textAlign : "center",
				alignment : go.Spot.Center,
				wrap : go.TextBlock.WrapFit,
				editable : true
			}, new go.Binding("text").makeTwoWay())), makePort("T",
			go.Spot.Top, true, true), makePort("L", go.Spot.Left, true, true),
			makePort("R", go.Spot.Right, true, true), makePort("B",
					go.Spot.Bottom, true, true)));

	//开始定义子流程节点的形状
	/*myDiagram.nodeTemplateMap.add("subProcess", $(go.Node, "Spot", nodeStyle(), $(
				go.Panel, "Auto", $(go.Shape, "SixPointedBurst", {
							minSize : new go.Size(50, 50),
							fill : "#00A9C9",
							stroke : null
						},
						new go.Binding("stroke", "highlight", function(v) { return v ? "red" : null; }),
		        		new go.Binding("strokeWidth", "highlight", function(v) { return v ? 3 : 1; })
						), $(go.TextBlock, "End", {
							font : "bold 11pt Helvetica, Arial, sans-serif",
							stroke : lightText
						}, new go.Binding("text"))),
		makePort("T", go.Spot.Top, false, true), makePort("L",
				go.Spot.Left, false, true), makePort("R", go.Spot.Right,
				false, true)));		*/

	//开始定义画线的形状
	myDiagram.linkTemplate = $(go.Link, // the whole link panel
	{
		routing : go.Link.AvoidsNodes,
		curve : go.Link.JumpOver,
		corner : 5,
		toShortLength : 4,
		relinkableFrom : true,
		relinkableTo : true,
		reshapable : true,
		resegmentable : true,
		// mouse-overs subtly highlight links:
		mouseEnter : function(e, link) {
			link.findObject("HIGHLIGHT").stroke = "rgba(30,144,255,0.2)";
		},
		mouseLeave : function(e, link) {
			link.findObject("HIGHLIGHT").stroke = "transparent";
		}
	}, new go.Binding("points").makeTwoWay(), $(go.Shape, // the highlight shape, normally transparent
	{
		isPanelMain : true,
		strokeWidth : 8,
		stroke : "transparent",
		name : "HIGHLIGHT"
	}), $(go.Shape, // the link path shape
	{
		isPanelMain : true,
		stroke : "#2F4F4F",
		strokeWidth : 2.5
	}), $(go.Shape, // the arrowhead
	{
		toArrow : "kite",
		stroke : null,
		fill : "#2F4F4F",
		scale : 2
	}), $(go.Panel, "Auto", // the link label, normally not visible
	{
		visible : false,
		name : "LABEL",
		cursor : "pointer",
		segmentIndex : 2,
		segmentFraction : 0.5
	}, new go.Binding("visible", "visible").makeTwoWay(), $(go.Shape,
			"RoundedRectangle", // the label shape
			{
				fill : null,
				stroke : null
			}), $(go.TextBlock, "说明", // the label
	{
		textAlign : "center",
		font : "10pt helvetica, arial, sans-serif",
		stroke : "#333333",
		editable : true
	}, new go.Binding("text", "text").makeTwoWay()), new go.Binding(
			"segmentOffset", "segmentOffset", go.Point.parse)
			.makeTwoWay(go.Point.stringify)));

	myDiagram.toolManager.linkingTool.temporaryLink.routing = go.Link.Orthogonal;
	myDiagram.toolManager.relinkingTool.temporaryLink.routing = go.Link.Orthogonal;
	initload(); // load an initial diagram from some JSON text
	if (isPalette) {
		myPalette = $(go.Palette, "myPalette", // must name or refer to the DIV HTML element
		{
			"animationManager.duration" : 800, // slightly longer than
			nodeTemplateMap : myDiagram.nodeTemplateMap, // share the
			model : new go.GraphLinksModel([ // specify the contents of
			// the Palette
			{
				category : "start",
				text : "开始",
				nodeType : "start",
				color:"#8C8C8C"
			}, {
				text : "任务",
				nodeType : "task",
				color:"#8C8C8C"
			}, {
				text : "判断",
				//figure : "Diamond",
				nodeType : "decision",
				color:"#8C8C8C"
			}, {
				category : "parallel",
				text : "并行",
				nodeType : "parallel",
				color:"#8C8C8C"
			}, {
				category : "join",
				text : "合并",
				nodeType : "join",
				color:"#8C8C8C"
			}, {
				category : "subprocess",
				text : "子流程",
				nodeType : "subprocess",
				color:"#8C8C8C"
			}, {
				category : "end",
				text : "结束",
				nodeType : "end",
				color:"#8C8C8C"
			} ])
		});
	}

}

/**
 * 节点靠齐
 * @param {} flag
 */
function nodeAlign(flag) {
	var sset = myDiagram.selection;
	var it = sset.iterator;
	var myArray = new Array();
	var keys = new Array();
	var i = 0;
	var index = 0;
	if (flag == "h") {
		index = 1
	}
	;
	while (it.next()) {
		if (it.value instanceof go.Node) {
			var locs = it.value.data.loc;
			var array = locs.split(" ");
			myArray[i] = parseInt(array[index]);
			keys[i] = it.value.data.key;
			i++;
		}
	}
	myArray = myArray.sort(function(a, b) {
		return a - b;
	});
	var mid = parseInt(myArray.length / 2);
	var midx = myArray[mid];
	for (var j = 0; j < keys.length; j++) {
		var curnode = myDiagram.findNodeForKey(keys[j]);
		var data = myDiagram.model.findNodeDataForKey(keys[j]);
		// if (data !== null) myDiagram.model.setDataProperty(data, "color",
		// "green");
		var list = new go.List(go.Node);
		list.add(curnode);
		if (flag == "h") {
			var xy = midx - curnode.location.y;
			myDiagram.moveParts(list, go.Point.parse("0 " + xy + ""), false);
		} else {
			myDiagram.moveParts(list, go.Point.parse("" + midx
					- curnode.location.x + " " + 0 + ""), false);
		}
	}
}

function submitFlowDef(){
	
	if(PlatUtil.isFormValid("#FLowDesignForm")){
		var url = $("#FLowDesignForm").attr("action");
		var jsonCode = myDiagram.model.toJson();
		$("#FLOWDEF_JSON").val(jsonCode);
		var svg = myDiagram.makeSvg({
			scale : 1
		});
		$("#FLOWDEF_XML").val(svg.outerHTML);
		var formData = PlatUtil.getFormEleData("FLowDesignForm");
		PlatUtil.ajaxProgress({
			url:url,
			params : formData,
			callback : function(resultJson) {
				if (resultJson.success) {
					parent.layer.alert("流程部署成功!", {
						icon : 1,
						resize : false,
						closeBtn : 0
					}, function() {
						opener.reloadFlowDefGrid();
						window.close();
					});
				} else {
					parent.layer.alert(PlatUtil.FAIL_MSG,{icon: 2,resize:false});
				}
			}
		});
	}
}

