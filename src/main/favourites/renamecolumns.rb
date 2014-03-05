#Rename columns of active spreadsheet with undesirable names
#Replaces all special characters (particularly . and space) with underscore
#Note: ordering may change
require 'Datavyu_API.rb'

begin
   allColumns = getColumnList()

   for cName in allColumns
      cur = getColumn(cName)
      newName = cName.gsub(/[^A-Za-z0-9]/, '_') #replace any special chars with underscore
      newName[0].to_s.match(/[^A-Za-z]/) {newName = "col" + newName} #if first name is non-letter just prepend 'col'
      if newName != cName
        delete_column(cName)
        setColumn(newName, cur)
        puts cur.name + " renamed to " + newName
      end
   end
   puts "\nFile is NOT saved. It is recommended that you do so as soon as you verify that you are content with the changes."
end
