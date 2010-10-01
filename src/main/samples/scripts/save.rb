# The usual imports for working with the database.
import 'org.openshapa.models.db.legacy.Database'
import 'org.openshapa.models.db.legacy.MacshapaDatabase'
import 'org.openshapa.models.db.legacy.DataColumn'
import 'org.openshapa.models.db.legacy.MatrixVocabElement'
import 'org.openshapa.models.db.legacy.Matrix'
import 'org.openshapa.models.db.legacy.FloatDataValue'
import 'org.openshapa.models.db.legacy.IntDataValue'
import 'org.openshapa.models.db.legacy.TextStringDataValue'
import 'org.openshapa.models.db.legacy.QuoteStringDataValue'
import 'org.openshapa.models.db.legacy.UndefinedDataValue'
import 'org.openshapa.models.db.legacy.NominalDataValue'
import 'org.openshapa.models.db.legacy.PredDataValue'
import 'org.openshapa.models.db.legacy.Predicate'
import 'org.openshapa.models.db.legacy.PredicateVocabElement'
import 'org.openshapa.models.db.legacy.FloatFormalArg'
import 'org.openshapa.models.db.legacy.IntFormalArg'
import 'org.openshapa.models.db.legacy.NominalFormalArg'
import 'org.openshapa.models.db.legacy.PredFormalArg'
import 'org.openshapa.models.db.legacy.QuoteStringFormalArg'
import 'org.openshapa.models.db.legacy.UnTypedFormalArg'
import 'org.openshapa.models.db.legacy.DBElement'
import 'org.openshapa.models.db.legacy.TimeStamp'
import 'org.openshapa.models.db.legacy.DataCell'
import 'org.openshapa.models.db.legacy.SystemErrorException'

# Packages needed for opening and saving projects and databases.
import 'org.openshapa.controllers.SaveC'
import 'org.openshapa.controllers.OpenC'

#
# Main body of example script:
#
puts "Saving Database: "

# Create a new database and add six new columns.
db = MacshapaDatabase.new(1000)
colnames = ["col1", "col2", "col3", "col4", "col5", "col6"]
coltypes = [
  MatrixVocabElement::MatrixType::FLOAT,
  MatrixVocabElement::MatrixType::INTEGER,
  MatrixVocabElement::MatrixType::TEXT,
  MatrixVocabElement::MatrixType::NOMINAL,
  MatrixVocabElement::MatrixType::PREDICATE,
  MatrixVocabElement::MatrixType::MATRIX ]

for cc in 0...colnames.length
  if !db.col_name_in_use(colnames[cc])
    col = DataColumn.new(db, colnames[cc], coltypes[cc])
    db.add_column(col)
  end
end

# Create the controller that holds all the logic for opening projects and
# databases.
save_c = SaveC.new

#
# Saves a database (i.e. a .odb or .csv file). If you want to save a project
# call save_project("project file", project, database) instead.
# These methods do *NOT* alter the OpenSHAPA UI.
#
save_c.save_database("out.csv", db)

puts "Finished."
