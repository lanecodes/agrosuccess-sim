from me.ajlane.loadcypher import GraphLoaderType

# workaround to add Lib/site-packages to sys.path http://bugs.jython.org/issue2143 
import site
from org.python.util import jython
jar_location = jython().getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
import os.path
site.addsitedir(os.path.join(jar_location, 'Lib/site-packages'))

import sys

print sys.path

import cymod
print cymod.__file__

#from cymod import EmbeddedGraphLoader
#print EmbeddedGraphLoader.__file__
#eg = EmbeddedGraphLoader()

#class GraphLoader(GraphLoaderType):
    