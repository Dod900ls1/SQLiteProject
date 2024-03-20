import requests
import pandas as pd
import json


df = pd.read_csv("data/Films.csv")

names = []
for i in range(1000):
    # Check if the value is a string
    if isinstance(df["Cast"][i], str):
        if "|" in df["Cast"][i]:
            separatedNames = df["Cast"][i].split('|')
            for name in separatedNames:
                # Additional checks to ensure valid names
                if all(char.isalpha() or char in "' " for char in name.strip()):
                    names.append(name.strip())  # Strip leading/trailing whitespace and append
        else:
            # Additional checks to ensure valid names
            if all(char.isalpha() or char in "' " for char in df["Cast"][i].strip()):
                names.append(df["Cast"][i].strip())  # Strip leading/trailing whitespace and append

# Print the list of names
print(len(names))

celebrities_data = []

# API endpoint URL
api_url_base = 'https://api.api-ninjas.com/v1/celebrity?'

# API key
api_key = 'htq3Qv20l9lWIsG6TcbaIQ==c7FOOXK80Udm1t9b'

for name in names:
    # Construct API URL with the name
    api_url = api_url_base + 'name=' + name
    response = requests.get(api_url, headers={'X-Api-Key': api_key})
    
    # Check if the request was successful
    if response.status_code == requests.codes.ok:
        celebrity_info = response.json()
        
        # Append the celebrity info to the list
        celebrities_data.append(celebrity_info)
    else:
        print("Error:", response.status_code, response.text)

# Write the collected data to a JSON file
with open('celebrities_data.json', 'w') as json_file:
    json.dump(celebrities_data, json_file, indent=2)

