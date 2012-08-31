+SimpleNumber {
	
	midicnote {
		var midi, notes;
		midi = (this + 0.5).asInteger;
		notes = ["c ", "c#", "d ", "d#", "e ", "f ", "f#", "g ", "g#", "a ", "a#", "b "];
		^(notes[midi%12] ++ (midi.div(12) - 1));
	}
	
	midinoteclass {
		var midi, notes;
		midi = (this + 0.5).asInteger;
		notes = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11];
		^(notes[midi%12])
	}

	//for simplicity c is 0, c# is 1, etc...
	pitchClass {
		var midi, notes;
		midi = (this + 0.5).asInteger;
		notes = ["c ", "c#", "d ", "d#", "e ", "f ", "f#", "g ", "g#", "a ", "a#", "b "];
		^(notes[midi%12])
	}
	
	midioctave {arg division=12;
		var midi, notes;
		midi = (this + 0.5).asInteger;
		^((midi.div(division))-(12/division)).round(1).linlin(0,127,0,127);
	}
	
	//returns octave and interval in array
	octint {var a,b,c,d;
	a = this;
	if(a.isPositive, {
	b = (a%12);
	c = (a/12).asInteger;
	d = [c, b]; //octave, interval
	}, {
	b = (a%12);
	if(b == 12, {b = 0});
	c = (a/(12)).asInteger-1;
	d = [c, b]; //octave, interval
	});
	^d;
	}

	midiRange {arg lowLimit, highLimit;
	var newNote, result;
	if((this >= lowLimit).and(this <= highLimit), {
	result = this;
	}, {
	newNote = this.min(highLimit).max(lowLimit);
	result = (this.midinoteclass.pitchClass ++ (newNote.midioctave)).cnotemidi;
	case
	{result > highLimit} {result = result - 12;}
	{result < lowLimit} {result = result + 12};
	});
	^result;
	}
	
	midiMax {arg limit;
	var newNote;
	if(this > limit, {
	newNote = (this.midinoteclass.pitchClass ++ (limit.midioctave-1)).cnotemidi;
	}, {
	newNote = this;
	});
	^newNote;
	}

	midiMin {arg limit;
	var newNote;
	if(this < limit, {
	newNote = (this.midinoteclass.pitchClass ++ (limit.midioctave+1)).cnotemidi;
	}, {
	newNote = this;
	});
	^newNote;
	}
	
	midiwhite {var white;
	white = [0, 2, 4, 5, 7, 9, 11];
	^white.includes(this.midinoteclass);
	}
	
	midiblack {var black;
	black = [1, 3, 6, 8, 10];
	^black.includes(this.midinoteclass);
	}
			
	linerange { arg start, end, dur;
	^this / (this.linlin(0.0, ((dur*end)/start), 0.0, 1)).range(start, end);
	}	
 
 	randDifSet {arg dec=0.1;
 	var number, numberNear, numberRound, list, decimal; 
 	number = this;
	list = [1e-10, 1e-09, 1e-08, 1e-07, 1e-06, 1e-05, 1e-04, 1e-03, 1e-02, 1e-01, 1.0, 10, 100, 1000, 10000, 100000, 1000000];
 	decimal = dec/number.nearestInList(list);
	numberNear = number.nearestInList(list)*decimal;
	numberRound = number.roundUp(numberNear);
	^[(numberRound-numberNear), numberRound]
 	}
 
 	randDif{arg dec=0.1;
 	^rrand(this.randDifSet(dec)[0], this.randDifSet(dec)[1])
 	}
 	
 	randDifMulSet {arg dec=0.1;
 	var number, numberNear, numberRound, list; 
 	number = this;
	list = [1e-10, 1e-09, 1e-08, 1e-07, 1e-06, 1e-05, 1e-04, 1e-03, 1e-02, 1e-01, 1.0, 10, 100, 1000, 10000, 100000, 1000000];
	numberNear = number.nearestInList(list)*dec;
	numberRound = number.roundUp(numberNear);
	^[(numberRound-numberNear), numberRound]
 	}
 
 	randDifMul{arg dec=0.1;
 	^rrand(this.randDifMulSet(dec)[0], this.randDifMulSet(dec)[1])
 	}
 	
 	scale {arg type=\mayor, octaves=1;
 	var first, array, adjOct;
 	if(type == \natMinor, {
 		adjOct = 2;
	 	} , {
	 	adjOct = 1;
 	});
 	octaves = octaves - 1;
 	case
 	{type == \mayor} {array = [0,2,4,5,7,9,11]}
 	{(type == \harmMinor).or(type == \minor)} {array = [0,2,3,5,7,8,11]}
 	{type == \melMinor} {array = [0,2,3,5,7,9,11]}
 	{type == \natMinor} {array = [0,2,3,5,7,8,10]}
 	;
 	first = (this + array);
 	octaves.do{first = first ++ ((first.last+adjOct) + array)}; 	^(first ++ (first.last+adjOct));}
 	
 	melMinorScale {arg octaves=1;
 	var a, b;
 	a = this.scale(\melMinor, octaves); //acending harmonic minor
	b = this.scale(\natMinor, octaves).reverse; //decending natural minor
	b.removeAt(0);
	^(a ++ b)}
 	
 	cpsnote { ^(this.cpsmidi.midicnote)} 
 	
 	midiinterval {var d;
	d = Dictionary["Uni"-> 0, "m2" -> 1, "M2" -> 2, "m3" -> 3, "M3" -> 4, "P4" -> 5, "A4" -> 6, "D5" -> 6, "P5" -> 7, "m6" -> 8, "M6" -> 9, "m7" -> 10, "M7" -> 11, "Oct" -> 12]; 
	^d.findKeyForValue(this);
	}
	
	line {arg end=127,time=0.5,step=1,func={arg val; val.postln};
	var steps,start,a,p,q,r;
	start = this;
	if(start < end, { a = (start, start+step..end); }, {  a = (start, start-step..end); });
	steps = time / a.size  ;			
	p = Pseq((a));							
	q = p.asStream;		
	^Routine({ a.size.do({ var val;
	val = q.next;
	func.value(val);
	steps.yield}) }).play ;		
	
	}
	
	time {arg func={arg val;val.postln}, wait=1;
	^Routine({this.do({
	SystemClock.sched(0.0,{ arg time;  
			func.value(time); 
			nil;
		});
	if(wait.isFunction, 
		{wait.value.yield;
	}, {
		wait.yield;});
	})}).play;
	
	}
	
	recursiveClip {arg limit;
	var result, max;
	max = (this/limit).roundUp(1)-1;
	result = this - (max*limit);
	^result;
	}
	
	midiout {arg port=0; 
	MIDIClient.destinations[this].postln;
	^MIDIOut(port,MIDIClient.destinations[this].uid);
	}
	
	midiin {var initialArr=[], func, routine;
	func = {routine = Routine({
	inf.do({
	if(initialArr != MIDIClient.prList, {MIDIIn.allsources; initialArr = MIDIClient.prList}, 	{initialArr = MIDIClient.prList});
	1.0.yield;
	})}).play;};
	if(this == 0, {
	MIDIIn.allsources;
	initialArr = MIDIClient.prList;
	},{
	func.value;
	CmdPeriod.add(func);
	});
	^func;
	}
	
	chanRec {arg string= "int16";
	var recToFile;
	recToFile = RecordToFile.save(this, string);
	^recToFile;
	}
	
	midipyth {|tune = 440|
	var unison, min2, maj2, min3, maj3, p4,a4, p5, min6, maj6, min7, maj7, octava, note;
	note = this.round(1);
	unison = 1;
	min2 = 256/243;
	maj2 = 9/8;
	min3 = 32/27;
	maj3 = 81/64;
	p4 = 4/3;
	a4 = 729/512;
	p5 = 3/2;
	min6 = 128/81;
	maj6 = 27/16;
	min7 = 16/9;
	maj7 = 243/128;
	octava = 2/1; 
		
	^case
	//midi notes between 21 and 93 
	{note == 21} {unison * tune / 16} //A0
	{note == 22} {min2 * tune / 16}
	{note == 23} {maj2 * tune / 16}
	{note == 24} {min3 * tune / 16} //C1
	{note == 25} {maj3 * tune/ 16}
	{note == 26} {p4 * tune / 16}
	{note == 27} {a4 * tune / 16}
	{note == 28} {p5 * tune / 16}
	{note == 29} {min6 * tune / 16}
	{note == 30} {maj6 * tune / 16}
	{note == 31} {min7 * tune / 16}
	{note == 32} {maj7 * tune / 16}
	{note == 33} {unison * tune / 8} //A1
	{note == 34} {min2 * tune / 8}
	{note == 35} {maj2 * tune / 8}
	{note == 36} {min3 * tune / 8} //C2
	{note == 37} {maj3 * tune / 8}
	{note == 38} {p4 * tune / 8}
	{note == 39} {a4 * tune / 8}
	{note == 40} {p5 * tune / 8}
	{note == 41} {min6 * tune / 8}
	{note == 42} {maj6 * tune / 8}
	{note == 43} {min7 * tune / 8}
	{note == 44} {maj7 * tune / 8}
	{note == 45} {unison * tune / 4} //A2
	{note == 46} {min2 * tune / 4}
	{note == 47} {maj2 * tune / 4}
	{note == 48} {min3 * tune / 4} //C3
	{note == 49} {maj3 * tune / 4}
	{note == 50} {p4 * tune / 4}
	{note == 51} {a4 * tune / 4}
	{note == 52} {p5 * tune / 4}
	{note == 53} {min6 * tune / 4}
	{note == 54} {maj6 * tune / 4}
	{note == 55} {min7 * tune / 4}
	{note == 56} {maj7 * tune / 4}
	{note == 57} {unison * tune / 2} //A3
	{note == 58} {min2 * tune / 2}
	{note == 59} {maj2 * tune / 2}
	{note == 60} {min3 * tune / 2} //C4
	{note == 61} {maj3 * tune / 2}
	{note == 62} {p4 * tune / 2}
	{note == 63} {a4 * tune / 2}
	{note == 64} {p5 * tune / 2}
	{note == 65} {min6 * tune / 2}
	{note == 66} {maj6 * tune / 2}
	{note == 67} {min7 * tune / 2}
	{note == 68} {maj7 * tune / 2}
	{note == 69} {unison * tune} //A4
	{note == 70} {min2 * tune}
	{note == 71} {maj2 * tune}
	{note == 72} {min3 * tune} //C5
	{note == 73} {maj3 * tune}
	{note == 74} {p4 * tune}
	{note == 75} {a4 * tune}
	{note == 76} {p5 * tune}
	{note == 77} {min6 * tune}
	{note == 78} {maj6 * tune}
	{note == 79} {min7 * tune}
	{note == 80} {maj7 * tune}
	{note == 81} {unison * tune * 2}//A5
	{note == 82} {min2 * tune * 2}
	{note == 83} {maj2 * tune * 2}
	{note == 84} {min3 * tune * 2} //C6
	{note == 85} {maj3 * tune * 2}
	{note == 86} {p4 * tune * 2}
	{note == 87} {a4 * tune * 2}
	{note == 88} {p5 * tune * 2}
	{note == 89} {min6 * tune * 2}
	{note == 90} {maj6 * tune * 2}
	{note == 91} {min7 * tune * 2}
	{note == 92} {maj7 * tune * 2}
	{note == 93} {unison * tune * 4} //A6
	
	}
	
	midijust {|tune = 440|
	var unison, min2, maj2, min3, maj3, p4,a4, p5, min6, maj6, min7, maj7, octava, note;
	note = this.round(1);
	unison = 1;
	min2 = 16/15;
	maj2 = 9/8;
	min3 = 6/5;
	maj3 = 5/4;
	p4 = 4/3;
	a4 = 64/45;
	p5 = 3/2;
	min6 = 8/5;
	maj6 = 5/3;
	min7 = 9/5;
	maj7 = 15/8;
	octava = 2/1; 
		
	^case
	//midi notes between 21 and 93 
	
	{note < 0} {min3 * tune / 64}
	{note == 0} {min3 * tune / 64} //C1
	{note == 1} {maj3 * tune/ 64}
	{note == 2} {p4 * tune / 64}
	{note == 3} {a4 * tune / 64}
	{note == 4} {p5 * tune / 64}
	{note == 5} {min6 * tune / 64}
	{note == 6} {maj6 * tune / 64}
	{note == 7} {min7 * tune / 64}
	{note == 8} {maj7 * tune / 64}
	
	{note == 9} {unison * tune / 32} //A0
	{note == 10} {min2 * tune / 32}
	{note == 11} {maj2 * tune / 32}
	{note == 12} {min3 * tune / 32} //C1
	{note == 13} {maj3 * tune/ 32}
	{note == 14} {p4 * tune / 32}
	{note == 15} {a4 * tune / 32}
	{note == 16} {p5 * tune / 32}
	{note == 17} {min6 * tune / 32}
	{note == 18} {maj6 * tune / 32}
	{note == 19} {min7 * tune / 32}
	{note == 20} {maj7 * tune / 32}
	
	{note == 21} {unison * tune / 16} //A0
	{note == 22} {min2 * tune / 16}
	{note == 23} {maj2 * tune / 16}
	{note == 24} {min3 * tune / 16} //C1
	{note == 25} {maj3 * tune/ 16}
	{note == 26} {p4 * tune / 16}
	{note == 27} {a4 * tune / 16}
	{note == 28} {p5 * tune / 16}
	{note == 29} {min6 * tune / 16}
	{note == 30} {maj6 * tune / 16}
	{note == 31} {min7 * tune / 16}
	{note == 32} {maj7 * tune / 16}
	{note == 33} {unison * tune / 8} //A1
	{note == 34} {min2 * tune / 8}
	{note == 35} {maj2 * tune / 8}
	{note == 36} {min3 * tune / 8} //C2
	{note == 37} {maj3 * tune / 8}
	{note == 38} {p4 * tune / 8}
	{note == 39} {a4 * tune / 8}
	{note == 40} {p5 * tune / 8}
	{note == 41} {min6 * tune / 8}
	{note == 42} {maj6 * tune / 8}
	{note == 43} {min7 * tune / 8}
	{note == 44} {maj7 * tune / 8}
	{note == 45} {unison * tune / 4} //A2
	{note == 46} {min2 * tune / 4}
	{note == 47} {maj2 * tune / 4}
	{note == 48} {min3 * tune / 4} //C3
	{note == 49} {maj3 * tune / 4}
	{note == 50} {p4 * tune / 4}
	{note == 51} {a4 * tune / 4}
	{note == 52} {p5 * tune / 4}
	{note == 53} {min6 * tune / 4}
	{note == 54} {maj6 * tune / 4}
	{note == 55} {min7 * tune / 4}
	{note == 56} {maj7 * tune / 4}
	{note == 57} {unison * tune / 2} //A3
	{note == 58} {min2 * tune / 2}
	{note == 59} {maj2 * tune / 2}
	{note == 60} {min3 * tune / 2} //C4
	{note == 61} {maj3 * tune / 2}
	{note == 62} {p4 * tune / 2}
	{note == 63} {a4 * tune / 2}
	{note == 64} {p5 * tune / 2}
	{note == 65} {min6 * tune / 2}
	{note == 66} {maj6 * tune / 2}
	{note == 67} {min7 * tune / 2}
	{note == 68} {maj7 * tune / 2}
	{note == 69} {unison * tune} //A4
	{note == 70} {min2 * tune}
	{note == 71} {maj2 * tune}
	{note == 72} {min3 * tune} //C5
	{note == 73} {maj3 * tune}
	{note == 74} {p4 * tune}
	{note == 75} {a4 * tune}
	{note == 76} {p5 * tune}
	{note == 77} {min6 * tune}
	{note == 78} {maj6 * tune}
	{note == 79} {min7 * tune}
	{note == 80} {maj7 * tune}
	{note == 81} {unison * tune * 2}//A5
	{note == 82} {min2 * tune * 2}
	{note == 83} {maj2 * tune * 2}
	{note == 84} {min3 * tune * 2} //C6
	{note == 85} {maj3 * tune * 2}
	{note == 86} {p4 * tune * 2}
	{note == 87} {a4 * tune * 2}
	{note == 88} {p5 * tune * 2}
	{note == 89} {min6 * tune * 2}
	{note == 90} {maj6 * tune * 2}
	{note == 91} {min7 * tune * 2}
	{note == 92} {maj7 * tune * 2}
	
	{note == 93} {unison * tune * 4}//A5
	{note == 94} {min2 * tune * 4}
	{note == 95} {maj2 * tune * 4}
	{note == 96} {min3 * tune * 4} //C6
	{note == 97} {maj3 * tune * 4}
	{note == 98} {p4 * tune * 4}
	{note == 99} {a4 * tune * 4}
	{note == 100} {p5 * tune * 4}
	{note == 101} {min6 * tune * 4}
	{note == 102} {maj6 * tune * 4}
	{note == 103} {min7 * tune * 4}
	{note == 104} {maj7 * tune * 4}
	
	{note == 105} {unison * tune * 8}//A5
	{note == 106} {min2 * tune * 8}
	{note == 107} {maj2 * tune * 8}
	{note == 108} {min3 * tune * 8} //C6
	{note == 109} {maj3 * tune * 8}
	{note == 110} {p4 * tune * 8}
	{note == 111} {a4 * tune * 8}
	{note == 112} {p5 * tune * 8}
	{note == 113} {min6 * tune * 8}
	{note == 114} {maj6 * tune * 8}
	{note == 115} {min7 * tune * 8}
	{note == 116} {maj7 * tune * 8}
	
	{note == 117} {unison * tune * 16}//A5
	{note == 118} {min2 * tune * 16}
	{note == 119} {maj2 * tune * 16}
	{note == 120} {min3 * tune * 16} //C6
	{note == 121} {maj3 * tune * 16}
	{note == 122} {p4 * tune * 16}
	{note == 123} {a4 * tune * 16}
	{note == 124} {p5 * tune * 16}
	{note == 125} {min6 * tune * 16}
	{note == 126} {maj6 * tune * 16}
	{note == 127} {min7 * tune * 16}
	{note > 127} {min7 * tune * 16}
	

	}
	
	distro4 {var array, val;
	
	val = this.linlin(0,1,0,126).round(1);

	case
	{val <= 42} {array = [1-(val/42), val/42, 0, 0];}
	{(val >= 43).and(val < 85)} {array = [0, 1-((val-42)/42), (val-42)/42, 0];}
	{(val >= 85).and(val <= 126)} {array = [0, 0, 1-((val-84)/42), (val-84)/42];};
	^array;

	}
	
	distroOrder {var arr, arr2, num;
	num = this.min(2.99).max(0);
	arr = [num.asInteger, num.asInteger+1];
	if(num.round != num.asInteger, {arr = arr.reverse});
	[0,1,2,3].do({|item| if(arr.includes(item).not, {arr2 = arr2.add(item)})});
	arr2 = arr2.scramble;
	arr = arr ++ arr2;
	^arr;
	}
	
	indexed {arg start=0;
	^Array.series(this,start,1);
	}
	
	excess2 {arg num;
	var result;
	if(this > num, {result = this.excess(num)}, {
	result = this;
	});
	^result;
	}
	
	calcPVRecReverse {arg hop=0.25, framesize=1024, sampleRate;
	var rawsize, ceil, result;
	sampleRate = sampleRate ?? {Server.default.sampleRate};
	rawsize = this - 3 / hop.reciprocal;	
	ceil = rawsize / framesize;
	result = (ceil * framesize / sampleRate).round(0.1);
	^result;
	}
	
	bufRdRound {arg min=0, max=100;
	var value;
	value = this.linlin(0,1,0,(max-min)) + min;
	^value;	
	}

}

+ Collection {
 
	//rejectSame { this.do({|item, index| this.occurrencesOf(item).do({if(this.occurrencesOf(item) > 1, {this.removeAt(index)})}) })
//	}
	recurrentItem {var arr, a;
	a = this;
	a.do({|item| arr = arr.add(a.indicesOfEqual(item).size > 1)});
	^arr.includes(true);
	}

	rejectSame {
	this.do({|item|
	var array;
	array = this.indicesOfEqual(item);
	if(array.size > 1, {array.copyRange(1, array.size-1).do({|item,index| this.removeAt(item-index);});});
	});
	}
	
	inCommon {var val;
	this.do({|item|
	var array;
	array = this.indicesOfEqual(item);
	if(array.size > 1, {array.copyRange(1, array.size-1).do({|item,index| val = this[item];});});
	});
	^val;
	}


	rejectNil {var orgArr, newArr;
	newArr = this.reject({|item| item == nil});
	^newArr;
	}
	
	rejectAllNil {var func;
	func = this.asString.replace(", nil", "").replace("nil", "").compile;
	^func.value;
	}
		
	rejectConsecutive {var newItems;
	newItems = [];
	this.do({|item, index| if(item != this[index+1], {newItems = newItems.add(item); }); });
	^newItems;
	}
	
	countConsecutive {var oldItem, arrayNew;
	oldItem = this[0];
	arrayNew = [];
	this.do({|item, index| if(oldItem != item, {arrayNew = arrayNew.add(index);}); oldItem = item;});
	^arrayNew.differentiate;
	}
	
	multyArray {arg cont=0;
	var arrayOf;
	arrayOf = [];
	this.do({|item|  arrayOf = arrayOf.add(Array.fill(item, cont));});
	^arrayOf;
	}
	
	empty {
	^this.flat.unbubble == nil;
	}
	
	full {
	^this.flat.unbubble.notNil;
	}

	expand {var arrayNew;
	arrayNew = [];
	(this.size-1).do({|item, index| arrayNew = arrayNew.add( [this[index], this[index+1]]); });
	^arrayNew;
	}
	
	itemsAt {arg indexes;
	var newArray;
	newArray = [];
	indexes.do({|item| newArray = newArray.add(this.[item])});
	^newArray;
	}
	
	indexInMin {arg item;
	^if(item > this[this.indexIn(item)], {this.indexIn(item)}, {this.indexIn(item)-1});
	}
	
	nearestInMinList { arg list;  // collection is sorted
		^this.collect({ arg item; list.at(list.indexInMin(item)) })
	}
	
	nearestInMaxList { arg list;  // collection is sorted
		^this.collect({ arg item; list.at(list.indexInMin(item)) })
	}
	
	indexInMax {arg item;
	^this.indexInBetween(item).roundUp(1);
	}
	
	itemRoundUp {arg item;
	^this[this.indexInBetween(item).roundUp(1)];
	}
	
	roundUpList {arg list;
	var newArr=[];
	list.do({|item| newArr = newArr.add(this.itemRoundUp(item)); });
	^newArr;
	}
	
	indexesInMin {arg list;
	^this.collect({ arg item; list.indexInMin(item) })
	}
	
	postAll {
	Post << this;
	^'';
	}

	indexOfAll {arg list;
	var newArray=[];
	list.do({ |item| newArray = newArray.add(this.indexOf(item)); });
	^newArray;
	}
	
	evenIndex {
	var newArray=[];
	this.do({|item, index| if(index.even, {newArray = newArray.add(item)}) });
	^newArray;
	}
	
	oddIndex {
	var newArray=[];
	this.do({|item, index| if(index.odd, {newArray = newArray.add(item)}) });
	^newArray;
	}
	
	recursiveClip {arg limit;
	var newGlobArr, array, newArray;
	newGlobArr = [];
	array = this;
	((array/limit).maxItem.roundUp(1)).do({
	newArray=[];
	array.do({|item| if(item < limit, {newArray=newArray.add(item)}) });
	newGlobArr = newGlobArr.add(newArray);
	array = array.reject({|item| item < limit});
	array = array-limit;
	});
	^newGlobArr;
	}
	
	pairsInArray {var array;
	array = [];
	([0] ++ this).doAdjacentPairs({ arg a, b; array = array.add([a, b])});
	^array;
	}
	
	collectDiff {arg list;
	var newArr = [];
	this.do({|item, index| if( list.includes(item).not, {newArr = newArr.add(item)}); })
	^newArr
	}
	
	replaceAdd {arg list;
	list = list.copyRange(this.size, list.size);
	^(this ++ list);
	}
	
	spectralmidi {
	var frequency, magnitude, freqnote, midinote, velo, bend;
	
	frequency = this[0];
	magnitude = this[1];
	
	freqnote = frequency.cpsmidi;
	
	bend = freqnote - freqnote.round(1);
	
	if(freqnote > 108, {freqnote = freqnote.midiMax(108)});
	if(freqnote < 21, {freqnote = freqnote.midiMin(21)});
	
	midinote = freqnote.round(1);
	
	velo = magnitude.linlin(0, 5.0,0,127.0);
	
	^[midinote, velo, bend];
	}
	
	toPan {var list;
	list = this.normalize * 2 - 1;
	^list;
	}
	
	readBuffers {arg varString, action="";
	var string, pathArr;
	string = "";
	this.do{|item|
	pathArr = string ++ varString ++ " = " ++ varString ++ ".add(Buffer.read(s, "  ++  "\""  ++ item ++  "\"" ++ ", action: 	{";
	string = pathArr;
	};
	pathArr = string ++ action;
	string = pathArr;
	this.do{|item|
	pathArr = string ++ "}).postln);";
	string = pathArr;
	};
	string.compile.value; 
	}
	
	readPaths {arg basicPath, tail="wav";
	var arr;
	this.do{|item|
	arr = arr.add(basicPath ++ item ++ "." ++ tail);
	}
	^arr;
	}
	
	indecesIn {arg val;
	var index, result, arr, arr2;
	arr2 = this;
	arr = arr2.round(1);
	arr.sort;
	index = arr.indexIn(val);
	result = arr2.round(1).indicesOfEqual(arr[index]);
	^result;
	}
	
	dataAlphabetize {var a, b;
	a = this;
	(a.flop[0].order).do({|item| b = b.add(a[item])});
	^b;
	}
	
	forcePut {arg index,val;
	var a;
	a = this;
	if(a[index].isNil, {
	Array.series((index+1),0,1).do({|item|
	if((item < index).and(a[item].isNil), {
	a = a.insert(index,"nil");
	});
	if(item == index, {
	a = a.insert(index,val);
	});
	});
	}, {
	a.put(index,val)
	});
	^a;
	}
	
	asPairs {
	var arr2, arr3;
	arr2 = Array.fill2D(this.size/2,2);
	arr3 = this.reshapeLike(arr2);
	^arr3	
	}

}

+SequenceableCollection {

	midicnote { ^this.performUnaryOp('midicnote') }
	cnotemidi { ^this.performUnaryOp('cnotemidi') }
	
	midioctave {arg division=12; ^this.performBinaryOp('midioctave', division) }
	midinotename { ^this.performUnaryOp('midinotename') }
	midinoteclass { ^this.performUnaryOp('midinoteclass') }
	intervalmidi { ^this.performUnaryOp('intervalmidi') }
	midiinterval { ^this.performUnaryOp('midiinterval') }
	invertion { ^this.performUnaryOp('invertion') } 
	asTimeString { ^this.performUnaryOp('asTimeString') }
	
	excess2 {arg num=1; ^this.performBinaryOp('excess2', num) }
	midiMin {arg value; ^this.performBinaryOp('midiMin', value) }
	midiMax {arg value; ^this.performBinaryOp('midiMax', value) }
	asAscii {  ^String.fill(this.size, { |i| {this[i].asInteger.asAscii}.try ? "" }) } // part of wslib 
	midipyth {|tune=440| ^this.performBinaryOp('midipyth', tune) }
	midijust {|tune=440| ^this.performBinaryOp('midijust', tune) }
	
	*equalPan {arg size, maxSize;
	var frac, list, arr1, arr2, final;
	if(maxSize.isNumber, {
	frac = 1/(maxSize+1);
	list = Array.series(maxSize, 1, 1);
	arr1 = list/ (maxSize+1) * 2 - 1;
	}, {
	arr1 = [-1, 1];
	});
	frac = 1/(size+1);
	list = Array.series(size, 1, 1);
	arr2 = list/ (size+1) * 2 - 1;
	final = arr2.linlin(arr1.first, arr1.last, -1, 1).round(0.0000001);
	^final;
	}
	
	indexOfEqualGreaterThan { arg val;
		^this.detectIndex { |item| item >= val };
	}
	
	indexInEqual { arg val; // collection is sorted
		var i, a, b;
		var j = this.indexOfEqualGreaterThan(val);
		if(j.isNil) { ^this.size - 1 };
		if(j == 0) { ^j };
		i = j - 1;
		^if((val - this[i]) < (this[j] - val)) { i } { j }
	}
	
}

 + Env {

	*dasr { arg delayTime=0.1, attackTime=0.01, sustainLevel=1.0, releaseTime=1.0, curve = -4.0;
		^this.new(
			[0, 0, sustainLevel, 0],
			[delayTime, attackTime, releaseTime], 
			curve,
			2)
	}
	
	*dasdr { arg delayTime=0.1, attackTime=0.01, sustainLevel=1.0, releaseTime=1.0, susdelayTime = 1.0, curve = -4.0;
		^this.new(
			[0, 0, sustainLevel,sustainLevel, 0],
			[delayTime, attackTime, susdelayTime, releaseTime], 
			curve,
			2)
	}
	
	*dadsdr { arg delayTime=0.1, attackTime=0.01, decayTime=0.3, 
			sustainLevel=0.5, susdelayTime = 1.0, releaseTime=1.0,
				peakLevel=1.0, curve = -4.0, bias = 0.0;
		^this.new(
			[0, 0, peakLevel, peakLevel * sustainLevel, peakLevel * sustainLevel, 0] + bias,
			[delayTime, attackTime, decayTime, susdelayTime, releaseTime], 
			curve,
			2)
	}
}

 + MIDIIn {
 
 	*allsources { MIDIClient.init;
	MIDIClient.init(MIDIClient.sources.size, MIDIClient.destinations.size);
	MIDIClient.sources.size.do({ |i|
			this.connect(i, MIDIClient.sources[i]);
	 });
	
	 }
 
 }
 
 
 + MIDIOut {
 
	noteOnMicro {arg chan, num, vel;
	this.bend(chan, (num.roundUp(1) - num).linlin(0,1,8192, 4096));
	this.noteOn(chan, num.roundUp(1), vel);
	}
	
	noteOffMicro {arg chan, num, vel;
	this.noteOff(chan, num.roundUp(1), vel);
	}
	
	noteMicro {arg chan, num, vel, dur;
	Routine({1.do({
	this.noteOnMicro(chan, num, vel);
	dur.yield;
	3.do({this.noteOffMicro(chan, num, vel);})
	});
	}).play
	}
	
	noteMIDI {arg chan, num, vel, dur, legato=1;
	this.midipattern([chan],[num],[vel],[dur],[legato],1);
	}

	midipattern {arg chan=0, note=69, vel=60, dur=1,legato=1,repetitions=1;
	var pat, event, patChan, patNote, patVel, patDur, patLegato;
	
	if(chan.isArray, {patChan = Pseq(chan, repetitions)}, { patChan = chan });
	if(note.isArray, {patNote = Pseq(note, repetitions)}, { patNote = note });
	if(vel.isArray, {patVel = Pseq(vel/127, repetitions)}, { patVel = vel/127 });
	if(dur.isArray, {patDur = Pseq(dur, repetitions)}, { patDur = dur });
	if(legato.isArray, {patLegato = Pseq(legato, repetitions)}, { patLegato = legato });
	
	pat = Pbind(\midinote, patNote, \amp, patVel, \dur, patDur, \chan, patChan, \legato, patLegato); 
	event = pat <> (type: \midi, midiout:  this);  
	1.do{event.next(Event.new).play};
	}

	midiPbind {arg chan=0, note=69, vel=60, dur=1,legato=1;
	var pat, event;
	pat = Pbind(\midinote, note, \amp, vel/127, \dur, dur, \chan, chan, \legato, legato); 
	event = pat <> (type: \midi, midiout:  this);  
	1.do{event.next(Event.new).play};
	}
	
	midiChord {arg notes=[69,69],vels=[69,69],arpeg=0.01, lengh=10, sort=\normal, jitterNum=10;
	var jitter, jitter2, legato, chordNotes, chordVels, indexArr;

	case
	{sort == \normal} {chordNotes = notes}
	{sort == \downUp} {chordNotes = notes.sort}
	{sort == \upDown} {chordNotes = notes.sort.reverse}
	{sort == \random} {chordNotes = notes.scramble};
	
	indexArr = [];
	chordNotes.do({|item, index| indexArr = indexArr.add(chordNotes.indexOf(item))});
	chordVels = vels.itemsAt(indexArr); 
	
	legato = lengh * 0.5; 
	jitter = arpeg/10;
	jitter2 = legato/arpeg/jitterNum;
	
	this.midiPbind([0], Pseq(chordNotes.round(1), 1), Pseq(chordVels.round(1), 1), Pseq(Array.fill(chordNotes.size, {arpeg+[jitter, jitter.neg].choose}), 1), 	Pseq(Array.fill(chordNotes.size, {(legato/arpeg)+rrand(jitter2, jitter2.neg)}), 1));
	
	}
	
	noteMIDIDel {arg chan, num, vel, dur, del;
	Routine({1.do({
	del.yield;
	this.noteOn(chan, num, vel);
	dur.yield;
	3.do({this.noteOff(chan, num, vel);})
	});
	}).play
	}

 	allNotesOn {var allnotes, c, z;
	allnotes = (21,22..108);
		c = Pbind(\midinote, Pseq(allnotes,1), \amp, 0.5,  \dur, 0.01, \legato, 0.001); 
		z = c <> (type: \midi, midiout:  this); 
	1.do{z.next(Event.new).play};
	
	}	
	
}

+ Server {
	
	bootAndMIDI {
	this.waitForBoot({MIDIIn.connectAll;});	
	}
	
	bufFreeRange{arg lo=0, hi=10, time=0.001;
	var total, counter;
	total = hi-lo;
	counter = lo;
	Routine({
	total.do{this.sendMsg("/b_free", counter); time.yield; counter = counter + 1};
	('buffers ' ++ lo.asString ++ ' to ' ++ hi.asString ++ ' are now free').postln; 
	}).play;

	}
	
	bufArr {var arr;
	this.cachedBuffersDo({ |buf| arr = arr.add(buf) });
	if(arr.notNil, {
	^arr;
	}, {
	"No Buffers in Server".warn;
	});
	}
	
	freeBuffers {var arr;
	arr = this.bufArr;
	if(arr.notNil, {
	arr.do{|item| item.free};
	});
 	}
 	
}

+ Buffer {
	*queryAll {var array;
	array = Server.default.bufferArray;
	array = array.reject({|item| item == nil});
	^array;
	}
	
	seconds {var result;
	result = numFrames/sampleRate;
	^result;	
	}
	
	pvseconds {var result;
	result = (numFrames-(0.05*sampleRate))/sampleRate/4;
	^result;	
	}

}

+ Score {


	sortAll {
			score = score.sort({ arg a, b; a[0] <= b[0] });
		}
	
}

+ String {
	
	cnotemidi {
		var twelves, ones, octaveIndex, midis;
		
		midis = Dictionary[($c->0),($d->2),($e->4),($f->5),($g->7),($a->9),($b->11)];
		ones = midis.at(this[0].toLower);
		
		if( (this[1].isDecDigit), {
			octaveIndex = 1;
		},{
			octaveIndex = 2;
			if( (this[1] == $#) || (this[1].toLower == $s) || (this[1] == $+), {
				ones = ones + 1;
			},{
				if( (this[1] == $b) || (this[1].toLower == $f) || (this[1] == $-), {
					ones = ones - 1;
				});
			});
		});
		twelves = (this.copyRange(octaveIndex, this.size).asInteger) * 12;
		^(twelves + ones)+12
	}
	
	notecps { ^(this.cnotemidi.midicps)}
	
	intervalmidi {var d;
	d = Dictionary["Uni"-> 0, "m2" -> 1, "M2" -> 2, "m3" -> 3, "M3" -> 4, "P4" -> 5, "A4" -> 6, "D5" -> 6, "P5" -> 7, "m6" -> 8, "M6" -> 9, "m7" -> 10, "M7" -> 11, "Oct" -> 12]; 
	^d.at(this);
	
	}
	
	invertion {var d, v;
	d = Dictionary["Uni"-> 0, "m2" -> 1, "M2" -> 2, "m3" -> 3, "M3" -> 4, "P4" -> 5, "A4" -> 6, "D5" -> 6, "P5" -> 7, "m6" -> 8, "M6" -> 9, "m7" -> 10, "M7" -> 11, "Oct" -> 12]; 
	v = 12 - this.intervalmidi;
	^d.findKeyForValue(v);
	
	}
		
	path {
		CocoaDialog.getPaths({ arg paths;
		paths.do({ arg p;
			p.postln;
		})
		},{
			"cancelled".postln;
		});
		}
		
	filesInDir {
	var paths, arr;
	paths = (this ++ "/*").pathMatch;
	paths.do({|item| arr = arr.add(item.basename);})
	^arr;
	}
	
	stripFileType {
	var path;
	path = this;
	path = path.dirname ++ "/" ++ path.filename;
	^path;
	}
	
	stripFileTypeArr {
	var path, arr;
	path = this;
	path = path.filesInDir;
	path.do({|item| arr = arr.add(item.filename);});
	^arr; 
	}
	
	stripPVTypeArr {
	var path, arr;
	this.stripFileTypeArr.do({|item| arr = arr.add(item.replace(".", "").asSymbol); });
	^arr; 
	}
	
	findFullFileName {arg fileName;
	var a, b, c;
	a = this.filesInDir;
	a.do({|item, index| if(item.filename == fileName.asString, {b = index}) });
	if(b.notNil, {c = a[b]});	
	^c;
	}
	
	findFileExtension {arg fileName;
	var a, b;
	a = this.findFullFileName(fileName);
	b = "." ++ a.split($.)[1];	
	^b;
	}
	
	isStringNumber {
	var bol, item;
	item = this;
	if(item.contains("."), {item = item.replace(".")});
	bol = "1234567890".ascii.includesAll(item.ascii);
	^bol;
	}
	
	splitArgArr {
	var begin, end, b, function;
	function = this;
	begin = function.find(" ")+1;
	end = function.find(";")-1;
	b = function.copyRange(begin, end).replace("=", "/").replace(",", "/").replace(" ", "").split;
	b = b.reject({|item, index| index.odd});
	^b;
	}

	splitArgValsArr {
	var begin, end, b, function;
	function = this;
	begin = function.find(" ")+1;
	end = function.find(";")-1;
	b = function.copyRange(begin, end).replace("=", "/").replace(",", "/").replace(" ", "").split;
	b = b.reject({|item, index| index.even});
	^b;
	}
	
	filename {var name, arr, result;
	name = this.basename;
	arr = name.split($.);
	if(arr.size > 1, { 
	result = name.findReplace("." ++ arr[arr.size-1], "");
	}, {
	result = name;
	});
	^result;
	}
	
	charPix {arg letterSize=12,lower=0.5,upper=0.75;
		var a;
		this.do{|item| if(item.isLower, {a = a.add(lower)}, {a = a.add(upper)})}
		^(a.sum*letterSize)
	}
	
	}

+ Interpreter {

	switchServer {
		if(Server.default == Server.local, { 
			s.quit;
			Server.default = s = Server.internal.boot; 
			},{ 
			s.quit;
			Server.default = s = Server.local.boot; 
		});
		
		}

	}
	
+ Object {

	postlnbool {arg bool=true;
	
	if(bool, {
	this.postln;
	});
	
	}
	
	postbool {arg bool=true;
	
	if(bool, {
	this.post;
	});
	
	}
	
	getVarInfo {
	var vars, vals;
	this.getSlots.do({|item, index| 
		if(index.even, {vars = vars.add(item)}, {vals = vals.add(item)});
	});
	^[vars,vals];		
	}
	
	getVars {
	var vars;
	this.getSlots.do({|item, index| 
		if(index.even, {vars = vars.add(item)});
	});
	^vars;		
	}
	
	getVals {
	var vals;
	this.getSlots.do({|item, index| 
		if(index.odd, {vals = vals.add(item)});
	});
	^vals;		
	}
	
	getVarPairs {
	var result;
	result = this.getVarInfo.flop;	
	^result;
	}
	
	getValFromVar {arg instVar;
	var arr, result;
	arr = this.getVarInfo;
	result = arr[1][arr[0].indexOf(instVar)];
	^result;	
	}
	
	pushSlots {arg array;
	var count=0;
	array.pairsDo {|key, value|
	this.slotPut(count, value);
	count = count+1;
	}
	}
	
}


+ GUI {

		*swingOSC {arg pathName;
		pathName = pathName ?? { "/Applications/SwingOSC" };
		("cd " ++ pathName ++ ";  sh SwingOSC_TCP.command").unixCmd;
		}
		
		*connect {
		NetAddr( "127.0.0.1", 57111 ).connect;
		this.swing;  
		this.current.postln;
		}

	}
	
+ SoundFile {

	*numFrames {arg pathName;
	var file, number;
	file = this.new;
	file.openRead(pathName);
	number = file.numFrames;
	file.close;
	^number;
	}
	
	*duration {arg pathName;
	var file, number;
	file = this.new;
	file.openRead(pathName);
	number = file.duration;
	file.close;
	^number;
	}
	
}

+ Segmentation {

	writeFiles {var step=0, path, file, array, array2, name, start, end, newFile;
	
	Routine({
	path = filename;
	file = SoundFile.new;
	file.openRead(path);
	array = FloatArray.newClear(file.numFrames * file.numChannels);
	(file.numFrames/44100/1000).yield;
	file.readData(array);
	"loading...".postln;
	(file.numFrames/44100/50).yield;
	name = filename.basename.copyRange(0,filename.basename.size-5).split($ )[0];
	("mkdir " ++ filename.dirname ++"/" ++ name ++ "cut").unixCmd;
	".".postln;
	0.1.yield;
	
	outputarray.size.do({
	start = outputarray[step][0];
	end = (outputarray[step][0]+(outputarray[step][1]*44100).round(1));
	array2 = array.copyRange(start.asInteger, end.asInteger);
	0.01.yield;
	newFile=SoundFile.new.headerFormat_("WAV").sampleFormat_("int16").numChannels_(file.numChannels);
	0.01.yield;
	newFile.openWrite(((filename.dirname)++"/" ++ name ++ "cut/" ++step++".wav").postln);
	0.01.yield;
	newFile.writeData(array2); 
	0.01.yield;
	newFile.close;
	step = step + 1;
	});
	file.close;
	"done".postln;
	}).play
	}
	
	writeCutFiles {var step=0, path, file, array, array2, name, start, end, newFile;
	
	Routine({
	path = filename;
	file = SoundFile.new;
	file.openRead(path);
	array = FloatArray.newClear(file.numFrames * file.numChannels);
	(file.numFrames/44100/1000).yield;
	file.readData(array);
	"loading...".postln;
	(file.numFrames/44100/50).yield;
	name = filename.basename.copyRange(0,filename.basename.size-5).split($ )[0];
	("mkdir " ++ filename.dirname ++"/" ++ name ++ "cut").unixCmd;
	".".postln;
	0.1.yield;
	
	outputarray.size.do({
	start = outputarray[step][0];
	if(outputarray[step] != outputarray.last, {
	end = outputarray[step+1][0];
	}, {
	end = (outputarray[step][0]+(outputarray[step][1]*44100).round(1));
	});
	array2 = array.copyRange(start.asInteger, end.asInteger);
	0.01.yield;
	newFile=SoundFile.new.headerFormat_("WAV").sampleFormat_("int16").numChannels_(file.numChannels);
	0.01.yield;
	newFile.openWrite(((filename.dirname)++"/" ++ name ++ "cut/" ++step++".wav").postln);
	0.01.yield;
	newFile.writeData(array2); 
	0.01.yield;
	newFile.close;
	step = step + 1;
	});
	file.close;
	"done".postln;
	}).play
	}

}

//extensions to wslib


+ SimpleMIDIFile {

	arr { |inst, amp = 0.2| // amp: amp when velo == 127
		var thisFile;
		inst = ( inst ? 'default' ).asCollection;
		
		// todo: create multi-note events from chords, detect rests
		
		if( timeMode == 'seconds' )
			{ thisFile = this }
			{ thisFile = this.copy.timeMode_( 'seconds' ); };
		 ^(
			({ |tr|
				var sustainEvents, deltaTimes;
				sustainEvents = thisFile.noteSustainEvents( nil, tr );
				if( sustainEvents.size > 0 )
					{ 
					sustainEvents = sustainEvents.flop;
					deltaTimes = sustainEvents[1].differentiate;
					[inst.wrapAt( tr + 1 ), 
					deltaTimes ++ [0], 
					[\rest] ++ sustainEvents[4],
					[0] ++ ( sustainEvents[5] / 127 ) * amp,
					[0] ++ sustainEvents[6];
					] 
					}
					{ nil }
				}!this.tracks).select({ |item| item.notNil })
			);
		}
		
}

//+ SimpleMIDIFile {
//
//	trackSilence {arg track=1, thresh=0;
//	var timeOnTrk, timeOffTrk, b,c,d,e, f, removeOff, arr, removeOffArr;
//	
//	arr = this.midiEvents.flop[1].indexOfAll(this.endOfTrack.flop[1]);
//	if(arr.includes(0), {arr.remove(0);});
//	arr = arr.rejectNil;
//	
//	if(this.midiEvents.itemsAt(arr).notNil, {
//	this.midiEvents.removeAll(this.midiEvents.itemsAt(arr));
//	});
//	
//	removeOff = this.midiEvents.flop[2].findAll([\noteOff, \noteOff]);
//
//	if(removeOff.notNil, {
//	removeOffArr = this.midiEvents.itemsAt(removeOff);
//	this.midiEvents.removeAll(removeOffArr);
//	});
//	
//	timeOnTrk = this.midiTrackTypeEvents(track, \noteOn).flop[1]; //get times for noteOn events in track
//	timeOffTrk = this.midiTrackTypeEvents(track, \noteOff).flop[1]; //get times for noteOff events in track
//	
//	b = timeOffTrk.collectDiff(timeOnTrk); //difference between timeOffTrk and timeOnTrk
//
//	d = (timeOnTrk ++ this.endOfTrack(track).flat[1]); //add endoftrack to noteOn
//
//	c = d.roundUpList(b); //looks for closest item after noteOff
//
//	e = [b, c].flop; //pairs of noteOff and noteOn for silence
//	
//	e.do{|item| 
//	if(item[1] - item[0] > thresh, {
//	f = f.add(item);
//	});
//	}; //time threshold, ignores silences smaller than the threshold
//
//	if(timeOnTrk[0] != 0, {f = [[0, timeOnTrk[0]]] ++ f}); //if first noteOn is not 0, then add silence
//	
//	^f;
//
//	}
//	
//	phraseStructure {arg numTracks=4, gridScale=2.6, sysPage=4, color=\color, winSize=\small, thresh=0;
//var w, rect, squareWidth, squareHeight, trackDiv, trackWidth, trackHeight, toPix, patTrack, trackPos, busyOrder, pat, newPix, thisPix, colorArr, track1Pix, pixLimit, screenArr, lenghPix, toSecs, amountWin, secsPage, nameNum, fileName, lastFunc, endofThisTrack, inWhichRow, thisLine;
//
//		if(winSize == \fullScreen, {
//			screenArr =  SCWindow.screenBounds.asArray;
//			rect = Rect(screenArr[0],screenArr[1]+20, screenArr[2], screenArr[3]-40);
//			}, {
//			rect = Rect(128, 64, 400, 400);
//		});
//		
//		squareWidth = (rect.width-40);
//		squareHeight =(rect.height-40);
//		trackDiv = numTracks * sysPage;
//		trackWidth = squareWidth / trackDiv;
//		trackHeight = squareHeight / trackDiv;
//		toPix = trackWidth / gridScale;
//		
//		lenghPix = this.length * toPix;
//		amountWin = (lenghPix / (squareWidth*sysPage)).roundUp(1);
//		toSecs = gridScale / trackWidth;
//		secsPage = (squareWidth*sysPage) * toSecs;
//
//		"amount of pages: ".post; amountWin.postln;
//		"amount of time per page: ".post; secsPage.postln;
//		
//		nameNum = this.pathName.findAll("/").maxItem + 1;
//		fileName = this.pathName.copyRange(nameNum, this.pathName.size-1);
//
//		
//		w = SCWindow(fileName, rect).front;
//		w.view.background_(Color.white);
//		w.drawHook = {var step=0, trackStep=0;
//
//		
//		patTrack = Pseq([0,1,2,3,2,1], inf).asStream; //pattern for coloring tracks
//
//			//grid
//			trackDiv.do{
//				// set the Color
//				Pen.color = Color.grey;
//				Pen.addRect(
//					Rect(20, 20+(step*trackHeight), squareWidth, (rect.height-(40+(step*trackHeight))))
//				);
//				Pen.perform(\stroke);
//				step  = step + 1;
//				
//			};
//			step=0;
//			trackDiv.do{
//				// set the Color
//				Pen.color = Color.grey;
//				Pen.addRect(
//					Rect(20+(step*trackWidth), 20, (rect.width-(40+(step*trackWidth))), squareHeight)
//				);
//				Pen.perform(\stroke);
//				step  = step + 1;
//				
//			};
//			//function for ending
//			lastFunc = {arg barStart, barEnd, whereBar, position;
//			Pen.width = 1.5;
//			Pen.color = Color.white;		
//			Pen.addRect(
//			Rect(20+barStart, 20+(trackHeight*(whereBar*numTracks+position)), barEnd, trackHeight)
//			);
//			Pen.perform([\stroke]);
//			Pen.color = Color.white;		
//			Pen.addRect(
//			Rect(20+barStart, 20+(trackHeight*(whereBar*numTracks+position)), barEnd, trackHeight)
//			);
//			Pen.perform([\fill]);
//			 };
//			 
//			//info
//			trackPos=1;
//			numTracks.do{
//			track1Pix = (this.trackSilence(trackPos, thresh).flat * toPix);
//			newPix = track1Pix.recursiveClip(squareWidth);
//			//for different colors (change this for more tracks)
//			busyOrder = [0,0,0,0.6];
//			busyOrder.put(patTrack.next, rrand(0.8, 0.5));
//			if(color == \color, {
//			pat = Pseq([busyOrder, [0,0,0,0.2]], inf).asStream;
//			} , {
//			pat = Pseq([[0,0,0,0.0], [0,0,0,0.6]], inf).asStream;
//			});
//			trackStep=0;
//
//			sysPage.do({
//			step=0;
//			thisPix = newPix[trackStep] ++ [squareWidth];
//			thisPix.do({ 
//			pixLimit = thisPix.pairsInArray[step].differentiate;
//				// set the Color
//				colorArr = pat.next;
//				if(colorArr.notNil, {
//				Pen.color = Color.new(colorArr[0], colorArr[1], colorArr[2], colorArr[3]);
//				});
//				//Pen.color = Color.green(colorArr[0],colorArr[1]);
//				Pen.addRect(
//					Rect(20+pixLimit[0], 20+(trackHeight*((trackPos+(trackStep*numTracks))-1)), pixLimit[1], trackHeight)
//				);
//				Pen.perform([\fill]);
//				step  = step + 1;
//				
//			
//			 });
//
//			 trackStep = trackStep + 1;
//			 pat.next;
//			 });
//			 
//			//ending
//			endofThisTrack = this.endOfTrack(trackPos).flat[1] * toPix;
//			inWhichRow = [(endofThisTrack)].recursiveClip(squareWidth); //find out rows
//			step = 0;
//			numTracks.do({
//			thisLine = inWhichRow[step];
//			if(thisLine.notNil, {
//			if(thisLine.notEmpty, {
//			lastFunc.value(thisLine[0], squareWidth-thisLine[0], step, (trackPos-1));
//			});
//			}, {
//			lastFunc.value(0, squareWidth, step, (trackPos-1));
//			});
//			step = step + 1;
//			});
//			
//			trackPos = trackPos + 1;
//			 
//		};	
//		
//		
//		
//		};
//		
//		w.refresh;
//
//	}
//	
//	trackArrMIDIEvents {
//	^midiEvents.flop[0].rejectSame;
//	}
//	
//	trackArrMetaEvents {
//	^metaEvents.flop[0].rejectSame;
//	}
//	
//	equalCutArray {arg cut;
//	var arr;
//	arr = (cut, cut*2.. midiEvents.flop[1].maxItem) ++ midiEvents.flop[1].maxItem;
//	^arr;
//	}
//	
//	eggCut {arg arr, tracksMIDI, otherPath;
//	var globArrMIDI, globArrMeta, noteTypeArr, ccArr, programArr, trackName, timeSignature, tempo, copyRight, keySignature, whichCut, lowLimit, highLimit, groupMIDI, initTracks, firstTracks, finishNote, groupMeta, groupMIDI2, groupMeta2 , newPath, cc, noteType, program; 
//	
//	//if other path is nil then ignore, otherwise make new path
//	if(otherPath.notNil, {pathName = otherPath});
//	
//	//groups midi events in different cuts
//	globArrMIDI = []; 
//	midiEvents.do({|item|
//	var group;
//	group = (arr.indexInBetween(item[1]).roundUp(1));
//	globArrMIDI = globArrMIDI.add([group, item]);
//	});
//	
//	//groups meta events in different cuts
//	globArrMeta = [];
//	metaEvents.do({|item|
//	var group;
//	group = (arr.indexInBetween(item[1]).roundUp(1)); 
//	globArrMeta = globArrMeta.add([group, item]);
//	});
//	
//	noteTypeArr = [];
//	ccArr = [];
//	programArr = [];
//	trackName = [];
//	timeSignature = nil; //quitar esto para hacer la clase
//	tempo = nil;
//	copyRight = nil;
//	keySignature = nil;
//	
//	//loop starts
//	//midiEvents:
//	//finds low and high limits for each group
//	whichCut = 0;
//	
//	arr.do{
//	
//	lowLimit = if(arr[whichCut-1].notNil, {lowLimit = arr[whichCut-1]}, {lowLimit = 0});
//	highLimit = arr[whichCut];
//	
//	groupMIDI = [];
//	globArrMIDI.do({|item| 
//		if(item[0] == whichCut, {
//			groupMIDI = groupMIDI.add([item[1][0],item[1][1]-lowLimit,item[1][2], item[1][3], item[1][4], item[1][5]] )}); }); //acces midiEvents for each group
//	
//	midiEvents = groupMIDI; //new midiEvents to SimpleMIDIFile
//	
//	initTracks = [];
//	tracksMIDI.do({|tracks|
//	initTracks = initTracks.add(groupMIDI[groupMIDI.flop[0].indexOf(tracks)]);
//	}); 
//	
//	firstTracks = [];
//	initTracks.do({|item|
//		if(item[2] == \noteOff, {
//		firstTracks = firstTracks.add([item[0], 0, \noteOn, item[3], item[4], item[5]]);
//		}); //inserts noteOns if the first midiEvent is a noteOff
//	});
//	
//	if(firstTracks.notEmpty, {
//	this.addAllMIDIEvents(firstTracks);
//	groupMIDI = groupMIDI.add(firstTracks)
//	}); //adds first noteOns in tracks if there are any
//	
//	if(ccArr.notEmpty, {
//	this.addAllMIDIEvents(ccArr);
//	groupMIDI = groupMIDI.add(ccArr);
//	}); //adds ccs is any
//	
//	if(programArr.notEmpty, {
//	this.addAllMIDIEvents(programArr);
//	groupMIDI = groupMIDI.add(programArr);
//	}); //adds program if any
//	
//	//goes individually from track to track finding out last noteType, cc and program
//	noteTypeArr = [];
//	ccArr = [];
//	programArr = [];
//	
//	tracksMIDI.do({|track|
//	groupMIDI.do({|item|
//	if(item[0] == track, { item;
//	case
//		{(item[2] == \noteOn).or(item[2] == \noteOff)} {noteType = item;}
//		{item[2] == \cc} {cc = item;}
//		{item[2] == \program} {program = item;}
//		})
//	});
//	noteTypeArr = noteTypeArr.add(noteType);
//	ccArr = ccArr.add(cc);
//	programArr = programArr.add(program);
//	
//	});
//	
//	noteTypeArr = noteTypeArr.rejectNil;
//	//adds note off is last midiEven found is a noteOn
//	finishNote = [];
//	noteTypeArr.do({|item| 
//		if(item[2] == \noteOn, {
//			finishNote = finishNote.add([item[0], (highLimit-lowLimit), \noteOff, item[3], item[4], item[5]]);
//		});
//	});
//	
//	this.addAllMIDIEvents(finishNote); //add ending to not ons
//	
//	//metaEvents:
//	
//	groupMeta = [];
//	globArrMeta.do({|item| 
//		if(item[0] == whichCut, {
//			groupMeta = groupMeta.add([item[1][0],item[1][1]-lowLimit,item[1][2], item[1][3], item[1][4], item[1][5]] 
//		)}); 
//	}); //acces midiEvents for each group
//	
//	metaEvents = groupMeta; //new midiEvents to SimpleMIDIFile
//	
//	if(copyRight.notNil, {
//	copyRight.put(1,0);
//	metaEvents = metaEvents.add(copyRight);
//	});
//	
//	if(timeSignature.notNil, {
//	timeSignature.put(1,0);
//	metaEvents = metaEvents.add(timeSignature);
//	});
//	
//	if(keySignature.notNil, {
//	keySignature.put(1,0);
//	metaEvents = metaEvents.add(keySignature);
//	});
//	
//	if(tempo.notNil, {
//	tempo.put(1,0);
//	metaEvents = metaEvents.add(tempo);
//	});
//	
//	if(trackName.notEmpty, {
//	trackName.do({|item| item.put(1,0);
//	metaEvents = metaEvents.add(item);
//	});
//	});
//	
//	//goes individually from track to track finding out last noteType, cc and program
//	//trackName = [];
//	groupMeta.do({|item|
//			case
//			{item[2] == \copyright} {copyRight = item;}
//			{item[2] == \timeSignature} {timeSignature = item }
//			{item[2] == \keySignature} {keySignature = item }
//			{item[2] == \tempo} {tempo = item }
//			{item[2] == \trackName} {trackName = trackName.add(item) }
//	});
//	
//	
//	groupMIDI2 = [];
//	midiEvents.do({|item| groupMIDI2 = groupMIDI2.add(item.rejectNil)});
//	midiEvents = groupMIDI2;
//	
//	groupMeta2 = [];
//	metaEvents.do({|item| groupMeta2 = groupMeta2.add(item.rejectNil)});
//	metaEvents = groupMeta2;
//	
//	this.sortMetaEvents;
//	 
//	newPath = pathName.copyRange(0, pathName.size-5) ++ "_" ++ (whichCut+1).asString ++ ".mid";
//	
//	this.write( newPath ); //write new MIDIfile with information
//	
//	whichCut = whichCut + 1;
//
//	}
//
//	}
//
//	getStartIndex {arg track, type=\noteOn, newTime, adjTempo;
//	var trackInfo, times, times2, step;
//	trackInfo = this.midiTrackTypeEvents(track, type);
//	times = trackInfo.flop[1]; //get times for routine
//	times2 = times*adjTempo; //get times with adjust tempo
//	step = (times2.indexInBetween(newTime).roundUp).asInteger;
//	^step
//	}
//
//	playTrackType {arg track, type=\noteOn, function = {arg chan, note, vel; [chan,note,vel].postln}, newTime = 0, adjTempo=1;
//	var trackInfo, times, times2, lock, step, wait;
//	//gets track info for midiFile
//	if(this.timeMode != \seconds, {this.timeMode = \seconds});
//	trackInfo = this.midiTrackTypeEvents(track, type);
//	times = trackInfo.flop[1]; //get times for routine
//	times2 = times*adjTempo; //get times with adjust tempo
//	step = (times2.indexInBetween(newTime).roundUp).asInteger;
//	wait = (times2[step] - newTime);
//	if(wait == 0, {lock = 0}, {lock = 1}); //if wait is not 0, then ignore first yield 
//	//routine
//	Routine({
//	wait.yield; //initial wait time
//	trackInfo.size.do({
//	if(lock == 0, {
//	(times.differentiate[step]*adjTempo).yield;
//	});
//	lock = 0;
//	function.value(trackInfo.flop[3][step], trackInfo.flop[4][step], trackInfo.flop[5][step]);
//	//trackInfo[step].postln;
//	step = step + 1;
//	})}).play;
//	
//	}
//	
//	noteSustain {var arr;
//	if(this.timeMode != \seconds, {this.timeMode = \seconds});
//	arr = this.noteSustainEvents.collect({|item| [ item[0], item[1], item[4], item[5], item[6] ];});
//	arr.sort({ arg a, b; b[1] >= a[1] });
//	^arr;
//	}
//
//
//	playSustain {arg function = {arg track, note, vel, dur; [track,note,vel,dur].postln}, newTime = 0, adjTempo=1;
//	var sustainInfo, times, times2, lock, step, wait;
//
//	sustainInfo = this.noteSustain;
//	times = sustainInfo.flop[1]; //get times for routine
//	times2 = times*adjTempo; //get times with adjust tempo
//	step = (times2.indexOfEqualGreaterThan(newTime)).asInteger;
//	wait = (times2[step] - newTime);
//	if(wait == 0, {lock = 0}, {lock = 1}); //if wait is not 0, then ignore first yield 
//	//routine
//	Routine({
//	wait.yield; //initial wait time
//	sustainInfo.size.do({
//	if(lock == 0, {
//	(times.differentiate[step]*adjTempo).yield;
//	});
//	lock = 0;
//	function.value(sustainInfo.flop[0][step], sustainInfo.flop[2][step], sustainInfo.flop[3][step], sustainInfo.flop[4][step]);
//	//trackInfo[step].postln;
//	step = step + 1;
//	})}).play;
//
//}
//
//	
//	sectionPlay {arg track, function = {arg val; val.postln}, newTime = 0, adjTempo=1;
//	var times, section, lock, step, step2=0;
//	if(this.timeMode != \seconds, {this.timeMode = \seconds}); //make sure it's seconds
//	
//	times = this.trackSilence(track).flat*adjTempo; //get silence times for routine
//	step = (times.indexInBetween(newTime).roundUp).asInteger; //get steps
//	times = times-newTime; //get new arr taking newTime in consideration
//	times = times.reject({|item| item.isNegative }); //get rid of negative numbers
//	
//	section = step + 1; //sections start in 1
//	//routine
//	Routine({
//
//	times.size.do({
//	function.value(section);
//	
//	(times.differentiate[step2]).yield;
//	
//	step = step + 1;
//	section = step + 1;
//	step2 = step2 + 1;
//	})}).play;
//	
//	}
//
//	phrasePlay {arg track, func= {arg sec; sec.postln;}, funcSilence = {arg silence; silence.postln;}, newTime=0, adjTempo=1;
//	this.sectionPlay(track, {arg val;
//	var section, silence;
//	"Track".post; track.post;
//	if(val.odd, {" : Play: section: ".post;
//	//this is the sections starting with 1 (for better order);
//	section = val+1/2;
//	//specific info
//	func.value(section);
//	}, {
//	" : Silence: ".post;
//	silence = val/2;
//	funcSilence.value(silence);
//	}); 
//	}, 
//	newTime, //this is the new step 
//	adjTempo);
//
//	}
//
//	noteValues {arg track=1;
//	var timeOnTrk, timeOffTrk, noteValue;
//	  
//	timeOnTrk = this.midiTrackTypeEvents(track, \noteOn); //get times for noteOn events in track
//	timeOffTrk = this.midiTrackTypeEvents(track, \noteOff); //get times for noteOff events in track
//	
//	noteValue = [];
//	timeOnTrk.do({|item|
//	var array, type, note, time, noteOffArr;
//	array = item;
//	type = array[2];
//	note = array[4];
//	time = array[1];
//	
//	noteOffArr = [];
//	timeOffTrk.do({|item| if((item[2] == \noteOff).and(item[4] == note).and(item[1] > time), {noteOffArr = noteOffArr.add(item)}); });
//	if(noteOffArr.notEmpty, {
//	noteValue = noteValue.add(noteOffArr[0][1]-time);
//	});
//	});
//	
//	^noteValue;
//
//	}
//
//	playTrackNotes {arg track=1, duration, function = {arg chan, note, vel, dur; [chan,note,vel,dur].postln}, newTime=0, adjTempo=1;
//	var trackInfo, times, step;
//	if(this.timeMode != \seconds, {this.timeMode = \seconds});
//	trackInfo = this.midiTrackTypeEvents(track, \noteOn);
//	times = trackInfo.flop[1]*adjTempo; //get times for routine
//	"index start: ".post;
//	step = (times.indexInBetween(newTime).roundUp).asInteger.postln;
//	this.playTrackType(track, \noteOn, {arg chan,note,vel,dur; 
//	function.value(chan,note,vel,duration[step]);
//	step = step + 1;
//	}, newTime, adjTempo);
//	}
//
//}
