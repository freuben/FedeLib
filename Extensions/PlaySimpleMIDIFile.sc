PlaySimpleMIDI : SimpleMIDIFile {var <>adjGlobalTempo;

setGlobalTempo {adjGlobalTempo = Array.fill(this.tracks, 1);  
^adjGlobalTempo}

playTrackType {arg track, type=\noteOn, function = {arg chan, note, vel; [chan,note,vel].postln}, newTime = 0, adjTempo=1;
	var trackInfo, times, times2, lock, step, wait;
	//gets track info for midiFile
	this.timeMode = \seconds;
	trackInfo = this.midiTrackTypeEvents(track, type);
	times = trackInfo.flop[1]; //get times for routine
	times2 = times*adjTempo; //get times with adjust tempo
	step = (times2.indexInBetween(newTime).roundUp).asInteger;
	wait = (times2[step] - newTime);
	if(wait == 0, {lock = 0}, {lock = 1}); //if wait is not 0, then ignore first yield 
	//routine
	Routine({
	(wait*adjGlobalTempo[track]).yield; //initial wait time
	trackInfo.size.do({
	if(lock == 0, {
	((times.differentiate[step]*adjTempo)*adjGlobalTempo[track]).yield;
	});
	lock = 0;
	function.value(trackInfo.flop[3][step], trackInfo.flop[4][step], trackInfo.flop[5][step]);
	//trackInfo[step].postln;
	step = step + 1;
	})}).play;
	
}

sectionPlay {arg track, function = {arg val; val.postln}, newTime = 0, adjTempo=1;
	var times, section, lock, step, step2=0;
	this.timeMode = \seconds; //make sure it's seconds
	
	times = this.trackSilence(track).flat*adjTempo; //get silence times for routine
	step = (times.indexInBetween(newTime).roundUp).asInteger; //get steps
	times = times-newTime; //get new arr taking newTime in consideration
	times = times.reject({|item| item.isNegative }); //get rid of negative numbers
	
	section = step + 1; //sections start in 1
	//routine
	Routine({

	times.size.do({
	function.value(section);
	
	(times.differentiate[step2]*adjGlobalTempo[track]).yield;
	
	step = step + 1;
	section = step + 1;
	step2 = step2 + 1;
	})}).play;
	
}

phrasePlay {arg track, func= {arg sec; sec.postln;}, funcSilence = {arg silence; silence.postln;}, newTime=0, adjTempo=1;
	this.sectionPlay(track, {arg val;
	var section, silence;
	"Track".post; track.post;
	if(val.odd, {" : Play: section: ".post;
	//this is the sections starting with 1 (for better order);
	section = val+1/2;
	//specific info
	func.value(section);
	}, {
	" : Silence: ".post;
	silence = val/2;
	funcSilence.value(silence);
	}); 
	}, 
	newTime, //this is the new step 
	adjTempo);

}

noteValues {arg track=1;
var timeOnTrk, timeOffTrk, noteValue;
  
timeOnTrk = this.midiTrackTypeEvents(track, \noteOn); //get times for noteOn events in track
timeOffTrk = this.midiTrackTypeEvents(track, \noteOff); //get times for noteOff events in track

noteValue = [];
timeOnTrk.do({|item|
var array, type, note, time, noteOffArr;
array = item;
type = array[2];
note = array[4];
time = array[1];

noteOffArr = [];
timeOffTrk.do({|item| if((item[2] == \noteOff).and(item[4] == note).and(item[1] > time), {noteOffArr = noteOffArr.add(item)}); });
if(noteOffArr.notEmpty, {
noteValue = noteValue.add(noteOffArr[0][1]-time);
});
});

^noteValue;

}

playTrackNotes {arg track=1, duration, function = {arg chan, note, vel, dur; [chan,note,vel,dur].postln}, newTime=0, adjTempo=1;
var trackInfo, times, step;
this.timeMode = \seconds;
trackInfo = this.midiTrackTypeEvents(track, \noteOn);
times = trackInfo.flop[1]*adjTempo; //get times for routine
step = (times.indexInBetween(newTime).roundUp).asInteger;
this.playTrackType(track, \noteOn, {arg chan,note,vel,dur; 
function.value(chan,note,vel,duration[step]);
step = step + 1;
}, newTime, adjTempo);
}
}