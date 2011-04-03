Counter {var s, <>highLimit, <>lowLimit, <>upDown, <>count, >function, <>carry=0, pattern, doubleDir, <whichDir; 
		
	*new {arg highLimit=inf, lowLimit=0, whichDir=\up;
		^super.new.initCounter(highLimit, lowLimit, whichDir);
	}
	
	initCounter {arg startHigh=inf, startLow=0, dir=\up;
		highLimit = startHigh;
		lowLimit = startLow;
		this.direction(dir);

		count = lowLimit;	
		function = {};
		if(dir == \down, {this.step});

	}


	step {var high, boolean, value;
	high = highLimit.round+1;
	
	if(doubleDir == 1, {
	if(count == highLimit, {
	if(upDown == 1, {upDown = 0}, {upDown = 1});
	});
	if(count == lowLimit, {
	if(upDown == 0, {upDown = 1});
	});
	});	
	
	value = count;
	
	if(upDown == 1, {count = count + 1},{count = count - 1});
	count = count.wrap(lowLimit.round, high);
	if(value == highLimit, {function.value; carry = carry + 1;
	});
	
	^value;
	}
	
	direction {arg dir=\up;
	
	whichDir = dir;
	case
	{dir == \down}{upDown = 0; doubleDir=0}
	{dir == \up}{upDown = 1; doubleDir=0}
	{dir == \upDown}{doubleDir = 1;};
		
	}
	
	

}