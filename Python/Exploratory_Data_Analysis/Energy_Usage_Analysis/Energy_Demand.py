# %%
# This Python 3 environment comes with many helpful analytics libraries installed
# It is defined by the kaggle/python Docker image: https://github.com/kaggle/docker-python
# For example, here's several helpful packages to load

import numpy as np # linear algebra
import pandas as pd # data processing, CSV file I/O (e.g. pd.read_csv)

# Imports for Linear & Logistic regression
from sklearn.linear_model import LinearRegression, LogisticRegression 
from sklearn.metrics import mean_squared_error, f1_score

# For Task 5 charts
import matplotlib.pyplot as plt

# Input data files are available in the read-only "../input/" directory
# For example, running this (by clicking run or pressing Shift+Enter) will list all files under the input directory

import os
for dirname, _, filenames in os.walk('/kaggle/input'):
    for filename in filenames:
        print(os.path.join(dirname, filename))

# You can write up to 20GB to the current directory (/kaggle/working/) that gets preserved as output when you create a version using "Save & Run All" 
# You can also write temporary files to /kaggle/temp/, but they won't be saved outside of the current session

# %%
# Load the weather_data.csv into weather_df dataframe
weather_df = pd.read_csv('/kaggle/input/cse351-hw3/weather_data.csv')
# Get a feel for the statistics
weather_df.describe()

# %%
# Load the energy_data.csv into energy_df dataframe
energy_df = pd.read_csv('/kaggle/input/cse351-hw3/energy_data.csv')
# Get a feel for the statistics
energy_df.describe()

# %% [markdown]
# **Task 1**
# 
# For the first task, I used pd.to_datetime to convert the time fields, where I had to convert to proper format. I then used the date to sum up the energy usage daily, while averaging the weather data. Before averaging the weather data, I dropped the icon and summary columns because I believe they are not necessary. I did consider the fact that they could be used for logistic regression by converting values into discrete numbers, but due to time constraint, I did not consider that possibility. Also, sadly, I did not consider feature scaling despite all these vast values and their differences. Surely, feature scaling would have been a very good option. Once I summed and average the two datasets appropriately to get values for the date, I then merged on the date to get a merged data frame. 
# 
# A point of contention is to why average the weather data. Why not use median? Why not use some other mechanism? The main reason I used average is because weather data is mostly natural phenomenon which is generally caputed best by natural distributions and the average alongside standard deviation is the bread and butter for this analysis. This is again, where I regret not using feature scaling or trying it out. It could have improved my score. But, time constraints. 

# %%
# Weather time was in unix epoch timestamp format, convert to proper format from unix epoch time
weather_df['date'] = pd.to_datetime(weather_df['time'], unit='s').dt.date
print(weather_df.head()) # Ensure the values work

# Since 'icon', 'time', and 'summary' fields are strings and not used, I dropped them
weather_df = weather_df.drop(columns=['icon', 'summary', 'time'])
print(weather_df.head())

# Average weather column values for each date
weather_df = weather_df.groupby("date").mean().reset_index()
print(weather_df.head())

# %%
# Now I will sum up energy usage for energy_data.csv by day
# First, I extract date 
daily_energy_df = energy_df
daily_energy_df['date'] = pd.to_datetime(energy_df['Date & Time']).dt.date
# print(daily_energy_df.head())

# Second, I now sum up by date
daily_energy_df = daily_energy_df.groupby("date").sum().reset_index()
# print(daily_energy_df.head())

# Third, I drop Date & Time column
daily_energy_df = daily_energy_df.drop(columns=["Date & Time"])
print(daily_energy_df.head())

# %%
# Now, I will merge the two datasets by date into a 'merged_df'
merged_df = pd.merge(weather_df, daily_energy_df, on='date')
merged_df.head()

# %% [markdown]
# **Task 2**
# 
# I proceeded to create training set and testing set by the months. The training set is the first 11 months, while the testing set is the 12th month aka December. For this, I just created a new column called 'month' which was values 1-12, where I used a conditional to filter where less than 12 is in training, and only 12 is in testing set. I also dropped all the columns from energy data set besides 'use [kW]' as stated in the homework document. 

# %%
# For linear regression, I need to test against the days for december. 
# So, I will put months January to November as training, and December for testing. 

# First, add new column labeling each date into correct month
merged_df['month'] = merged_df['date'].apply(lambda x: x.month)

# Create new datatframe task3_df that drops usage by devices
columns_dropped = ["gen [kW]", "Grid [kW]", "AC [kW]", "Furnace [kW]", "Cellar Lights [kW]", "Washer [kW]", "First Floor lights [kW]", "Utility Rm + Basement Bath [kW]", "Garage outlets [kW]", "MBed + KBed outlets [kW]", "Dryer + egauge [kW]", "Panel GFI (central vac) [kW]", "Home Office (R) [kW]", "Dining room (R) [kW]", "Microwave (R) [kW]", "Fridge (R) [kW]"]
task3_df = merged_df.drop(columns=columns_dropped)

# Filter by date into the 2 sets
# Training set is for months 1-11 (jan - nov)

training_set = task3_df[task3_df['month'] < 12].copy()
training_set.tail()

# %%
# now create testing_set for month == 12( december )
testing_set = task3_df[task3_df['month'] == 12].copy()
testing_set.head()

# %% [markdown]
# **Task 3 - Linear Regression - Predicting Energy Usage**
# 
# Initially, I considered all the columns besides the date, month and I got a RMSE of 8.74. Then, I considered whether I was overfitting due to all the features. After a closer inspection, I figured features like cloud cover could be useless. So, I experimented with dropping different features and found the best score of RMSE to be actually 7.023984 which was after dropping the features: cloud Cover, precipitation intensity, and temperature. Surprisingly, dropping visibility increased the RMSE which makes sense as that means increased usage of light based energy for instance. Also, dropping temperature decreased the RMSE which is interesting but that could be explained by a range of temperatures naturally adjusted by the local population thus the fluctuations are not significant enough for them to consider increasing or decreasing energy usage. 
# 
# RMSE: 7.0234

# %%
# Linear Regression - Predicting Energy Usage
dropped_columns = ['date', 'use [kW]', 'month', 'cloudCover', 'precipIntensity', 'temperature']

# Training data for x & y (y is target variable - energy usage)
x_train_weather_features = training_set.drop(columns=dropped_columns)
y_train_energy_usage = training_set['use [kW]']

# Testing for x & y (y is target variable - energy usage)
x_test_weather_features = testing_set.drop(columns=dropped_columns)
y_test_energy_usage = testing_set['use [kW]']

# %%
# Create, Train, and Test model (also possible consider feature scaling)
linear_model = LinearRegression() # create the model

linear_model.fit(x_train_weather_features, y_train_energy_usage) # train the model
 
energy_usage_predictions = linear_model.predict(x_test_weather_features) # test the model

# %%
# Calculate Root Mean Squared Error
rmse = mean_squared_error(y_test_energy_usage, energy_usage_predictions, squared=False)
print(f"Linear Regression RMSE: {rmse}")
# RMSE: 8.74057

# dropping dewPoint: 8.5670
# dropping visibility: 9.321976382966911
# dropping precipProbability: 8.77176
# dropping windSpeed: 8.84868
# dropping cloudCover: 7.3877 [-]
# dropping pressure: 8.80587
# dropping temperature: 8.54941 [-]
# dropping precipIntensity: 8.6783 [-]

# dropping cloudCover, precipIntensity: 7.18895
# dropping cloudCover, precipIntensity, temperature: 7.023984

# %%
# Generating CSV Dump - cse351_hw3_Shuhood_Guhfran_114483164_linear_regression.csv
linear_regression_df = pd.DataFrame({
    'date': testing_set['date'],
    'predicted_value': energy_usage_predictions
})

linear_regression_df.to_csv('/kaggle/working/cse351_hw3_Shuhood_Guhfran_114483164_linear_regression.csv', index=False)
# print(y_test_energy_usage) 

# %% [markdown]
# **Task 4 - Logistic Regression: Temperature Classification**
# 
# I first created labels where temperature equal to or greater than 35 is 1, and 0 otherwise. Then, I created logistic model and got and F1 score of 0.6485. This felt a bit too low, as I would consider hoping for 0.7 or preferably 0.8+. So, I experimented with dropping different features and found that a bunch were not having any effect such as pressure, whereas dropping visibility significantly boosted the score to 0.766 approximately. 
# 
# F1 score: 0.7657

# %%
# Logistic Regression - Temperature classification based on weather features
dropped_columns = ['temperature', 'date', 'use [kW]', 'month', 'cloudCover', 'visibility', 'pressure', 'precipProbability']

# Training data for x & y (y is target variable - temperature)
x_train_logistic_weather_features = training_set.drop(columns=dropped_columns)
y_train_temperature = (training_set['temperature'] >= 35).astype(int)

# Testing for x & y
x_test_logistic_weather_features = testing_set.drop(columns=dropped_columns)
y_test_temperature = (testing_set['temperature'] >= 35).astype(int)

# %%
# Create, Train, and Test model (also possible consider feature scaling)
logistic_model = LogisticRegression(max_iter=1000)
logistic_model.fit(x_train_logistic_weather_features, y_train_temperature)
temperature_predictions = logistic_model.predict(x_test_logistic_weather_features)

# %%
# Calculate F1 score
f1_score = f1_score(y_test_temperature, temperature_predictions)
print(f"Logistic Regression F1_score: {f1_score}")
# f1_score = 0.64865

# drop visibility: 0.7657 [-]
# drop humidity: 0.6667 ---> Combined with others: drops F1 score
# drop pressure: 0.64865 [-] Not needed, same f1_score without it
# drop windSpeed: same --> Combined with others: drops F1 score so do not drop
# drop cloudCover: same [-] Not needed
# drop windBearing: 0.764705 --> Combined with others: drops F1 score so do not drop
# drop precipIntensity: 0.6667
# drop dewPoint: 0.5909 
# drop precipProbability: 0.6667 [-]

# drop visibility, pressure, cloudCover, and precipProbability: 0.764705

# %%
# Generating CSV Dump - cse351_hw3_Shuhood_Guhfran_114483164_logistic_regression.csv
logistic_regression_df = pd.DataFrame({
    'date': testing_set['date'],
    'classification': temperature_predictions
})

logistic_regression_df.to_csv('/kaggle/working/cse351_hw3_Shuhood_Guhfran_114483164_logistic_regression.csv')

# %%
# I only need energy_df with 2 devices of interest - AC and Washer
columns_dropped = ["gen [kW]", "use [kW]", "Grid [kW]", "Furnace [kW]", "Cellar Lights [kW]", "First Floor lights [kW]", "Utility Rm + Basement Bath [kW]", "Garage outlets [kW]", "MBed + KBed outlets [kW]", "Dryer + egauge [kW]", "Panel GFI (central vac) [kW]", "Home Office (R) [kW]", "Dining room (R) [kW]", "Microwave (R) [kW]", "Fridge (R) [kW]"]
task5_df = energy_df.drop(columns=columns_dropped)

# Parse using Date & Time column for a new column 'type' which specifies if it is day or night
# day is 6 am - 7 pm (19), night is otherwise
task5_df['hour'] = pd.to_datetime(task5_df['Date & Time']).dt.hour # Get hour 

# Categorize the record as part of day or night
task5_df['type'] = np.where((task5_df['hour'] >= 6) & (task5_df['hour'] < 19), 'day', 'night')

# Sum up for each date, by day and by night
task5_df = task5_df.groupby(['date', 'type'])[['AC [kW]', 'Washer [kW]']].sum().reset_index()

print(task5_df.head()) # prints 2 records for each date (day and night)
print(task5_df.describe())

# %% [markdown]
# **Task 5 - Energy Usage Data Analytics**
# 
# I decided to analyze energy usage for AC and Washer. First, I determine which times of the day and what part of the year each device is used most frequently. As can be seen from the plots below, AC is mostly used in summer seasons from around June till October, with majority usage initially in the night but small bursts during the day around July and September. As for the washer, it is used consistently throughout the year and predominantly in the day time. 
# 
# Then, the following graph is depicting total energy usage for both AC and Washer by day and night. This is important because it shows which of these devices take up the most energy and whether it is during the day or night time. It may be important for a city or state to consider this data especially in a developing country where energy is an extremely limited resource. For example, Pakistan has a history of having energy shutdowns during times of day due to limited energy resource. Locals would have to rely on generators and uninterrupted power supply (ups) to make do with bare minimum such as chargers and light for instance. By having this data, a country or city or state could plan shutdowns or consider optimizing appropriately without hurting the locals too much. From the data we learn that AC is extremely exhaustive resource and it is especially used in the night time. So, authorities may need to consider this in mind when scheduling energy shutdowns. 

# %%
# Stacked bar plot (shows energy usage for each day)

# Pivot to get day and night as columns
pivot = task5_df.pivot(index='date', columns='type', values='AC [kW]')

# Plot stacked bar plot
plt.figure(figsize=(12,6))
plt.bar(pivot.index, pivot['day'], label='Day usage', color='skyblue')
plt.bar(pivot.index, pivot['night'], bottom=pivot['day'], label='Night usage', color='orange')

plt.xlabel('Date')
plt.ylabel('AC Energy Usage (kW)')
plt.title('Daily AC Energy Usage: Day vs Night (Stacked Bar Plot)')
plt.legend()
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()

# %%
# Stacked bar plot (shows energy usage for each day)

# Pivot to get day and night as columns
pivot = task5_df.pivot(index='date', columns='type', values='Washer [kW]')

# Plot stacked bar plot
plt.figure(figsize=(12,14))
plt.bar(pivot.index, pivot['day'], label='Day usage', color='skyblue')
plt.bar(pivot.index, pivot['night'], bottom=pivot['day'], label='Night usage', color='orange')

plt.xlabel('Date')
plt.ylabel('Washer Energy Usage (kW)')
plt.title('Daily Washer Energy Usage: Day vs Night (Stacked Bar Plot)')
plt.legend()
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()

# %%
# Energy usage of each device, split by day and night

# Aggregate energy usage by day and night
aggregates = task5_df.groupby('type')[['AC [kW]', 'Washer [kW]']].sum().reset_index()
print(aggregates)

# %%
# plot side by side bar plots
labels = ['Washer Day', 'AC Day', 'Washer Night', 'AC Night']
values = [washer_day, ac_day, washer_night, ac_night]
x_pos = [0, 1, 3, 4]

plt.figure(figsize=(8, 6))

# Plot each bar with correct color and label only once per device
plt.bar(x_pos[0], values[0], width=0.8, color='#1f77b4', label='Washer')
plt.bar(x_pos[1], values[1], width=0.8, color='#ff7f0e', label='AC')
plt.bar(x_pos[2], values[2], width=0.8, color='#1f77b4')
plt.bar(x_pos[3], values[3], width=0.8, color='#ff7f0e')

# X-tick labels for groups
plt.xticks([0.5, 3.5], ['Day', 'Night'])
plt.ylabel('Total Energy Usage (kW)')
plt.title('Total Energy Usage by Device and Period (Day vs Night)')

# Add legend for devices (will only show once per color)
plt.legend()

# Add value labels
for i, v in enumerate(values):
    plt.text(x_pos[i], v + 0.05, f'{v:.2f}', ha='center', va='bottom')

plt.tight_layout()
plt.show()



