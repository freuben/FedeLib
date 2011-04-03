//Needs to be revised - buffer numbers should be removed - SEE PARTIAL TRACKER

FFTFilter : PartialTracker {var <>z, task2;

startEtudeSynth {arg witchIn=1, out=30;
		audioIn = witchIn;
	s.sendMsg(\s_new, \partialthresh, y = 1009, 1, 2002, \fftbuf, fftbuf, \magbuf, magbuf, \freqbuf, freqbuf, \num, number, \thresh, magThresh, \bus, audioIn);
	 s.sendMsg(\s_new, \fftfilterbuf, z = 1010, 1, 2002, \cutoff, 200, \qu, (50/200), \out, out, \sndbuf, 3);
	}
	
startEtudeSynth2 {arg witchIn=1, out=42;
		audioIn = witchIn;
	s.sendMsg(\s_new, \partialthresh, y = 1022, 1, 2002, \fftbuf, fftbuf, \magbuf, magbuf, \freqbuf, freqbuf, \num, number, \thresh, magThresh, \bus, audioIn);
	 s.sendMsg(\s_new, \fftfilterbuf, z = 1023, 1, 2002, \cutoff, 200, \qu, (50/200), \out, out);
}
	
startsynth3 {arg witchIn=1, out=0;
		audioIn = witchIn;
	 y = Synth.tail(s, \partialthresh, [\fftbuf, fftbuf, \magbuf, magbuf, \freqbuf, freqbuf, \num, number, \thresh, magThresh, \bus, audioIn]);
	 z = Synth.tail(s, \fftfilter, [\cutoff, 400, \qu, (50/200), \out, out]);
	}
	
startsynth3buf {arg witchIn=1, out=0, vol=1, soundbuf=3;
		audioIn = witchIn;
	y = Synth.tail(s, \partialthresh, [\fftbuf, fftbuf, \magbuf, magbuf, \freqbuf, freqbuf, \num, number, \thresh, magThresh, \bus, audioIn]);
	 z = Synth.tail(s, \fftfilterbuf, [\cutoff, 400, \qu, (50/200), \out, out, \sndbuf, soundbuf, \amp2, vol]);
	}
	
	startNodes {arg chanOffset=0;
	z = NodeProxy.audio(s, 1);
	z.play(chanOffset);

}
	startsynth3bufNode {arg witchIn=1, out=0, vol=1, soundbuf=3;
		audioIn = witchIn;
	 z.put(0, \partialthresh, 0, [\fftbuf, fftbuf, \magbuf, magbuf, \freqbuf, freqbuf, \num, number, \thresh, magThresh, \bus, audioIn]);
	  z.put(1, \fftfilterbuf, 0, [\cutoff, 400, \qu, (50/200), \out, out, \sndbuf, soundbuf, \amp2, vol]);
	}
	
getQ {var minfreq, maxfreq, bandwith, rq, centerfreq;
rqtime = 0.1;
task2 = Task({inf.do({

minfreq = this.frequencies.minItem;
maxfreq = this.frequencies.maxItem;

if((minfreq.notNil).and(maxfreq.notNil), {
bandwith = maxfreq-minfreq;
  centerfreq = (bandwith/2)+minfreq;
  	cutfreq = centerfreq.value({|item| if(item < 1, {1000;},{item;})});
  	rq = bandwith/cutfreq;
  	 if(rq == 0, 
  	 	{rq2 = 0.5; s.sendMsg(\n_set, z, \amp, 0.0);
  	 	},
  	 	{rq2 = rq; s.sendMsg(\n_set, z, \amp, 1.0);}
  	 ); 
  	}, {rq2 = 0.5; cutfreq = 1000});
  	s.sendMsg(\n_set, z, \cutoff, cutfreq, \qu, rq2, \lagtime, rqtime/2);
  	//[rq2, cutfreq].postln;
  	rqtime.yield;
 
})}).play;
}

ffttime {arg filterlag, playrate=1.2;
this.rqtime = filterlag;
s.sendMsg(\n_set, z, \lagamp, filterlag, \rate, playrate); 
}

getQu {var minfreq, maxfreq, bandwith, rq, centerfreq;
rqtime = 0.1;
task2 = Task({inf.do({

minfreq = this.frequencies.minItem;
maxfreq = this.frequencies.maxItem;

if((minfreq.notNil).and(maxfreq.notNil), {
bandwith = maxfreq-minfreq;
  centerfreq = (bandwith/2)+minfreq;
  	cutfreq = centerfreq.value({|item| if(item < 1, {1000;},{item;})});
  	rq = bandwith/cutfreq;
  	 if(rq == 0, 
  	 	{rq2 = 0.5;  z.set(\amp, 0.0);
  	 	},
  	 	{rq2 = rq; z.set(\amp, 1.0);}
  	 ); 
  	}, {rq2 = 0.5; cutfreq = 1000});
  	 z.set(\cutoff, cutfreq, \qu, rq2, \lagtime, rqtime/2);
  	//[rq2, cutfreq].postln;
  	rqtime.yield;
 
})}).play;
}

fftTime {arg filterlag, playrate=1.2;
this.rqtime = filterlag;
	z.set(\lagamp, filterlag, \rate, playrate); 
}

stoptask2 {task2.stop;}
	
resumetask2 {task2.resume;}

volume {arg vol=1; z.set(\amp2, vol);}

changeRate {arg newRate=1; z.set(\amp2, newRate);}

}