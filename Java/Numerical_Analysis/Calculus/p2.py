import math
import time

#y1, y2 for the heart equation in terms of y
flops = 0
start = time.time()

def y1(x):
    expr = 0.5 - x**2
    global flops
    flops += 41
    if(expr >= 0):
        return math.cbrt(x**2) + math.sqrt(expr)
    return 0

def y2(x):
    expr = 0.5 - x**2
    global flops
    flops += 41
    if(expr >= 0):
        return math.cbrt(x**2) + math.sqrt(expr)
    return 0

#Quadri smth
def polar_curve(t):
    global flops
    flops += 12
    return math.sin(2*t + math.pi/2)

# Integration limits for x and t
x_start = -1 * math.sqrt(0.5)
x_end = math.sqrt(0.5)
t_start = 0
t_end = math.pi/2

flops += 23

# Sub Intervals
num_intervals = 1000

# Interval Lengths 
dx = (x_end - x_start) / num_intervals
dt = (t_end - t_start) / num_intervals

flops += 4

area = 0
# Trapezoid Rule
for i in range(num_intervals):
    x1 = x_start + i * dx
    x2 = x_start + (i + 1) * dx
    y1_1 = y1(x1)
    y1_2 = y1(x2)
    y2_1 = y2(x1)
    y2_2 = y2(x2)

    t1 = t_start + i * dt
    t2 = t_start + (i + 1) * dt
    r1 = polar_curve(t1)
    r2 = polar_curve(t2)

    flops += 10

    # Consider area at each point
    #Over here I was trying to determine postive and negative area, take the smallest bound of both on origin
    #but it was undercalculating

    # area1 = 0.5 * (y1_1 + y1_2) * dx
    # area2 = 0.5 * (y2_1 + y2_2) * dx
    # area3 = 0.5 * (r1 + r2) * r1 * dt

    # list1 = []
    # list2 = []
    # if(area1 > 0):
    #     list1.append(area1)
    # else:
    #     list2.append(area1)

    # if(area2 > 0):
    #     list1.append(area2)
    # else:
    #     list2.append(area2)

    # if(area3 > 0):
    #     list1.append(area3)
    # else:
    #     list2.append(area3)

    # a1 = 0
    # a2 = 0
    # if(len(list1) > 0):
    #     a1 = min(list1)
    # if(len(list2) > 0):
    #     a2 = max(list2)
    # area += a1 + abs(a2)


    # # Determine point most closest to origin, use that to add to area
    flops += 140
    a = min(math.sqrt(x1**2 + y1_1**2), math.sqrt(x1**2 + y2_1**2), r1)
    b = min(math.sqrt(x2**2 + y1_2**2), math.sqrt(x2**2 + y2_2**2), r2)

    flops += 30
    if(a == r1):
        area += abs(0.5 * (r1 + r2) * r1 * dt)
    elif(a == math.sqrt(x1**2 + y1_1**2)):
        area += abs(0.5 * (y1_1 + y1_2) * dx)
    else:
        area += abs(0.5 * (y2_1 + y2_2) * dx)

    if(b == r2):
        area += abs(0.5 * (r1 + r2) * r1 * dt)
    elif(b == math.sqrt(x1**2 + y1_2**2)):
        area += abs(0.5 * (y1_1 + y1_2) * dx)
    else:
        area += abs(0.5 * (y2_1 + y2_2) * dx)

print("The area of the figure is:", area)
print("Operations #:", flops)
print("Time taken: " + str(time.time() - start))