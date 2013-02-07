require 'java'
require 'csv'
require 'time'

import 'org.datavyu.models.db.legacy.Database'
import 'org.datavyu.models.db.legacy.DataColumn'
import 'org.datavyu.models.db.legacy.MatrixVocabElement'
import 'org.datavyu.models.db.legacy.Matrix'
import 'org.datavyu.models.db.legacy.FloatDataValue'
import 'org.datavyu.models.db.legacy.IntDataValue'
import 'org.datavyu.models.db.legacy.TextStringDataValue'
import 'org.datavyu.models.db.legacy.QuoteStringDataValue'
import 'org.datavyu.models.db.legacy.UndefinedDataValue'
import 'org.datavyu.models.db.legacy.NominalDataValue'
import 'org.datavyu.models.db.legacy.PredDataValue'
import 'org.datavyu.models.db.legacy.Predicate'
import 'org.datavyu.models.db.legacy.PredicateVocabElement'
import 'org.datavyu.models.db.legacy.FloatFormalArg'
import 'org.datavyu.models.db.legacy.IntFormalArg'
import 'org.datavyu.models.db.legacy.NominalFormalArg'
import 'org.datavyu.models.db.legacy.PredFormalArg'
import 'org.datavyu.models.db.legacy.QuoteStringFormalArg'
import 'org.datavyu.models.db.legacy.UnTypedFormalArg'
import 'org.datavyu.models.db.legacy.DBElement'
import 'org.datavyu.models.db.legacy.TimeStamp'
import 'org.datavyu.models.db.legacy.DataCell'
import 'org.datavyu.models.db.legacy.SystemErrorException'

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
    puts "Datavyu Exception: '" + e + "'"
end


