require 'java'
require 'csv'
require 'time'

import 'org.openshapa.models.db.Database'
import 'org.openshapa.models.db.DataColumn'
import 'org.openshapa.models.db.MatrixVocabElement'
import 'org.openshapa.models.db.Matrix'
import 'org.openshapa.models.db.FloatDataValue'
import 'org.openshapa.models.db.IntDataValue'
import 'org.openshapa.models.db.TextStringDataValue'
import 'org.openshapa.models.db.QuoteStringDataValue'
import 'org.openshapa.models.db.UndefinedDataValue'
import 'org.openshapa.models.db.NominalDataValue'
import 'org.openshapa.models.db.PredDataValue'
import 'org.openshapa.models.db.Predicate'
import 'org.openshapa.models.db.PredicateVocabElement'
import 'org.openshapa.models.db.FloatFormalArg'
import 'org.openshapa.models.db.IntFormalArg'
import 'org.openshapa.models.db.NominalFormalArg'
import 'org.openshapa.models.db.PredFormalArg'
import 'org.openshapa.models.db.QuoteStringFormalArg'
import 'org.openshapa.models.db.UnTypedFormalArg'
import 'org.openshapa.models.db.DBElement'
import 'org.openshapa.models.db.TimeStamp'
import 'org.openshapa.models.db.DataCell'
import 'org.openshapa.models.db.SystemErrorException'

begin

  numrows = 10

  # Create a text column
  puts "Create a text column"
  colname = "text"
  $db.add_column(DataColumn.new($db, colname, MatrixVocabElement::MatrixType::TEXT))
  
  # Create some data  
  coldata = "textdata\ntextdata\ntextdata\ntextdata\n"
  col1 = [0, 4, 5, 10, 11, 15, 17, 22, 24, 34, 34, 35, 35, 50, 52, 53, 54, 55, 56, 57]
  specialstring = "moo"

    col = $db.get_column(colname)
    mve = $db.get_matrix_ve(col.its_mve_id)

    for dd in 0...numrows
      cell = DataCell.new($db, col.get_id, mve.get_id)
#      onset = cc * 1000 + (cc + dd) * 2000
#      offset = onset + (dd * 200)

      # Set different data values
        dv = TextStringDataValue.new($db)
		if dd % 2 == 0 
		 dv.set_its_value(coldata + dd.to_s())
		else
		 dv.set_its_value(specialstring + dd.to_s())
		end
        cell.onset = TimeStamp.new(1000, col1[dd * 2] * 1000)
        cell.offset = TimeStamp.new(1000, col1[dd * 2 + 1] * 1000)

# the ones that are only datavalues need to be put in a 1 arg matrix
      mat = Matrix.new(Matrix.construct($db, mve.get_id, dv))

      # set the cell value
      cell.set_val(mat)
     
      # Add the cell to the database.
      $db.append_cell(cell)

    end

  puts "Finished"

rescue NativeException => e
    puts "OpenSHAPA Exception: '" + e + "'"
end


