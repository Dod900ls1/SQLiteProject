import csv

INPUT1 = "data/Directors2.csv"
OUTPUT1 = "data/Directors2_cleaned.csv"
INPUT2 = "data/Actors2.csv"
OUTPUT2 = "data/Actors2_cleaned.csv"
INPUT3 = "data/Films.csv"
OUTPUT3 = "data/Films_cleaned.csv"

def check_empty_values(row):
    # Check if any of the first 5 columns have empty values
    for value in row[:5]:
        if not value:
            return True
        if len(value) == 1:
            return True
        if not value.strip():
            return True
    return False



def clean_person_data(input_file, output_file, status):
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
            if not (status in row[5]) and (row[0] != None):
                # Write row to the output file if no empty values found
                writer.writerow(row[:5] + [status])
    print("Data cleaning completed. Cleaned data saved to:", output_file)



def get_fifth_column(csv_file):
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)
        fifth_column = [row[4] for row in reader]  # Indexing starts from 0, so the fifth column is at index 4
    return fifth_column

def clean_films_data(films_file, output_file, ids):
    # Open input and output files
    with open(films_file, 'r', newline='') as infile, open(output_file, 'w', newline='') as outfile:
        reader = csv.reader(infile)
        writer = csv.writer(outfile)
        
        # Write header to the output file
        header = next(reader)
        writer.writerow(header)
        
        # Iterate over rows in the input file
        for row in reader:
            # Check if the row number is in the list of IDs
            if int(row[-1]) in ids:  # Assuming Row Number is the last column
                writer.writerow(row)

    print("Data cleaning completed. Cleaned data saved to:", output_file)



def add_value_to_column(csv_file, column_index, value_to_add):
    with open(csv_file, mode='r') as file:
        csv_reader = csv.reader(file)
        data = list(csv_reader)

    for row in data:
        try:
            row[column_index] = str(int(row[column_index]) + value_to_add)
        except ValueError:
            pass  # Skip non-integer values

    with open(csv_file, mode='w', newline='') as file:
        csv_writer = csv.writer(file)
        csv_writer.writerows(data)



def main():
    clean_person_data(INPUT1, OUTPUT1, "film_director")
    clean_person_data(INPUT2, OUTPUT2, "actor")
    value_to_add = 5000
    add_value_to_column(OUTPUT1, 3, value_to_add)
    
    # Update these lines with the correct column index for filmIDs
    ids_prep = [get_fifth_column(OUTPUT1)[1:], get_fifth_column(OUTPUT2)[1:]]
    
    ids = []
    for i in ids_prep:
        for j in i:
            ids.append(int(j))
    ids = sorted(set(ids))
    print(ids)

    clean_films_data(INPUT3, OUTPUT3, ids)

if __name__ == "__main__":
    main()




