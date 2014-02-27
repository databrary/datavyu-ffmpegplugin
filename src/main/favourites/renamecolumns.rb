#Rename columns of active spreadsheet with undesirable names
#Replaces all special characters (particularly . and space) with underscore
#Note: ordering may change
require 'Datavyu_API.rb'

begin
   allColumns = getColumnList()

   for cName in allColumns
      cur = getColumn(cName)
      newName = cName.gsub(/[^A-Za-z0-9]/, '_')
      if newName != cName
        delete_column(cName)
        setColumn(newName, cur)
        puts cur.name + " renamed to " + newName
      end
   end

end
