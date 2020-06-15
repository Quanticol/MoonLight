clear;
close all;
load('testProp.mat');
script = [
"signal { real x; real y;}",...
"space {edges { int hop; real dist; }}",...
"domain boolean;",... 
"formula MyFirstFormula = ( nodeType==3 ) reach (hop)[0, 1] ( nodeType==2 ) ;"
];
moonlightScript = ScriptLoader.loadFromText(script);
boolSpTempMonitor = moonlightScript.getMonitor("MyFirstFormula");
%%%%% phi 1 %%%%%%
result1 = boolSpTempMonitor.monitor(spatialModel,time,signal);

