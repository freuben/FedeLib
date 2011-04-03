//Needs to be fixed - no Buffer numbers please - SEE PARTIAL TRACKER

PartialTrackerFile : PartialTracker {
	var <>soundbuf, <>channels, <>d, <>path;

	*new {arg path, channels=1, soundbuf=3, window=256, number=1, fftbuf=0, magbuf=1, freqbuf=2;
		^super.new.initPartialTrackerFile(path, channels, soundbuf, window, number, fftbuf, magbuf, freqbuf);
	}
	
	initPartialTrackerFile {arg soundpath, chan=1,bufsound=3, windowSize=256, numberPartials=1, buffft=0, bufmag=1, buffreq=2;
		s = Server.local;
		window = windowSize;
		number = numberPartials+1;
		fftbuf = buffft;
		magbuf = bufmag;
		freqbuf = buffreq;
		soundbuf = bufsound;
		channels = chan;
		path = soundpath;
		time = 2048/44100;
		
		a = Buffer.alloc(s, windowSize*2, 1, 1, buffft);
		b = Buffer.alloc(s, windowSize, 1, 1, bufmag);
		c = Buffer.alloc(s, windowSize, 1, 1, buffreq);
		d = Buffer.read(s, path, bufnum: bufsound)
		//s.sendMsg(\b_allocRead, bufsound, path, channels);
		 
		
		}
			
	startsynthbuf {arg start=0;
	y = Synth(\numpar2, [\fftbuf, fftbuf, \magbuf, magbuf, \freqbuf, freqbuf, \soundbuf, soundbuf, \num, number, \start, start]);
	}

	startsynthbufmono {arg start=0;
	y = Synth(\numpar3,[\fftbuf, fftbuf, \magbuf, magbuf, \freqbuf, freqbuf, \soundbuf, soundbuf, \num, number, \start, start]);
	}
	
	startsynthbuf2 {arg thresh = 0;
		magThresh = thresh;
	y = Synth(\partialthresh2, [\fftbuf, fftbuf, \magbuf, magbuf, \freqbuf, freqbuf, \soundbuf, soundbuf, \num, number, \thresh, magThresh]);
	}

	startsynthbuf2mono {arg thresh = 0;
		magThresh = thresh;
	y = Synth(\partialthresh3, [\fftbuf, fftbuf, \magbuf, magbuf, \freqbuf, freqbuf, \soundbuf, soundbuf, \num, number, \bins, magThresh]);
	}
	
	startSavingDataBuf {arg threshold = 0.1, timeround=0.00001, bufnum=4, trigtime = 4, thisvol=1, bufrate= 1, loop=0; 
				z = Synth("fftonset3",[\vol, thisvol, \rate, trigtime, \thresh, threshold, \fftbuf, bufnum, \ratebuf, bufrate, \soundbuf, soundbuf, \loops, loop]);
				this.startsynthbufmono;
				this.getarrays;
				t = StopWatch(timeround);
				dataArray = [];
				o = OSCresponder(s.addr,'/tr',{ arg time,responder,msg;
	if((msg[3] != 0).and(this.frequencies.unbubble.notNil), {dataArray = dataArray.add([t.getTime, this.frequencies, this.magnitudes].postln);});
}).add;

}

	startSavingDataBuf2 {arg threshold = 0.1, timeround=0.00001, bufnum=4, trigtime = 4, thisvol=1, bufrate= 1, loop=0; 
				z = Synth("fftonset3", [\vol, thisvol, \rate, trigtime, \thresh, threshold, \fftbuf, bufnum, \ratebuf, bufrate, \soundbuf, soundbuf, \loops, loop]);
				this.startsynthbuf2mono;	
				this.getarrays;		
				t = StopWatch(timeround);
				dataArray = [];
				o = OSCresponder(s.addr,'/tr',{ arg time,responder,msg;
	if((msg[3] != 0).and(this.frequencies.unbubble.notNil), {dataArray = dataArray.add([t.getTime, this.frequencies, this.magnitudes].postln);});
}).add;

}
		
	*initClass {
	
		SynthDef.writeOnce(\numpar2, {arg fftbuf, soundbuf, magbuf, freqbuf, vol = 1, num = 1, rate = 1, trig=1.0, start=0.0, loops=1;
	var in, chain;
	in = PlayBuf.ar(2, soundbuf, rate, trig, start, loops) * vol;
	chain = FFT(fftbuf, in);
	chain = PV_MaxMagN(chain, num);
	chain = PV_MagBuffer(chain, magbuf);
	chain = PV_FreqBuffer(chain, freqbuf);
	IFFT(chain);
	});

		SynthDef.writeOnce(\numpar3, {arg fftbuf, soundbuf, magbuf, freqbuf, vol = 1, num = 1, rate = 1, trig=1.0, start=0.0, loops=1;
	var in, chain;
	in = PlayBuf.ar(1, soundbuf, rate, trig, start, loops) * vol;
	chain = FFT(fftbuf, in);
	chain = PV_MaxMagN(chain, num);
	chain = PV_MagBuffer(chain, magbuf);
	chain = PV_FreqBuffer(chain, freqbuf);
	IFFT(chain);
	});
	
		SynthDef.writeOnce(\partialthresh2, {arg fftbuf, soundbuf, magbuf, freqbuf, num = 1, rate = 1, trig=1.0, start=0.0, loops=1, vol = 1, bins = 0;
	var in, chain;
	in = PlayBuf.ar(2, soundbuf, rate, trig, start, loops) * vol;
	chain = FFT(fftbuf, in);
	chain = PV_MagAbove(chain, bins);
	chain = PV_MaxMagN(chain, num);
	chain = PV_MagBuffer(chain, magbuf);
	chain = PV_FreqBuffer(chain, freqbuf);
	IFFT(chain);
	});
	
		SynthDef.writeOnce(\partialthresh3, {arg fftbuf, soundbuf, magbuf, freqbuf, num = 1, rate = 1, trig=1.0, start=0.0, loops=1, vol = 1, bins = 0;
	var in, chain;
	in = PlayBuf.ar(1, soundbuf, rate, trig, start, loops) * vol;
	chain = FFT(fftbuf, in);
	chain = PV_MagAbove(chain, bins);
	chain = PV_MaxMagN(chain, num);
	chain = PV_MagBuffer(chain, magbuf);
	chain = PV_FreqBuffer(chain, freqbuf);
	IFFT(chain);
	});
	

	SynthDef.writeOnce(\fftonset3, {arg thresh = 0.4, rate = 4, bus=1, vol=1, fftbuf=4,  ratebuf = 1, trigbuf=1.0, start=0.0, loops=1,soundbuf=3;
	var source1, detect, trig, info;
	trig = Impulse.kr(rate);
	source1=  PlayBuf.ar(1, soundbuf, ratebuf, trigbuf, start, loops) * vol;
	detect= PV_JensenAndersen.ar(FFT(fftbuf,source1), threshold: thresh);
	info = Decay.ar(0.1*detect,0.01);
	SendTrig.kr(trig, 1, info);
	
});
	
	}
		


}