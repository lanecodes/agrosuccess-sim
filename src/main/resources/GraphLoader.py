from me.ajlane.loadcypher import GraphLoaderType

# workaround to add Lib/site-packages to sys.path http://bugs.jython.org/issue2143 
import site
from org.python.util import jython
jar_location = jython().getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
import os.path
site.addsitedir(os.path.join(jar_location, 'Lib/site-packages'))

from cymod import EmbeddedGraphLoader

class GraphLoader(GraphLoaderType):
    def __init__(self, root_dir, fname_suffix, global_param_file=None):
        self.gl =  EmbeddedGraphLoader(root_dir, 
                                       fname_suffix, 
                                       global_param_file)
        
        self.qg = self.gl.query_generator()
        
    def getNextQuery(self):
        try:
            next_query = self.qg.next()
            if next_query:
                return next_query
            else:
                # avoid case where an empty string causes self.qg.next() to return None
                return ""       
        
        except StopIteration:
            return None   
    