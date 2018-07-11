package me.ajlane.loadcypher;

import me.ajlane.loadcypher.GraphLoaderType;
import org.python.util.PythonInterpreter;
import org.python.core.PyObject;

public class GraphLoaderFactory {
	
	private PyObject graphLoaderClass;
	
   public GraphLoaderFactory() {
       PythonInterpreter interp = new PythonInterpreter();
       interp.exec("import GraphLoader");
       //graphLoaderClass = interp.get("GraphLoader");
   }

}
