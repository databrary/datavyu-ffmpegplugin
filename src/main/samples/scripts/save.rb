# The usual imports for working with the database.
import 'org.openshapa.models.db.Database'
import 'org.openshapa.models.db.MacshapaDatabase'
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
