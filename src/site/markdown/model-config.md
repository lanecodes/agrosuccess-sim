# Model configuration

This page documents the locations within Repast Symphony configuration files
where model parameters and data locations are set.

## Overview of configuration locations

- `AgroSuccess.rs/context.xml` is used to make model components available to
  the Repast Symphony GUI.
- `AgroSuccess.rs/parameters.xml` is used to specify model run specific
  parameters relating to the study site and model control parameters. In
  production many of these control parameters will be varied over the course of
  many simulations to facilitate model optimization.

## Study site boundary conditions

- Path to geographic data directory, `geoDataDir`, is specified in
  `AgroSuccess.rs/context.xml`

## References
[SF parameters.xml]: https://sourceforge.net/p/repast/mailman/message/32594638/
