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

begin
  puts "Begining export..."

  # Open the file that we are going to write too.
  File.open("openshapa_out.txt", 'w') do |f|
    # Output the name of the database.
    f.write($db.get_name + "\n")

    # Output the name of each predicate vocab element.
    f.write("Predicates: \n")
    $db.get_pred_ves.each do |pred_vocab_element|
      f.write(pred_vocab_element.get_name + "\n")
    end

    # For each of the columns within the database - start to save them to disk.
    f.write("Columns: \n")
    $db.get_col_order_vector.each do |col_index|
      # Get the column and write its name to the file.
      dc = $db.get_data_column(col_index)
      f.write(dc.get_name)

      # Get the matrix vocab definition for the column
      mve = $db.get_matrix_ve(dc.get_its_mve_id)

      # If the column/variable is a matrix type - dump out the vocab definition
      # for the column.
      if dc.get_its_mve_type == MatrixVocabElement::MatrixType::MATRIX
        f.write(" - ")

        # For each of the formal arguments defined in the matrix column.
        for i in (0 .. (mve.get_num_formal_args - 1))
          fa = mve.get_formal_arg_copy(i)
          f.write(fa.get_farg_name + "|" + fa.get_farg_type.to_string)

          if i < (mve.get_num_formal_args - 2)
            f.write(",")
          end
        end        
      end
      f.write("\n")

      # Output the cells for the column.
      for i in (1 .. dc.get_num_cells)      
        cell = dc.get_db.get_cell(dc.get_id, i)
        f.write(cell.get_onset.to_string)
        f.write("," + cell.get_offset.to_string)
        f.write("," + cell.get_val.to_escaped_string)
        f.write("\n")
      end
    end
  end

  puts "Finished Export."
rescue NativeException => e
    puts "OpenSHAPA Exception: '" + e + "'"
end
