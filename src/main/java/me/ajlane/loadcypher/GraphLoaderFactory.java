package me.ajlane.loadcypher;

import me.ajlane.loadcypher.GraphLoaderType;
import org.python.util.PythonInterpreter;
import org.python.core.PyObject;
import org.python.core.PyString;

public class GraphLoaderFactory {
	
	private PyObject graphLoaderClass;
	
	public GraphLoaderFactory() {
		PythonInterpreter interp = new PythonInterpreter();
		interp.exec("from GraphLoader import GraphLoader");
		graphLoaderClass = interp.get("GraphLoader");
   }
	
   public GraphLoaderType create (String rootDir, String fnameSuffix, 
		   String globalParamFile) {

       PyObject graphLoaderObject = graphLoaderClass.__call__(new PyString(rootDir),
    		   												  new PyString(fnameSuffix),
    		   												  new PyString(globalParamFile));
       
       return (GraphLoaderType)graphLoaderObject.__tojava__(GraphLoaderType.class);
   }

}
