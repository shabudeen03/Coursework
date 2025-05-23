import time
from matplotlib import pyplot as plt
import math

# Interval for x
a = 0.0
b = 1.0

def initialize(n, h):
  x = [0]
  for i in range(1, n):
    x.append(x[i-1] + h)

  y = [-1.0]
  for i in range(1, n):
    y.append(0)

  return x, y

#f(x, y)
def f(x, y):
  return math.exp(-y) - y - x**2 - y**2

#Forward Euler Method
def forwardEuler(x, y, n, h):
  ops = 3 #Flops
  for i in range(n-1):
    ops += 16
    y[i+1] = y[i] + h * f(x[i], y[i])

  return ops

#Midpoint Method
def midpoint(x, y, n, h):
  ops = 3 #flops
  for i in range(n-1):  
    ops += 19
    y[i+1] = y[i] + h * f(x[i] + h/2, (y[i] + (h/2) * (f(x[i], y[i]))))
  
  return ops

def output(methodName, n, ops, start, finalValue):
  print(methodName + ", n = " + str(n))
  print("Time Duration: " + str(time.time() - start))
  print("# Operations: " + str(ops))
  print("When x=1.0, y=" + str(finalValue))


#Start calculating how long each method takes
start = time.time()

#Forward Euler n = 10, 000
n = 10000
h = (b - a) / n
x1, y1 = initialize(n, h)
operations = forwardEuler(x1, y1, n, h)
output("Forward Euler", n, operations, start, y1[n-1])
title1 = "Forward Euler w/ N = 10, 000"

#Forward Euler n = 100, 000
n = 100000
h = (b - a) / n
x2, y2 = initialize(n, h)
operations = forwardEuler(x2, y2, n, h)
output("Forward Euler", n, operations, start, y2[n-1])
title2 = "Forward Euler w/ N = 100,000"

#Midpoint n = 10, 000
n = 10000
h = (b - a) / n
x3, y3 = initialize(n, h)
operations = midpoint(x3, y3, n, h)
output("Midpoint", n, operations, start, y3[n-1])
title3 = "Midpoint w/ N = 10,000"

#Midpoint n = 100, 000
n = 100000
h = (b - a) / n
x4, y4 = initialize(n, h)
operations = midpoint(x4, y4, n, h)
output("Midpoint", n, operations, start, y4[n-1])
title4 = "Forward Euler w/ N = 100,000"

# plt.figure(10, 8)
plt.plot(x1, y1, label = title1)
plt.plot(x2, y2, label = title2)
plt.plot(x3, y3, label = title3)
plt.plot(x4, y4, label = title4)
plt.xlabel("X")
plt.ylabel("Y(X)")
plt.legend()
plt.show()