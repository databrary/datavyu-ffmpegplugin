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
