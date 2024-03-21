import csv

input_csv_path = 'data/oscars2.csv'  # Original CSV file path
output_csv_path = 'corrected_file.csv'  # Corrected CSV file path

with open(input_csv_path, mode='r', newline='') as infile, \
     open(output_csv_path, mode='w', newline='') as outfile:

    # Read the original content
    original_content = infile.readlines()
    
    # Corrected content
    corrected_content = [',' + line if not line.startswith(',') else line for line in original_content]
    
    # Write the corrected content
    outfile.writelines(corrected_content)
