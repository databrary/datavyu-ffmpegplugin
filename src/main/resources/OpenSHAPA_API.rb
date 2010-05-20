#-------------------------------------------------------------------
# OpenSHAPA API v 0.8
# Please read the function headers for information on how to use them.
#-------------------------------------------------------------------

require 'java'
require 'csv'
require 'time'

import 'org.openshapa.models.db.Database'
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
            argvals[i] = val
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
        $db.remove_column(oldcol.get_id)
        
        col = DataColumn.new($db, name, MatrixVocabElement::MatrixType::MATRIX)
        $db.add_column(col)
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

def setVariable(var)
  setVariable(var.name, var)
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
  
  # Set the argument names in arg_names and set the database internal style with <argname> in old_args
  arg_names = Array.new
  old_args = Array.new
  for arg in args
    arg_names << arg
    old_args << "<" + arg + ">"
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

def make_duration_trial_rel(relname, var_to_copy, skip_trials)
  
end

#-----------------------------------------------------------------
# USER EDITABLE SECTION: Use this section between begin and end
# to make your scripts.
#-----------------------------------------------------------------

begin
  
end
