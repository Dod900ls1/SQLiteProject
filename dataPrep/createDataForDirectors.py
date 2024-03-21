import requests
import pandas as pd
import json


df = pd.read_csv("data/Films.csv")

names_dict = {}
# len(df["Director"])//2
for i in range(len(df["Director"])//2 ):
    # Check if the value is a string
    if isinstance(df["Director"][i], str):
        if "|" in df["Director"][i]:
            separatedNames = df["Director"][i].split('|')
            for name in separatedNames:
                # Additional checks to ensure valid names
                if all(char.isalpha() or char in "' " for char in name.strip()):
                    names_dict[df.index[i]] = name.strip()  # Use index as key and strip leading/trailing whitespace
        else:
            # Additional checks to ensure valid names
            if all(char.isalpha() or char in "' " for char in df["Director"][i].strip()):
                names_dict[df.index[i]] = df["Director"][i].strip()  # Use index as key and strip leading/trailing whitespace

print(list(names_dict.items())[:10])
print(list(names_dict.values())[:10])

celebrities_data = []
# API endpoint URL
api_url_base = 'https://api.api-ninjas.com/v1/celebrity?'

# API key
api_key = '60Afbhk8fLbdw9inW7M9Dg==MxsvOUji9Gm2EJU3'

for key, name in names_dict.items():
    # Construct API URL with the name
    api_url = api_url_base + 'name=' + name
    response = requests.get(api_url, headers={'X-Api-Key': api_key})
    
    # Check if the request was successful
    if response.status_code == requests.codes.ok:
        celebrity_info = response.json()
        for obj in celebrity_info:
            obj['id'] = key
        # Check if celebrity_info is not empty
        if celebrity_info:
            celebrities_data.append(celebrity_info)

# Write the collected data to a JSON file
with open('celebrities_data24.json', 'w') as json_file:
    json.dump(celebrities_data, json_file, indent=2)




