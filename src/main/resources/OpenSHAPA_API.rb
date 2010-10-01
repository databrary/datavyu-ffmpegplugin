#-------------------------------------------------------------------
# OpenSHAPA API v 0.95

# Please read the function headers for information on how to use them.

# CHANGE LOG
# 0.95 7/22/10 -  Added a function to transfer columns between files and
#                 added headers to functions that didn't have any.
# 0.94 7/22/10 -  Fixed the save_db function so it works with opf files
#                 and will detect if you are saving a csv file.
# 0.93 7/20/10 -  Merged in function to read MacSHAPA Closed database
#                 files into OpenSHAPA.
# 0.92 6/29/10 -  Added function to delete columns
# 0.91 6/25/10 -  Added load functions, fixed some issues with Mutex
# =>              save still has some issues though; working out how to
# =>              access the project variables from Ruby.

# Licensing information:
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#-------------------------------------------------------------------

require 'java'
require 'csv'
require 'time'

import 'org.openshapa.models.db.legacy.Database'
import 'org.openshapa.models.db.legacy.DataColumn'
import 'org.openshapa.models.db.legacy.MacshapaDatabase'
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
import 'org.openshapa.models.project.Project'
import 'org.openshapa.controllers.SaveC'
import 'org.openshapa.controllers.OpenC'
import 'org.openshapa.controllers.project.ProjectController'
include_class(['java.lang.Object', 'java.awt.event.ActionListener', 'javax.swing.JFrame','javax.swing.JLabel','javax.swing.JComboBox','javax.swing.JButton','javax.swing.JPanel','javax.swing.JTable','javax.swing.JTextField', 'java.awt.GridBagLayout', 'java.awt.GridBagConstraints'])

class Cell

   attr_accessor :ordinal, :onset, :offset, :arglist, :argvals


   #-------------------------------------------------------------------
   # Note: This method is not for general use, it is used only when creating
   #       this variable from the database in the getVariable method.
   #
   # Method name: set_args
   # Function: sets up methods that can be used to reference the arguments in
   #           the cell.
   # Arguments:
   # => argvals (required): Values of the arguments being created
   # => arglist (required): Names of the arguments being created
   #-------------------------------------------------------------------

   def set_args(argvals, arglist)
      @arglist = arglist
      @argvals = argvals
      i = 0
      if argvals == ""
         @argvals = Array.new
         arglist.each do |arg|
            @argvals << nil
         end
      end
      arglist.each do |arg|

         if @argvals[i].nil?
            @argvals[i] = ""
         end
         #Tricky magic part where we are defining var names on the fly.  Escaped quotes turn everything to strings.
         #Handle this later by allowing numbers to be numbers but keeping strings.

         instance_eval "def #{arg}; return argvals[#{i}]; end"
         i += 1
      end
   end

   #-------------------------------------------------------------------
   # Method name: change_arg
   # Function: Changes the value of an argument in a cell.
   # Arguments:
   # => arg (required): Name of the argument to be changed
   # => val (required): Value to change the argument to
   # Returns:
   # => None
   # Usage:
   #       trial = getVariable("trial")
   #       trial.cells[0].change_arg("onset", 1000)
   #       setVariable("trial",trial)
   #-------------------------------------------------------------------

   def change_arg(arg, val)
      if arg == "onset"
         @onset = val
      elsif arg == "offset"
         @offset = val
      elsif arg == "ordinal"
         @ordinal = val
      else
         for i in 0..arglist.length-1
            if arglist[i] == arg and not arg.nil?
               argvals[i] = val.to_s
            end
         end
      end
   end

   #-------------------------------------------------------------------
   # Method name: print_all
   # Function: Dumps all of the arguments in the cell to a string.
   # Arguments:
   # => p (optional): The seperator used between the arguments.  Defaults to tab (\t)
   # Returns:
   # => A string of the arguments starting with ordinal/onset/offset then argument.
   # Usage:
   #       trial = getVariable("trial")
   #       print trial.cells[0].print_all()
   #-------------------------------------------------------------------

   def print_all(*p)
      if p.empty?
         p << "\t"
      end
      print @ordinal.to_s + p[0] + @onset.to_s + p[0] + @offset.to_s + p[0]
      @arglist.each do |arg|
         t = eval "self.#{arg}"
         if t == nil
            v = ""
         else
            v = t
         end
         print v + p[0]
      end
   end
end

#-------------------------------------------------------------------
# Class name: Variable
# Function: This is the Ruby container for OpenSHAPA variables.
#-------------------------------------------------------------------

class Variable

   attr_accessor :name, :type, :cells, :arglist, :old_args

   #-------------------------------------------------------------------
   # NOTE: This function is not for general use.
   #
   # Method name: set_cells
   # Function: Creates the cell object in the Variable object.
   # Arguments:
   # => newcells (required): Array of cells coming from the database via getVariable
   # => arglist (required): Array of the names of the arguments from the database
   #-------------------------------------------------------------------

   def set_cells(newcells, arglist)
      @cells = Array.new
      @arglist = Array.new
      arglist.each do |arg|
         # Regex to delete any character not a-z,0-9,or _
         if ["0","1","2","3","4","5","6","7","8","9"].include?(arg[1].chr)
            arg = arg[2..arg.length]
         end
         @arglist << arg.gsub(/(\W)+/,"").downcase
      end
      if !newcells.nil?
         newcells.each do |cell|
            c = Cell.new
            c.onset = cell[0]
            c.offset = cell[1]
            c.set_args(cell[2],@arglist)
            c.ordinal = cell[3]
            @cells << c
         end
      end
   end

   #-------------------------------------------------------------------
   # Method name: make_new_cell
   # Function: Creates a new, blank cell at the end of this variable's cell array
   # Arguments:
   # => None
   # Returns:
   # => Reference to the cell that was just created.  Modify the cell using this reference.
   # Usage:
   #       trial = getVariable("trial")
   #       new_cell = trial.make_new_cell()
   #       new_cell.change_arg("onset", 1000)
   #       setVariable("trial", trial)
   #-------------------------------------------------------------------
   def make_new_cell()
      c = Cell.new
      c.onset = 0
      c.offset = 0
      c.ordinal = 0
      c.set_args("", @arglist)
      @cells << c
      return c
   end

   def sort_cells()
      cells.sort! { |a,b| a.onset <=> b.onset }
   end
end

#-------------------------------------------------------------------
# Method name: getVariable
# Function: getVariable retrieves a variable from the database and puts it into a Ruby object.
# Arguments:
# => name (required): The OpenSHAPA name of the variable being retrieved
# Returns:
# => A Ruby object representation of the variable inside OpenSHAPA.
# Usage:
#       trial = getVariable("trial")
#-------------------------------------------------------------------

def getVariable(name)
   index = -1

   # Find the internal database index of the column we are looking for.
   $db.get_col_order_vector.each do |col_index|
      if name == $db.get_data_column(col_index).get_name
         index = col_index
      end
   end

   puts "Got column index."
   puts index


   dc = $db.get_data_column(index)
   mve = $db.get_matrix_ve(dc.get_its_mve_id)


   # Convert each cell into an array and store in an array of arrays
   cells = Array.new
   arg_names = Array.new

   if dc.get_its_mve_type == MatrixVocabElement::MatrixType::MATRIX
      for i in (0 .. (mve.get_num_formal_args - 1))
         fa = mve.get_formal_arg_copy(i)
         arg_names << fa.get_farg_name
      end
   end

   for i in (1 .. dc.get_num_cells)
      cell = dc.get_db.get_cell(dc.get_id, i)
      c = Array.new
      c << cell.get_onset.get_time
      c << cell.get_offset.get_time
      c << cell.get_val.to_escaped_string.tr_s("(", "").tr_s(")", "").split(",")
      c << i
      cells << c
   end


   v = Variable.new
   v.name = name
   v.old_args = arg_names
   #v.type = dc.get_its_mve_type
   v.set_cells(cells, arg_names)

   return v
end

#-------------------------------------------------------------------
# Method name: setVariable
# Function: setVariable will overwrite a variable in the database with the same name as the name argument.
#           If no variable with the same name exists, it will create a new variable.
# Arguments:
# => name (optional): The name of the variable being created
# => var  (required): The Ruby container of the variable to be put into the database.  This is the return value of
#         createNewVariable or getVariable that has been modified.
# Usage:
#       trial = getVariable("trial")
#       ** Do some modification to trial
#       setVariable("trial", trial)
#-------------------------------------------------------------------

def setVariable(name, var)

   # Since this code was already written for three separate values,
   # I'm just splitting it back up for now.

   arg_names = var.old_args
   cells = Array.new
   var.cells.each do |cell|
      c = Array.new
      c << cell.onset
      c << cell.offset
      c << Array.new
      var.arglist.each do |arg|
         t = eval "cell.#{arg}"
         c[2] << t
      end
      cells << c
   end

   # If the column already exists, delete it and build a new one.
   # If it doesn't, just add a new one.
   if not $db.col_name_in_use(name)
      col = DataColumn.new($db, name, MatrixVocabElement::MatrixType::MATRIX)
      $db.add_column(col)
   else
      oldcol = $db.get_column(name)
      numcells = oldcol.get_num_cells
      numcells.downto(1) do |i|
         $db.remove_cell($db.get_cell(oldcol.get_id, i).get_id)
      end
      #$db.remove_column(oldcol.get_id)

      #col = DataColumn.new($db, name, MatrixVocabElement::MatrixType::MATRIX)
      #$db.add_column(col)
   end
   # Check if matrix already defined
   col = $db.get_column(name)
   mve0 = $db.get_matrix_ve(col.its_mve_id)
   if mve0.get_num_formal_args() == 1
      # Setup structure of matrix column
      mve0 = MatrixVocabElement.new(mve0)

      mve0.delete_formal_arg(0)
      arg_names.each do |arg|
         farg = NominalFormalArg.new($db, arg)
         mve0.append_formal_arg(farg)
      end

      $db.replace_matrix_ve(mve0)
   end
   col = $db.get_column(name)
   mve0 = $db.get_matrix_ve(col.its_mve_id)
   matID0 = mve0.get_id()
   cells.each do |cell|
      c = DataCell.new($db, col.get_id, matID0)
      mat = Matrix.new($db, matID0)

      if cell[0].to_i > 0
         c.onset = TimeStamp.new(1000, cell[0].to_i)
      end
      if cell[1].to_i > 0
         c.offset = TimeStamp.new(1000, cell[1].to_i)
      end

      narg = 0
      cell[2].each do |dv|
         argid = mve0.get_formal_arg(narg).get_id()
         if dv == "" or dv == nil
            a = arg_names[narg]
            fdv = NominalDataValue.new($db, argid)
            fdv.clearValue()
         else
            fdv = NominalDataValue.new($db, argid, dv)
         end

         mat.replaceArg(narg,fdv)
         narg += 1
      end
      c.set_val(mat)
      $db.append_cell(c)
   end
   puts "ecells"
end

#-------------------------------------------------------------------
# Method name: make_rel
# Function: This function will create a reliability column that is a copy
#           of another column in the database, copying every nth cell and
#           carrying over some of the arguments from the original, if wanted.
# Arguments:
# => relname (required): The name of the reliability column to be created.
# => var_to_copy (required): The name of the variable in the database you
#                   wish to copy.
# => multiple_to_keep: The number of cells to skip.  For every other cell, use 2.
# => *args_to_keep: Comma separated strings for the arguments you want to keep
#             between cells.  For example, "onset", "trialnum", "block" would keep
#             those three arguments in the new cells that are created.
# Returns:
# => A Ruby object representation of the rel column inside OpenSHAPA.
# Usage:
#       rel_trial = make_rel("rel.trial", "trial", 2, "onset", "trialnum", "unit")
#-------------------------------------------------------------------

def make_rel(relname, var_to_copy, multiple_to_keep, *args_to_keep)
   # Get the primary variable from the DB
   var_to_copy = getVariable(var_to_copy)

   # Clip down cells to fit multiple to keep
   for i in 0..var_to_copy.cells.length-1
      if multiple_to_keep == 0
         var_to_copy.cells[i] = nil
      elsif var_to_copy.cells[i].ordinal % multiple_to_keep != 0
         var_to_copy.cells[i] = nil
      else
         var_to_copy.cells[i].ordinal = var_to_copy.cells[i].ordinal / multiple_to_keep
      end
   end
   # Clear out the nil cells
   var_to_copy.cells.compact!

   var_to_copy.cells.each do |cell|
      if !args_to_keep.include?("onset")
         cell.onset = 0
      end
      if !args_to_keep.include?("offset")
         cell.offset = 0
      end
      cell.arglist.each do |arg|
         if !args_to_keep.include?(arg)
            cell.change_arg(arg,"")
         end
      end
   end
   setVariable(relname, var_to_copy)
   return var_to_copy
end

#-------------------------------------------------------------------
# Method name: createNewVariable
# Function: Creates a brand new blank variable with argument *args and name name.
# Arguments:
# => name (required): The OpenSHAPA name of the variable being retrieved
# => *args: (optional): List of arguments that the variable will contain.  Onset, Offset, and
#               ordinal are created by default.
# Returns:
# => A Ruby object representation of the variable inside OpenSHAPA.
# Usage:
#       trial = createNewVariable("trial", "trialnum", "unit")
#       blank_cell = trial.make_new_cell()
#       setVariable(trial)
#-------------------------------------------------------------------

def createNewVariable(name, *args)
   v = Variable.new

   v.name = name

   if args[0].class == Array
      args = args[0]
   end

   # Set the argument names in arg_names and set the database internal style with <argname> in old_args
   arg_names = Array.new
   old_args = Array.new
   for arg in args
      arg_names << arg
      old_args << "<" + arg.to_s + ">"
   end
   c = Array.new
   v.old_args = old_args
   v.set_cells(nil, arg_names)

   # Return reference to this variable for the user
   return v
end

#-----------------------------------------------------------------
# EXPERIMENTAL METHODS FOR FUTURE RELEASE
#-----------------------------------------------------------------

#-----------------------------------------------------------#
# make_duration_rel: Makes a duration based reliability column
# based on John's method.  It will create two new columns, one
# that contains a cell with a number for that block, and another
# blank column for the free coding within that block.
#-----------------------------------------------------------#

#-------------------------------------------------------------------
# Method name: makeDurationBlockRel
# Function: Makes a duration based reliability column
# based on John's method.  It will create two new columns, one
# that contains a cell with a number for that block, and another
# blank column for the free coding within that block.
# Arguments:
# => relname (required): The name of the rel column to be made.
# => var_to_copy (required): The name of the variable being copied.
# => binding (required): The name of the variable to bind the copy to.
# => block_dur (required): How long (in seconds) should the blocks be?
# => skip_blocks (required): How many blocks of block_dur should we skip between
#     each coding block?
#
# # Returns:
# => Nothing.  Variables are written to the database.
# #-------------------------------------------------------------------
def makeDurationBlockRel(relname, var_to_copy, binding, block_dur, skip_blocks)
   block_var = createNewVariable(relname + "_blocks", "block_num")
   rel_var = make_rel(relname, var_to_copy, 0)

   var_to_copy = getVariable(var_to_copy)
   binding = getVariable(binding)


   block_dur = block_dur * 1000 # Convert to milliseconds
   block_num = 1
   for bindcell in binding.cells
      cell_dur = bindcell.offset - bindcell.onset
      if cell_dur <= block_dur
         cell = block_var.make_new_cell()
         cell.change_arg("block_num", block_num.to_s)
         cell.change_arg("onset", bindcell.onset)
         cell.change_arg("offset", bindcell.offset)
         block_num += 1
      else
         num_possible_blocks = cell_dur / block_dur  #Integer division
         if num_possible_blocks > 0
            for i in 0..num_possible_blocks
               if i % skip_blocks == 0
                  cell = block_var.make_new_cell()
                  cell.change_arg("block_num", block_num.to_s)
                  cell.change_arg("onset", bindcell.onset + i * block_dur)
                  if bindcell.onset + (i + 1) * block_dur <= bindcell.offset
                     cell.change_arg("offset", bindcell.onset + (i + 1) * block_dur)
                  else
                     cell.change_arg("offset", bindcell.offset)
                  end
                  block_num += 1
               end
            end
         end
      end
   end
   setVariable(relname + "_blocks", block_var)
end

#-------------------------------------------------------------------
# Method name: add_args_to_var
# Function: Add new arguments to any variable
# Arguments:
# => var (required): The variable to add args to.  This can be a name or a variable object.
# => *args (required): A list of the arguments to add to var (can be any number of args)
#
# Returns:
# => The new Ruby representation of the variable.  Write it back to the database
# to save it.
#
# Example:
# test = add_args_to_var("test", "arg1", "arg2", "arg3")
# setVariable("test",test)
# -------------------------------------------------------------------
def add_args_to_var(var, *args)
   if var.class == "".class
      var = getVariable(var)
   end

   var_new = createNewVariable(var.name, var.arglist + args)

   for cell in var.cells
      new_cell = var_new.make_new_cell()
      new_cell.change_arg("onset", cell.onset)
      new_cell.change_arg("offset", cell.offset)
      for arg in var.arglist
         v = eval "cell.#{arg}"
         new_cell.change_arg(arg, v)
      end
   end

   return var_new
end

#-------------------------------------------------------------------
# Method name: create_mutually_exclusive
# Function: Create a new column from two others, mixing their cells together
#  such that the new variable has all of the arguments of both other variables
#  and a new cell for each overlap and mixture of the two cells.  Mixing two
#  variables together.
# Arguments:
# => name (required): The name of the new variable.
# => var1name (required): Name of the first variable to be mutexed.
# => var2name (required): Name of the second variable to be mutexed.
#
# Returns:
# => The new Ruby representation of the variable.  Write it back to the database
# to save it.
#
# Example:
# test = create_mutually_exclusive("test", "var1", "var2")
# setVariable("test",test)
# -------------------------------------------------------------------
def create_mutually_exclusive(name, var1name, var2name)
   var1 = getVariable(var1name)
   var2 = getVariable(var2name)

   var1_argprefix = var1.name.gsub(/(\W)+/,"").downcase + "_"
   var2_argprefix = var2.name.gsub(/(\W)+/,"").downcase + "_"

   var1_argprefix.gsub(".", "")

   v1arglist = var1.arglist.map { |arg| var1_argprefix + arg }
   v2arglist = var2.arglist.map { |arg| var2_argprefix + arg }

   args = Array.new
   args << (var1_argprefix + "ordinal")
   args += v1arglist

   args << (var2_argprefix + "ordinal")
   args += v2arglist

   puts "Creating mutex var", var1.arglist
   mutex = createNewVariable(name, args)
   puts "Mutex var created"

   # Now we have to go thru var1 and var2 and modify the argument names

   usedV1Cells = Array.new
   usedV2Cells = Array.new
   for i in 0..var1.cells.length - 1
      v1cell = var1.cells[i]
      prev_over = false
      within = false
      next_over = false

      # Figure out the cell relations
      for v2cell in var2.cells
         if v1cell.onset > v2cell.onset and v1cell.onset < v2cell.offset \
            and v1cell.offset > v2cell.offset
            prev_over = v2cell
         elsif v2cell.onset >= v1cell.onset and v2cell.onset <= v1cell.offset \
            and v2cell.offset <= v1cell.offset and v2cell.offset >= v1cell.onset
            if within == false
               within = Array.new
            end
            within << v2cell
         elsif v1cell.offset > v2cell.onset and v1cell.offset < v2cell.offset \
            and v1cell.onset < v2cell.onset
            next_over = v2cell
         end
      end
      v1_new_onset = v1cell.onset
      v1_new_offset = 0

      puts "Finding and adding previous crossover cells"
      # Create the prev cells
      if prev_over != false
         # Make the cell that overlaps the beginning of this one

         # This is the overlap cell
         cell2 = mutex.make_new_cell()
         cell2.change_arg("onset", v1cell.onset)
         cell2.change_arg("offset", prev_over.offset)
         for arg in mutex.arglist
            v = nil
            if arg.include?(var2.name + "_")
               a = arg.gsub(var2_argprefix, "")
               puts "Argname:" + arg
               v = eval "prev_over.#{a}"
            elsif arg.include?(var1.name + "_")
               a = arg.gsub(var1_argprefix, "")
               puts "Argname:" + arg
               v = eval "v1cell.#{a}"
            end
            cell2.change_arg(arg, v)
         end

         # cell3 = mutex.make_new_cell()
         # cell3.change_arg("onset", v1cell.offset)
         # cell3.change_arg("offset", prev_over.offset)
         v1_new_onset = prev_over.offset + 1

         printf("PREV V1:on=%d off=%d, cell1:on=%d off=%d\n", \
                v1cell.onset, v1cell.offset, cell2.onset, cell2.offset)

         # printf("PREV V1:on=%d off=%d, cell1:on=%d off=%d, cell2:on=%d off=%d\n", \
         #         v1cell.onset, v1cell.offset, cell1.onset, cell1.offset, cell2.onset, cell2.offset)

         usedV2Cells << prev_over
      end

      puts "Finding and adding cells within cells"
      if within != false
         # Make cells for each cell within the main cell
         for wcell in within

            cell1 = mutex.make_new_cell()
            cell1.change_arg("onset", wcell.onset)
            cell1.change_arg("offset", wcell.offset)
            for arg in mutex.arglist
               v = nil
               if arg.include?(var2.name + "_")
                  a = arg.gsub(var2_argprefix, "")
                  v = eval "wcell.#{a}"
               elsif arg.include?(var1.name + "_")
                  a = arg.gsub(var1_argprefix, "")
                  v = eval "v1cell.#{a}"
               end
               cell1.change_arg(arg, v)
            end

            printf("WITHIN V2:on=%d off=%d, cell1:on=%d off=%d, new:on=%d, off=:%d\n", \
                   v1cell.onset, v1cell.offset, cell1.onset, cell1.offset, wcell.onset, wcell.offset)

            usedV2Cells << wcell
            usedV1Cells << v1cell
         end
      end

      puts "Finding and adding next crossing cells for onset V1: onset=" + v1cell.onset.to_s + " off=" + v1cell.offset.to_s
      if next_over != false
         # Make cells for cell overlapping end
         cell1 = mutex.make_new_cell()
         cell1.change_arg("onset", next_over.onset)
         cell1.change_arg("offset", v1cell.offset)
         for arg in mutex.arglist
            v = nil
            if arg.include?(var2.name + "_")
               a = arg.gsub(var2_argprefix, "")
               v = eval "next_over.#{a}"
            elsif arg.include?(var1.name + "_")
               a = arg.gsub(var1_argprefix, "")
               v = eval "v1cell.#{a}"
            end
            puts arg, v
            cell1.change_arg(arg, v)
         end

         # cell2 = mutex.make_new_cell()
         #       cell2.change_arg("onset", v1cell.offset)
         #       cell2.change_arg("offset", next_over.offset)

         v1_new_onset = next_over.onset + 1

         usedV2Cells << next_over

         # printf("NEXT V1:on=%d off=%d, cell1:on=%d off=%d, cell2:on=%d off=%d\n", \
         #        v1cell.onset, v1cell.offset, cell1.onset, cell1.offset, cell2.onset, cell2.offset)
         printf("NEXT V1:on=%d off=%d, cell1:on=%d off=%d\n", \
                v1cell.onset, v1cell.offset, cell1.onset, cell1.offset)
      end
      if prev_over != false or within != false or next_over != false
         usedV1Cells << v1cell
      end
   end

   puts "Adding V2 within cells"
   # Now add in the v1 cells that are within v2 cells
   for i in 0..var2.cells.length - 1
      within = Array.new
      v2cell = var2.cells[i]

      for v1cell in var1.cells
         if v1cell.onset >= v2cell.onset and v1cell.onset <= v2cell.offset \
            and v1cell.offset <= v2cell.offset and v1cell.offset >= v2cell.onset \
            and not (v1cell.onset == v2cell.onset and v1cell.offset == v2cell.offset)
            within << v1cell
         end
      end

      if within.length > 0
         # Make cells for each cell within the main cell
         for wcell in within

            cell1 = mutex.make_new_cell()
            cell1.change_arg("onset", wcell.onset)
            cell1.change_arg("offset", wcell.offset)
            for arg in mutex.arglist
               v = nil
               if arg.include?(var1.name + "_")
                  a = arg.gsub(var1_argprefix, "")
                  v = eval "wcell.#{a}"
               elsif arg.include?(var2.name + "_")
                  a = arg.gsub(var2_argprefix, "")
                  v = eval "v2cell.#{a}"
               end
               cell1.change_arg(arg, v)
            end

            printf("WITHIN V2:on=%d off=%d, cell1:on=%d off=%d, new:on=%d, off=:%d\n", \
                   v1cell.onset, v1cell.offset, cell1.onset, cell1.offset, wcell.onset, wcell.offset)

            usedV1Cells << wcell
            usedV2Cells << v2cell
         end
      end

   end

   puts "Erasing dupes."
   # Erase dupes
   usedV1Cells.uniq!
   usedV2Cells.uniq!

   for c in usedV2Cells
      var2.cells.delete(c)
   end
   for c in usedV1Cells
      var1.cells.delete(c)
   end
   for c in var1.cells
      cell = mutex.make_new_cell()
      cell.change_arg("onset", c.onset)
      cell.change_arg("offset", c.offset)
      for arg in mutex.arglist
         if arg.include?(var1.name + "_")
            puts "C:",arg, v, c.arglist

            a = arg.gsub(var1_argprefix, "")
            v = eval "c.#{a}"
            cell.change_arg(arg, v)
         end
      end
   end
   for c in var2.cells
      cell = mutex.make_new_cell()
      cell.change_arg("onset", c.onset)
      cell.change_arg("offset", c.offset)
      for arg in mutex.arglist
         if arg.include?(var2.name + "_")
            puts arg, v, c.arglist

            a = arg.gsub(var2_argprefix, "")
            v = eval "c.#{a}"
            cell.change_arg(arg, v)
         end
      end
   end

   # Now we sort the cells in mutex so we can
   # search thru what we have and add in blocks
   mutex.sort_cells()

   # Calculate and add cells for gaps in v1
   var1 = getVariable(var1name)


   for v1cell in var1.cells
      dirty = false
      for i in 0...mutex.cells.length-1
         if dirty == true
            mutex.sort_cells()
            dirty = false
         end
         mcell_cur = mutex.cells[i]
         mcell_next = mutex.cells[i+1]
         # puts "V1:" + v1cell.onset.to_s + " " + v1cell.offset.to_s
         # puts mcell_cur.onset.to_s + " " + mcell_cur.offset.to_s \
         #     + "/" + mcell_next.onset.to_s + " " + mcell_next.offset.to_s

         v1range = (v1cell.onset..v1cell.offset).to_a
         if v1range.include?(mcell_cur.onset) and \
            v1range.include?(mcell_cur.offset) and \
            v1range.include?(mcell_next.onset) and \
            v1range.include?(mcell_next.offset)

            if mcell_cur.offset + 1 < mcell_next.onset - 1
               cell = mutex.make_new_cell()
               cell.change_arg("onset", mcell_cur.offset + 1)
               cell.change_arg("offset", mcell_next.onset - 1)
               for arg in mutex.arglist
                  a = arg.gsub(var1_argprefix, "")
                  if arg.include?(var1.name + "_")
                     puts "ONE SIDE +1-1:", arg, v, cell.onset, cell.offset
                     v = eval "v1cell.#{a}"
                     cell.change_arg(arg, v)
                     dirty = true
                  end
               end
            end
         elsif v1range.include?(mcell_cur.onset) and \
            v1range.include?(mcell_cur.offset) and \
            mcell_next.onset - 1 > mcell_cur.offset

            if mcell_cur.onset == v1cell.onset
               if mcell_cur.offset + 1 < v1cell.offset
                  cell = mutex.make_new_cell()
                  cell.change_arg("onset", mcell_cur.offset + 1)
                  cell.change_arg("offset", v1cell.offset)
                  for arg in mutex.arglist
                     a = arg.gsub(var1_argprefix, "")
                     if arg.include?(var1.name + "_")
                        puts "ONE SIDE ==on:" , arg, v, v1cell.arglist, cell.onset, cell.offset
                        v = eval "v1cell.#{a}"
                        cell.change_arg(arg, v)
                        dirty = true
                     end
                  end
               end
               # elsif mcell_cur.offset == v1cell.offset
               #           if v1cell.onset < mcell_cur.onset - 1
               #             cell = mutex.make_new_cell()
               #             cell.change_arg("onset", v1cell.onset)
               #             cell.change_arg("offset", mcell_cur.onset - 1)
               #             for arg in mutex.arglist
               #               a = arg.gsub(var1_argprefix, "")
               #               if arg.include?(var1.name + "_")
               #                 v = eval "v1cell.#{a}"
               #                 puts "ONE SIDE ==off:", arg, v, v1cell.onset, v1cell.offset, cell.onset, cell.offset, mcell_cur.onset, mcell_cur.offset
               #                 cell.change_arg(arg, v)
               #                 dirty = true
               #               end
               #             end
               #           end
            end
         end
      end
   end

   mutex.sort_cells()
   var2 = getVariable(var2name)

   for v2cell in var2.cells
      dirty = false
      for i in 0...mutex.cells.length-1
         if dirty == true
            mutex.sort_cells()
            dirty = false
         end
         mcell_cur = mutex.cells[i]
         mcell_next = mutex.cells[i+1]
         # puts "V2:" + v2cell.onset.to_s + " " + v2cell.offset.to_s
         # puts mcell_cur.onset.to_s + " " + mcell_cur.offset.to_s \
         #     + "/" + mcell_next.onset.to_s + " " + mcell_next.offset.to_s

         v2range = (v2cell.onset..v2cell.offset).to_a
         if v2range.include?(mcell_cur.onset) and \
            v2range.include?(mcell_cur.offset) and \
            v2range.include?(mcell_next.onset) and \
            v2range.include?(mcell_next.offset)

            if mcell_cur.offset + 1 < mcell_next.onset - 1
               cell = mutex.make_new_cell()
               cell.change_arg("onset", mcell_cur.offset + 1)
               cell.change_arg("offset", mcell_next.onset - 1)
               for arg in mutex.arglist
                  a = arg.gsub(var2_argprefix, "")
                  if arg.include?(var2.name + "_")
                     v = eval "v2cell.#{a}"
                     puts "V2 Single:", arg, v, v2cell.arglist, cell.onset, cell.offset
                     cell.change_arg(arg, v)
                     dirty = true
                  end
               end
            end
         elsif v2range.include?(mcell_cur.onset) and \
            v2range.include?(mcell_cur.offset) and \
            mcell_next.onset - 1 > mcell_cur.offset

            if mcell_cur.onset == v2cell.onset
               if mcell_cur.offset + 1 < v2cell.offset
                  cell = mutex.make_new_cell()
                  cell.change_arg("onset", mcell_cur.offset + 1)
                  cell.change_arg("offset", v2cell.offset)
                  for arg in mutex.arglist
                     a = arg.gsub(var2_argprefix, "")
                     if arg.include?(var2.name + "_")
                        v = eval "v2cell.#{a}"
                        puts "V2 Single:",arg, v, v2cell.arglist, cell.onset, cell.offset
                        cell.change_arg(arg, v)
                        dirty = true
                     end
                  end
               end
            end
            if mcell_next.offset == v2cell.offset
               if v2cell.onset < mcell_next.onset - 1


                  cell = mutex.make_new_cell()
                  cell.change_arg("onset", v2cell.onset)
                  cell.change_arg("offset", mcell_next.onset - 1)
                  for arg in mutex.arglist
                     a = arg.gsub(var2_argprefix, "")
                     if arg.include?(var2.name + "_")
                        v = eval "v2cell.#{a}"
                        puts "V2 Single:", arg, v, v2cell.arglist, cell.onset, cell.offset
                        cell.change_arg(arg, v)
                        dirty = true
                     end
                  end
               end
            end
         end
      end
   end

   mutex.sort_cells()

   puts "MUTEX FINISHED"

   return mutex

   # Have it write ordinals in at end
   # Have it put ordinals of original cells in
   # Have it have two arguments to name the prefixes for the arguments
   #
end

#-------------------------------------------------------------------
# Method name: load_db
# Function: Loads a new database from a file.  DOES NOT ALTER THE GUI.
# Arguments:
# => filename (required): The FULL PATH to the saved OpenSHAPA file.
#
# Returns:
# => db: The database of the opened project.  Set to $db to use other
#     functions with it.
# => pj: The project data of the opened project.  Set to $pj to use other
#     functions with it.
#
# Example:
# $db,$pj = load_db("/Users/username/Desktop/test.opf")
# -------------------------------------------------------------------

def load_db(filename)
   # Packages needed for opening and saving projects and databases.


   #
   # ****************************************************************************
   # *** Check to make sure filename below is the absolute path to a project. ***
   # ****************************************************************************
   #
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
   open_c.open_project(filename)

   # Get the database that was opened.
   db = open_c.get_database

   # Get the project that was opened (if you want).
   proj = open_c.get_project

   # If the open went well - query the database, do calculations or whatever
   unless db.nil?
      # This just prints the number of columns in the database.
      puts "Opened a project with '" + db.get_columns.length.to_s + "' columns!"
   else
      puts "Unable to open the project '" + filename + "'"
      exit
   end

   puts filename + " has been loaded."

   return db, proj
end


#-------------------------------------------------------------------
# Method name: save_db
# Function: Saves the current $db and $pj variables to filename.  If
#     filename ends with .csv, it saves a .csv file.  Otherwise it saves
#     it as a .opf.
# Arguments:
# => filename (required): The FULL PATH to where the OpenSHAPA file should
#        be saved.
#
# Returns:
# => Nothing.
#
# Example:
# save_db("/Users/username/Desktop/test.opf")
# -------------------------------------------------------------------
def save_db(filename)
   #
   # Main body of example script:
   #
   puts "Saving Database: " + filename

   # Create the controller that holds all the logic for opening projects and
   # databases.
   save_c = SaveC.new

   #
   # Saves a database (i.e. a .odb or .csv file). If you want to save a project
   # call save_project("project file", project, database) instead.
   # These methods do *NOT* alter the OpenSHAPA UI.
   #
   if filename.include?('.csv')
      save_c.save_database(filename, $db)
   else
      if $pj == nil or $pj.getDatabaseFileName == nil
         $pj = Project.new()
         $pj.setDatabaseFileName("db")
         dbname = filename[filename.rindex("/")+1..filename.length]
         $pj.setProjectName(dbname)
      end
      save_file = java.io.File.new(filename)
      save_c.save_project(save_file, $pj, $db)
   end

   puts "Save successful."

end

def delete_column(colname)
   col = $db.get_column(colname)
   numcells = col.get_num_cells
   numcells.downto(1) do |i|
      $db.remove_cell($db.get_cell(col.get_id, i).get_id)
   end
   $db.remove_column(col.get_id)
end


#-------------------------------------------------------------------
# Method name: open_macshapa_closed_db
# Function: Opens an old, closed database format MacSHAPA file and loads
#     it into the current open database.
#
#     WARNING: This will only read in
#     matrix and string variables.  Predicates are not yet supported.
#     Queries will not be read in.  Times are translated to milliseconds
#     for compatibility with OpenSHAPA.
# Arguments:
# => filename (required): The FULL PATH to the saved MacSHAPA file.
# => write_to_gui (required): Whether the MacSHAPA file should be read into
#        the database currently open in the GUI or whether it should just be
#        read into the Ruby interface.  After this script is run $db and $pj
#        are now the MacSHAPA file.
#
# Returns:
# => db: The database of the opened project.
# => pj: The project data of the opened project.
#
# Example:
# $db,$pj = load_db("/Users/username/Desktop/test.opf")
# -------------------------------------------------------------------
def open_macshapa_closed_db(filename, write_to_gui)

   # Create a new DB for us to use so we don't touch the GUI... some of these
   # files can be huge.
   # Since I don't know how to make a whole new project, lets just load a blank file.
   if not write_to_gui
      #$db,$pj = load_db("/Users/j4lingeman/Desktop/blank.opf")
      $db = MacshapaDatabase.new(1000)
      $pj = Project.new()
   end



   f = File.open(filename, 'r')

   # Read and split file by lines.  '\r' is used because that is the default
   # format for OS9 files.
   file = f.gets
   lines = file.split(/\r/)

   # Find the variable names in the file and use these to create and set up
   # our columns.
   predIndex = lines.index("***Predicates***")
   varIndex = lines.index("***Variables***")
   spreadIndex = lines.index("***SpreadPane***")
   predIndex += 2

   variables = Hash.new
   varIdent = Array.new

   while predIndex < varIndex
      l = lines[predIndex].split(/ /)[5]
      varname = l[0..l.index("(") - 1]
      if varname != "###QueryVar###" and varname != "div"
         variables[varname] = l[l.index("(")+1..l.length-2].split(/,/)
         varIdent << l
      end
      predIndex += 1
   end

   # Create the columns for the variables
   variables.each do |key, value|
      # Create column
      puts key
      if !$db.col_name_in_use(key)
         col = DataColumn.new($db, key, MatrixVocabElement::MatrixType::MATRIX)
         $db.add_column(col)
      end

      mve0 = $db.get_vocab_element(key)
      if mve0.get_num_formal_args() == 1
         # Setup structure of matrix column
         mve0 = MatrixVocabElement.new(mve0)
         mve0.delete_formal_arg(0)
         value.each { |v|
            # Strip out the ordinal, onset, and offset.  These will be handled on a
            # cell by cell basis.
            if v != "<ord>" and v != "<onset>" and v != "<offset>"
               #puts v
               farg = NominalFormalArg.new($db, v)
               mve0.append_formal_arg(farg)
            end
         }
         $db.replace_matrix_ve(mve0)
      end
   end

   # Search for where in the file the var's cells are, create them, then move
   # on to the next variable.
   varSection = lines[varIndex..spreadIndex]

   varIdent.each do |id|
      col = $db.get_column(id[0..id.index("(")-1])
      mve = $db.get_matrix_ve(col.its_mve_id)
      matid = mve.get_id()

      # Search the variable section for the above id
      varSection.each do |l|
         line = l.split(/[\t\s]/)
         if line[2] == id
            #puts varname
            start = varSection.index(l) + 1

            stringCol = false

            if varSection[start - 2].index("strID") != nil
               stringCol = true
            end

            #Found it!  Now build the cells
            while varSection[start] != "0"

               if stringCol == false
                  cellData = varSection[start].split(/[\t\s]/)
               else
                  puts "Processing string variable..."
                  puts cellData
                  cellData = varSection[start].split(/[\t]/)
               end

               # Init cell to null
               cell = DataCell.new($db, col.get_id, mve.get_id)
               mat = Matrix.new($db, matid)

               # Convert onset/offset from 60 ticks/sec to milliseconds
               onset = cellData[0].to_i / 60.0 * 1000
               offset = cellData[1].to_i / 60.0 * 1000

               # Set onset/offset of cell
               cell.onset = TimeStamp.new(1000, onset.round)
               cell.offset = TimeStamp.new(1000, offset.round)

               # Split up cell data
               data = cellData[cellData.length - 1]
               #puts data

               if stringCol == false
                  data = data[1..data.length-2]
                  data = data.split(/,/)
               else
                  data = data.strip()
                  data = data.gsub(",", "")
               end
               #puts data

               # Cycle thru cell data arguments and fill them into the cell matrix
               narg = 0
               data.each do |d|
                  fargid = mve.get_formal_arg(narg).get_id()
                  if d == "" or d == nil or d.index("<") != nil
                     fdv = NominalDataValue.new($db, fargid)
                     fdv.clearValue()
                  else
                     fdv = NominalDataValue.new($db, fargid, d)
                  end
                  mat.replaceArg(narg,fdv)
                  narg += 1
               end

               # Put cell into database
               cell.set_val(mat)
               $db.append_cell(cell)
               start += 1
            end
         end
      end
   end

   f.close()

   return $db, $pj
end


#-------------------------------------------------------------------
# Method name: transfer_columns
# Function: Transfers columns between databases.  If db1 or db2 are set
#     to the empty string "", then that database is the current database
#     in $db (usually the GUI's database).  So if you want to transfer a
#     column into the GUI, set db2 to "".  If you want to tranfer a column
#     from the GUI into a file, set db1 to "".  Setting remove to true will
#     DELETE THE COLUMNS YOU ARE TRANSFERRING FROM DB1.  Be careful!
# Arguments:
# => db1 (required): The FULL PATH to the saved OpenSHAPA file or set to
#     "" to use the currently opened database. Columns are transferred FROM here.
# => db2 (required): The FULL PATH to the saved OpenSHAPA file or set to
#     "" to use the currently opened database.  Columns are tranferred TO here.
# => remove (required): Set to true to delete columns in DB1 as they are moved to
#     db2.  Set to false to leave them intact.
# => varnames (requires at least 1): You can specify as many var names as you like
#     that will be retrieved from db1.  These should be the string names of the
#     variables.
#
# Returns:
# => Nothing.  Saves the files in place or modifies the GUI
#
# Example:
#  transfer_columns("/Users/username/Desktop/test.opf","",true,"idchange")
#  The above example will transfer the column "idchange" from test.opf to the GUI
#  and leave test.opf intact with no modifications.
# -------------------------------------------------------------------
def transfer_columns(db1, db2, remove, *varnames)
   puts "Transfering the following columns from " + db1 + " to " + db2 + ":"
   puts varnames

   if remove
      puts "WARNING: These columns will be deleted from " + db1
   end

   if db1 == ""
      from_db = $db
      from_pj = $pj
   else
      from_db, from_pj = load_db(db1)
   end

   if db2 == ""
      to_db = $db
      to_pj = $pj
   else
      to_db, to_pj = load_db(db2)
   end

   # Get from DB1
   $db, $pj = from_db, from_pj
   vars_to_trans = Array.new
   for v in varnames
      vars_to_trans << getVariable(v)
   end

   # Transfer to DB2
   $db, $pj = to_db, to_pj
   for i in 0...vars_to_trans.length
      setVariable(varnames[i],vars_to_trans[i])
   end
   if db2 != ""
      puts "Saving " + db2
      save_db(db2)
   end

   # Removing columns should be the last thing we do in case anything goes wrong
   # We don't want to lose a column for any reason.
   if remove
      $db, $pj = from_db, from_pj
      if remove
         for v in varnames
            delete_column(v)
         end
      end
      if db1 != ""
         puts "Saving " + db1
         save_db(db1)
      end
   end

   puts "Columns transferred successfully."
end

#-----------------------------------------------------------------
# USER EDITABLE SECTION: Use this section between begin and end
# to make your scripts.
#-----------------------------------------------------------------

begin

end
