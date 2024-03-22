import csv

def check_empty_values(row):
    # Check if any of the first 5 columns have empty values
    for value in row[:5]:
        if not value.strip():
            return True
    return False

def main():
    input_file = "data/Directors2.csv"
    output_file = "data/Directors2_cleaned.csv"
    
    # Open input and output files
    with open(input_file, 'r', newline='') as infile, open(output_file, 'w', newline='') as outfile:
        reader = csv.reader(infile)
        writer = csv.writer(outfile)
        
        # Write header to the output file
        header = next(reader)
        writer.writerow(header)
        
        # Iterate over rows in the input file
        for row in reader:
            # Check for empty values in the first 5 columns
            if not check_empty_values(row):
                # Add 10000 to the fourth column value
                row[3] = str(int(row[3]) + 5000)
                # Write row to the output file
                writer.writerow(row)

    print("Data cleaning completed. Cleaned data saved to:", output_file)

if __name__ == "__main__":
    main()
