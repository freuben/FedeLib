StopWatch {var speed, now, freez, <getTime;
	
	*new {arg speed=0.0001;
	^super.new.initStopWatch(speed)
	}

	initStopWatch {arg watchSpeed=0.0001;
	speed = watchSpeed;
	SystemClock.sched(0.0,{ arg time;  
			freez = time; 
			 nil
		});

	SystemClock.sched(0.0,{ arg time;  
			now = time; 
			getTime = (now - freez).round(watchSpeed);
			 watchSpeed
		});
	}
		
	reset {SystemClock.sched(0.0,{ arg time;  
			freez = time; 
			 nil
		});
	}
	
	stop {SystemClock.clear;
	getTime = nil;
	}

}

Chronometer {var w, a, t, <>b;

*start {arg start=false;
	^super.new.init1(start);
	}
	
init1 {arg start=false;
	var count=0;
w = GUI.window.new("Timer").bounds_(Rect(220,290,300,70)).front;
a = GUI.numberBox.new(w, Rect(30, 20, 100, 30));
a.font_(GUI.font.new("Helvetica", 26));
a.value = 0;
a.action = {arg field; field.value.postln; };
b = GUI.button.new(w, Rect(145, 20, 50, 30)).states_([["Start"], ["Pause"]]).action_({|view|  
		if (view.value ==1,
						{t = { inf.do{a.value_(count.round(0.1)); 0.1.yield; count = count + 0.1; }}.fork(AppClock);}, {t.stop});});
GUI.button.new(w, Rect(210, 20, 50, 30)).states_([["Reset"]]).action_({[count = 0; a.value_(0.0);]});

if(start, {b.value = 1; t = { inf.do{a.value_(count.round(0.1)); 0.1.yield; count = count + 0.1; }}.fork(AppClock) ;});

}

*new {arg newTime=10, start=true;
	^super.new.init2(newTime, start);
	}

init2 {arg newTime=10, start = true;
var count=0, oldTime;
oldTime = newTime;
w = GUI.window.new("Timer").bounds_(Rect(220,290,300,70)).front;
a = GUI.numberBox.new(w, Rect(30, 20, 100, 30));
a.font_(GUI.font.new("Helvetica", 26));
a.value = newTime;
a.action = {arg field; field.value.postln; };
b = GUI.button.new(w, Rect(145, 20, 50, 30)).states_([["Start"], ["Pause"]]).action_({|view|  
		if (view.value ==1,
						{t = { (newTime/0.1).do{oldTime = oldTime - 0.1; a.value_(oldTime.round(0.1));  0.1.yield; }}.fork(AppClock);}, {t.stop});});
GUI.button.new(w, Rect(210, 20, 50, 30)).states_([["Reset"]]).action_({[count = 0; a.value_(newTime); oldTime = newTime; b.value = 0]});

if(start, {b.value = 1; t = { (newTime/0.1).do{oldTime = oldTime - 0.1; a.value_(oldTime.round(0.1));  0.1.yield; }}.fork(AppClock) ;});
}
}

TimeDif {var oldTime, newTime, func;

*new {arg func={arg val;val.postln};
^super.new.init(func);
}

init {arg function={arg val;val.postln};
func = function;
1.time({arg val; oldTime = val; newTime = val; 
func.value((newTime - oldTime));
});
}

value {arg function=nil;
if(function.notNil, {
func = function; });
1.time({arg val; newTime = val;  
func.value((newTime - oldTime));  
oldTime = newTime;});
}

}

