MIDIOut2 : MIDIOut {
	classvar r;
	
	playAllNotes {
	var allnotes, c, z;
	allnotes = (21,22..108);
		c = Pbind(\midinote, Pseq(allnotes,1), \amp, 0.5,  \dur, 0.01, \legato, 0.001); 
		z = c <> (type: \midi, midiout:  this); 
	r = Routine({inf.do({
	1.do{z.next(Event.new).play};
	1.0.yield;
	this.allNotesOff(0)
	});
	}).play;
	
	}
	
	stopAllNotes { r.stop;
	}
	
	}