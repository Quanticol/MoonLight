/*
 * generated by Xtext 2.18.0.M3
 */
package eu.quanticol.moonlight.xtext.tests

import com.google.inject.Inject
import eu.quanticol.moonlight.xtext.moonLightScript.Model
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import eu.quanticol.moonlight.xtext.generator.ScriptToJava
import eu.quanticol.moonlight.compiler.MoonlightCompiler
import eu.quanticol.moonlight.MoonLightScript

@ExtendWith(InjectionExtension)
@InjectWith(MoonLightScriptInjectorProvider)
class TestCompiler {
	@Inject
	ParseHelper<Model> parseHelper
	
	@Test
	def void loadModel() {
		val result = parseHelper.parse('''
			type poiType = BusStop|Hospital|MetroStop|MainSquare|Museum;		
			
			monitor City {
				signal { bool taxi; int peole; }
				space { locations {poiType poi; }
				edges { real length; }
				}
				domain boolean;
				formula somewhere [0.0, 1.0] #[ taxi ]#;
			}
			
			monitor Temporal( int a, int b) {
				signal { bool x; real y; int z; }
				domain boolean;
				formula eventually [a,b] #[ y>0 ]#;
			}
		''')
		Assertions.assertNotNull(result)
		val errors = result.eResource.errors
		Assertions.assertTrue(errors.isEmpty, '''Unexpected errors: «errors.join(", ")»''')
	}
	
	@Test
	def void loadModelWithParameters() {
		val result = parseHelper.parse('''
			type poiType = BusStop|Hospital|MetroStop|MainSquare|Museum;		
			
			monitor City( real distance ) {
				signal { bool taxi; int peole; }
				space { locations {poiType poi; }
					edges { 
						real length; 
						int hop;
					}
				}
				domain boolean;
				formula somewhere(hop) [0.0, distance] #[ taxi ]#;
			}
			
			monitor Temporal( int a, int b) {
				signal { bool x; real y; int z; }
				domain boolean;
				formula eventually [a,b] #[ y>0 ]#;
			}
		''')
		Assertions.assertNotNull(result)
		val errors = result.eResource.errors
		Assertions.assertTrue(errors.isEmpty, '''Unexpected errors: «errors.join(", ")»''')
	}
	
	
	@Test
	def void compileAndLoadClass() {
		val result = parseHelper.parse('''
			type poiType = BusStop|Hospital|MetroStop|MainSquare|Museum;		
			
			monitor City {
				signal { bool taxi; int peole; }
				space { locations {poiType poi; }
				edges { real length; }
				}
				domain boolean;
				formula somewhere [0.0, 1.0] #[ taxi ]#;
			}

			monitor City2 {
				signal { bool taxi; int peole; }
				space { locations {poiType poi; }
				edges { real length; }
				}
				domain boolean;
				formula somewhere [0.0, 1.0] #[ taxi ]#;
			}

			monitor City3 {
				signal { bool taxi; int peole; }
				domain boolean;
				formula globally [0.0, 1.0] #[ taxi ]#;
			}

			monitor City4 {
				signal { bool taxi; int peole; }
				domain boolean;
				formula eventually [0.0, 1.001] #[ taxi ]#;
			}
			
			monitor City5 {
				signal { bool taxi; int peole; }
				domain boolean;
				formula historically [0.0, 1.0] #[ taxi ]#;
			}

			monitor City6 {
				signal { bool taxi; int peole; }
				domain boolean;
				formula once [0.0, 1.0] #[ taxi ]#;
			}
			
			monitor City7( int steps ) {
							signal { bool taxi; int peole; }
							space { 
							edges { real length; 
								int hop;
							}
							}
							domain boolean;
							formula somewhere(hop) [0.0, steps] #[ taxi ]#;
						}
			
			
		''')
		val scriptToJava = new ScriptToJava();		
		val generatedCode = scriptToJava.getJavaCode(result,"moonlight.test","CityMonitor")
		System.out.println(generatedCode);
		val comp = new MoonlightCompiler();
		val script = comp.getIstance("moonlight.test","CityMonitor",generatedCode.toString,typeof(MoonLightScript))
		Assertions.assertEquals(3, script.spatioTemporalMonitors.length)
	}	
	
	@Test
	def void testReachMonitor() {
		val result = parseHelper.parse('''
monitor SensNetkBool {
                signal { int nodeType; real battery; real temperature; }
             	space {
             	edges { int hop; real dist; }
             	}
             	domain boolean;
             	formula everywhere[0.0, 5.0] #[  nodeType==2 ]# ;
             }
//Questo è un commento.
monitor SensNetkQuant {
                signal { int nodeType; real battery; real temperature; }
             	space {
             	edges { int hop; real dist; }
             	}
             	domain minmax;
             	formula everywhere[0.0, 5.0] #[ battery - 0.5 ]# ;
             }
            		''')
		Assertions.assertNotNull(result)
		val errors = result.eResource.errors
		Assertions.assertTrue(errors.isEmpty, '''Unexpected errors: «errors.join(", ")»''')
		val scriptToJava = new ScriptToJava();		
		val generatedCode = scriptToJava.getJavaCode(result,"moonlight.test","CityMonitor")
		System.out.println(generatedCode);
		val comp = new MoonlightCompiler();
		val script = comp.getIstance("moonlight.test","CityMonitor",generatedCode.toString,typeof(MoonLightScript))
		Assertions.assertNotNull(script)
	}	
	
	
	@Test
	def void testSpatioTemporal() {
		val result = parseHelper.parse('''
		monitor SensTemp {
                signal { int nodeType; real battery; real temperature; }
             	space {
             	edges { int hop; real dist; }
             	}
             	domain boolean;
                formula somewhere(hop)[0, 3]{globally [0, 0.2]  #[  battery > 0.5 ]#};
             }

		monitor SensTemp2 {
                signal { int nodeType; real battery; real temperature; }
             	space {
             	edges { int hop; real dist; }
             	}
             	domain boolean;
                formula somewhere(hop)[0, 3]{eventually [0, 0.2]  #[  battery > 0.5 ]#};
             }

		monitor SensTemp3 {
                signal { int nodeType; real battery; real temperature; }
             	space {
             	edges { int hop; real dist; }
             	}
             	domain boolean;
                formula somewhere(hop)[0, 3]{once [0, 0.2]  #[  battery > 0.5 ]#};
             }

		monitor SensTemp3 {
                signal { int nodeType; real battery; real temperature; }
             	space {
             	edges { int hop; real dist; }
             	}
             	domain boolean;
                formula somewhere(hop)[0, 3]{historically [0, 0.2]  #[  battery > 0.5 ]#};
             }

        ''')
		Assertions.assertNotNull(result)
		val errors = result.eResource.errors
		Assertions.assertTrue(errors.isEmpty, '''Unexpected errors: «errors.join(", ")»''')
		val scriptToJava = new ScriptToJava();		
		val generatedCode = scriptToJava.getJavaCode(result,"moonlight.test","CityMonitor")
		System.out.println(generatedCode);
		val comp = new MoonlightCompiler();
		val script = comp.getIstance("moonlight.test","CityMonitor",generatedCode.toString,typeof(MoonLightScript))
		Assertions.assertNotNull(script)
	}		
}
