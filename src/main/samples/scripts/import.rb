require 'java'
require 'csv'
require 'time'

import 'au.com.nicta.openshapa.db.Database'
import 'au.com.nicta.openshapa.db.DataColumn'
import 'au.com.nicta.openshapa.db.MatrixVocabElement'
import 'au.com.nicta.openshapa.db.FloatDataValue'
import 'au.com.nicta.openshapa.db.DBElement'
import 'au.com.nicta.openshapa.db.TimeStamp'
import 'au.com.nicta.openshapa.db.DataCell'
import 'au.com.nicta.openshapa.db.SystemErrorException'

begin
  # Create a data column, for our new
  puts "Begining import.."
  col = DataColumn.new($db, "gait", MatrixVocabElement::MatrixType::FLOAT)
  $db.add_column(col)

  last_time = nil
  data = Hash.new

  # Parse the CSV data file.
  f = File.open("samples/scripts/import_data.txt")
  parsed_file = CSV::Reader.parse(f)

  # For each row in the parsed CSV value, inspect the values and store the
  # needed data.
  parsed_file.each do |row|
    # Parse the time stamp for the current row (the 5th element stored in the
    # CSV file.)
    time = Time.parse(row[5]).to_i * 1000

    # Create a data sample - only if we have a unique timestamp.
    if last_time != time
      last_time = time
      data[time] = row[7]
    end
  end

  # Get the matrix vocab element.
  col = $db.data_columns[0]
  mve = $db.get_matrix_ve(col.its_mve_id)

  origin = nil
  # Sort the data we pulled from the CSV file by time, and for each data point
  # collected, create a cell in the OpenSHAPA database.
  data.sort.each do |key, value|
    if origin == nil
      origin = key
    end

    # Create the OpenSHAPA time stamp identifiying the data point.
    cell = DataCell.new($db, col.get_id, mve.get_id)
    cell.onset = TimeStamp.new(1000, (key - origin))

    # Add the cell to the database.
    $db.append_cell(cell)
    puts "Importing: [#{key} => #{value}]"
  end
  puts "Finished Import"

rescue NativeException => e
    puts "OpenSHAPA Exception: '" + e + "'"
end
