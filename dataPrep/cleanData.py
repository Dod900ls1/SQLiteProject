import pandas as pd

# Read the CSV file into a DataFrame
df = pd.read_csv("data/Hydra-Movie-Scrape.csv")

# Drop the specified columns
columns_to_drop = ["Summary", "Short Summary", "IMDB ID", "YouTube Trailer", "Movie Poster"]
df.drop(columns=columns_to_drop, inplace=True)

# Add a row number to the last column
df['Row Number'] = df.reset_index().index

# Define the path for the new CSV file
output_file_path = "data/Films.csv"

# Write the modified DataFrame to a new CSV file
df.to_csv(output_file_path, index=False)

print("DataFrame has been written to", output_file_path)
