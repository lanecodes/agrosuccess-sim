import numpy as np

def wheat_prod_rate(precip: float, fert: float) -> float:
    precip_comp = 0.51 * np.log(precip / 1000) + 1.03
    fert_comp = 0.19 * np.log(fert) + 1
    return (precip_comp + fert_comp) / 2

wheat_prod_rate(500, 0.6)
0.790 * 0.75 * 3500 * 625 / 10000
