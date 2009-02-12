require 'java'
require 'csv'
require 'time'

import 'au.com.nicta.openshapa.db.Database'
import 'au.com.nicta.openshapa.db.DataColumn'
import 'au.com.nicta.openshapa.db.MatrixVocabElement'
import 'au.com.nicta.openshapa.db.Matrix'
import 'au.com.nicta.openshapa.db.FloatDataValue'
import 'au.com.nicta.openshapa.db.IntDataValue'
import 'au.com.nicta.openshapa.db.TextStringDataValue'
import 'au.com.nicta.openshapa.db.QuoteStringDataValue'
import 'au.com.nicta.openshapa.db.UndefinedDataValue'
import 'au.com.nicta.openshapa.db.NominalDataValue'
import 'au.com.nicta.openshapa.db.PredDataValue'
import 'au.com.nicta.openshapa.db.Predicate'
import 'au.com.nicta.openshapa.db.PredicateVocabElement'
import 'au.com.nicta.openshapa.db.FloatFormalArg'
import 'au.com.nicta.openshapa.db.IntFormalArg'
import 'au.com.nicta.openshapa.db.NominalFormalArg'
import 'au.com.nicta.openshapa.db.PredFormalArg'
import 'au.com.nicta.openshapa.db.QuoteStringFormalArg'
import 'au.com.nicta.openshapa.db.UnTypedFormalArg'
import 'au.com.nicta.openshapa.db.DBElement'
import 'au.com.nicta.openshapa.db.TimeStamp'
import 'au.com.nicta.openshapa.db.DataCell'
import 'au.com.nicta.openshapa.db.SystemErrorException'

begin

  numrows = 10

  # Create a data columns
  puts "Set up columns.."
  colnames = ["float", "int", "text", "nominal", "predicate", "matrix"]
  coltypes = [
    MatrixVocabElement::MatrixType::FLOAT,
    MatrixVocabElement::MatrixType::INTEGER,
    MatrixVocabElement::MatrixType::TEXT,
    MatrixVocabElement::MatrixType::NOMINAL,
    MatrixVocabElement::MatrixType::PREDICATE,
    MatrixVocabElement::MatrixType::MATRIX ]

  for cc in 0...colnames.length
    if !$db.col_name_in_use(colnames[cc])
      col = DataColumn.new($db, colnames[cc], coltypes[cc])
      $db.add_column(col)
    end
  end

  # Check if predicate already defined
  if !$db.pred_name_in_use("test0")
    # Make demo Predicate structure
    pve0 = PredicateVocabElement.new($db, "test0");
    farg = FloatFormalArg.new($db, "<float>")
    pve0.append_formal_arg(farg)
    farg = IntFormalArg.new($db, "<int>")
    pve0.append_formal_arg(farg)
    farg = NominalFormalArg.new($db, "<nominal>")
    pve0.append_formal_arg(farg)
    farg = PredFormalArg.new($db, "<pred>")
    pve0.append_formal_arg(farg)
    farg = QuoteStringFormalArg.new($db, "<qstring>")
    pve0.append_formal_arg(farg)
    farg = UnTypedFormalArg.new($db, "<untyped>")
    pve0.append_formal_arg(farg)
    predID0 = $db.add_pred_ve(pve0)
  end
  predID0 = $db.get_pred_ve("test0").get_id()

  # Check if matrix already defined
  mve0 = $db.get_vocab_element("matrix")
  if mve0.get_num_formal_args() == 1
    # Setup structure of matrix column
    mve0 = MatrixVocabElement.new(mve0)
    mve0.delete_formal_arg(0)

    farg = FloatFormalArg.new($db, "<float>")
    mve0.append_formal_arg(farg)
    farg = IntFormalArg.new($db, "<int>")
    mve0.append_formal_arg(farg)
    farg = NominalFormalArg.new($db, "<nominal>")
    mve0.append_formal_arg(farg)
    farg = QuoteStringFormalArg.new($db, "<qstring>")
    mve0.append_formal_arg(farg)
    farg = UnTypedFormalArg.new($db, "<untyped>")
    mve0.append_formal_arg(farg)
    $db.replace_matrix_ve(mve0)
  end
  matID0 = mve0.get_id()

  # Create some data
  coldata = [0.1234, 1234, "textdata1", "nom1"]
  for cc in 0...colnames.length
    col = $db.get_column(colnames[cc])
    mve = $db.get_matrix_ve(col.its_mve_id)

    for dd in 0...numrows
      cell = DataCell.new($db, col.get_id, mve.get_id)
      cell.onset = TimeStamp.new(1000, dd * 2000)

      # Set different data values
      if coltypes[cc] == MatrixVocabElement::MatrixType::FLOAT
        dv = FloatDataValue.new($db)
        dv.set_its_value(coldata[cc] * dd)
      elsif coltypes[cc] == MatrixVocabElement::MatrixType::INTEGER
        dv = IntDataValue.new($db)
        dv.set_its_value(coldata[cc] * dd)
      elsif coltypes[cc] == MatrixVocabElement::MatrixType::TEXT
        dv = TextStringDataValue.new($db)
        dv.set_its_value(coldata[cc])
      elsif coltypes[cc] == MatrixVocabElement::MatrixType::NOMINAL
        dv = NominalDataValue.new($db)
        dv.set_its_value(coldata[cc])
      elsif coltypes[cc] == MatrixVocabElement::MatrixType::PREDICATE
        pve0 = $db.get_pred_ve(predID0)

        fargid = pve0.get_formal_arg(0).get_id()
        fdv0 = FloatDataValue.new($db, fargid, 1.234)
        fargid = pve0.get_formal_arg(1).get_id()
        fdv1 = IntDataValue.new($db, fargid, 1234)
        fargid = pve0.get_formal_arg(2).get_id()
        fdv2 = NominalDataValue.new($db, fargid, "a_nominal")
        fargid = pve0.get_formal_arg(3).get_id()
        fdv3 = PredDataValue.new($db, fargid, Predicate.new($db, predID0))
        fargid = pve0.get_formal_arg(4).get_id()
        fdv4 = QuoteStringDataValue.new($db, fargid, "quote_string")
        fargid = pve0.get_formal_arg(5).get_id()
        fdv5 = UndefinedDataValue.new($db, fargid, pve0.get_formal_arg(5).get_farg_name())

        if dd == 0
          # construct a predicate with null args in first cell of column
          pred = Predicate.new($db, predID0)
        else
          pred = Predicate.new(Predicate.construct($db, predID0, fdv0, fdv1, fdv2, fdv3, fdv4, fdv5))
        end

        dv = PredDataValue.new($db)
        dv.set_its_value(pred)

      elsif coltypes[cc] == MatrixVocabElement::MatrixType::MATRIX
        mve0 = $db.get_matrix_ve(matID0)

        fargid = mve0.get_formal_arg(0).get_id()
        fdv0 = FloatDataValue.new($db, fargid, 1.2)
        fargid = mve0.get_formal_arg(1).get_id()
        fdv1 = IntDataValue.new($db, fargid, 4)
        fargid = mve0.get_formal_arg(2).get_id()
        fdv2 = NominalDataValue.new($db, fargid, "nm")
        fargid = mve0.get_formal_arg(3).get_id()
        fdv3 = QuoteStringDataValue.new($db, fargid, "qs")
        fargid = mve0.get_formal_arg(4).get_id()
        fdv4 = UndefinedDataValue.new($db, fargid, mve0.get_formal_arg(4).get_farg_name())

        if dd == 0
          # construct a matrix with null args in first cell of column
          mat = Matrix.new($db, matID0)
        else
          mat = Matrix.new(Matrix.construct($db, matID0, fdv0, fdv1, fdv2, fdv3, fdv4))
        end
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


