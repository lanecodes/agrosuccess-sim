# Repast Simphony setting up batch runs

Running batch runs in headless mode is discussed in [this SO][batch_run_so].

See <https://repast.github.io/docs/RepastBatchRunsGettingStarted.pdf>, especially 9. Command Line Batch Run Capabilities

I haven't been able to find the `batch_runner.jar` file mentioned in RepastBatchRunsGettingStarted.pdf but note it is possible to generate a `complete_model.jar` using the GUI accessed by starting Repast from eclipse, and clicking Tools > Configure / Run Batch Runs and configuring parameters to run.

The approach in `complete_model.jar` is to package up model with everything needed to run it, including which parameter combinations to run. This wouldn't allow choosing which models to run in response to previous outputs in the batch but it's a start.

The [SO discussed above][batch_run_so] also sketches an outline of how one might programmatically run an RS model while choosing parameters as part of a larger program.

[batch_run_so]: https://stackoverflow.com/questions/59528147
