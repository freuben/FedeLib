GlobalMixer {var window, <>oldVolumes, <>spec, h, s, setFunc, chan1, chan2, chan3, chan4, chan5, chan6, chan7, chan8;

	*new {
		^super.new.initGlobalMixer;
	}
	

	initGlobalMixer {var step = 0, slider1, slider2, slider3, slider4, slider5, slider6, slider7, slider8, textArr, numberArr, spec, font;
	
		s = Server.default;
		//set initial volumes to 0db
		oldVolumes = Array.fill(8, 0);
		//controlspec for db to midi
		spec = ControlSpec(-90, 6, \db, units: " dB");
		
		//mixer GUI:
		window = Window.new("global mixer")
		.bounds_(Rect(400, 250, 355, 285))
		.front;
		window.view.background_(Color.grey(0.4));
		
		spec = [-90, 6, \db].asSpec;
		font = Font.new("Helvetica", 10);
		
		8.do{textArr =  textArr.add(StaticText(window, Rect(30+step, 260, 200, 20)).stringColor_(Color.white));
		step = step + 41};
		step = 0;
		textArr.do({|item| item.string = step.asString; step = step + 1});		
		step = 0;
		
		8.do{numberArr = numberArr.add(NumberBox(window, Rect(20+step, 20, 29, 16)).font_(font)); step = step + 41};
		

		//sliders - here I did it this way because otherwise ones combined with Volume.sc it didn't work anymore
		step = 0;
		slider1= Slider(window, Rect(23+step, 60, 23, 200)).value_(spec.unmap(0));
		step = step + 41;

		numberArr.do({|item| item.value_(spec.map(slider1.value).round(0.1)); });
		
		slider2= Slider(window, Rect(23+step, 60, 23, 200)).value_(spec.unmap(0));
		step = step + 41;
		
		slider3= Slider(window, Rect(23+step, 60, 23, 200)).value_(spec.unmap(0));
		step = step + 41;
		
		slider4= Slider(window, Rect(23+step, 60, 23, 200)).value_(spec.unmap(0));
		step = step + 41;	
		
		slider5= Slider(window, Rect(23+step, 60, 23, 200)).value_(spec.unmap(0));
		step = step + 41;

		slider6= Slider(window, Rect(23+step, 60, 23, 200)).value_(spec.unmap(0));
		step = step + 41;		
		
		slider7= Slider(window, Rect(23+step, 60, 23, 200)).value_(spec.unmap(0));
		step = step + 41;	
		
		slider8 = Slider(window, Rect(23+step, 60, 23, 200)).value_(spec.unmap(0));
		
		//function with routine that resets each time synths are freed. it stores the old volumes in an array and restarts Volume instances with previous volumes 
		
		setFunc = {
		Routine({1.do({
		if(chan1.notNil, {
		oldVolumes = [chan1.volume, chan2.volume, chan3.volume, chan4.volume, chan5.volume, chan6.volume, chan7.volume, chan8.volume];
		});
		0.1.yield;
		chan1 = Volume(s,0,1);
		chan2 = Volume(s,1,1);
		chan3 = Volume(s,2,1);
		chan4 = Volume(s,3,1);
		chan5 = Volume(s,4,1);
		chan6 = Volume(s,5,1);
		chan7 = Volume(s,6,1);
		chan8 = Volume(s,7,1);
		
		//mixer GUI:
		{
		
		slider1.action_({
		var vol;
		vol =  spec.map(slider1.value).round(0.1);
		numberArr[0].value_(vol); chan1.volume_(vol); 
		}); 
		
		slider2.action_({
		var vol;
		vol =  spec.map(slider2.value).round(0.1);
		numberArr[1].value_(vol); chan2.volume_(vol); 
		}); 
		
		slider3.action_({
		var vol;
		vol =  spec.map(slider3.value).round(0.1);
		numberArr[2].value_(vol); chan3.volume_(vol); 
		}); 
		
		slider4.action_({
		var vol;
		vol =  spec.map(slider4.value).round(0.1);
		numberArr[3].value_(vol); chan4.volume_(vol); 
		}); 
		
		slider5.action_({
		var vol;
		vol =  spec.map(slider5.value).round(0.1);
		numberArr[4].value_(vol); chan5.volume_(vol); 
		}); 
			
		slider6.action_({
		var vol;
		vol =  spec.map(slider6.value).round(0.1);
		numberArr[5].value_(vol); chan6.volume_(vol); 
		}); 
		
		slider7.action_({
		var vol;
		vol =  spec.map(slider7.value).round(0.1);
		numberArr[6].value_(vol); chan7.volume_(vol); 
		}); 	
		
		slider8.action_({
		var vol;
		vol =  spec.map(slider8.value).round(0.1);
		numberArr[7].value_(vol); chan8.volume_(vol); }); 
		
		}.defer; //use AppClock because called from routine
		
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
	window.close;
	}
	
	

}