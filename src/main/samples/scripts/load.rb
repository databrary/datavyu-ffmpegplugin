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
# ****************************************************************************
# *** Check to make sure filename below is the absolute path to a project. ***
# ****************************************************************************
#
project_file = "/your_path_to/openshapa/src/main/samples/scripts/dump.shapa"

#
# Main body of example script:
#
puts "Opening Project: "

# Create the controller that holds all the logic for opening projects and
# databases.
open_c = OpenC.new

#
# Opens a project and associated database (i.e. either compressed or
# uncompressed .shapa files). If you want to just open a standalone database
# (i.e .odb or .csv file) call open_c.open_database("filename") instead. These
# methods do *NOT* open the project within the OpenSHAPA UI.
#
open_c.open_project(project_file)

# Get the database that was opened.
db = open_c.get_database

# Get the project that was opened (if you want).
proj = open_c.get_project

# If the open went well - query the database, do calculations or whatever
unless db.nil?
  # This just prints the number of columns in the database.
  puts "Opened a project with '" + db.get_columns.length.to_s + "' columns!"
else
  puts "Unable to open the project '" + project_file + "'"
end

puts "Finished."
