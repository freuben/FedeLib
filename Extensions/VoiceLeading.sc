VoiceLeading {var s, <>oldPitches, <>ranges, <>pivot, <>displacement=12, <>smallLimit=6; 
		
	*new {arg startPitches = [67,62,65,60], ranges = [[81,59], [72,55], [67,48], [62,38]], pivot = [67, 60, 52];
		^super.new.initVoiceLeading(startPitches, ranges, pivot);
	}
	
	initVoiceLeading {arg pitchStart, ambitus, point;
		oldPitches = pitchStart;
		ranges = ambitus;
		pivot = point;
	}

	voiceFunc {arg pitchIn, oldPitch, lowPitch, highPitch;
	var newPitch, difference, microtone, pitch;
	
	newPitch = pitchIn.round(1);
	microtone = pitchIn - newPitch;
	difference = newPitch.midinoteclass-oldPitch.midinoteclass;
	if(difference > smallLimit, {difference = difference-displacement; difference}, {difference});
	if(difference < smallLimit.neg, {difference = difference+displacement; difference}, {difference});
	pitch = (oldPitch+difference);
	if(pitch >= highPitch, {pitch = pitch - displacement;});
	if(pitch <= lowPitch, {pitch = pitch + displacement;});
	^[pitch, microtone];
	}
	
	lead1 {arg limit1, limit2, newPitch1, newPitch2, newPitch3;
	var array, pitch1, pitch2, pitch3;
	pitch1 = newPitch1;
	pitch2 = newPitch2;
	pitch3 = newPitch3;
	
	if((pitch2 > pitch1).and(pitch2 >= limit1), {pitch2 = pitch2 - displacement;});
	
	if((pitch3.notNil).and(limit2.notNil), {
	if((pitch3 > pitch2).and(pitch3 >= limit2), {pitch3 = pitch3 - displacement;});
	if((pitch2 == pitch3).and(pitch2 <= (limit2-1)), {pitch2 = pitch2 + displacement;});
	if((pitch2 == pitch3).and(pitch3 >= limit2), {pitch3 = pitch3 - displacement;});
	if((pitch3 > pitch2).and(pitch3 >= limit2), {pitch3 = pitch3 - displacement;});
	});
	
	if((pitch1 == pitch2).and(pitch1 <= (limit1-1)), {pitch1 = pitch1 + displacement;});
	if((pitch1 == pitch2).and(pitch2 >= limit1), {pitch2 = pitch2 - displacement;});
	
	if((pitch2 > pitch1).and(pitch2 >= limit1), {pitch2 = pitch2 - displacement;});
	
	if((pitch3.notNil).and(limit2.notNil), {
	array = [pitch1, pitch2, pitch3];
	}, {array = [pitch1, pitch2]});
	
	^array;
	}
	
	voices {arg newNotes;
	var totalArr, newPitches, microTones, result, arrSize, counter, arr;
	
	arrSize = newNotes.size;
	counter = Counter.new;
	
	totalArr = [];
	arrSize.do{var index;
	index = counter.step;
	totalArr = totalArr.add(this.voiceFunc(newNotes[index], oldPitches[index], ranges[index][1], ranges[index][0])); 
	};	
	
	newPitches = totalArr.flop[0];
	microTones = totalArr.flop[1];
	
	counter.count = 0;
	
	arrSize.do{var index, arr;
	index = counter.step;
	case
	{index == 0} { 
	arr = this.lead1(limit1:pivot[index], newPitch1:newPitches[index], newPitch2:newPitches[index+1]);
	
	newPitches[0] = arr[0];
	newPitches[1] = arr[1];
	}
	{(index != 0).and((index != (arrSize-1)))} {
	
	arr = this.lead1(pivot[index-1], pivot[index], newPitches[index-1], newPitches[index], newPitches[index+1]); 
	
	newPitches[index-1] = arr[0];
	newPitches[index] = arr[1];
	newPitches[index+1] = arr[2];
	}
	
	{index == (arrSize-1)} {
	
	arr = this.lead1(limit1:pivot[index-1], newPitch1:newPitches[index-1], newPitch2:newPitches[index]); 
	
	newPitches[index-1] = arr[0];
	newPitches[index] = arr[1];
	}
	};
	
	oldPitches = newPitches;
	
	result = (newPitches + microTones);
	
	^result;
	}

}