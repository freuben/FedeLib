PartialTracker {
	var <>window, <>time, task, <mags, <freq, <>number, <>audioIn=1;
	var <>a, <>b, <>c, <f, <j, s, <>y, <>rq2, <>cutfreq, <>rqtime, <>o, <>t, <>z, <>dataArray;
	var myfreq, <>magnitudes, <>frequencies, <>magThresh=0, <>m;
	var fftbuf, magbuf, freqbuf; //this should go - should fix PartialTrackerFile and FFTFilter

	
	*new {arg window=256, number=20;
		^super.new.initPartialTracker(window, number);
	}
	
	initPartialTracker {arg windowSize=256, numberPartials=20;
		s = Server.local;
		window = windowSize;
		number = numberPartials;
		time = 2048/44100;
		
		a = Buffer.alloc(s, windowSize*2, 1, 1);
		b = Buffer.alloc(s, windowSize, 1, 1);
		c = Buffer.alloc(s, windowSize, 1, 1);
		
		//a = Buffer.alloc(s, windowSize*2, 1, 1, buffft);
//		b = Buffer.alloc(s, windowSize, 1, 1, bufmag);
//		c = Buffer.alloc(s, windowSize, 1, 1, buffreq);
		
		}
			
	startsynth {arg witchIn=1;
		audioIn = witchIn;
	y = Synth(\numpar, [\fftbuf, a.bufnum, \magbuf, b.bufnum, \freqbuf, c.bufnum, \num, number+1, \bus, audioIn]);
	}
	
	startsynth2 {arg witchIn=1, thresh = 0;
		audioIn = witchIn;
		magThresh = thresh;
	y = Synth(\partialthresh, [\fftbuf, a.bufnum, \magbuf, b.bufnum, \freqbuf, c.bufnum, \num, number+1, \thresh, magThresh, \bus, audioIn]);
	}
	
	startNode {
	y = NodeProxy.audio(s, 1);
	y.play;
	}
	
	startsynthNode {arg witchIn=1;
		audioIn = witchIn;
	y.put(0, \numpar, 0, [\fftbuf, a.bufnum, \magbuf, b.bufnum, \freqbuf, c.bufnum, \num, number+1, \bus, audioIn]);
	}
	
	clear {
	y.clear;
	}
	
	startsynth2Node {arg witchIn=1, thresh = 0;
		audioIn = witchIn;
		magThresh = thresh;
	y.put(0, \partialthresh, 0, [\fftbuf, a.bufnum, \magbuf, b.bufnum, \freqbuf, c.bufnum, \num, number+1, \thresh, magThresh, \bus, audioIn]);
	}
	
	
	numpartials {arg numberPartials = 20;
		number = numberPartials;
		y.set(\num, number+1);
	}
	
	thresh {arg thresh = 0;
		magThresh = thresh;
		 y.set(\thresh, magThresh);
	}
	
	threshEtudes {arg thresh = 0;
		magThresh = thresh;
		 s.sendMsg(\n_set, y, \thresh, magThresh);
}	
		
	getarrays {
		window = window-1;
		Routine({1.do({myfreq = [];
			mags = [];
	
				
			  b.getn(0,window,{|msg| f = msg}); //get mag info from buffer
			  	c.getn(0,window,{|msg| j = msg}); //get freq info from buffer
			  	if(f.do{|item, index| 
			  			if(item != 0,
				  			 {mags = mags.add(item); myfreq = myfreq.add(index)}
				  		) //select max magnitudes and their index
				  	}.notNil, 
					{freq = j[myfreq]; //get frequencies for selected magnitudes
					},
					{''} //if mag is nil, ignore
				);	
					
			time.yield;
	
			task = Task({
			inf.do({
			myfreq = [];
			magnitudes = [];
			
				b.getn(0,window,{|msg| f = msg});
				c.getn(0,window,{|msg| j = msg}); 
				f.do{|item, index| if(item != 0, {magnitudes = magnitudes.add(item); myfreq = myfreq.add(index)})};
				  	
					frequencies = j[myfreq];
					frequencies = frequencies.copyRange(1,frequencies.size);
					magnitudes = magnitudes.copyRange(1, magnitudes.size);
					time.yield;
					})
				}).play
			})
			 
		}).play
	}
	
	stoptask {task.stop;}
	
	resume {task.resume;}
	
	stop {s.freeAll;}
	
	list {[frequencies,magnitudes].flop.dopostln}
	
	buffForData {arg bufnum=4;
				s.sendMsg(\b_alloc, bufnum, 2048, 1);
}
	
	startSavingData {arg witchIn=1, threshold = 0.1, timeround=0.00001, bufnum=4, trigtime = 4, thisvol=1; 
				z = Synth("fftonset", [\bus, witchIn, \vol, thisvol, \rate, trigtime, \thresh, threshold, \fftbuf, bufnum]);
				this.startsynth(witchIn);
				this.getarrays;
				t = StopWatch(timeround);
				dataArray = [];
				o = OSCresponder(s.addr,'/tr',{ arg time,responder,msg;
	if((msg[3] != 0).and(this.frequencies.unbubble.notNil), {dataArray = dataArray.add([t.getTime, this.frequencies, this.magnitudes].postln);});
}).add;

}

	startSavingData2 {arg witchIn=1, threshold = 0.1, timeround=0.00001, bufnum=4, trigtime = 4, thisvol=1; 
				z = Synth("fftonset", [\bus, witchIn, \vol, thisvol, \rate, trigtime, \thresh, threshold, \fftbuf, bufnum]);
				this.startsynth2(witchIn);
				this.getarrays;
				t = StopWatch(timeround);
				dataArray = [];
				o = OSCresponder(s.addr,'/tr',{ arg time,responder,msg;
	if((msg[3] != 0).and(this.frequencies.unbubble.notNil), {dataArray = dataArray.add([t.getTime, this.frequencies, this.magnitudes].postln);});
}).add;

}

	stopSavingData {
	s.freeAll;
	t.stop;
	o.remove;
	}
	
	free {
	//free synths and buffers and stops reading info
	y.free;
	this.stoptask;
	a.free;
	b.free;
	c.free;
	}
	
	makeMidiFile {arg pathMIDIFile="~/Desktop/midifile.mid", tempo=120, timeSig="4/4", tracknum, beginning=0;
		var load, duration, count, count2, extraArray;
		
		Routine({
		'computing...'.postln;
		load = this.dataArray.flop[2].flat.maxItem;
		extraArray = this.dataArray.copyRange(1, this.dataArray.size) ++  [[this.dataArray.last[0]+rrand(0.5,1.0),[0],[0]]];
		m = SimpleMIDIFile( pathMIDIFile ); 
		m.init1( (tracknum+1), tempo, timeSig );	
		m.timeMode = \seconds;  
		count = 0;
		0.01.yield;
		this.dataArray.size.do({
			count2 = 0;
			duration = extraArray[count][0]-this.dataArray[count][0];
			this.dataArray[count][1].size.do({ m.addNote(this.dataArray[count][1][count2].cpsmidi.round(1), this.dataArray[count][2][count2].linlin(0,load,0,127).round(1), this.dataArray[count][0]+beginning, duration, 127, track: count2.linlin(0,this.dataArray[count][1].size, 0, (tracknum-1)).round(1)); count2 = count2+1; 0.01.yield;});
		count = count + 1;
		0.02.yield;
		});
		m.prAdjustEndOfTrack(1, 2.0); 
		0.02.yield;
		m.write; 
		'done'.postln;
		}).play;
		
		
}

	
	
	//write Synth Defs 
	*initClass {
	
	//	SynthDef.writeOnce(\numpar, {arg fftbuf, magbuf, freqbuf, bus = 1, num = 1, vol = 1;
//	var in, chain;
//	in = AudioIn.ar(bus, vol);
//	chain = FFT(fftbuf, in);
//	chain = PV_MaxMagN(chain, num);
//	chain = PV_MagBuffer(chain, magbuf);
//	chain = PV_FreqBuffer(chain, freqbuf);
//	IFFT(chain);
//	});
//	
//	SynthDef.writeOnce(\partialthresh, {arg fftbuf, magbuf, freqbuf, bus = 1, num = 1, vol = 1, thresh = 0;
//	var in, chain;
//	in = AudioIn.ar(bus, vol);
//	chain = FFT(fftbuf, in);
//	chain = PV_MagAbove(chain, thresh);
//	chain = PV_MaxMagN(chain, num);
//	chain = PV_MagBuffer(chain, magbuf);
//	chain = PV_FreqBuffer(chain, freqbuf);
//	IFFT(chain);
//	});
	
		SynthDef.writeOnce(\fftfilter,{arg out = 0, cutoff = 100, qu = 0.1, amp = 1.0, amp2 = 1.0, volex = 1, lagtime = 0.05, lagamp=0.1; 
	var signal; 
	signal = Resonz.ar(AudioIn.ar(5)*volex, Lag2.kr(cutoff, lagtime), Lag2.kr(qu, lagtime), Amplitude.kr(AudioIn.ar(1)).lag(lagamp)*amp);

	Out.ar(out, (signal*amp2))});

	SynthDef.writeOnce(\fftfilterbuf,{arg out = 0, cutoff = 100, qu = 0.1, amp = 1.0, amp2 = 1.0, sndbuf = 3, volex = 1, lagtime = 0.05, lagamp=0.1,rate=1, globamp=1.0; 
	var signal; 
	signal = Resonz.ar(PlayBuf.ar(1,sndbuf,rate, loop: 1)*volex, Lag2.kr(cutoff, lagtime), Lag2.kr(qu, lagtime), Amplitude.kr(AudioIn.ar(1)).lag(lagamp)*amp);

	Out.ar(out, (signal*amp2))});
	
	SynthDef.writeOnce(\fftonset, {arg thresh = 0.4, rate = 4, bus=1, vol=1, fftbuf=4;
	var source1, detect, trig, info;
	trig = Impulse.kr(rate);
	source1= AudioIn.ar(bus, vol); 
	detect= PV_JensenAndersen.ar(FFT(fftbuf, source1), threshold: thresh);
	info = Decay.ar(0.1*detect,0.01);
	SendTrig.kr(trig, 1, info);
	
});	
	
	SynthDef.writeOnce(\fftonset2, {arg thresh = 0.4, rate = 4, bus=1, vol=1, fftbuf=4;
	var source1, detect, trig, info;
	trig = Impulse.kr(rate);
	source1= AudioIn.ar(bus, vol); 
	detect= PV_JensenAndersen.ar(FFT(fftbuf, source1), threshold: thresh);
	info = Decay.ar(0.1*detect,0.01);
	SendTrig.kr(trig, 1, info);
	
});	
				
	}

}


