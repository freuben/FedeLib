MouseMap {var s, o, minvalX, maxvalX, warpX, minvalY, maxvalY, warpY, synth;

	*new {arg minvalX=0, maxvalX=1, warpX=0, minvalY=0, maxvalY=1, warpY=0;
		if (warpX === \linear, { warpX = 0 });
		if (warpX === \exponential, { warpX = 1 });
		if (warpY === \linear, { warpY = 0 });
		if (warpY === \exponential, { warpY = 1 });

	
	^super.new.initMouseMap(minvalX, maxvalX, warpX, minvalY, maxvalY, warpY);
	}
	
	initMouseMap {arg minX=0, maxX=1, lineX=0, minY=0, maxY=1, lineY=0;
	minvalX = minX;
	maxvalX = maxX;
	warpX = lineX;
	minvalY = minY;
	maxvalY = maxY;
	warpY = lineY;
	s = Server.default;
	o = OSCresponderNode(s.addr,'/tr',{ arg time,responder,msg;
	if(msg[2] == 0, {
	'MouseX: '.post; msg[3].postln;}, {'MouseY: '.post; msg[3].postln;
	});
}).add;
	}

	start {
	synth = Synth("mouseInfo", [\minX, minvalX, \maxX, maxvalX, \warpX, warpX, \minY, minvalY, \maxY, maxvalY, \warpY, warpY]);
	}
	
	stop {
	o.remove;
	synth.free;
	}
	
	*initClass {
	SynthDef.writeOnce("mouseInfo",{arg minX=0, maxX=1, warpX=0, minY=0, maxY=1, warpY=0;
	SendTrig.kr(MouseButton.kr,0,MouseX.kr(minX, maxX, warpX));
	SendTrig.kr(MouseButton.kr,1,MouseY.kr(minY, maxY, warpY));
})
	}
	
}
