function createStars() {
	var stars = 800;
	
	for (var i = 0; i < stars; i++) {
		var star = $('<canvas/>',{'class':'star'})
			.width(32)
			.height(32)
			.attr('id', 's' + i);
			
		var variable = Math.random();
		var list = $('<li/>',{'class':'layer'}).attr('data-depth', variable);
		
		list.append(star);
		
		$("#scene").append(list);
			
		var c=document.getElementById("s" + i);
		c.style.position = "absolute";
		var l = Math.floor(Math.random() * ($(window).width() + 512)) - 256;
		var t = Math.floor(Math.random() * ($(window).height() + 64));
		//var l = (Math.floor((Math.random() * ($(window).width() - 64)) + 16));
		//var t = (Math.floor((Math.random() * ($(window).height() - 64)) + 16));
		c.style.left = "" + l + "px";
		c.style.top = "" + t + "px";
		c.style.zIndex = "-1";
		var uwotm8 = 'blur(' + (1 + (1 - variable)) + 'px) brightness(' + (3 * (variable + 0.4)) + ')';
		c.style.webkitFilter = uwotm8;
		c.style.filter = uwotm8;
		
		
		var ctx=c.getContext("2d");
		var x = 8;
		var y = 8;
		var w = 16;
		var h = 16;
		var cx = x + 0.5 * w;
		var cy = y + 0.5 * h;
		ctx.translate(cx, cy);              //translate to center of shape
		ctx.rotate( (Math.PI / 180) * (Math.floor((Math.random() * 360) + 1)));  //rotate
		ctx.translate(-cx, -cy);            //translate center back to 0,0
		ctx.fillStyle="#404040";
		ctx.fillRect(x, y, w, h);
	}
}

var percent_l = 1;
var percent_t = 1;
var ow;
var oh;
function offsetStars() {
	$("#scene").each(function() {
		$(this).find('canvas').each(function() {
			var ele = $(this)[0];
			var c=document.getElementById(ele.id);	
			var left = c.style.left;
			var top = c.style.top;
			top = top.substring(0, top.length - 2);
			left = left.substring(0, left.length - 2);
			var why = "" + (left * percent_l) + "px"
			var why2 = "" + (top * percent_t) + "px";
			c.style.left = why;
			c.style.top = why2;
		});
	});
}

$( document ).ready(function() {
	createStars();
	$("#scene").parallax();
	ow = $(window).width();
	oh = $(window).height();
	
	$(window).resize(function() {
		percent_l = $(window).width() / ow;
		percent_t = $(window).height() / oh;
		
		console.log(percent_l);
		console.log(percent_t);
		
		offsetStars();
		
		ow = $(window).width();
		oh = $(window).height();
	});
});