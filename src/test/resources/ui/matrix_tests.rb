require 'java'
require 'csv'
require 'time'

import 'org.openshapa.db.Database'
import 'org.openshapa.db.DataColumn'
import 'org.openshapa.db.MatrixVocabElement'
import 'org.openshapa.db.Matrix'
import 'org.openshapa.db.FloatDataValue'
import 'org.openshapa.db.IntDataValue'
import 'org.openshapa.db.TextStringDataValue'
import 'org.openshapa.db.QuoteStringDataValue'
import 'org.openshapa.db.UndefinedDataValue'
import 'org.openshapa.db.NominalDataValue'
import 'org.openshapa.db.PredDataValue'
import 'org.openshapa.db.Predicate'
import 'org.openshapa.db.PredicateVocabElement'
import 'org.openshapa.db.FloatFormalArg'
import 'org.openshapa.db.IntFormalArg'
import 'org.openshapa.db.NominalFormalArg'
import 'org.openshapa.db.PredFormalArg'
import 'org.openshapa.db.QuoteStringFormalArg'
import 'org.openshapa.db.UnTypedFormalArg'
import 'org.openshapa.db.DBElement'
import 'org.openshapa.db.TimeStamp'
import 'org.openshapa.db.DataCell'
import 'org.openshapa.db.SystemErrorException'

begin

  numrows = 10

  # Create a data columns
  puts "Set up columns.."
  colnames = ["matrixNominal1", "matrixFloat1", "matrixInteger1", "matrixNominal2", "matrixFloat2", "matrixInteger2", "matrixMixed1", "matrixMixed2"]

  for cc in 0...colnames.length
    if !$db.col_name_in_use(colnames[cc])
      col = DataColumn.new($db, colnames[cc], MatrixVocabElement::MatrixType::MATRIX)
      $db.add_column(col)
    end
  end

  ##  # 1. Check if matrix already defined
  #  mve1 = $db.get_vocab_element("matrixText1")
  #  if mve1.get_num_formal_args() == 1
  #    # Setup structure of matrix column
  #    mve1 = MatrixVocabElement.new(mve1)
  #    mve1.delete_formal_arg(0)
  #
  #    farg = NominalFormalArg.new($db, "<nominal>")
  #    mve1.append_formal_arg(farg)
  #    $db.replace_matrix_ve(mve1)
  #  end
  #  matID1 = mve1.get_id()

  # 2. Check if matrix already defined
  mve1 = $db.get_vocab_element("matrixNominal1")
  if mve1.get_num_formal_args() == 1
    # Setup structure of matrix column
    mve1 = MatrixVocabElement.new(mve1)
    mve1.delete_formal_arg(0)
    farg = NominalFormalArg.new($db, "<nominal>")
    mve1.append_formal_arg(farg)
    $db.replace_matrix_ve(mve1)
  end
  matID1 = mve1.get_id()

  # 3. Check if matrix already defined
  mve3 = $db.get_vocab_element("matrixFloat1")
  if mve3.get_num_formal_args() == 1
    # Setup structure of matrix column
    mve3 = MatrixVocabElement.new(mve3)
    mve3.delete_formal_arg(0)

    farg = FloatFormalArg.new($db, "<float>")
    mve3.append_formal_arg(farg)
    $db.replace_matrix_ve(mve3)
  end
  matID3 = mve3.get_id()

  # 4. Check if matrix already defined
  mve4 = $db.get_vocab_element("matrixInteger1")
  if mve4.get_num_formal_args() == 1
    # Setup structure of matrix column
    mve4 = MatrixVocabElement.new(mve4)
    mve4.delete_formal_arg(0)

    farg = IntFormalArg.new($db, "<int>")
    mve4.append_formal_arg(farg)
    $db.replace_matrix_ve(mve4)
  end
  matID4 = mve4.get_id()

  #  # 5. Check if matrix already defined
  #  mve5 = $db.get_vocab_element("matrixText2")
  #  if mve5.get_num_formal_args() == 1
  #  #  # Setup structure of matrix column
  #  #  mve5 = MatrixVocabElement.new(mve5)
  #  #  mve5.delete_formal_arg(0)
  #
  #  #  farg = TextFormalArg.new($db, "<text>")
  #  #  mve5.append_formal_arg(farg)
  #  #  $db.replace_matrix_ve(mve5)
  #  end
  #  matID5 = mve5.get_id()

  # 6. Check if matrix already defined
  mve6 = $db.get_vocab_element("matrixNominal2")
  if mve6.get_num_formal_args() == 1
    # Setup structure of matrix column
    mve6 = MatrixVocabElement.new(mve6)
    mve6.delete_formal_arg(0)
    farg = NominalFormalArg.new($db, "<nominal1>")
    mve6.append_formal_arg(farg)
    farg = NominalFormalArg.new($db, "<nominal2>")
    mve6.append_formal_arg(farg)
    $db.replace_matrix_ve(mve6)
  end
  matID6 = mve6.get_id()

  # 7. Check if matrix already defined
  mve7 = $db.get_vocab_element("matrixFloat2")
  if mve7.get_num_formal_args() == 1
    # Setup structure of matrix column
    mve7 = MatrixVocabElement.new(mve7)
    mve7.delete_formal_arg(0)

    farg = FloatFormalArg.new($db, "<float1>")
    mve7.append_formal_arg(farg)
    farg = FloatFormalArg.new($db, "<float2>")
    mve7.append_formal_arg(farg)
    $db.replace_matrix_ve(mve7)
  end
  matID7 = mve7.get_id()

  # 8. Check if matrix already defined
  mve8 = $db.get_vocab_element("matrixInteger2")
  if mve8.get_num_formal_args() == 1
    # Setup structure of matrix column
    mve8 = MatrixVocabElement.new(mve8)
    mve8.delete_formal_arg(0)

    farg = IntFormalArg.new($db, "<int1>")
    mve8.append_formal_arg(farg)
    farg = IntFormalArg.new($db, "<int2>")
    mve8.append_formal_arg(farg)
    $db.replace_matrix_ve(mve8)
  end
  matID8 = mve8.get_id()

  # 9. Check if matrix already defined
  mve9 = $db.get_vocab_element("matrixMixed1")
  if mve9.get_num_formal_args() == 1
    # Setup structure of matrix column
    mve9 = MatrixVocabElement.new(mve9)
    mve9.delete_formal_arg(0)

    farg = FloatFormalArg.new($db, "<float>")
    mve9.append_formal_arg(farg)
    farg = IntFormalArg.new($db, "<int>")
    mve9.append_formal_arg(farg)
    farg = NominalFormalArg.new($db, "<nominal>")
    mve9.append_formal_arg(farg)
    farg = QuoteStringFormalArg.new($db, "<text>") # My changes
    mve9.append_formal_arg(farg)			       # ..
    $db.replace_matrix_ve(mve9)
  end
  matID9 = mve9.get_id()

  # 10. Check if matrix already defined
  mve0 = $db.get_vocab_element("matrixMixed2")
  if mve0.get_num_formal_args() == 1
    # Setup structure of matrix column
    mve0 = MatrixVocabElement.new(mve0)
    mve0.delete_formal_arg(0)

    #        farg = QuoteStringFormalArg.new($db, "<text>")
    #    mve0.append_formal_arg(farg)
    farg = FloatFormalArg.new($db, "<float1>")
    mve0.append_formal_arg(farg)
    farg = IntFormalArg.new($db, "<int1>")
    mve0.append_formal_arg(farg)
    farg = IntFormalArg.new($db, "<int2>")
    mve0.append_formal_arg(farg)
    farg = NominalFormalArg.new($db, "<nominal1>")
    mve0.append_formal_arg(farg)
    farg = FloatFormalArg.new($db, "<float2>")
    mve0.append_formal_arg(farg)
    #    farg = QuoteStringFormalArg.new($db, "<text>")
    #    mve0.append_formal_arg(farg)
    farg = NominalFormalArg.new($db, "<nominal2>")
    mve0.append_formal_arg(farg)
    $db.replace_matrix_ve(mve0)
  end
  matID0 = mve0.get_id()

  puts "Finished"

rescue NativeException => e
  puts "OpenSHAPA Exception: '" + e + "'"
end


