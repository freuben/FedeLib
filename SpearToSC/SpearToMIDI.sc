SpearToMIDI : SpearToSC {

		var <>threshAmp=0.01, <>frequencyMod=1.0, <>dataGliss, <>dataNote, <>m;
		var <>x1, <>x2, <>x3, <>x4, <>x5, <>x6, <>x7, <>x8, <>x9, <>x10;
		var <>freqArray, <>ampArray, <>startTimeArray, <>durArray, <>oscArray, <>timesArr;
		var <>noteNumber, <>velo, <>startTime, <>dur, <loud, <infoArray, <infoArrayPar, <>midiNoteArr;
		
		
		threshGroups {arg ampthresh = 0.01, partialnumber=partialNum;
		var b,c,e,f,g,h,i,j,k,l,m;
		partialNum = partialnumber;
		threshAmp = ampthresh;
		if(this.partial(partialnumber) == nil, {'not any more partials'.postln}, {
		b = this.partial(partialnumber);
		c = [];
		b.do({|item, index| if(index < (b.size-1), {c = c.add((b[index+1][1]-item[1])) })});
		c.insert(0,0);
		e = b.flop ++ [c];
		e = e.flop;
		
		e.do({|item| if(item[2] < ampthresh, {item[2] = 'silence'; item[3] = 'silence'}, {item[2]}); });
		
		if(e.flop[2].every({|item| item == \silence}), {^nil}, 
		{
		
		
		e.do({|item| if(item[3] < 0, {item[3] = 1}, {item[3] = 2})});
		
		f = e.flop[3].insert(0, e.flop[3][0]);
		
		g = [];
		h = [];
		e.do({|item, index| if(index < (e.size-1), {
							 if(item[3] !=  f[index], {
							 	if(e[index-1][2] == \silence, {
							 		g = g.add(index);
							 		h = h.add(index);
							 		}, {
							 		g = g.add(index-1); 
							 	});
							 });
						   });
		});
		
		
		i = [];
		
		h.do({|item| i = i.add(g.indexOf(item)) });
		
		i = i.add(g.size-1);
		if(i[0] != 0, {i = i.insert(0,0)});
		
		j = [];
		i.do({|item, index| if(index < (i.size-1), {j = j.add(g.copyRange(item, i[index+1]-1));}); });
		
		j.do({|item, index| if((item[0] == item[1]).and(item.full), {item.removeAt(0)})});
		
		if(g.full, {k = j[j.size-1] ++ [g[g.size-1]];}, {k = j[j.size-1] ++ g;});
		l = j ++ [k];
		
		if(l.size >= 2, {l.removeAt(l.size-2);});
		m = Array.fill(l.size,[]);
		l.do({|item, index| l[index].do({|item| m[index] = m[index].add( e[item])}); }); 
		m.do({|item, index| if(index > 0, {item.removeAt(0)})});
		^m;
		});
		});
		}	
		
		threshMIDIMod {arg freqMod=1.0, ampthresh=threshAmp, partialnumber=partialNum;
		var m, n;
		partialNum = partialnumber;
		threshAmp = ampthresh;
		frequencyMod = freqMod;
		m = this.threshGroups(ampthresh, partialnumber);
		n = Array.fill(m.size,[]);
		m.do({|item, index| m[index].do({|item|  n[index] = n[index].add([item[0], item[1].cpsmidi.round(freqMod), item[2]]) });});
		n.do({|item, index| if(index > 0, {item.removeAt(0)})});
		^n;
		}
		
		noteMIDI {arg freqMod=frequencyMod, ampthresh=threshAmp, partialnumber=partialNum;
		var step, count, e, f, g, h;
		partialNum = partialnumber;
		threshAmp = ampthresh;
		frequencyMod = freqMod;
		e = this.	threshMIDIMod(freqMod, ampthresh, partialnumber);
		count = 0;
		e.size.do({
		f = [0] ++ e[count].flop[1];
		g = [];
		step = 0;
		e[count].do({|item, index| if(f[step] != item[1], {g = g.add(index);}); step = step + 1;  });
		g = g.reject({|item| item < 0}) ;
		g.rejectSame;
		h = [];
		g.do({|item| h = h.add(e[count][item])});
		h.rejectSame;
		e[count] = h;
		count = count + 1;
		})
		^e;
}

		glissMIDI {arg freqMod=frequencyMod, ampthresh=threshAmp, partialnumber=partialNum;
		var step, count, e, f, g, h;
		partialNum = partialnumber;
		threshAmp = ampthresh;
		frequencyMod = freqMod;
		e = this.	threshMIDIMod(freqMod, ampthresh, partialnumber);
		count = 0;
		e.size.do({
		f = [0] ++ e[count].flop[1];
		g = [];
		step = 0;
		e[count].do({|item, index| if(f[step] != item[1], {g = g.add(index-1); g = g.add(index); g = g.add(index); }); step = step + 1;  });
		g = g.reject({|item| item < 0}) ;
		g.rejectSame;
		g = if(g.full, {g.add(e[count].indexOf(e[count][e[count].size-1])); });
		h = [];
		g.do({|item| h = h.add(e[count][item])});
		h.rejectSame;
		e[count] = h;
		count = count + 1;
		})
		^e;
}
		
		getDataGliss {arg freqMod=frequencyMod, ampthresh=threshAmp;
		var step, calc;
		threshAmp = ampthresh;
		frequencyMod = freqMod;
		'computing...'.postln;
		step = 0;
		dataGliss = [];
		Routine({this.dataArray.size.do({
		calc = this.glissMIDI(freqMod, ampthresh, partialnumber: step);
		if(calc.flat.unbubble.notNil, 
			{dataGliss  = dataGliss.add(calc);
		});
		0.01.yield;
		step = step + 1;
		});
		'done'.postln;
		}).play;
}

	maxSizeGliss {var xArr;
	xArr = [];
	dataGliss.do({|item, index| xArr = xArr.addAll(item.unbubble.size) });
	^xArr.maxItem;
	}
		
		midiFileGliss {arg pathMIDIFile="~/Desktop/midiSpeartest.mid", tempo=120, timeSig="4/4", trackDiv=6, beginning=1, filter=0;
		var count, count2, count3, g, h, z, f, minTrack, tracknum, loud, duration, track; 
		count = 0;
		Routine({
		'computing...'.postln;
		g = this.dataGliss;
		g = g.flat;
		h = g.size/3;
		z = Array.iota(h.asInteger, 3);
		g = g.reshapeLike(z);
		f = [];
		g.do({|item, index|  f = f.add(item[1]) });
		minTrack = f.minItem.midioctave(trackDiv);
		tracknum = f.maxItem.midioctave(trackDiv)-minTrack;
		m = SimpleMIDIFile( pathMIDIFile ); 
		m.init1( (tracknum+1), tempo, timeSig );			
		m.timeMode = \seconds;  
		//figuring out loadest partial
		f = [];
		g.do({|item, index|  f = f.add(item[2]) });
		loud = f.maxItem;
		0.02.yield;	
		this.dataGliss.size.do({count2 = 0;
		this.dataGliss[count].size.do({count3 = 0;
		this.dataGliss[count][count2].size.do({
		 if(this.dataGliss[count][count2].flop[0][count3+1] == nil, {duration = [0.1,0.05].choose}, {
		 duration = this.dataGliss[count][count2].flop[0][count3+1] - this.dataGliss[count][count2].flop[0][count3]});
		track = this.dataGliss[count][count2].flop[1][count3].midioctave(trackDiv);
		if(filter == 1, {
		if((this.dataNote[count][count2].flop[1][count3] >= 21).and(this.dataNote[count][count2].flop[1][count3] < 108), { //filter notes higher than a0 and c8
		m.addNote(this.dataGliss[count][count2].flop[1][count3], this.dataGliss[count][count2].flop[2][count3].linlin(0,loud,0,127).round(1), this.dataGliss[count][count2].flop[0][count3]+beginning, duration, 127, track:  (tracknum-(track-minTrack)));
		});
		} , { //if filter is not 1 then ignore filter
		m.addNote(this.dataGliss[count][count2].flop[1][count3], this.dataGliss[count][count2].flop[2][count3].linlin(0,loud,0,127).round(1), this.dataGliss[count][count2].flop[0][count3]+beginning, duration, 127, track:  (tracknum-(track-minTrack)));
		});
		
		count3 = count3 + 1; 
		0.01.yield;
		});
		count2 = count2 + 1; 
		0.02.yield;
		});
		count = count + 1; 
		count.postln;
		0.02.yield;
		});
		m.prAdjustEndOfTrack(1, 2.0); 
		0.02.yield;
		m.write; 
		'done'.postln;
		}).play

}

		

		dataGlissPitch {var g,h,z;
		g = this.dataGliss;
		g = g.flat;
		h = g.size/3;
		z = Array.iota(h.asInteger, 3);
		g = g.reshapeLike(z);
		g.do({|item| item[1] = item[1].midicnote });
		g.dopostln;
		}
		
		getDataNote {arg freqMod=frequencyMod, ampthresh=threshAmp;
		var step, calc;
		threshAmp = ampthresh;
		frequencyMod = freqMod;
		//'computing...'.postln;
		step = 0;
		dataNote = [];
		//Routine({
		this.dataArray.size.do({
		calc = this.noteMIDI(freqMod, ampthresh, partialnumber: step);
		if(calc.flat.unbubble.notNil, 
			{dataNote  = dataNote.add(calc);
		});
		//0.01.yield;
		step = step + 1;
		});
		//'done'.postln;
		//}).play;
				
}
		
		findLoudest {var g, h, z, f;
		//figuring out loadest partial
		g = this.dataNote;
		g = g.flat;
		h = g.size/3;
		z = Array.iota(h.asInteger, 3);
		g = g.reshapeLike(z);
		f = [];
		g.do({|item, index|  f = f.add(item[2]) });
		loud = f.maxItem;
		^loud;
		}
		
		findInfo {arg filter=0, minRange=21, maxRange=108;
		var count, count2, count3, duration, midiNumber, veloNumber, startTime;
		count = 0;
		infoArray = [];
		infoArrayPar = this.dataNote;
		this.findLoudest;
		this.dataNote.size.do({count2 = 0;
		this.dataNote[count].size.do({count3 = 0;
		this.dataNote[count][count2].size.do({
		if(this.dataNote[count][count2].flop[0][count3+1] == nil, {duration = rrand(0.1,0.05)}, {
		duration = this.dataNote[count][count2].flop[0][count3+1] - this.dataNote[count][count2].flop[0][count3]}); //find duration
		 midiNumber = this.dataNote[count][count2].flop[1][count3]; //find midiNum
		veloNumber = this.dataNote[count][count2].flop[2][count3].linlin(0,loud,0,127).round(1); //find velocity
		startTime = this.dataNote[count][count2].flop[0][count3]+1; //find startTime
		//filter notes
		if(filter == 1, {
		if((this.dataNote[count][count2].flop[1][count3] >= minRange).and(this.dataNote[count][count2].flop[1][count3] < maxRange), { //filter notes higher than a0 and c8
		infoArray = infoArray.add([startTime, duration, midiNumber, veloNumber]);
		infoArrayPar[count][count2][count3] = [startTime, duration, midiNumber, veloNumber];
		});
		},{ //if filter is not 1 then ignore filter
		infoArray = infoArray.add([startTime, duration, midiNumber, veloNumber]);
		infoArrayPar[count][count2][count3] = [startTime, duration, midiNumber, veloNumber];
		});
		
		count3 = count3 + 1; 
		});
		count2 = count2 + 1; 
		});
		count = count + 1; 
		});
		}
		
		//midiFileNote {arg pathMIDIFile="~/Desktop/midiSpeartest.mid", tempo=120, timeSig="4/4", trackDiv=6, filter=0, velAdj=10, wait=0, post=false;
//		var count, count2, count3, g, h, z, f, minTrack, tracknum, loud, duration, track; 
//		count = 0;
//		Routine({
//		'computing...'.postln;
//		g = this.dataNote;
//		g = g.flat;
//		h = g.size/3;
//		z = Array.iota(h.asInteger, 3);
//		g = g.reshapeLike(z);
//		f = [];
//		g.do({|item, index|  f = f.add(item[1]) });
//		minTrack = f.minItem.midioctave(trackDiv);
//		tracknum = f.maxItem.midioctave(trackDiv)-minTrack;
//		m = SimpleMIDIFile( pathMIDIFile ); 
//		m.init1( (tracknum+1), tempo, timeSig );			
//		m.timeMode = \seconds;  
//		//figuring out loadest partial
//		f = [];
//		g.do({|item, index|  f = f.add(item[2]) });
//		loud = f.maxItem;
//		wait.yield;
//		this.dataNote.size.do({count2 = 0;
//		this.dataNote[count].size.do({count3 = 0;
//		this.dataNote[count][count2].size.do({
//		 if(this.dataNote[count][count2].flop[0][count3+1] == nil, {duration = [0.1,0.05].choose}, {
//		 duration = this.dataNote[count][count2].flop[0][count3+1] - this.dataNote[count][count2].flop[0][count3]});
//		track = this.dataNote[count][count2].flop[1][count3].midioctave(trackDiv);
//		if(filter == 1, {
//		if((this.dataNote[count][count2].flop[1][count3] >= 21).and(this.dataNote[count][count2].flop[1][count3] < 108), { //filter notes higher than a0 and c8
//		m.addNote(this.dataNote[count][count2].flop[1][count3], (this.dataNote[count][count2].flop[2][count3].linlin(0,loud,0,127)+velAdj).round(1).min(127), this.dataNote[count][count2].flop[0][count3]+1, duration, 127, track:  (tracknum-(track-minTrack)));
//		});
//		},{ //if filter is not 1 then ignore filter
//		m.addNote(this.dataNote[count][count2].flop[1][count3], (this.dataNote[count][count2].flop[2][count3].linlin(0,loud,0,127)+velAdj).round(1).min(127), this.dataNote[count][count2].flop[0][count3]+1, duration, 127, track:  (tracknum-(track-minTrack)));
//		});
//		
//		count3 = count3 + 1; 
//		wait.yield;
//		});
//		count2 = count2 + 1; 
//		wait.yield;
//		});
//		count = count + 1; 
//		count.postlnbool(post);	
//		wait.yield;
//		});
//		0.02.yield;
//		m.prAdjustEndOfTrack(1, 2.0); 
//		0.02.yield;
//		m.write; 
//		'done'.postln;
//		}).play
//
//}


		midiFileFormat {var newArr;
			midiNoteArr.do({|item| 
				newArr = newArr.add([item[4], item[2], \noteOn, 0, item[0], item[1]]); 
				newArr = newArr.add([item[4], item[2] + item[3], \noteOff, 0, item[0], 127]);
			});
			^newArr;
		}
		
		midiFileNote {arg pathMIDIFile="~/Desktop/midiSpeartest.mid", tempo=120, timeSig="4/4", wait=0.2;
		var midiData;
		
		if(midiNoteArr.notNil, {
		
		Routine({1.do({
			
		midiData = this.midiFileFormat;
		
		wait.yield;
		
		m = SimpleMIDIFile( pathMIDIFile ); 
		
		m.init1( midiNoteArr.flop[0].maxItem, tempo, timeSig );			
		m.timeMode = \seconds;  

		m.addAllMIDIEvents(midiData);
		
		wait.yield;
		
		m.prAdjustEndOfTrack(1, 2.0); 
		
		m.write;
		})}).play; 	
		});
		}
		
		midiFileNoteInfo {arg trackDiv=6, filter=0, velAdj=10, wait=0, post=false;
		var count, count2, count3, g, h, z, f, minTrack, tracknum, loud, duration, track; 
		count = 0;
		Routine({
		'computing...'.postln;
		g = this.dataNote;
		g = g.flat;
		h = g.size/3;
		z = Array.iota(h.asInteger, 3);
		g = g.reshapeLike(z);
		f = [];
		midiNoteArr = [];
		g.do({|item, index|  f = f.add(item[1]) });
		minTrack = f.minItem.midioctave(trackDiv);
		tracknum = f.maxItem.midioctave(trackDiv)-minTrack;
		
		f = [];
		g.do({|item, index|  f = f.add(item[2]) });
		loud = f.maxItem;
		wait.yield;
		this.dataNote.size.do({count2 = 0;
		this.dataNote[count].size.do({count3 = 0;
		this.dataNote[count][count2].size.do({
		 if(this.dataNote[count][count2].flop[0][count3+1] == nil, {duration = [0.1,0.05].choose}, {
		 duration = this.dataNote[count][count2].flop[0][count3+1] - this.dataNote[count][count2].flop[0][count3]});
		track = this.dataNote[count][count2].flop[1][count3].midioctave(trackDiv);
		if(filter == 1, {
		if((this.dataNote[count][count2].flop[1][count3] >= 21).and(this.dataNote[count][count2].flop[1][count3] < 108), { //filter notes higher than a0 and c8
		
		midiNoteArr = midiNoteArr.add([this.dataNote[count][count2].flop[1][count3], (this.dataNote[count][count2].flop[2][count3].linlin(0,loud,0,127)+velAdj).round(1).min(127), this.dataNote[count][count2].flop[0][count3], duration, (tracknum-(track-minTrack))]); //noteNumber, velo, startTime, duration, track
		
		});
		},{ //if filter is not 1 then ignore filter
		midiNoteArr = midiNoteArr.add([this.dataNote[count][count2].flop[1][count3], (this.dataNote[count][count2].flop[2][count3].linlin(0,loud,0,127)+velAdj).round(1).min(127), this.dataNote[count][count2].flop[0][count3], duration, (tracknum-(track-minTrack))]); //noteNumber, velo, startTime, duration, track
		
		});
		
		count3 = count3 + 1; 
		wait.yield;
		});
		count2 = count2 + 1; 
		wait.yield;
		});
		count = count + 1; 
		count.postlnbool(post);	
		wait.yield;
		});
		'done'.postln;
		}).play

}

		midiArrayNote {
		var count, count2, count3, duration, g, h, z, f,loud; 
		count = 0;
		freqArray = [];
		ampArray = [];
		startTimeArray = [];
		durArray = [];

		Routine({
		'computing...'.postln;
			g = this.dataNote;
		g = g.flat;
		h = g.size/3;
		z = Array.iota(h.asInteger, 3);
		g = g.reshapeLike(z);
		f = [];
		g.do({|item, index|  f = f.add(item[1]) });
		//figuring out loadest partial
		f = [];
		g.do({|item, index|  f = f.add(item[2]) });
		loud = f.maxItem;
		0.02.yield;	

		0.02.yield;	
		this.dataNote.size.do({count2 = 0;
		this.dataNote[count].size.do({count3 = 0;
		this.dataNote[count][count2].size.do({
		 if(this.dataNote[count][count2].flop[0][count3+1] == nil, {duration = [0.1,0.05].choose}, {
		 duration = this.dataNote[count][count2].flop[0][count3+1] - this.dataNote[count][count2].flop[0][count3]});
		
		freqArray = freqArray.add(this.dataNote[count][count2].flop[1][count3].midicps);
		ampArray = ampArray.add((this.dataNote[count][count2].flop[2][count3]).linlin(0,loud,0,127).linlin(0,127,0.0,1.0));
		startTimeArray = startTimeArray.add(this.dataNote[count][count2].flop[0][count3]);
		durArray =  durArray.add(duration);
		count3 = count3 + 1; 
		0.001.yield;
		});
		count2 = count2 + 1; 
		//0.001.yield;
		});
		count = count + 1; 
		count.postln;
		//0.02.yield;
		});
		//0.02.yield;
		'done'.postln;
		}).play

}

midiFileMIDI {arg filter=0;
		var count, count2, count3, g, h, z, f, minTrack, tracknum, loud, duration, track; 
		count = 0;
		Routine({
		'computing...'.postln;
		g = this.dataNote;
		g = g.flat;
		h = g.size/3;
		z = Array.iota(h.asInteger, 3);
		g = g.reshapeLike(z);
		f = [];
		g.do({|item, index|  f = f.add(item[1]) });
		noteNumber = [];
		velo = [];
		startTime = [];
		dur = [];
		//minTrack = f.minItem.midioctave(trackDiv);
//		tracknum = f.maxItem.midioctave(trackDiv)-minTrack;

		f = [];
		g.do({|item, index|  f = f.add(item[2]) });
		loud = f.maxItem;
		0.02.yield;	
		this.dataNote.size.do({count2 = 0;
		this.dataNote[count].size.do({count3 = 0;
		this.dataNote[count][count2].size.do({
		 if(this.dataNote[count][count2].flop[0][count3+1] == nil, {duration = [0.1,0.05].choose}, {
		 duration = this.dataNote[count][count2].flop[0][count3+1] - this.dataNote[count][count2].flop[0][count3]});
		//track = this.dataNote[count][count2].flop[1][count3].midioctave(trackDiv);
		if(filter == 1, {
		if((this.dataNote[count][count2].flop[1][count3] >= 21).and(this.dataNote[count][count2].flop[1][count3] < 108), { //filter notes higher than a0 and c8
		noteNumber = noteNumber.add(this.dataNote[count][count2].flop[1][count3]);
		velo =  velo.add(this.dataNote[count][count2].flop[2][count3].linlin(0,loud,0,127).round(1)); 
		startTime = startTime.add(this.dataNote[count][count2].flop[0][count3]+1); 
		dur = dur.add(duration); 
		});
		},{ //if filter is not 1 then ignore filter
		noteNumber = noteNumber.add(this.dataNote[count][count2].flop[1][count3]);
		velo =  velo.add(this.dataNote[count][count2].flop[2][count3].linlin(0,loud,0,127).round(1)); 
		startTime = startTime.add(this.dataNote[count][count2].flop[0][count3]+1); 
		dur = dur.add(duration); 
		});
		
		count3 = count3 + 1; 
		0.01.yield;
		});
		count2 = count2 + 1; 
		0.02.yield;
		});
		count = count + 1; 
		count.postln;
		0.02.yield;
		});
		'done'.postln;
		}).play

}

		getOSCArray {arg synth = "constSine", adjVol = 1.0, adjFreq=1, extraArgs=[], timeOffSet=0.0;
		var x, func=[], funcIndex=[];
		x = [];

extraArgs.do{|item, index| if(item.isFunction, {func = func.add(item); funcIndex = funcIndex.add(index)})};
	func.postln;
	funcIndex.postln;
Routine({var parStep, step, node;
parStep = 0;
this.durArray.size.do({var times, freq, amp, array;
step = 1;
node = 1000+parStep;
func.do({|item, index| if(item.notNil, { extraArgs.put(funcIndex[index], func[index].value) }); });
x = x.addAll([[this.startTimeArray[parStep]+timeOffSet, ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep]*adjFreq, "amp", this.ampArray[parStep]*adjVol]++extraArgs]]);
x = x.addAll([[this.startTimeArray[parStep]+this.durArray[parStep]+timeOffSet, ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.0001.yield;
parStep = parStep + 1;
});
"done".postln;
oscArray = x; 
}).play		
		
}

	midiArrayGliss {var brandNew, newArr;
	timesArr = [];
	freqArray = [];
	ampArray = [];

	dataGliss.do({|its|
	its.do({|item| var times, midi, amp; item.do({|it| times=times.add(it[0]); 	midi=midi.add(it[1].midicps); amp=amp.add(it[2]); }); newArr=newArr.add([times,midi,amp]) });
	});

	brandNew = newArr.sort({ arg a, b; a[0][0] <= b[0][0] });

	timesArr = brandNew.flop[0];
	freqArray = brandNew.flop[1];
	ampArray = brandNew.flop[2];

	
	}
	
	//	midiArrayGliss {
//	timesArr = [];
//	freqArray = [];
//	ampArray = [];
//
//Routine({var step, arr;
//step = 0;
//'computing...'.postln;
//(dataGliss.size).do({
//arr =  dataGliss[step].unbubble;
//timesArr = timesArr.addAll([arr.flop[0]]);
//freqArray = freqArray.addAll([arr.flop[1].midicps]);
//ampArray = ampArray.addAll([arr.flop[2]]);
//step = step + 1;
//0.01.yield;
//});
//"done".postln;
//}).play;
//
//	
//	}

	getOSCArrayGliss {arg synth = "envelopeFreq", adjVol = 1.0, adjFreq=1, extraArgs=[], timeOffSet=0.0;
var first, freqs, times, amps, func=[], funcIndex=[], x, y, z;
first = [];
extraArgs.do{|item, index| if(item.isFunction, {func = func.add(item); funcIndex = funcIndex.add(index)})};

Routine({var step, node;
step = 0;
node = 1000;
oscArray = [];
freqArray.size.do({
func.do({|item, index| if(item.notNil, { extraArgs.put(funcIndex[index], func[index].value) }); });
freqs = freqArray[step]*adjFreq;
times = (timesArr[step]+timeOffSet).differentiate;
first = times.removeAt(0);
amps = ampArray[step]*adjVol;

e = Env(freqs, times, \exp);
f = Env(amps, times, \exp);
x = [\s_new, synth, node, 0, 0, \t_gate, 1] ++ extraArgs;
y = [\n_setn, node, \envFreq, e.asArray.size, e.asArray].flat;
z = [\n_setn, node, \envAmp, f.asArray.size, f.asArray].flat;
oscArray = oscArray.addAll([[first, x, y, z]]);
oscArray = oscArray.addAll([[(times.sum+first), [\n_set, node, \gates, 0]]]);
0.001.yield;
step = step + 1;
node = node + 1;
});
"done".postln;
}).play;

}


	adjustEnd {arg extra=4;
	var g;
g = oscArray;
oscArray = g ++ [[(g[g.size-1][0]+extra), [\c_set, 0, 0]]];	
}

postOSCArray {
	Post << oscArray << nl; '';
}

		getOSCHarps {arg synth = "Karplus", adjVol = 1.0;
		var x, z;
		x = [];
		
Routine({var parStep, step, node;
parStep = 0;
this.durArray.size.do({var times, freq, amp;

step = 1;
node = 1000+parStep;
x = x.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "decayTime", rrand(4.0, 6.5), "rate", rrand(0.5,2.0), "atk", 0.01.randDifMul(0.01), "dec", 0.1.randDifMul(1.0), "bufnum", [8,9,10].choose, "start", rrand(2, 13230000), "window", rrand(0.1,0.05)]]]);
x = x.addAll([[this.startTimeArray[parStep]+this.durArray[parStep]+rrand(0.5,2.0), ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;
});
"done".postln;
oscArray = x.sort({ arg a, b; b[0] >= a[0] }); 

}).play		
		
}


		getOSCSimple {arg synth = "pvplay", adjVol = 1.0, maxBuffers = 23;
		var x, z;
		x1 = [];
	
	Routine({var parStep, node;
parStep = 0;	
		
this.durArray.size.do({var times, freq, amp;		
node = 1000+parStep;
x1 = x1.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "bufnum", 0, "recBuf", rrand(1,maxBuffers), "startRate", rrand(0.8,3)]]]);
x1 = x1.addAll([[this.startTimeArray[parStep]+this.durArray[parStep], ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;
});
"done".postln;

}).play			
}

		getOSCOverture {arg synth = "pvplay", adjVol = 1.0, maxBuffers = 23, pitchArr, frequency=440, rateArr;
		var x, z, randomBuf;

pitchArr ?? {pitchArr = Array.fill(maxBuffers, frequency)};
rateArr ?? {rateArr = Array.fill(maxBuffers, 1)};

x1 = []; x2 = []; x3 = []; x4 = []; x5 = []; x6 = []; x7 = []; x8 = []; x9 = []; x10 = [];

Routine({var parStep, node;
parStep = 0;

this.durArray.size.do({var times, freq, amp;

node = 1010+parStep;
randomBuf = rrand(1,maxBuffers);
x1 = x1.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "bufnum", 0, "recBuf", randomBuf, "startRate", rrand(0.8,2), "inFreq", pitchArr[randomBuf-1], "rate", (rateArr[randomBuf-1]/this.durArray[parStep])]]]);
x1 = x1.addAll([[this.startTimeArray[parStep]+this.durArray[parStep], ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;

node = 1000+parStep;
randomBuf = rrand(1,maxBuffers);
x2 = x2.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "bufnum", 0, "recBuf", randomBuf, "startRate", rrand(0.8,3), "inFreq", pitchArr[randomBuf-1], "rate", (rateArr[randomBuf-1]/this.durArray[parStep])]]]);
x2 = x2.addAll([[this.startTimeArray[parStep]+this.durArray[parStep], ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;

node = 1000+parStep;
randomBuf = rrand(1,maxBuffers);
x3 = x3.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "bufnum", 0, "recBuf", randomBuf, "startRate", rrand(0.8,3), "inFreq", pitchArr[randomBuf-1], "rate", (rateArr[randomBuf-1]/this.durArray[parStep])]]]);
x3 = x3.addAll([[this.startTimeArray[parStep]+this.durArray[parStep], ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;

node = 1000+parStep;
randomBuf = rrand(1,maxBuffers);
x4 = x4.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "bufnum", 0, "recBuf", randomBuf, "startRate", rrand(0.8,3), "inFreq", pitchArr[randomBuf-1], "rate", (rateArr[randomBuf-1]/this.durArray[parStep])]]]);
x4 = x4.addAll([[this.startTimeArray[parStep]+this.durArray[parStep], ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;

node = 1000+parStep;
randomBuf = rrand(1,maxBuffers);
x5 = x5.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "bufnum", 0, "recBuf", randomBuf, "startRate", rrand(0.8,3), "inFreq", pitchArr[randomBuf-1], "rate", (rateArr[randomBuf-1]/this.durArray[parStep])]]]);
x5 = x5.addAll([[this.startTimeArray[parStep]+this.durArray[parStep], ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;

node = 1000+parStep;
randomBuf = rrand(1,maxBuffers);
x6 = x6.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "bufnum", 0, "recBuf", randomBuf, "startRate", rrand(0.8,3), "inFreq", pitchArr[randomBuf-1], "rate", (rateArr[randomBuf-1]/this.durArray[parStep])]]]);
x6 = x6.addAll([[this.startTimeArray[parStep]+this.durArray[parStep], ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;

node = 1000+parStep;
randomBuf = rrand(1,maxBuffers);
x7 = x7.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "bufnum", 0, "recBuf", randomBuf, "startRate", rrand(0.8,3), "inFreq", pitchArr[randomBuf-1], "rate", (rateArr[randomBuf-1]/this.durArray[parStep])]]]);
x7 = x7.addAll([[this.startTimeArray[parStep]+this.durArray[parStep], ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;

node = 1000+parStep;
randomBuf = rrand(1,maxBuffers);
x8 = x8.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "bufnum", 0, "recBuf", randomBuf, "startRate", rrand(0.8,3), "inFreq", pitchArr[randomBuf-1], "rate", (rateArr[randomBuf-1]/this.durArray[parStep])]]]);
x8 = x8.addAll([[this.startTimeArray[parStep]+this.durArray[parStep], ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;

node = 1000+parStep;
randomBuf = rrand(1,maxBuffers);
x9 = x9.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "bufnum", 0, "recBuf", randomBuf, "startRate", rrand(0.8,3), "inFreq", pitchArr[randomBuf-1], "rate", (rateArr[randomBuf-1]/this.durArray[parStep])]]]);
x9 = x9.addAll([[this.startTimeArray[parStep]+this.durArray[parStep], ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;

node = 1000+parStep;
randomBuf = rrand(1,maxBuffers);
x10 = x10.addAll([[this.startTimeArray[parStep], ["/s_new", synth, node, 0, 0, "freq", this.freqArray[parStep], "amp", this.ampArray[parStep]*adjVol, "bufnum", 0, "recBuf", randomBuf, "startRate", rrand(0.8,3), "inFreq", pitchArr[randomBuf-1], "rate", (rateArr[randomBuf-1]/this.durArray[parStep])]]]);
x10 = x10.addAll([[this.startTimeArray[parStep]+this.durArray[parStep], ["/n_set", node, "gates", 0]]]);
'partial '.post; (parStep+1).postln;
0.001.yield;
parStep = parStep + 1;
});
"done".postln;

}).play		
		
}

		midiNotes {var newArray=[];
		this.dataNote.do({|item| newArray = newArray.add(item.flat[1])});
		^newArray;
		}
		
		listScore {arg dur=200;
		var scoreMidArr;
		scoreMidArr = infoArray.sort({arg a, b; a[0] <= b[0]});
		scoreMidArr = scoreMidArr.reject({|item| item[1] > dur});
		^scoreMidArr;
		}
		
		//midiVel {var newArray=[];
//		this.dataNote.do({|item| newArray = newArray.add(item.flat[1])});
//		^newArray;
//		}
		
		dataNotePitch {var g,h,z;
		g = this.dataNote;
		g = g.flat;
		h = g.size/3;
		z = Array.iota(h.asInteger, 3);
		g = g.reshapeLike(z);
		g.do({|item| item[1] = item[1].midicnote });
		g.dopostln;
		}
		
}