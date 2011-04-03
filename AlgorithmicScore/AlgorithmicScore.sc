AlgorithmicScore {var <>resize=1.4266666666667, <>resize2, <>func, <>w, <>w2, <>w3, <>w4, <>w5, <>g, <>func2, extra, octava, <>down, n, <>yellow, <>white, taskOn, yellow2, white2, yellow3, white3, yellow4, white4, taskOn2, synth, synth2, trebleClef, bassClef, altoClef, <>movie, x, y, <>staffArr, <>clefArr, adjWin1, addWin1, adjWin2, addWin2, winLeft, winRight, clefFunc, <>storeArrayClef, <>pianoArray, storeZoom, drawLines, <>staffGap, windowType=1, <>spacing, <>noteAdjust=13.75, <>cocoaAdj, clearStaffArr,<>clock, text, text2, <>express, tagWindow;

	*new {arg firstResize=1.5, adjNote=13.75, string="score";
			^super.new.init(firstResize, adjNote, string);
		}
	
	init {arg firstResize=1.5, adjNote=13.75, string="score";
		resize = firstResize;
		trebleClef = 71.asAscii.asString;
		bassClef = 63.asAscii.asString;
		altoClef = 66.asAscii.asString;
		noteAdjust = adjNote;
		w = GUI.window.new(string, Rect( 64, 860, 960*resize, 600*resize )).front;
   		w.view.background_( Color.white );
   		text = GUI.staticText.new(w, Rect(128, 64, 400, 400));
   		text2 = GUI.staticText.new(w, Rect(128, 64, 400, 400));
   		express = GUI.staticText.new(w, Rect(128, 64, 400, 400));
   		GUI.current.postln;
		if(GUI.current == CocoaGUI, {clock =  AppClock}, {clock =  SystemClock});
	}	
	
	*screenBounds {arg adjNote=13.75, string="score", firstResize=1.4266666666667;
	^super.new.init2(adjNote, string);
	}
	
	init2 {arg adjNote=13.75, string="score", firstResize=1.4266666666667;
	resize = firstResize;
	trebleClef = 71.asAscii.asString;
	bassClef = 63.asAscii.asString;
	altoClef = 66.asAscii.asString;
	noteAdjust = adjNote;
	w = GUI.window.new(string, GUI.window.screenBounds).front;
   	w.view.background_( Color.white );
   	text = GUI.staticText.new(w, Rect(128, 64, 400, 400));
   	text2 = GUI.staticText.new(w, Rect(128, 64, 400, 400));
   	express = GUI.staticText.new(w, Rect(128, 64, 400, 400));
   	GUI.current.postln;
   	if(GUI.current == CocoaGUI, {clock =  AppClock}, {clock =  SystemClock});
	}
	
	image {arg pathName, pix1=1101, pix2=674, above=nil;
	if(movie.notNil, {movie.remove});
	if((windowType == nil).or(windowType == 0), {this.clearWindow});
	if(above == nil, {above = w.bounds.asArray[2]-pix2/18});
	pathName = pathName ?? { "/DeskTop/test.mov" };
	windowType = 1;
	movie = GUI.movieView.new(w, Rect((w.bounds.asArray[2]-pix1/2), above, pix1, pix2))
	.showControllerAndAdjustSize( false, false )
	.path_(pathName)
	.background_( Color.white )
	.start;
	}	
	
	view {arg newStaffArr=0;
	var step1, step2, clef, fontSize, fontArr, adjClef, zoom;
	step1 = 0;
	step2 = 0;
	
	if( movie.notNil, {
	movie.remove;
	});
	zoom = storeZoom;
	clefArr = storeArrayClef;
	if((staffArr.notNil).and(newStaffArr == 0), {
		staffArr.do({|item|
			item.remove;
		});
	});

	staffArr = [];

	resize = (w.bounds.asArray[3]/600) * zoom;
	resize2 = w.bounds.asArray[2]/(780*resize);
	
	func = {arg first=1, size=1, size2=1, color=\black;
	var colFunc;
	case
		{color == \black} {colFunc = Color.black}
		{color == \blue} {colFunc = Color.blue}
		{color == \red} {colFunc = Color.red};
	first = first + (first-1);
	first = (first)*40;
	5.do({
	GUI.pen.color = colFunc;
	GUI.pen.line( 750*size2 @ (first+down)*size, 30 @ (first+down)*size );
	first = first + 10;
	});
	};
	
	clefFunc = {var fontType;
		
		clefArr.do({|item|  

		case
		{item == \treble} {clef = trebleClef; adjClef = 0;}
		{item == \bass} {clef = bassClef; adjClef = -0.8;}
		{item == \alto} {clef = altoClef; clef = altoClef; if(GUI.current == CocoaGUI, {adjClef = 0;}, {adjClef = 6;});}
		{item == \tenor} {clef = altoClef; if(GUI.current == CocoaGUI, {adjClef = -10;}, {adjClef = -5;});};
		
		if(item.notNil, 
				{staffArr = staffArr.add( GUI.staticText.new( w, Rect( 38*resize, ((3.9+adjClef+down)+step1)*resize, (170+step2)*resize, 100*resize )).string_( clef )); 
				clearStaffArr = staffArr;
				fontArr = fontArr.add( item );
				});
		step1 = step1 + (80*staffGap);
		step2 = step2 - (30*staffGap);
		});    
		
fontArr.do({|item, index| 
		
		case
		{item == \treble} {fontSize = 65; fontType = "MusiSync";}
		{item == \bass} {fontSize = 50; fontType = "MusiSync";}
		{(item == \alto).or(item == \tenor)} {fontSize = 62.5; fontType = "Sonora";};
		
		if(item.notNil, {
		staffArr[index].font = GUI.font.new(fontType , fontSize*resize );
			
			});
		});
		};
	
	drawLines = {arg color=\black; clefArr.do({|item, index| if(item.notNil, {func.value(((index*staffGap)+1), resize, resize2, color);}); });};	
    
    }
    
    draw {arg funcArray, color=\black;
     	var arryFunc;
     	arryFunc = [{drawLines.value(color)}];
     	arryFunc = funcArray ++ arryFunc;
    
    clefFunc.value; 	
		
    w.drawHook = {
		arryFunc.do({|item| item.value;});
	

	 GUI.pen.stroke; 
        };
       if(GUI.current == CocoaGUI, {w.refresh});
     

    
if(w2.notNil, {this.click1(adjWin1, addWin1, winLeft)});  
if(w3.notNil, {this.click2(adjWin1, addWin1, winLeft)});   

}

	draw2 {arg funcArray, color=\black;
     	var arryFunc;
     	arryFunc = [{drawLines.value(color)}];
     	arryFunc = funcArray ++ arryFunc;
    
   // clefFunc.value; 	
		
    w.drawHook = {
		arryFunc.do({|item| item.value;});
	

	 GUI.pen.stroke; 
        };
       if(GUI.current == CocoaGUI, {w.refresh});
     

    
if(w2.notNil, {this.click1(adjWin1, addWin1, winLeft)});  
if(w3.notNil, {this.click2(adjWin1, addWin1, winLeft)});   

}

	newScore {arg arrayClef = [\treble], zoom=1, adjDown=2, adjStaffGap=1.2, adjSpacing = 1;
	windowType = 0;
	storeArrayClef = arrayClef;
	storeZoom = zoom;
	down = (adjDown*20);
	staffGap = adjStaffGap;
	spacing = adjSpacing;
	this.clearWindow;
	this.view(0);
	this.draw;
	}
	
	score {arg arrayClef = [\treble], zoom=1, adjDown=2, adjStaffGap=1.2, adjSpacing = 1;
	var newArr;
	pianoArray = arrayClef;	
	newArr = [];
	arrayClef.do({|item| if(item == \piano, {newArr = newArr.add([\treble, \bass])}, {newArr = newArr.add(item) }); });
	newArr = newArr.flat;

	this.newScore(newArr, zoom, adjDown, adjStaffGap, adjSpacing);
	}
	
	refresh {
	if(w.notNil, {w.close});
	w = GUI.window.new("score", w.bounds).front;
   	w.view.background_( Color.white );
	}
    
    fullScreen {
    
    w.front.fullScreen;
    
    }    
    
    findNotes {arg arrayNotes= [[0,[0,0,0,0,0,0]]], staffColor= \black; //[pos, [staff, noteType, note, acc, color]];
	var count, newArray;
	count = 0;
	newArray = [];
	
	arrayNotes.size.do({
	var noteBlack, noteWhite, leasureLine, sharp, flat, nat, octava, quince;
	var pos1, step, penFunc;
	var symbol, noteHeight, staffAdj;
	var pos, staff, noteType, note, acc, oct, color;
	pos = arrayNotes[count][0] * resize2/1.2307692307692 * spacing;
	staff = arrayNotes[count][1][0];
	noteType = arrayNotes[count][1][1];
	note = arrayNotes[count][1][2];
	acc = arrayNotes[count][1][3];
	oct = arrayNotes[count][1][4];
	color = arrayNotes[count][1][5];
	
	if(GUI.current == CocoaGUI, {
	noteBlack = 207.asAscii.asString;  
	noteWhite = 146.asAscii.asString;  
	octava = 195.asAscii.asString; 
	cocoaAdj = 0;
	}, {
	noteBlack = [ 197, 147 ].asAscii.asString; 
	noteWhite = [ 195, 173 ].asAscii.asString; 
	octava = [ 226, 136, 154 ].asAscii.asString; 
	cocoaAdj = -3.65*resize;
	});
	
	leasureLine = 95.asAscii.asString;
	sharp = 35.asAscii.asString;
	flat = 98.asAscii.asString;
	nat = 110.asAscii.asString;
	quince = [49,53].asAscii.asString;
	
	pos1 = 90*resize ;
	step = 35*resize;
	penFunc = {arg symb, pos, height, colorPen= 0, fontSize=59;
			var colFunc;
			case
			{colorPen == 0} {colFunc = Color.black}
			{colorPen == 1} {colFunc = Color.blue}
			{colorPen == 2} {colFunc = Color.red};
				GUI.pen.font = GUI.font.new( "Sonora", fontSize*resize );
				GUI.pen.color = colFunc;
				GUI.pen.stringAtPoint( symb, Point( (pos1+pos), ((noteAdjust*resize)+(5*(height+cocoaAdj)+(down*resize))))) };
	staffAdj = {arg pix; (pix + ((16*staffGap)*staff)*resize)};		
	
	case
	{noteType == 0} {symbol = noteBlack}
	{noteType == 1} {symbol = noteWhite};
	noteHeight = ((note * -1) + 7);
	noteHeight = noteHeight + (((16*staffGap)*staff));
	noteHeight = noteHeight*resize;
	newArray = newArray.add({penFunc.value(symbol, (pos*step), noteHeight, color);});	
	case
	{acc == 1} {newArray = newArray.add({penFunc.value(sharp, (pos*step)-(14*resize), noteHeight, color);});}
	{acc == 2} {newArray = newArray.add({penFunc.value(flat, (pos*step)-(14*resize), noteHeight, color);});}
	{acc == 3} {newArray = newArray.add({penFunc.value(nat, (pos*step)-(14*resize), noteHeight, color);});};
	case
	{oct == 1} {newArray = newArray.add({penFunc.value(octava, (pos*step)-(2.5*resize), (noteHeight-(2*resize)), color);});}
	{oct == 2} {newArray = newArray.add({penFunc.value(octava, (pos*step)-(2.5*resize), (noteHeight+(6*resize)), color);});}
	{oct == 3} {newArray = newArray.add({penFunc.value(quince, (pos*step)-(2.5*resize), (noteHeight-(1*resize)), color, 40);});}
	{oct == 4} {newArray = newArray.add({penFunc.value(quince, (pos*step)-(2.5*resize), (noteHeight+(6*resize)), color, 40);});};
	
	case
	{note <= 0} {newArray = newArray.add({penFunc.value(leasureLine, (pos*step)-(2.5*resize), staffAdj.value(7), color);});}
	{note >= 12} {newArray = newArray.add({penFunc.value(leasureLine, (pos*step)-(2.5*resize), staffAdj.value(-5), color);});};
	case
	{note <= -2} {newArray = newArray.add({penFunc.value(leasureLine, (pos*step)-(2.5*resize), staffAdj.value(9), color);});}
	{note >= 14} {newArray = newArray.add({penFunc.value(leasureLine, (pos*step)-(2.5*resize), staffAdj.value(-7), color);});};
	case
	{note <= -4} {newArray = newArray.add({penFunc.value(leasureLine, (pos*step)-(2.5*resize), staffAdj.value(11), color);});}
	{note >= 16} {newArray = newArray.add({penFunc.value(leasureLine, (pos*step)-(2.5*resize), staffAdj.value(-9), color);});};
	case
	{note <= -6} {newArray = newArray.add({penFunc.value(leasureLine, (pos*step)-(2.5*resize), staffAdj.value(13), color);});}
	{note >= 18} {newArray = newArray.add({penFunc.value(leasureLine, (pos*step)-(2.5*resize), staffAdj.value(-11), color);});};
	
	count = count + 1;
	});
	this.view(1);
	this.draw2(newArray, staffColor);

    }
    
    findChromatic {arg array = [[0, [1, 0, "c#4", 0]], [0, [0, 0, "c#5", 1]], [1, [0, 0, "e6", 0]]]; //[pos, [staff, noteType, noteName, color]]
var count, newArray, pos, staff, noteType, noteName, color, white, midiNote, noteClass, octave, oct, note, acc, col;
count = 0;
newArray = [];

array.size.do({
pos = array[count][0];
staff = array[count][1][0];
noteType = array[count][1][1];
noteName = array[count][1][2];
color = array[count][1][3];

case
{color == \black} {col = 0}
{color == \blue} {col = 1}
{color == \red} {col = 2}
{color == nil} {col = 0};

case
{storeArrayClef[staff] == \treble} {
white = [0,2,4,5,7,9,11];
midiNote = noteName.notemidi;
noteClass = midiNote.midinoteclass;
octave = midiNote.midioctave;
oct = 0;
if(white.includes(noteClass), {note = white.indexOf(noteClass); acc = 3;
	}, {acc = 1; note = white.indexOf(noteClass-1);});
note = note + ((octave-4)*7);
}
{storeArrayClef[staff] == \bass} {
white = [0,1,3,5,7,8,10];
midiNote = noteName.notemidi;
noteClass = midiNote.midinoteclass-4;
octave = midiNote.midioctave+2;
if(noteClass < 0, {noteClass = 12+noteClass; octave = octave-1});
oct = 0;
if(white.includes(noteClass), {note = white.indexOf(noteClass); acc = 3;
	}, {acc = 1; note = white.indexOf(noteClass-1);});
note = note + ((octave-4)*7);
};

//still to add Alto and Tenor

case
{(note > 18).and(note <= 24)} {note = note - 7; oct = 1}
{note > 25} {note  = note - 14; oct = 3}
{(note < -6).and(note >= -12)} {note = note + 7; oct = 2}
{note < -13} {note = note + 14; oct = 4};

newArray = newArray.add([pos, [staff, noteType, note, acc, oct, col]]); //[pos, [staff, noteType, note, acc, oct, color]];
count = count + 1;
});

this.findNotes(newArray);
}

	findEnharmonic {arg array = [[0, [1, 0, "c4", \sharp, \black]], [0, [0, 0, "c5", \flat, \black]], [1, [0, 0, "e6", \nat, \black]]]; //[pos, [staff, noteType, noteName, accidental, color]]
var count, newArray, pos, staff, noteType, noteName, color, white, midiNote, noteClass, octave, oct, note, acc, accidental, col;
count = 0;
newArray = [];

array.size.do({
pos = array[count][0];
staff = array[count][1][0];
noteType = array[count][1][1];
noteName = array[count][1][2];
accidental = array[count][1][3];
color = array[count][1][4];

case
{accidental == \empty} {acc = 0}
{accidental == \sharp} {acc = 1}
{accidental == \flat} {acc = 2}
{accidental == \nat} {acc = 3}
{accidental == nil} {acc = 0};

case
{color == \black} {col = 0}
{color == \blue} {col = 1}
{color == \red} {col = 2}
{color == nil} {col = 0};

case
{storeArrayClef[staff] == \treble} {
white = [0,2,4,5,7,9,11];
midiNote = noteName.notemidi;
noteClass = midiNote.midinoteclass;
octave = midiNote.midioctave;
oct = 0;
if(white.includes(noteClass), {note = white.indexOf(noteClass); 
	}, { note = white.indexOf(noteClass-1);});
note = note + ((octave-4)*7);
}
{storeArrayClef[staff] == \bass} {
white = [0,1,3,5,7,8,10];
midiNote = noteName.notemidi;
noteClass = midiNote.midinoteclass-4;
octave = midiNote.midioctave+2;
if(noteClass < 0, {noteClass = 12+noteClass; octave = octave-1});
oct = 0;
if(white.includes(noteClass), {note = white.indexOf(noteClass); 
	}, { note = white.indexOf(noteClass-1);});
note = note + ((octave-4)*7);
};

case
{(note > 18).and(note <= 24)} {note = note - 7; oct = 1}
{note > 25} {note = note - 14; oct = 3}
{(note < -6).and(note >= -12)} {note = note + 7; oct = 2}
{note < -13} {note = note + 14; oct = 4};

newArray = newArray.add([pos, [staff, noteType, note, acc, oct, col]]); //[pos, [staff, noteType, note, acc, oct, color]];
count = count + 1;
});

this.findNotes(newArray);
}
	
	enharmonic {arg array = [[0, [1, 0, "c4", \sharp, \black]], [0, [0, 0, "c5", \flat, \black]], [1, [0, 0, "e6", \nat, \black]]];
	var count, newArray, pos, staff, noteType, noteName, color, accidental, newStaff, adjStaff;
	count = 0;
	newArray = [];

	array.size.do({
	pos = array[count][0];
	staff = array[count][1][0];
	noteType = array[count][1][1];
	noteName = array[count][1][2];
	accidental = array[count][1][3];
	color = array[count][1][4];
	
	if(pianoArray[staff] == \piano, {
	if(noteName.notemidi < 60, {newStaff = 1}, {newStaff = 0});
	}, {newStaff = 0});

	if(pianoArray[staff-1] != \piano, {adjStaff = (staff*1);}, {
	adjStaff = (staff*2);});

	newArray = newArray.add([pos, [newStaff+adjStaff, noteType, noteName, accidental, color]]);
	count = count + 1; 
});

	this.findEnharmonic(newArray);	
	
}

	chromatic {arg array = [[0, [1, 0, "c#4", 0]], [0, [0, 0, "c#5", 1]], [1, [0, 0, "e6", 0]]]; //[pos, [staff, noteType, noteName, color]]
var count, newArray, pos, staff, noteType, noteName, color, newStaff, adjStaff;
count = 0;
newArray = [];

array.size.do({
pos = array[count][0];
staff = array[count][1][0];
noteType = array[count][1][1];
noteName = array[count][1][2];
color = array[count][1][3];

if(pianoArray[staff] == \piano, {
	if(noteName.notemidi < 60, {newStaff = 1}, {newStaff = 0});
	}, {newStaff = 0});

	if(pianoArray[staff-1] != \piano, {adjStaff = (staff*1);}, {
	adjStaff = (staff*2);});

	newArray = newArray.add([pos, [newStaff+adjStaff, noteType, noteName, color]]);
	count = count + 1; 
});

	this.findChromatic(newArray);	
	
}

	clearStaff {
	this.draw2;
	}
	
	clearWindow {
	 w.drawHook = {};
	clearStaffArr.do({|item| item.string = ""}); 
	w.refresh;
	}

	note {arg midiNote=60, pos=0, color=\black, staff=0, noteType=0;
	
	this.chromatic([[pos, [staff, noteType, midiNote.midinote, color]]];);	
	}
	
	notes {arg midiNote=[60], pos, color=[\black], staff=[0], noteType=[0];
	var array, count, position, whichStaff, typeNote, whichColor;
	count = 0;
	array = [];
	pos ?? {pos = Array.series(midiNote.size)};
	midiNote.size.do({
	
	if(pos[count].notNil, {position = pos[count]}, {position = 0});
	if(staff[count].notNil, {whichStaff = staff[count]}, {whichStaff = 0});
	if(noteType[count].notNil, {typeNote = noteType[count]}, {typeNote = 0});
	if(color[count].notNil, {whichColor = color[count]}, {whichColor = \black});
	
	array = array.add([position, [whichStaff, typeNote, midiNote[count].midinote, color[count]]]);
	count = count+1;
	});
	
	this.chromatic(array);
	
	}
	    
    allScreen {
    w.bounds_(GUI.window.new.screenBounds);
    }
    
noteOn { 
	synth.set(\gate, 1);
	taskOn.reset;
     taskOn.play;
}    
      
noteOff {
	synth.set(\gate, 0);
	taskOn.stop;
	white.value
}

noteEvent {arg dur=1.0, art=0.6, vol=1.0;
var routine;
this.setsynth(vol*0.1);
routine = Routine({ 1.do({
this.noteOn;
(dur*art).yield;
this.noteOff;
(dur*(1.0-art)).yield;
});
}).play;
}

noteOn2 { 
	synth2.set(\gate, 1);
	taskOn2.reset;
     taskOn2.play;
}    
      
noteOff2 {
	synth2.set(\gate, 0);
	taskOn2.stop;
	white2.value
}

noteEvent2 {arg dur=1.0, art=0.6, vol=1.0;
var routine;
this.setsynth2(vol*0.1);
routine = Routine({1.do({
this.noteOn2;
(dur*art).yield;
this.noteOff2;
(dur*(1.0-art)).yield;
});
}).play;
}

click1 {arg winAdj = 0.8, winAdd = 20, leftWin=1, border=true;
adjWin1 = winAdj;
addWin1 = winAdd; 
winLeft = leftWin;
if(w2.notNil, {w2.close});
 w2 = GUI.window.new( "click1", Rect( w.bounds.asArray[0]+((80*winLeft)+resize), w.bounds.asArray[1]+(addWin1*resize), (250*resize)*adjWin1, (250*resize)*adjWin1 ), border: border).front;
w2.view.background_( Color.white );
white = { w2.drawHook = {
        GUI.pen.color = Color.new255(238, 233, 233);
        GUI.pen.fillOval( Rect( (10*resize)*adjWin1, (10*resize)*adjWin1, (230*resize)*adjWin1, (230*resize)*adjWin1 ) );
         if(w2.notNil, {w2.refresh;});
    }};
yellow = {arg alpha=1.0; w2.drawHook = {
        GUI.pen.color = Color.new255(255, 255, 0).alpha_(alpha);
        GUI.pen.fillOval( Rect( (10*resize)*adjWin1, (10*resize)*adjWin1, (230*resize)*adjWin1, (230*resize)*adjWin1 ) );
       if(w2.notNil, { w2.refresh;});
    }};   
white.value;  

//w.front;
w2.front;
if(w3.notNil, {w3.front});
}

timer {arg newTime=10, clockAdj=1,winAdj = 0.8, winAdd = 20, rightWin=1;
var timerWindow, oldTime, t, numBox;
adjWin2 = winAdj;
addWin2 = winAdd; 
winRight = rightWin;
oldTime = newTime;
if(timerWindow.notNil, {timerWindow.close});
 //timerWindow = GUI.window.new( "Timer", Rect( w.bounds.asArray[2]-((205*winRight)*resize), w.bounds.asArray[1]+(addWin2*resize), (250*resize)*adjWin2, (180*resize)*adjWin2 ), border: false).front;
 //timer goes left automatically
timerWindow = GUI.window.new( "Timer", Rect( w.bounds.asArray[2]-((205*winRight)*resize) + (w.bounds.asArray[0]-(64)), w.bounds.asArray[1]+(addWin2*resize), (250*resize)*adjWin2, (180*resize)*adjWin2 ), border: false).front; 
timerWindow.view.background_( Color.white );
numBox = GUI.numberBox.new(timerWindow, Rect( (30*resize)*adjWin2, (30*resize)*adjWin2, (200*resize)*adjWin2, (110*resize)*adjWin2));
numBox.font_(GUI.font.new("Helvetica", (100*resize)*adjWin2));
numBox.value = newTime;
numBox.action = {arg field; field.value.postln; };

//w.front;
timerWindow.front;
if(w3.notNil, {w3.front});
t = Routine({ (newTime/0.1).do({oldTime = oldTime - 0.1; numBox.value_(oldTime.round(0.1));  (0.1*clockAdj).yield; });
(0.1*clockAdj).yield;
timerWindow.close;
}).play( clock );

}

tag {arg string="Pedal",letterType="Helvetica", letterSize=60,winAdj = 0.7, winAdd = 20, rightWin=2.9, letterAdj=1;
var oldTime, t, tagBox,sizeLetter;
adjWin1 = winAdj;
addWin1 = winAdd; 
winRight = rightWin;
sizeLetter = letterSize*resize;
if(tagWindow.notNil, {tagWindow.close});
 tagWindow = GUI.window.new( "Tag", Rect( w.bounds.asArray[2]-((205*winRight)*resize), w.bounds.asArray[1]+(addWin1*resize), ((sizeLetter*(0.9*letterAdj))*string.size), (180*resize)*adjWin1 ), border: false).front;
tagWindow.view.background_( Color.white );
tagBox = GUI.staticText.new(tagWindow, Rect( (30*resize)*adjWin1, (30*resize)*adjWin1, ((sizeLetter*(0.7*letterAdj))*string.size), (sizeLetter*(1.1*letterAdj))));
tagBox.font_(GUI.font.new(letterType, sizeLetter)).string_(string);

}

tagClose {
tagWindow.close;
}

text {arg string="Hello", letterType="Helvetica", letterSize=90, adjLeft=20,adjUp=20,letterAdj=1,color=Color.black;
var textArr, sizeLetter, adjLetter;
textArr = w.bounds.asArray;
sizeLetter = letterSize*resize;
adjLetter = letterAdj*resize;
text.bounds_(Rect(adjLeft*resize, ((textArr[3])-sizeLetter)-(adjUp*resize), ((sizeLetter*(0.7*letterAdj))*string.size), (sizeLetter*(1.2*letterAdj)))).font_(GUI.font.new(letterType, sizeLetter)).string_(string).stringColor_(color);


}

text2 {arg string="Hello", letterType="Helvetica", letterSize=90, adjLeft=20,adjUp=20,letterAdj=1,color=Color.black;
var textArr, sizeLetter, adjLetter;
textArr = w.bounds.asArray;
sizeLetter = letterSize*resize;
adjLetter = letterAdj*resize;
text2.bounds_(Rect(adjLeft*resize, ((textArr[3])-sizeLetter)-(adjUp*resize), ((sizeLetter*(0.7*letterAdj))*string.size), (sizeLetter*(1.2*letterAdj)))).font_(GUI.font.new(letterType, sizeLetter)).string_(string).stringColor_(color);


}

expression {arg string="F", letterType="Sonora", letterSize=120,adjLeft=80,adjUp=300,letterAdj=1.5;
var expressionArr,sizeLetter,adjLetter;
expressionArr = w.bounds.asArray;
sizeLetter = letterSize*resize;
adjLetter = letterAdj*resize;
express.bounds_(Rect(adjLeft*resize, ((expressionArr[3])-sizeLetter)-(adjUp*resize), ((sizeLetter*(0.7*letterAdj))*string.size), (sizeLetter*(1.1*letterAdj)))).font_(GUI.font.new(letterType, sizeLetter)).string_(string);

}

textClose {
text.string_("");
}

text2Close {
text2.string_("");
}

expressionClose {
express.string_("");
}

click1On {yellow.value;
}

click1Off {white.value;
}

click1Note {arg dur=1.0;
Routine({1.do({
this.click1On;
dur.yield;
this.click1Off;
});
}).play
}	

click1Close {
	w2.close;
	w2 = nil;
}

click1CloseTime {arg dur=1;
	Routine({1.do({
	dur.yield;
	this.click1Close;
	})}).play;
}

click2 {arg winAdj = 0.8, winAdd = 20, leftWin=1, border=true;
adjWin1 = winAdj;
addWin1 = winAdd; 
winLeft = leftWin;
if(w3.notNil, {w3.close});
 w3 = GUI.window.new( "click2", Rect( w.bounds.asArray[0]+((80*winLeft)+resize), w.bounds.asArray[1]+(addWin1*resize), (250*resize)*adjWin1, (250*resize)*adjWin1 ), border: border).front;
w3.view.background_( Color.white );
white2 = { w3.drawHook = {
        GUI.pen.color = Color.new255(238, 233, 233);
        GUI.pen.fillOval( Rect( (10*resize)*adjWin1, (10*resize)*adjWin1, (230*resize)*adjWin1, (230*resize)*adjWin1 ) );
         if(w3.notNil, {w3.refresh;});
    }};
yellow2 = {arg alpha=1.0; w3.drawHook = {
        GUI.pen.color = Color.new255(255, 99, 71).alpha_(alpha);
        GUI.pen.fillOval( Rect( (10*resize)*adjWin1, (10*resize)*adjWin1, (230*resize)*adjWin1, (230*resize)*adjWin1 ) );
       if(w3.notNil, { w3.refresh;});
    }};   
white2.value;  

//w.front;
w3.front;
if(w2.notNil, {w2.front});
}

click2On {yellow2.value;
}

click2Off {white2.value;
}

click2Note {arg dur=1.0;
Routine({1.do({
this.click2On;
dur.yield;
this.click2Off;
});
}).play
}	

click2Close {
	w3.close;
	w3 = nil;
}

//two more clicks
click3 {arg winAdj = 0.8, winAdd = 20, leftWin=1, border=true;
adjWin1 = winAdj;
addWin1 = winAdd; 
winLeft = leftWin;
if(w4.notNil, {w4.close});
 w4 = GUI.window.new( "click3", Rect( w.bounds.asArray[0]+((80*winLeft)+resize), w.bounds.asArray[1]+(addWin1*resize), (250*resize)*adjWin1, (250*resize)*adjWin1 ), border: border).front;
w4.view.background_( Color.white );
white3 = { w4.drawHook = {
        GUI.pen.color = Color.new255(238, 233, 233);
        GUI.pen.fillOval( Rect( (10*resize)*adjWin1, (10*resize)*adjWin1, (230*resize)*adjWin1, (230*resize)*adjWin1 ) );
         if(w4.notNil, {w4.refresh;});
    }};
yellow3 = {arg alpha=1.0; w4.drawHook = {
        GUI.pen.color = Color.new255(80, 130, 30).alpha_(alpha);
        GUI.pen.fillOval( Rect( (10*resize)*adjWin1, (10*resize)*adjWin1, (230*resize)*adjWin1, (230*resize)*adjWin1 ) );
       if(w4.notNil, { w4.refresh;});
    }};   
white3.value;  

//w.front;
w4.front;
//if(w2.notNil, {w2.front});
}

click3On {yellow3.value;
}

click3Off {white3.value;
}

click3Note {arg dur=1.0;
Routine({1.do({
this.click3On;
dur.yield;
this.click3Off;
});
}).play
}	

click3Close {
	w4.close;
	w4 = nil;
}

//

click4 {arg winAdj = 0.8, winAdd = 20, leftWin=1, border=true;
adjWin1 = winAdj;
addWin1 = winAdd; 
winLeft = leftWin;
if(w5.notNil, {w5.close});
 w5 = GUI.window.new( "click3", Rect( w.bounds.asArray[0]+((80*winLeft)+resize), w.bounds.asArray[1]+(addWin1*resize), (250*resize)*adjWin1, (250*resize)*adjWin1 ), border: border).front;
w5.view.background_( Color.white );
white4 = { w5.drawHook = {
        GUI.pen.color = Color.new255(238, 233, 233);
        GUI.pen.fillOval( Rect( (10*resize)*adjWin1, (10*resize)*adjWin1, (230*resize)*adjWin1, (230*resize)*adjWin1 ) );
         if(w5.notNil, {w5.refresh;});
    }};
yellow4 = {arg alpha=1.0; w5.drawHook = {
        GUI.pen.color = Color.new255(0, 100, 140).alpha_(alpha);
        GUI.pen.fillOval( Rect( (10*resize)*adjWin1, (10*resize)*adjWin1, (230*resize)*adjWin1, (230*resize)*adjWin1 ) );
       if(w5.notNil, { w5.refresh;});
    }};   
white4.value;  

//w.front;
w5.front;
//if(w2.notNil, {w2.front});
}

click4On {yellow4.value;
}

click4Off {white4.value;
}

click4Note {arg dur=1.0;
Routine({1.do({
this.click4On;
dur.yield;
this.click4Off;
});
}).play
}	

click4Close {
	w5.close;
	w5 = nil;
}

//

setsynth {arg amp = 0.1;
	synth.set(\amp, amp);
}

setsynth2 {arg amp = 0.1;
	synth2.set(\amp, amp);
}    
	
	close {
	case
	{w.notNil} {w.close}
	{w2.notNil} {w2.close}
	{w3.notNil} {w3.close};
	}

}

+ SCView {
remove {
		if(dataptr.notNil,{
			parent.prRemoveChild(this);
			this.prRemove;
			this.prClose;
		});
	}

}



