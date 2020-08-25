# Repast Simphony reporting

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

## Specifying a custom data source

To configure Repast Simphony to use use a class implementing
[NonAgregateDataSource](https://repast.github.io/docs/api/repast_simphony/repast/simphony/data2/NonAggregateDataSource.html)
as a data source there are two main steps:

### Make the context aware of the data source

The object whose type corresponds to `NonAggregateDataSource#getSourceType()`
should be added to the simulation context.

### Configure Repast Simphony

- Start the Repast Simphony GUI
- In 'Scenario Tree' Right-click on 'Data Sets' -> Add Data Set
- Give the data set a name and select Non-Aggregate dataset from the drop-down
  menu
- In the 'Custom Data Sources' tab give the _fully qualified_ path to this
  class and click 'Add' (see
- Click 'Next' and specify when data should be collected during the simulation



Adding custom data sources in this way is discussed in the following mailing
list posts:

- [Sink file does not write non-aggregate data
  sets](https://sourceforge.net/p/repast/mailman/message/35869931/)
- [Explanation](http://repast.10935.n7.nabble.com/Data-Source-instantiation-td11871.html)
  that if the returned data is a collection, the DataSource class should handle
  converting it to a string
- Another option for reporting might involve specifying a custom
  `DataSetBuilder`. See discussion and especially attached code
  [here](https://sourceforge.net/p/repast/mailman/repast-interest/thread/50845FA3.6060103%40win.rwth-aachen.de/#msg29994946)

## Managing locations of custom data outputs in batch runs

- Using file name matching to copy output files into batch run output directory
  [[link]](https://sourceforge.net/p/repast/mailman/message/35599846/).
