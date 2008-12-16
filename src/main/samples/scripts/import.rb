require 'java'
import 'au.com.nicta.openshapa.db.Database'
import 'au.com.nicta.openshapa.db.DataColumn'
import 'au.com.nicta.openshapa.db.MatrixVocabElement'
import 'au.com.nicta.openshapa.db.FloatDataValue'
import 'au.com.nicta.openshapa.db.DBElement'

begin
  col = DataColumn.new($db, "moo", MatrixVocabElement::MatrixType::TEXT)
  $db.add_column(col)
rescue SystemErrorException => e
  puts "SystemErrorException: #{e.message}"
end
