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

  # Create a data columns
  puts "Set up columns.."
  colnames = ["text"]
  coltypes = [
    MatrixVocabElement::MatrixType::TEXT
    ]

  for cc in 0...colnames.length
    if !$db.col_name_in_use(colnames[cc])
      col = DataColumn.new($db, colnames[cc], coltypes[cc])
      $db.add_column(col)
    end
  end

  # Create some data
  coldata = ["blah", "far", "textdata", "nom1"]
  col1 = [0, 2, 3, 10, 10, 15, 20, 22, 30, 34, 33, 35, 35, 50, 52, 53, 54, 55, 56, 57]
  col2 = [0, 20, 30, 31, 32, 38, 46, 51, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62]
  col3 = [0, 4, 5, 10, 11, 15, 17, 22, 24, 34, 34, 35, 35, 50, 52, 53, 54, 55, 56, 57]
  col4 = [0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38]
  specialstring = "moo"
  for cc in 0...colnames.length
    col = $db.get_column(colnames[cc])
    mve = $db.get_matrix_ve(col.its_mve_id)

    for dd in 0...numrows
      cell = DataCell.new($db, col.get_id, mve.get_id)

      # Set different data values
      if coltypes[cc] == MatrixVocabElement::MatrixType::TEXT
        dv = TextStringDataValue.new($db)
		if dd % 2 == 0 
		 dv.set_its_value(coldata[cc] + dd.to_s())
		else
		 dv.set_its_value(specialstring + dd.to_s())
		end
        cell.onset = TimeStamp.new(1000, col3[dd * 2] * 1000)
        cell.offset = TimeStamp.new(1000, col3[dd * 2 + 1] * 1000)
      end

      # the ones that are only datavalues need to be put in a 1 arg matrix
      if coltypes[cc] != MatrixVocabElement::MatrixType::MATRIX
        mat = Matrix.new(Matrix.construct($db, mve.get_id, dv))
      end

      # set the cell value
      cell.set_val(mat)

      # Add the cell to the database.
      $db.append_cell(cell)
    end
  end

  puts "Finished"

rescue NativeException => e
    puts "OpenSHAPA Exception: '" + e + "'"
end


