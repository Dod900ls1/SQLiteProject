import json
import csv

# Load the JSON data from the file
with open('celebrities_data.json', 'r') as json_file:
    data = json.load(json_file)

# Remove empty lists from the data
data = [item for item in data if item]

# Define the CSV file path
csv_file_path = 'celebrities_data.csv'

# Extract all unique keys from the JSON data
unique_keys = set()
for item in data:
    for entry in item:
        unique_keys.update(entry.keys())

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
