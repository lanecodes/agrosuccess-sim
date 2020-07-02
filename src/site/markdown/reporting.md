# Repast simphony reporting

Want to report a quantity which summarises the state of a grid value layer.

As of June 2020 this is approached using classes in `repast.model.agrosuccess.reporting`

However, reporting objectives might be achievable using a [non-aggregate data source](https://repast.github.io/docs/RepastReference/RepastReference.html#runtme_gui_nonaggregate_data) based on the fields of a LandCoverTypeReporter pseudo-agent. This would avoid the need to write my own output files, I could jut rely on RS reporting mechanism.

Class outline might look something like:

```java
    class LandCoverTypeReporter {
        ...

        public double getPineProp() {
    	...
        }

        public double getOakProp() {
    	...
        }
    }
```
