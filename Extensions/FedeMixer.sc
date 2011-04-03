FedeMixer {var <>oldVolumes, <>spec, h, s, setFunc, chan1, chan2, chan3, chan4, chan5, chan6, chan7, chan8;

	*behringer {
		^super.new.initFedeMixer;
	}
	

	initFedeMixer {var mixFunc;
		s = Server.default;
		0.midiin; //contect MIDI
		//h = 1.midiout; //uid for behringer
		h = MIDIOut(0,1165770703); //uid for behringer
		//set initial volumes to 0db
		oldVolumes = Array.fill(8, 0);
		//controlspec for db to midi
		spec = ControlSpec(-90, 6, \db, units: " dB");
		//set sliders to 0db
		h.control(0,81,(spec.unmap(oldVolumes[0])*127).round(1));
		h.control(0,82,(spec.unmap(oldVolumes[1])*127).round(1));
		h.control(0,83,(spec.unmap(oldVolumes[2])*127).round(1));
		h.control(0,84,(spec.unmap(oldVolumes[3])*127).round(1));
		h.control(0,85,(spec.unmap(oldVolumes[4])*127).round(1));
		h.control(0,86,(spec.unmap(oldVolumes[5])*127).round(1));
		h.control(0,87,(spec.unmap(oldVolumes[6])*127).round(1));
		h.control(0,88,(spec.unmap(oldVolumes[7])*127).round(1));
		//function that starts instances of Volume and asigns sliders to channels
		mixFunc = {
		
		chan1 = Volume(s,0,1);
		chan2 = Volume(s,1,1);
		chan3 = Volume(s,2,1);
		chan4 = Volume(s,3,1);
		chan5 = Volume(s,4,1);
		chan6 = Volume(s,5,1);
		chan7 = Volume(s,6,1);
		chan8 = Volume(s,7,1);
		
		MIDIIn.control = { arg src, chan, num, val; 	 
		
		if(num == 81, {"Chan1: ".post; chan1.volume_(spec.map(val/127.0).postln);});
		if(num == 82, {"Chan2: ".post; chan2.volume_(spec.map(val/127.0).postln);});
		if(num == 83, {"Chan3: ".post; chan3.volume_(spec.map(val/127.0).postln);});
		if(num == 84, {"Chan4: ".post; chan4.volume_(spec.map(val/127.0).postln);});
		if(num == 85, {"Chan5: ".post; chan5.volume_(spec.map(val/127.0).postln);});
		if(num == 86, {"Chan6: ".post; chan6.volume_(spec.map(val/127.0).postln);});
		if(num == 87, {"Chan7: ".post; chan7.volume_(spec.map(val/127.0).postln);});
		if(num == 88, {"Chan8: ".post; chan8.volume_(spec.map(val/127.0).postln);});
		};
		};
		
		//function with routine that resets each time synths are freed. it stores the old volumes in an array and restarts Volume instances with previous volumes 
		
		setFunc = {
		Routine({1.do({
		if(chan1.notNil, {
		oldVolumes = [chan1.volume, chan2.volume, chan3.volume, chan4.volume, chan5.volume, chan6.volume, chan7.volume, chan8.volume];
		});
		0.1.yield;
		mixFunc.value;
		0.05.yield;
		chan1.volume_(oldVolumes[0]);
		0.05.yield;
		chan2.volume_(oldVolumes[1]);
		0.05.yield;
		chan3.volume_(oldVolumes[2]);
		0.05.yield;
		chan4.volume_(oldVolumes[3]);
		0.05.yield;
		chan5.volume_(oldVolumes[4]);
		0.05.yield;
		chan6.volume_(oldVolumes[5]);
		0.05.yield;
		chan7.volume_(oldVolumes[6]);
		0.05.yield;
		chan8.volume_(oldVolumes[7]);
		"reset mixer".postln;
		})}).play
		};
		
		CmdPeriod.add(setFunc);
		setFunc.value;
	}

	testMixer {
		{SinOsc.ar(Array.fill(8, 440),0,1.0)}.play; //sines in first 8 channels		
	}
	
	remove {
	CmdPeriod.remove(setFunc);
	MIDIIn.control = nil; //removes the midicontroller 	 
	}
	
	

}