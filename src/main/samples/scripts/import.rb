require 'java'

include_class 'au.com.nicta.openshapa.db.DataColumn'
include_class 'au.com.nicta.openshapa.db.MatrixVocabElement'
include_class 'au.com.nicta.openshapa.db.FloatDataValue'
include_class 'au.com.nicta.openshapa.db.DBElement'
#include_class 'au.com.nicta.openshapa.db.Song'

fdb = FloatDataValue.new($database)
puts fdb

clues = ['vitamins', 'minerals', 'chocolates']
puts clues

puts Song.constants
puts "a"

puts FloatDataValue.constants
puts "b"

column = DataColumn.new($database, "moo", MatrixVocabElement::MatrixType::TEXT)
puts column
puts "c"
