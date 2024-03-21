import json
import pandas as pd
import csv

# Load the JSON data from the file
with open('celebrities_data23.json', 'r') as json_file:
    data = json.load(json_file)

# Remove empty lists from the data
data = [item for item in data if item]
df = pd.read_json('celebrities_data23.json')

# Define the CSV file path
csv_file_path = 'celebrities_data2.csv'

# Extract all unique keys from the JSON data
unique_keys = ["name","gender","birthday","occupation","age","height","death","nationality","net_worth","is_alive","id"]

print(len(data[1])) 
# Write the data to a CSV file
with open(csv_file_path, 'w', newline='', encoding='utf-8') as csv_file:
    # Create a CSV writer object
    writer = csv.DictWriter(csv_file, fieldnames=unique_keys)

    # Write the header
    writer.writeheader()

    # Write the data rows
    for item in data:
        for entry in item:
            writer.writerow(entry)






