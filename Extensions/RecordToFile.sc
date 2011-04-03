RecordToFile {var <>pathName, <>chanNum, <>sampleFormat, <>headerFormat, s;

*new {arg pathName, chanNum = 2, sampleFormat = "int16", headerFormat="wav";
		^super.new.initRecordToFile(pathName, chanNum, sampleFormat, headerFormat);
	}
	
*save {arg chanNum = 2, sampleFormat = "int16";
		^super.new.initRecordToFile2(chanNum, sampleFormat);
	}

	initRecordToFile {arg path, chan=2, sample="int16", header="wav";
		s = Server.default;
		chanNum = chan;
		sampleFormat = sample;
		pathName = path;
		headerFormat = header;	
		
		s.recHeaderFormat = headerFormat;
		s.recSampleFormat = sampleFormat; 
		s.recChannels = chanNum; 	
		s.prepareForRecord(pathName);
		
	}

	initRecordToFile2 {arg chan=2, sample="int16", header="wav";
		s = Server.default;
		chanNum = chan;
		sampleFormat = sample;
		
		s.recHeaderFormat = headerFormat;
		s.recSampleFormat = sampleFormat;
		s.recChannels = chanNum; 
		
		CocoaDialog.savePanel({ arg path;	
		s.prepareForRecord(path);
		pathName = path;
		},{
		"cancelled".postln;
		});
		
	}
	
	startRec {s.record
	}

	stopRec {s.stopRecording
	}
	

}