require 'java'
require 'csv'
require 'time'

import 'org.openshapa.models.db.legacy.Database'
import 'org.openshapa.models.db.legacy.DataColumn'
import 'org.openshapa.models.db.legacy.MatrixVocabElement'
import 'org.openshapa.models.db.legacy.FloatDataValue'
import 'org.openshapa.models.db.legacy.DBElement'
import 'org.openshapa.models.db.legacy.TimeStamp'
import 'org.openshapa.models.db.legacy.DataCell'
import 'org.openshapa.models.db.legacy.SystemErrorException'
import 'org.openshapa.models.db.legacy.Matrix'
import 'org.openshapa.models.db.legacy.TextStringDataValue'

find = "moo"
replace = "frog"
 
fl = find.length()
rl = replace.length()

# Searches "source" for "term", and returns the first position at which "term" appears,
# else returns -1.
def find_substring(source, term)
 sl = source.length()
 tl = term.length()
 for j in (0 .. sl - tl)
  if source[j..(j + tl - 1)] == term
   return j
  end
 end
 return -1
end

begin
 puts "Modifying database..."

 # For each of the columns within the database - start to save them to disk. 
 $db.get_col_order_vector.each do |col_index|
  # Get the column and write its name to the file.
  dc = $db.get_data_column(col_index)

  # Get the matrix vocab definition for the column
  mve = $db.get_matrix_ve(dc.get_its_mve_id)
  
  if dc.get_its_mve_type == MatrixVocabElement::MatrixType::TEXT
   # Output the cells for the column.
   for i in (1 .. dc.get_num_cells)      
	cell = dc.get_db.get_cell(dc.get_id, i)
    string = cell.get_val.to_escaped_string
	string = string [1 .. (string.length() - 2)]
	
	# If there are more terms to replace, keep finding them
	while find_substring(string, find) > -1
	 index = find_substring(string,find)
	 start_bit = ""
	 end_bit = ""
	 if index > 0
	  start_bit = string[0..index - 1]
     end
	 if index < string.length() - fl
	  end_bit = string[(index + fl) .. string.length()]
	 end
	 # Replace the old cell with the new one (with new contents)
	 string = start_bit + replace + end_bit
	 dv = TextStringDataValue.new($db)
	 dv.set_its_value(string)
	 mat = Matrix.new(Matrix.construct($db, mve.get_id, dv))	
	 cell.set_val(mat)
	 $db.replaceCell(cell)
	end
   end
  end
 end

  puts "Finished modifying."
rescue NativeException => e
    puts "OpenSHAPA Exception: '" + e + "'"
end

