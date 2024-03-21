import pandas as pd

# Load the CSV file into a DataFrame
df = pd.read_csv('data/Directors2.csv')

# Create a new column with integers starting from 10000 and decreasing by 1 for each row
df['directorID'] = range(10000, 10000 + len(df))

# Save the DataFrame back to the CSV file
df.to_csv('data/Directors2.csv', index=False)
