FedeDrumMachine {var s, <bpm, <>timeDiv, <arg3, <>drumArray, <>funcIndv, <>funcLoop, <mainRoutine, time, <>dataArr, <>bufferArr, <>fileArray, <>atkArray, <>relArray, <>volArray, <>startOff, <>endOff, <>count; 
		
	*new {arg bpm=60, timeDiv=1, arg3;
		^super.new.initFedeDrumMachine(bpm, timeDiv, arg3);
	}
	
	initFedeDrumMachine {arg tempo=60, timeDivision=1, argument3;
		bpm = tempo;
		timeDiv = timeDivision;
		arg3 = argument3;
	
		time = 60/bpm;
		
		s = Server.default;
		
		drumArray=[Array.fill(16,0)];
		fileArray=[0];
		atkArray=[0];
		relArray=[0];
		volArray=[1];
		startOff=[0];
		endOff=[0];
		count = [0];
		funcIndv={arg val; val.postln};
		funcLoop={};
	}


	startMain {
	var step = 0;
	mainRoutine = Routine({inf.do({
	
	drumArray[0].size.do({
	
	funcIndv.value(step);
	
	(time/timeDiv).yield;
	step = step + 1;
	});
	step = 0;
	
	funcLoop.value;
	
	})}).play;
	
	}
	
	indexGrain {arg index,file, atk=0.0, rel=0.0, vol=1, startOffset=0, endOffset=0;
	var buffer, data, whichSegment, start, end, loopStart, loopEnd;
	
	data = dataArr[file];
	buffer = bufferArr[file];
	
	start = data.flop[0];
	end = data.flop[1];
	
	loopStart = (0+startOffset);
	loopEnd =  ((start.size-1)-endOffset);
	
	whichSegment = index.wrap(loopStart, loopEnd);
	
	Synth((\fedeplaybuf++(buffer.numChannels)), [\bufnum, buffer.bufnum, \startPos,start[whichSegment.postln],\dur, end[whichSegment], \atk,atk,\rel,rel, \vol, vol]);
	}
	
	randGrain {arg file, atk=0.0, rel=0.0, vol=1, startOffset=0, endOffset=0;
	var buffer, data, whichSegment, start, end;
	
	data = dataArr[file];
	buffer = bufferArr[file];
	
	start = data.flop[0];
	end = data.flop[1];
	whichSegment = rrand(0+startOffset, (start.size-1)-endOffset);
	Synth((\fedeplaybuf++(buffer.numChannels)), [\bufnum, buffer.bufnum, \startPos,start[whichSegment.postln],\dur, end[whichSegment], \atk,atk,\rel,rel, \vol, vol]);
	}
	
	seqGrain {arg index, file, atk=0.0, rel=0.0, vol=1, startOffset=0, endOffset=0;
	this.indexGrain(count[index], file, atk, rel, vol, startOffset, endOffset);
	count[index] = count[index] + 1;
	}
	
	set {arg drumPat=[Array.fill(16,0)], fileArr=[0], atkArr=[0], relArr=[0], volArr=[1], startArr, endArr, step;
	var boolean, array;
	
	startArr ?? {startArr = startOff};
	endArr ?? {endArr = endOff};
	step ?? {step = Array.size(endArr.size, 0);};
	
	array = [drumPat.size, fileArr.size, atkArr.size, relArr.size, volArr.size, startArr.size, endArr.size];
	array.do({|item| if(item != array[0], {boolean=1}, {boolean=0}); });
	
	if(boolean == 0, {
	drumArray = drumPat;
	fileArray = fileArr;
	atkArray = atkArr;
	relArray = relArr;
	volArray = volArr;
	startOff = startArr;
	endOff = endArr;
	count = step;
	}, {
	"not equal number of files and patters".postln;
	});
	}
	
	randMachine {
	funcIndv = {arg val;
	drumArray.flop[val].do({|item, index|
	if(item.next == 1, {
	this.randGrain(fileArray[index],atkArray[index],relArray[index],volArray[index],startOff[index],endOff[index]); //plays grains, args: file, atk, rel, vol
	});
	});
	};
	}
	
	seqMachine {
	funcIndv = {arg val;
	drumArray.flop[val].do({|item, index|
	if(item.next == 1, {
	this.seqGrain(count[index], fileArray[index],atkArray[index],relArray[index],volArray[index],startOff[index],endOff[index]); //plays grains, args: file, atk, rel, vol
	});
	});
	};
	}

	*initClass {
		
		2.do({arg i;

		SynthDef.writeOnce(\fedeplaybuf++((i+1).asSymbol),{arg bufnum=0,outbus=0,rate=1,startPos=0,dur=0.1,atk=0.005, rel=0.005, curve=0, vol=1;  
		var playbuf, env;
		
		playbuf= PlayBuf.ar(i+1,bufnum,BufRateScale.kr(bufnum)*rate,1,startPos,1);
		
		env= EnvGen.ar(Env([0,1,1,0],[atk,dur-atk-rel,rel],curve),doneAction:2);
		
		Out.ar(outbus,playbuf*env*vol);
		});
		});
	}

}