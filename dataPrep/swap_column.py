import pandas as pd

# Load the CSV file into a DataFrame
df = pd.read_csv('data/Actors2.csv')

# Specify the columns you want to swap
column1 = 'occupation'  # Replace 'column_name1' with the name of the first column you want to swap
column2 = 'height'  # Replace 'column_name2' with the name of the second column you want to swap

# Swap the columns
df[column1], df[column2] = df[column2], df[column1]

# Save the DataFrame back to the CSV file
df.to_csv('data/Actors2.csv', index=False)

