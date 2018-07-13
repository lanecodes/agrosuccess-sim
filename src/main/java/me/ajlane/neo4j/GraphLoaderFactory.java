package me.ajlane.neo4j;

import org.python.util.PythonInterpreter;

import me.ajlane.neo4j.GraphLoaderType;

import org.python.core.PyObject;
import org.python.core.PyString;

public class GraphLoaderFactory {
	
	private PyObject graphLoaderClass;
	
	public GraphLoaderFactory() {
		PythonInterpreter interp = new PythonInterpreter();
		interp.exec("from GraphLoader import GraphLoader");
		graphLoaderClass = interp.get("GraphLoader");
		interp.cleanup();
   }
	
   public GraphLoaderType create (String rootDir, String fnameSuffix, 
		   String globalParamFile) {

       PyObject graphLoaderObject = graphLoaderClass.__call__(new PyString(rootDir),
    		   												  new PyString(fnameSuffix),
    		   												  new PyString(globalParamFile));
       
       return (GraphLoaderType)graphLoaderObject.__tojava__(GraphLoaderType.class);
   }

}
