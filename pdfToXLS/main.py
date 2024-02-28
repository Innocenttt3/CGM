import csv

def process_file(input_file):
    processed_lines = []
    # Open the file in read mode with utf-8 encoding
    with open(input_file, 'r', encoding='utf-8') as file:
        # Read all lines from the file
        lines = file.readlines()

    # Process each line
    for line in lines:
        # Replace all whitespaces (including tabs and spaces) with a single space
        processed_line = ' '.join(line.split())
        # Replace spaces with semicolons
        processed_line = processed_line.replace(' ', ';')
        processed_lines.append(processed_line)

    return processed_lines

def write_to_csv(lines, output_file):
    # Open the file in write mode with utf-8 encoding
    with open(output_file, 'w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file, delimiter=';')
        # Write processed lines to the file
        for line in lines:
            writer.writerow([line])

# Example usage:
input_file = "/Users/kamilgolawski/CGM-priv/CGM/dane.csv"
output_file = ("/Users/kamilgolawski/CGM-priv/CGM/final.csv")
processed_lines = process_file(input_file)
write_to_csv(processed_lines, output_file)
