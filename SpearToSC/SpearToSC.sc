SpearToSC {

	var <>pathName, fileName;
	var <>dataArray, <>partialNum=0;
	var <>e, <>f;
	
	*new { arg pathName; 
		^super.new.pathName_(pathName).init;
	}
	
	*read { arg pathName; ^SpearToSC( pathName ).read; }
	
	init {var a, b, c, d;
		pathName = pathName ?? {  "~/Desktop/Spear.txt"  };
		pathName = pathName.standardizePath;
		fileName = File( pathName , "r");
		a = fileName.readAllString;
		fileName.close;
		a = a.copyRange(a.find("data")+4, a.size-1);
		b = a.split($ );
		c = b.collect({arg item, i; if(item.size > 14, {item = item.replaceAt(" ", 8); item.split($ )}, {item})});
		c = c.asFloat;
		c.do({|item, index| if(item.size > 1, {c.removeAt(index-1); c.removeAt(index-2);})});
		d = c.collect({|item, index| if(item.size > 1, {item.removeAt(0); item = ['partial', item] },{item})});
		d.removeAt(0);
		e = d.flat;
		f = [];
		e.do({|item, index| if(item == 'partial', {f = f.add(index)})});
		f = f.add(e.size);
		dataArray = [];
		this.getData;
		}
		
	getData { var step=0; 
			//'computing...'.postln;
			//Routine({var step=0; 
			(f.size-1).do({var array;
				array = e.copyRange(f[step],f[step+1]-1);
				array.removeAt(0);
			dataArray = dataArray.addAll([array.clump(3)]);
			//0.01.yield;
			step = step + 1;
			});
			//'done'.postln;
			//}).play ;
		}
		
		//partial number, starting with 0
		partial {arg number;
		partialNum = number;
		^this.dataArray[number];
		}
		
		
}		
		
