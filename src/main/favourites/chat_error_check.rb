# error-check.rb
# Checks for errors in Datavyu file intended for chat export. Ensures that all speakers are listed as participants. Non-conforming codes are indicated. 
#
# 2014-09-02	thatrickgilmore@gmail.com
# 2014-09-11	Jon Coe

require 'Datavyu_API.rb'

begin
	# USER CAN CHANGE VALUES BETWEEN HERE...
	source_col = "SPEECH"
	header_col = "CHILDES_HEADER"
	speaker_code = "speaker"
	content_code = "content"
	combined_code = "code01" #if this is empty string use speaker/content separately. otherwise this represents both
	# ... AND HERE

	speaker_list = []
	header_cell = getColumn(header_col).cells[0]
	for i in header_cell.arglist
		code_contents = header_cell.send(i.to_sym)
		if code_contents.partition(":")[0] == "@Participants"
			for j in code_contents.partition(":")[2].split(",")
				speaker_list.push(j.lstrip.slice(0,3))
			end
		end
	end
	p speaker_list

	time_code_sep = 21.chr # CHILDES .cha files use as time separator
	bad_codes = 0
	bad_codes_list = []

	# Loop through cells in source column, flag and highlight anomalous results
	data_col = getColumn( source_col )
	for curr_cell in data_col.cells
		if combined_code != "" && (curr_cell.methods.include? combined_code)
			full_line = curr_cell.send(combined_code.to_sym)
			speaker_tok = full_line.partition(" ")[0]
			content_tok = full_line.partition(" ")[2]
		else
			speaker_tok = curr_cell.send(speaker_code.to_sym)
			content_tok = curr_cell.send(content_code.to_sym)
		end
		cell_num = curr_cell.ordinal
		if ( speaker_list.include? speaker_tok )
			current_str = cell_num.to_s + "\t*" + speaker_tok + ":\t" + content_tok + " " + time_code_sep + curr_cell.onset.to_s + "_" + curr_cell.offset.to_s + time_code_sep + "\n"
			puts current_str
		else
			puts cell_num.to_s + ">>>>\t*" + speaker_tok + ":\t" + content_tok + " " + time_code_sep + curr_cell.onset.to_s + "_" + curr_cell.offset.to_s + time_code_sep + "\n"
			bad_codes += 1
			bad_codes_list.push( cell_num )
		end # if ( speaker_list...
	end # for curr_cell

	# Report errors
	puts "---Total bad codes: " + bad_codes.to_s + "\n"
	if bad_codes
		puts "---Bad code indices: "
		bad_str = ""
		for i in bad_codes_list
			bad_str = bad_str + i.to_s + " "
		end
		puts bad_str + "\n"
	end
end
