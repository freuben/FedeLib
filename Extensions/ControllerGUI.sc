ControllerGUI {var <numContr, <right, <top, <>funcSliderArr, <>funcKnobArr, spec, knobArr, sliderArr, window; 
		
	*new {arg numContr=8, right=400, top=250, string="Controller GUI";
		^super.new.initControllerGUI(numContr, right, top, string);
	}
	
	initControllerGUI {arg contrNum, derecha, arriba, string="Controller GUI";
		var space=28, font, step, textArr, numContr;
		numContr = contrNum;
		right = derecha;
		top = arriba;
		
		window = Window.new(string)
		.bounds_(Rect(right, top, space+(41 * numContr), 285))
		.front;
		window.view.background_(Color.grey(0.4));
		//midi spec
		spec = [0, 127].asSpec;
		font = Font.new("Helvetica", 10);
		step = 0;
		//make numbers
		textArr = [];
		numContr.do{textArr =  textArr.add(StaticText(window, Rect(30+step, 260, 200, 20)).stringColor_(Color.white));
		step = step + 41};
		step = 1;
		textArr.do({|item| item.string = step.asString; step = step + 1});		
		//make knobs
		step = 0;
		knobArr=[];
		numContr.do{knobArr = knobArr.add(Knob(window, Rect(20+step, 20, 29, 16))); step = step + 41};
		
		//make sliders 
		step = 0;
		sliderArr=[];
		numContr.do{sliderArr = sliderArr.add(Slider(window, Rect(23+step, 60, 23, 200))); step = step + 41};
		
		funcSliderArr = Array.fill(numContr, [{arg value; value}]).flat;
		funcKnobArr = Array.fill(numContr, [{arg value; value}]).flat;

		//slider to function
		sliderArr.do({|item, index| item.action_({ 
		funcSliderArr[index].value(spec.map(item.value).round(1);) }) }); 

		
		//knob to function
		knobArr.do({|item, index| item.action_({
		funcKnobArr[index].value(spec.map(item.value).round(1);) }) }); 
	
	}


	slider {arg whichSlider=1, func={arg val; val.postln};
	funcSliderArr[whichSlider-1] = func;
	}
	
	knob {arg whichKnob=1, func={arg val; val.postln};
	funcKnobArr[whichKnob-1] = func;
	}
	
	setSlider {arg whichSlider=1, val=0;
	sliderArr[whichSlider-1].value_(spec.unmap(val));
	funcSliderArr[whichSlider-1].value(val);
	}
	
	setKnob {arg whichKnob=1, val=0;
	knobArr[whichKnob-1].value_(spec.unmap(val));
	funcKnobArr[whichKnob-1].value(val);
	}
	
	setAllKnobs {arg val=0;
	knobArr.do({|item| item.value_(spec.unmap(val)); }); //all cero
	funcKnobArr.do({|item| item.value(val);})
	}
	
	setAllSliders {arg val=0;
	sliderArr.do({|item| item.value_(spec.unmap(val)); }); //all cero
	funcSliderArr.do({|item| item.value(val);})
	}
	
	close {window.close;
	}
	
}