FedePlayer {var s, <pathArr, <arg2, <arg3, <>bufferArr, <>synthArr; 
		
	*new {arg pathArr, arg2, arg3;
		^super.new.initFedePlayer(pathArr, arg2, arg3);
	}
	
	*open {
		^super.new.initFedePlayer2;
	}
	
	initFedePlayer {arg arrPath, argument2, argument3;
		pathArr = arrPath;
		arg2 = argument2;
		arg3 = argument3;
	
		s = Server.default;
		
		pathArr.do({|item| bufferArr = bufferArr.add(Buffer.read(s, item)); }); //reads paths into buffers
	}
	
	initFedePlayer2 {
		s = Server.default;
		this.addCocoa;
		
	}


	read {arg whichBuf=0, startPos=0, vol=1, rate=1, pos=0, out=0, loop=0;
	case 
	{bufferArr[whichBuf].numChannels == 1} {
	synthArr = synthArr.add(
	Synth("FedeMono", [\bufnum, bufferArr[whichBuf].bufnum, \startPos, startPos*44100, \vol, vol, \rate, rate, \pos, pos, \outbus, out, \loop, loop, \dur, ((bufferArr[whichBuf].numFrames-startPos)/44100)]);
	);
	} {
	bufferArr[whichBuf].numChannels == 2} {
	synthArr = synthArr.add(
	Synth("FedeStereo", [\bufnum, bufferArr[whichBuf].bufnum, \startPos, startPos*44100, \vol, vol, \rate, rate, \outbus, out, \loop, loop, \dur, ((bufferArr[whichBuf].numFrames-startPos)/44100)]);
	);
	};
	}
	
	play {arg whichBuf=0, startPos=0, vol=1, dur=5, rate=1, pos=0, atk=0.005, rel=0.005, curve=0, out=0, loop=0;
	case 
	{bufferArr[whichBuf].numChannels == 1} {
	synthArr = synthArr.add(
	Synth("FedeMono", [\bufnum, bufferArr[whichBuf].bufnum, \startPos, startPos, \vol, vol, \rate, rate, \pos, pos, \outbus, out, \loop, loop, \atk, atk, \rel, rel, \curve, curve, \dur, dur]);
	);
	} {
	bufferArr[whichBuf].numChannels == 2} {
	synthArr = synthArr.add(
	Synth("FedeStereo", [\bufnum, bufferArr[whichBuf].bufnum, \startPos, startPos, \vol, vol, \rate, rate, \outbus, out, \loop, loop, \atk, atk, \rel, rel, \curve, curve, \dur, dur]);
	);
	};
	}
	
	addBuffer {arg pathName;
	bufferArr = bufferArr.add(Buffer.read(s, pathName)); 
	}
	
	removeBuffer {arg index;
	bufferArr[index].free; //removes the buffer at index
	bufferArr.removeAt(0);
	}
	
	addCocoa {
	CocoaDialog.getPaths({ arg paths;
		paths.do({ arg p;
		bufferArr = bufferArr.add(Buffer.read(s, p)); 	
		})
		},{
			"cancelled".postln;
		});	
	}
	
	stop {arg whichSynth=0;
	synthArr[whichSynth].run(false);
	}
	
	resume {arg whichSynth=0;
	synthArr[whichSynth].run(true);
	}
		
	free {arg whichSynth=0;
	synthArr[whichSynth].free;
	synthArr.removeAt(whichSynth);
	}
	
	clearAllSynths {
	synthArr.do({|item| item.free});
	synthArr = [];
	}
	
	clearAllBuffers {
	bufferArr.do({|item| item.free});
	bufferArr = [];
	}
	
	readNode {arg whichBuf=0, startPos=0, vol=1, rate=1, pos=0, out=0, loop=0;
	var extraArgs;
	extraArgs = [\bufnum, bufferArr[whichBuf].bufnum, \startPos, startPos*44100, \vol, vol, \rate, rate, \pos, pos, \outbus, out, \loop, loop, \dur, ((bufferArr[whichBuf].numFrames-startPos)/44100)];
	^extraArgs;
	}
	
	playNode {arg whichBuf=0, startPos=0, vol=1, dur=5, rate=1, pos=0, atk=0.005, rel=0.005, curve=0, out=0, loop=0;
	var extraArgs;
	extraArgs = [\bufnum, bufferArr[whichBuf].bufnum, \startPos, startPos, \vol, vol, \rate, rate, \pos, pos, \outbus, out, \loop, loop, \atk, atk, \rel, rel, \curve, curve, \dur, dur];
	^extraArgs;
	}

	*initClass {

	SynthDef.writeOnce("FedeStereo", {arg bufnum=0,outbus=0,rate=1,startPos=0,dur=0.1,atk=0.005, rel=0.005, curve=0, vol=0, loop=0;  
		var playbuf, env;
		
		playbuf= PlayBuf.ar(2,bufnum,BufRateScale.kr(bufnum)*rate,1.0,startPos,loop);
		
		env= EnvGen.ar(Env([0,1,1,0],[atk,dur-atk-rel,rel],curve),doneAction:2);
		
		Out.ar(outbus,(playbuf*env*vol));
		});
	
	SynthDef.writeOnce("FedeMono", {arg bufnum=0,outbus=0,rate=1,startPos=0,dur=0.1,atk=0.005, rel=0.005, curve=0, vol=0, pos=0, loop=0;  
		var playbuf, env;
		
		playbuf= PlayBuf.ar(1,bufnum,BufRateScale.kr(bufnum)*rate,1.0,startPos,loop);
		
		env= EnvGen.ar(Env([0,1,1,0],[atk,dur-atk-rel,rel],curve),doneAction:2);
		
		Out.ar(outbus,Pan2.ar(playbuf*env, pos, vol));
		});
	
	}

}