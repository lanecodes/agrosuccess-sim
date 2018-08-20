package repast.model.agrosuccess;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class AgroSuccessContextBuilder implements ContextBuilder<Object> {
	  /* (non-Javadoc)
	   * @see repast.simphony.dataLoader.ContextBuilder#build(repast.simphony.context.Context)
	   */
	  public Context<Object> build(Context<Object> context) {
		  
		  Parameters params = RunEnvironment.getInstance().getParameters();
		  
		  return context;
	  }

}
