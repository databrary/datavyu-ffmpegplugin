require 'OpenSHAPA_API.rb'

##########################################
# USER PARAMETERS

# The video frame_rate. default: 30
frame_rate = 30

# Export file location. default: ~/Desktop/OpenSHAPA_Frame_Export.csv
export_file_location = "~/Desktop/OpenSHAPA_Frame_Export.csv"

##########################################################################
# YOU SHOULD NOT NEED TO EDIT BELOW THIS POINT
##########################################################################
##########################################################################
export_file_location = File.expand_path(export_file_location)
export_file = File.open(export_file_location, 'a')

# Get list of variables and sort them
variable_name_list = getVariableList()
variable_name_list.sort!

# Copy all of the variables into Rubyland
variable_list = Hash.new
for varname in variable_name_list
  variable_list[varname] = getVariable(varname)
end

# Keep an index of which cell we are on so we don't have to
# look it up each time
cell_index_list = Hash.new
for varname in variable_name_list
  cell_index_list[varname] = 0
end

# Find the earliest and the latest times across cells
start_time = 99999999999999999 # Ruby has no maxint variable! Arg!
end_time = 0 

time_step = 1.0 / frame_rate

for var in variable_list.values()
  for cell in var.cells
    if start_time < cell.onset
      start_time = cell.onset
    end
    if start_time < cell.offset
      start_time = cell.offset
    end
    if end_time > cell.onset
      end_time = cell.onset
    end
    if end_time > cell.offset
      end_time = cell.offset
    end
  end
end

# Now that we have the start and stop times of the video,
# loop thru each frame

current_time = start_time
while current_time <= end_time
  for var in varname.list
    cell_index = cell_index_list[var.name]
    
    if
    
  current_time += time_step
end
  