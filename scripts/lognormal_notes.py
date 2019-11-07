# coding: utf-8
# work done to look at the seed dispersal distribution kernels
import numpy as np
import matplotlib.pyplot as plt
from scipy import stats

# Check lognormal distribution

def lognormal(x, mu, sigma):
    d = x * sigma * np.sqrt(2*np.pi)
    n = np.exp(-np.power((np.log(x)-mu),2)/(2*sigma*sigma))
    return n/d

xs = np.linspace(1, 500, 5000)
plt.plot(xs, stats.lognorm.pdf(xs, s=0.851, scale=np.exp(3.844)), 'r')
plt.plot(xs, lognormal(xs, 3.844, 0.851), 'b', ls='--')
plt.title('Illustration of log-normal pdf, scipy (red) vs my implementation (blue)')
#plt.show()

# overlap of pdfs confirms that, as far as scipy goes, the correct
# paramaterisation of the log-normal distribution for my work is:
# s = sigma
# scale = exp(mu)
# Ignore scale, set to 0.

# check exponential distribution
def exponential(x, l):
    return l * np.exp(-l*x)

plt.plot(xs, stats.expon.pdf(xs, scale=1/(5.0/100)), 'r')
plt.plot(xs, exponential(xs, (5.0/100)), 'b', ls='--')
plt.title('Comparison between scipy exponential dist and my implementation')
#plt.show()

# overlap of pdfs confirms that, as far as scipy goes, the correct
# paramaterisation for the exponential distribution for my work is:
# loc=0
# scale = 1/lambda

# COMPARE EXPONENTIAL AND LOGNORMAL DISTRIBUTIONS
acorn_dist = stats.lognorm(s=0.851, scale=np.exp(3.844))
wind_dist = stats.expon(scale=100/5.0)

fig, ax = plt.subplots(ncols=2)
ax[0].plot(xs, acorn_dist.pdf(xs), 'r')
ax[0].plot(xs, wind_dist.pdf(xs), 'b')
ax[0].set_title('Acorn (r) and Wind (b) PDFs')

ax[1].plot(xs, acorn_dist.cdf(xs), 'r')
ax[1].plot(xs, wind_dist.cdf(xs), 'b')
ax[1].set_title('Acorn (r) and Wind (b) CDFs')

plt.show()

# GET PROBABILITY OF FALLING IN CELL AT SPECIFIC DISTANCE
def cell_occupancy_prob(d, l, cdf):
    return cdf(d+float(l)/2) - cdf(d-float(l)/2)

cell_len = 25
occupancy_xs = np.linspace(cell_len+1, 500, 5000)
plt.plot(occupancy_xs, cell_occupancy_prob(occupancy_xs, cell_len,
                                           acorn_dist.cdf), 'r', ls='--')

plt.plot(occupancy_xs, cell_occupancy_prob(occupancy_xs, cell_len,
                                           wind_dist.cdf), 'b', ls='--')
plt.title('Acorn (r) and Wind (b) cell occupancy prob')
#plt.show()

# make cell occupancy prob functions factoring in Millington 2009
# approximations
def acorn_cell_occupancy_prob(d, l):
    if d <= 550:
        return cell_occupancy_prob(d, l, acorn_dist.cdf)
    else:
        return 0.001

def wind_cell_occupancy_prob(d, l):
    if d <= 75:
        return 0.95
    elif d < 100:
        return cell_occupancy_prob(d, l, wind_dist.cdf)
    else:
        return 0.001

plt.plot(occupancy_xs, np.vectorize(acorn_cell_occupancy_prob)(
    occupancy_xs, cell_len), 'r')
plt.plot(occupancy_xs, np.vectorize(wind_cell_occupancy_prob)(
    occupancy_xs, cell_len), 'b')

print acorn_cell_occupancy_prob(90, 25)

plt.show()

    
    



