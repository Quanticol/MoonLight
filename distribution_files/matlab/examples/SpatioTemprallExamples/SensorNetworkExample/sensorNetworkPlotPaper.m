% clear;
% close all;

% %generation of the data
% numSteps        = 1;
% num_nodes       = 5;
% framePlot = false; % to enable or disable the plot of the graph
% [spatialModel,time,signal]= sensorModel(num_nodes,numSteps, framePlot);
load('dataInput.mat');
numframe = 1;
plotGraph(spatialModel, numframe , 'node');

%%%%%% monitor  %%%%
moonlightScript = ScriptLoader.loadFromFile("test");
moonlightScript.setBooleanDomain();
% MyFirstFormula = ( nodeType==3 ) reach (hop)[0, 1] ( nodeType==1 );
boolSpTempMonitor = moonlightScript.getMonitor("ReachFormula");
%%%%% phi 1 %%%%%%
result = boolSpTempMonitor.monitor(spatialModel,time,values);

plotResults(spatialModel, result, true)